-- File Storage Schema
-- 파일 저장 및 관리를 위한 스키마

-- 스키마 생성
CREATE SCHEMA IF NOT EXISTS file;

-- stored_files 테이블 (파일 메타데이터)
CREATE TABLE file.stored_files (
    id BIGSERIAL PRIMARY KEY,
    original_name VARCHAR(255) NOT NULL,
    stored_name VARCHAR(255) NOT NULL,
    path VARCHAR(500) NOT NULL,
    size BIGINT NOT NULL,
    mime_type VARCHAR(100) NOT NULL,
    extension VARCHAR(20),
    url VARCHAR(1000),
    uploaded_by BIGINT NOT NULL,
    course_id BIGINT,
    folder_id BIGINT,
    description TEXT,
    is_public BOOLEAN NOT NULL DEFAULT FALSE,
    download_count BIGINT NOT NULL DEFAULT 0,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100) NOT NULL,
    updated_by VARCHAR(100) NOT NULL,
    CONSTRAINT fk_stored_file_uploader FOREIGN KEY (uploaded_by)
        REFERENCES auth.users(id) ON DELETE CASCADE,
    CONSTRAINT fk_stored_file_course FOREIGN KEY (course_id)
        REFERENCES course.courses(id) ON DELETE CASCADE
);

-- stored_files 인덱스
CREATE INDEX idx_stored_file_course ON file.stored_files(course_id) WHERE is_deleted = FALSE;
CREATE INDEX idx_stored_file_folder ON file.stored_files(folder_id) WHERE is_deleted = FALSE;
CREATE INDEX idx_stored_file_uploader ON file.stored_files(uploaded_by) WHERE is_deleted = FALSE;
CREATE INDEX idx_stored_file_mime_type ON file.stored_files(mime_type) WHERE is_deleted = FALSE;
CREATE INDEX idx_stored_file_created_at ON file.stored_files(created_at DESC) WHERE is_deleted = FALSE;

-- file_folders 테이블 (폴더 구조)
CREATE TABLE file.file_folders (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    parent_id BIGINT,
    course_id BIGINT NOT NULL,
    created_by_user BIGINT NOT NULL,
    is_public BOOLEAN NOT NULL DEFAULT FALSE,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    sort_order INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100) NOT NULL,
    updated_by VARCHAR(100) NOT NULL,
    CONSTRAINT fk_folder_parent FOREIGN KEY (parent_id)
        REFERENCES file.file_folders(id) ON DELETE CASCADE,
    CONSTRAINT fk_folder_course FOREIGN KEY (course_id)
        REFERENCES course.courses(id) ON DELETE CASCADE,
    CONSTRAINT fk_folder_creator FOREIGN KEY (created_by_user)
        REFERENCES auth.users(id) ON DELETE CASCADE
);

-- file_folders 인덱스
CREATE INDEX idx_folder_course ON file.file_folders(course_id) WHERE is_deleted = FALSE;
CREATE INDEX idx_folder_parent ON file.file_folders(parent_id) WHERE is_deleted = FALSE;
CREATE INDEX idx_folder_sort_order ON file.file_folders(course_id, sort_order) WHERE is_deleted = FALSE;

-- stored_files 외래키 추가 (file_folders 생성 후)
ALTER TABLE file.stored_files
    ADD CONSTRAINT fk_stored_file_folder FOREIGN KEY (folder_id)
        REFERENCES file.file_folders(id) ON DELETE SET NULL;

-- file_permissions 테이블 (파일 권한 관리)
CREATE TABLE file.file_permissions (
    id BIGSERIAL PRIMARY KEY,
    file_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    permission VARCHAR(20) NOT NULL,
    granted_by BIGINT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100) NOT NULL,
    updated_by VARCHAR(100) NOT NULL,
    CONSTRAINT fk_permission_file FOREIGN KEY (file_id)
        REFERENCES file.stored_files(id) ON DELETE CASCADE,
    CONSTRAINT fk_permission_user FOREIGN KEY (user_id)
        REFERENCES auth.users(id) ON DELETE CASCADE,
    CONSTRAINT fk_permission_grantor FOREIGN KEY (granted_by)
        REFERENCES auth.users(id) ON DELETE SET NULL,
    CONSTRAINT uk_file_user_permission UNIQUE (file_id, user_id),
    CONSTRAINT chk_permission_type CHECK (permission IN ('READ', 'WRITE', 'DELETE', 'OWNER'))
);

-- file_permissions 인덱스
CREATE INDEX idx_permission_file ON file.file_permissions(file_id);
CREATE INDEX idx_permission_user ON file.file_permissions(user_id);
CREATE INDEX idx_permission_type ON file.file_permissions(permission);

-- 트리거: updated_at 자동 갱신
CREATE OR REPLACE FUNCTION file.update_file_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_stored_files_updated_at
    BEFORE UPDATE ON file.stored_files
    FOR EACH ROW
    EXECUTE FUNCTION file.update_file_updated_at();

CREATE TRIGGER trg_file_folders_updated_at
    BEFORE UPDATE ON file.file_folders
    FOR EACH ROW
    EXECUTE FUNCTION file.update_file_updated_at();

CREATE TRIGGER trg_file_permissions_updated_at
    BEFORE UPDATE ON file.file_permissions
    FOR EACH ROW
    EXECUTE FUNCTION file.update_file_updated_at();

-- 코멘트
COMMENT ON SCHEMA file IS '파일 저장 및 관리 스키마';
COMMENT ON TABLE file.stored_files IS '업로드된 파일 메타데이터';
COMMENT ON TABLE file.file_folders IS '파일 폴더 구조';
COMMENT ON TABLE file.file_permissions IS '파일 접근 권한';

COMMENT ON COLUMN file.stored_files.original_name IS '원본 파일명';
COMMENT ON COLUMN file.stored_files.stored_name IS '저장된 파일명 (UUID 등)';
COMMENT ON COLUMN file.stored_files.path IS '저장 경로';
COMMENT ON COLUMN file.stored_files.size IS '파일 크기 (bytes)';
COMMENT ON COLUMN file.stored_files.mime_type IS 'MIME 타입';
COMMENT ON COLUMN file.stored_files.download_count IS '다운로드 횟수';
COMMENT ON COLUMN file.stored_files.is_public IS '공개 파일 여부';
COMMENT ON COLUMN file.stored_files.is_deleted IS '소프트 삭제 플래그';

COMMENT ON COLUMN file.file_folders.parent_id IS '부모 폴더 ID (NULL이면 최상위)';
COMMENT ON COLUMN file.file_folders.sort_order IS '정렬 순서';

COMMENT ON COLUMN file.file_permissions.permission IS '권한 타입 (READ, WRITE, DELETE, OWNER)';
COMMENT ON COLUMN file.file_permissions.granted_by IS '권한 부여자';
