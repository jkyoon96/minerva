/**
 * 파일 관련 타입 정의
 */

import { User } from './index';

/**
 * 파일 타입
 */
export type FileType =
  | 'pdf'
  | 'doc'
  | 'docx'
  | 'ppt'
  | 'pptx'
  | 'xls'
  | 'xlsx'
  | 'image'
  | 'video'
  | 'audio'
  | 'archive'
  | 'code'
  | 'text'
  | 'other';

/**
 * 파일 권한 타입
 */
export type FilePermissionType = 'public' | 'enrolled' | 'instructors' | 'private';

/**
 * 저장된 파일 정보
 */
export interface StoredFile {
  id: string;
  courseId: string;
  folderId?: string;
  filename: string;
  originalName: string;
  mimeType: string;
  size: number;
  uploaderId: string;
  uploader?: User;
  downloadCount: number;
  permission: FilePermissionType;
  createdAt: string;
  updatedAt: string;
  folder?: FileFolder;
}

/**
 * 파일 폴더
 */
export interface FileFolder {
  id: string;
  courseId: string;
  parentId?: string;
  name: string;
  creatorId: string;
  creator?: User;
  createdAt: string;
  updatedAt: string;
  files?: StoredFile[];
  children?: FileFolder[];
}

/**
 * 파일 권한
 */
export interface FilePermission {
  fileId: string;
  type: FilePermissionType;
  allowedUserIds?: string[];
  allowedRoles?: string[];
}

/**
 * 파일 업로드 진행 상태
 */
export interface FileUploadProgress {
  file: File;
  progress: number;
  status: 'pending' | 'uploading' | 'success' | 'error';
  uploadedFile?: StoredFile;
  error?: string;
}

/**
 * 폴더 트리 노드
 */
export interface FolderTreeNode {
  id: string;
  name: string;
  parentId?: string;
  children: FolderTreeNode[];
  fileCount: number;
  isExpanded?: boolean;
}

/**
 * 파일 필터 옵션
 */
export interface FileFilters {
  search?: string;
  type?: FileType;
  folderId?: string;
  uploaderId?: string;
  dateFrom?: string;
  dateTo?: string;
}

/**
 * 파일 정렬 옵션
 */
export type FileSortBy = 'name' | 'size' | 'uploadedAt' | 'downloads';

export type FileSortOrder = 'asc' | 'desc';

export interface FileSortOptions {
  sortBy: FileSortBy;
  sortOrder: FileSortOrder;
}

/**
 * 파일 업로드 요청
 */
export interface FileUploadRequest {
  file: File;
  courseId: string;
  folderId?: string;
  permission?: FilePermissionType;
}

/**
 * 폴더 생성 요청
 */
export interface CreateFolderRequest {
  courseId: string;
  parentId?: string;
  name: string;
}

/**
 * 파일 다운로드 응답
 */
export interface FileDownloadResponse {
  url: string;
  filename: string;
  expiresAt: string;
}

/**
 * 파일 검색 결과
 */
export interface FileSearchResult {
  file: StoredFile;
  highlights?: {
    filename?: string[];
    content?: string[];
  };
}
