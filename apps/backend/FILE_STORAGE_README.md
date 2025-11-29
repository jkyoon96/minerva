# File Upload Service Implementation

## Overview

Comprehensive file storage and management system supporting both local file system and AWS S3 storage, with advanced features including folder organization, permission management, and file search capabilities.

## Features Implemented

### 1. Storage Service Layer (`common/storage/`)

#### Interfaces
- **StorageService**: Abstract interface for file storage operations
  - `upload()` - Upload files with MultipartFile or InputStream
  - `download()` - Download files as Resource
  - `delete()` - Delete files from storage
  - `getUrl()` - Get file access URL
  - `exists()` - Check file existence
  - `generatePresignedUrl()` - Generate temporary download URLs

#### Implementations
- **LocalStorageService**: Local file system storage
  - Stores files in configurable directory (`./uploads` by default)
  - Generates UUID-based filenames
  - Supports hierarchical folder structure

- **S3StorageService**: AWS S3 storage
  - Full S3 SDK v2 integration
  - Pre-signed URL generation
  - CloudFront CDN support
  - Automatic cleanup on shutdown

#### Configuration
- **StorageProperties**: Configurable storage settings
  - Provider selection (local/s3)
  - Local storage path
  - AWS S3 credentials and bucket
  - File upload limits and validation

#### Validation
- **FileValidator**: File validation component
  - File size limits (default 50MB)
  - MIME type whitelist
  - Filename length limits
  - Path traversal protection

---

### 2. File Domain (`domain/file/`)

#### Entities
- **StoredFile**: File metadata entity
  - Original and stored filenames
  - Size, MIME type, extension
  - Upload info (user, course, folder)
  - Download count tracking
  - Soft delete support

- **FileFolder**: Hierarchical folder structure
  - Parent-child relationships
  - Course association
  - Sort ordering
  - Full path calculation
  - Depth tracking

- **FilePermission**: Granular access control
  - READ, WRITE, DELETE, OWNER permissions
  - User-specific permissions
  - Permission granter tracking

#### Repositories
- **StoredFileRepository**: File queries
  - Find by course/folder
  - Search by filename/description
  - MIME type filtering
  - Size calculations

- **FileFolderRepository**: Folder queries
  - Root/sub-folder retrieval
  - Tree structure queries
  - Name-based search
  - Duplicate checking

- **FilePermissionRepository**: Permission queries
  - User permission lookup
  - Owner identification
  - Access control verification

#### DTOs
- **FileUploadRequest**: Upload parameters
- **FileResponse**: File metadata response
- **FolderRequest/Response**: Folder CRUD
- **FileSearchRequest**: Search parameters
- **PermissionRequest/Response**: Permission management

---

### 3. Services

#### FileService
- File upload with validation
- Download with access control
- Delete with permission checks
- Visibility management (public/private)
- Pre-signed URL generation
- Download count tracking

#### FolderService
- Folder CRUD operations
- Tree structure building
- Hierarchical navigation
- Folder search
- Cascading delete

#### FilePermissionService
- Permission granting/revoking
- Access control checks (read/write/delete)
- Owner verification
- Permission listing

#### FileSearchService
- Keyword-based search
- MIME type filtering
- Advanced filtering (size, date)
- Pagination and sorting

---

### 4. API Endpoints

#### File Management (`/v1/files`)
```
POST   /upload                    - Upload file
GET    /{fileId}                  - Get file info
GET    /{fileId}/download         - Download file
DELETE /{fileId}                  - Delete file
PUT    /{fileId}/visibility       - Change public/private
PUT    /{fileId}/description      - Update description
GET    /{fileId}/download-url     - Get pre-signed URL
GET    /search                    - Search files
GET    /courses/{courseId}        - List course files
GET    /folders/{folderId}        - List folder files
```

#### File Permissions (`/v1/files/{fileId}/permissions`)
```
POST   /                          - Grant permission
DELETE /{userId}                  - Revoke permission
GET    /                          - List permissions
```

#### Folder Management (`/v1/folders`)
```
POST   /                          - Create folder
GET    /{folderId}                - Get folder
PUT    /{folderId}                - Update folder
DELETE /{folderId}                - Delete folder
GET    /courses/{courseId}/tree   - Get folder tree
GET    /courses/{courseId}/root   - List root folders
GET    /{folderId}/children       - List sub-folders
GET    /search                    - Search folders
```

---

## Configuration

### application.yml

```yaml
spring:
  servlet:
    multipart:
      max-file-size: 50MB
      max-request-size: 100MB
      enabled: true

app:
  storage:
    provider: local  # local or s3

    # Local storage settings
    local:
      base-path: ./uploads

    # S3 storage settings
    s3:
      bucket: ${AWS_S3_BUCKET:eduforum-files}
      region: ${AWS_REGION:ap-northeast-2}
      access-key: ${AWS_ACCESS_KEY:}
      secret-key: ${AWS_SECRET_KEY:}
      cloudfront-url: ${AWS_CLOUDFRONT_URL:}

    # Upload validation
    upload:
      max-file-size: 52428800  # 50MB
      max-filename-length: 255
      allowed-mime-types: image/*,application/pdf,application/msword,
        application/vnd.openxmlformats-officedocument.wordprocessingml.document,
        application/vnd.ms-excel,application/vnd.openxmlformats-officedocument.spreadsheetml.sheet,
        application/vnd.ms-powerpoint,application/vnd.openxmlformats-officedocument.presentationml.presentation,
        text/*,video/*,audio/*
```

### Environment Variables

**For S3 storage:**
```bash
AWS_S3_BUCKET=eduforum-files
AWS_REGION=ap-northeast-2
AWS_ACCESS_KEY=your-access-key
AWS_SECRET_KEY=your-secret-key
AWS_CLOUDFRONT_URL=https://d123456.cloudfront.net  # Optional
```

---

## Database Schema

### Tables Created (Schema: `file`)

1. **stored_files** - File metadata
   - File identification and storage info
   - Upload tracking and statistics
   - Soft delete support

2. **file_folders** - Folder hierarchy
   - Tree structure with parent references
   - Course association
   - Sort ordering

3. **file_permissions** - Access control
   - User-specific permissions
   - Permission types: READ, WRITE, DELETE, OWNER
   - Permission granter tracking

### Indexes
- Course-based queries
- Folder navigation
- User permissions
- MIME type filtering
- Creation date sorting

---

## Usage Examples

### 1. Upload a File

**Request:**
```bash
curl -X POST http://localhost:8000/api/v1/files/upload \
  -H "Authorization: Bearer {token}" \
  -F "file=@document.pdf" \
  -F 'request={
    "courseId": 1,
    "folderId": 5,
    "description": "Lecture slides",
    "isPublic": false
  };type=application/json'
```

**Response:**
```json
{
  "status": 200,
  "message": "파일이 업로드되었습니다",
  "data": {
    "id": 123,
    "originalName": "document.pdf",
    "size": 1048576,
    "formattedSize": "1.00 MB",
    "mimeType": "application/pdf",
    "url": "/api/v1/files/123/download",
    "courseId": 1,
    "folderId": 5,
    "isPublic": false,
    "downloadCount": 0,
    "uploadedById": 10,
    "uploadedByName": "John Doe",
    "createdAt": "2025-11-29T10:30:00"
  }
}
```

### 2. Create Folder Structure

```bash
# Create root folder
curl -X POST http://localhost:8000/api/v1/folders \
  -H "Authorization: Bearer {token}" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Week 1",
    "courseId": 1,
    "isPublic": true
  }'

# Create sub-folder
curl -X POST http://localhost:8000/api/v1/folders \
  -H "Authorization: Bearer {token}" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Lecture Notes",
    "courseId": 1,
    "parentId": 10,
    "isPublic": true
  }'
```

### 3. Search Files

```bash
curl -X GET 'http://localhost:8000/api/v1/files/search?courseId=1&keyword=lecture&mimeType=application/pdf&page=0&size=20' \
  -H "Authorization: Bearer {token}"
```

### 4. Grant File Permission

```bash
curl -X POST http://localhost:8000/api/v1/files/123/permissions \
  -H "Authorization: Bearer {token}" \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 25,
    "permission": "READ"
  }'
```

### 5. Get Folder Tree

```bash
curl -X GET http://localhost:8000/api/v1/folders/courses/1/tree \
  -H "Authorization: Bearer {token}"
```

---

## Storage Path Structure

### Local Storage
```
./uploads/
├── courses/
│   ├── 1/
│   │   ├── root/
│   │   │   └── abc123.pdf
│   │   └── folders/
│   │       ├── 5/
│   │       │   └── def456.docx
│   │       └── 10/
│   │           └── ghi789.png
│   └── 2/
│       └── ...
```

### S3 Storage
```
s3://eduforum-files/
├── courses/
│   ├── 1/
│   │   ├── root/
│   │   │   └── abc123.pdf
│   │   └── folders/
│   │       └── 5/
│   │           └── def456.docx
```

---

## Security Features

1. **Authentication**: All endpoints require JWT token
2. **Authorization**: Permission-based access control
3. **Validation**: File size, type, and name validation
4. **Path Security**: Protection against path traversal
5. **Soft Delete**: Files marked deleted, not immediately removed
6. **Owner Control**: Uploaders and professors have full control

---

## Performance Optimizations

1. **Indexed Queries**: All major queries use database indexes
2. **Lazy Loading**: Entities use lazy fetching
3. **Pagination**: Large result sets are paginated
4. **Pre-signed URLs**: Direct S3 access for downloads
5. **CloudFront**: Optional CDN support for S3

---

## Migration Notes

### From Local to S3
1. Update `app.storage.provider` to `s3`
2. Configure AWS credentials
3. Optionally migrate existing files to S3
4. Update file URLs in database

### Database Migration
Run the migration script:
```bash
flyway migrate
```

The script (`V011__Create_File_Storage.sql`) creates:
- Schema `file`
- Tables with indexes
- Foreign key constraints
- Triggers for updated_at

---

## Related GitHub Issues

- **#104**: File Upload API (S3 Integration) ✅
- **#105**: Folder Structure CRUD API ✅
- **#106**: File Permission Management Logic ✅
- **#107**: File Search API ✅

---

## Future Enhancements

1. **Virus Scanning**: Integrate ClamAV or similar
2. **Image Thumbnails**: Auto-generate thumbnails
3. **File Versioning**: Track file revisions
4. **Batch Operations**: Multi-file upload/delete
5. **Quota Management**: Per-user/course storage limits
6. **File Preview**: In-browser preview for common formats
7. **Trash/Recycle Bin**: Restore deleted files
8. **Activity Logging**: Audit trail for file operations

---

## Dependencies

### Required
- Spring Boot 3.x
- Spring Data JPA
- PostgreSQL
- AWS SDK for Java 2.x (for S3)

### Optional
- CloudFront (for CDN)

---

## Testing

### Local Development
```bash
# Use local storage
app.storage.provider=local
app.storage.local.base-path=./uploads
```

### Production
```bash
# Use S3 storage
app.storage.provider=s3
# Configure AWS credentials via environment variables
```

---

## Support

For questions or issues:
1. Check the API documentation at `/api/docs/swagger-ui.html`
2. Review database schema in migration files
3. Examine service layer for business logic
4. Check controller layer for API contracts
