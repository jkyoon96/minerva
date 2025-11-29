/**
 * 파일 관련 API 함수
 */

import apiClient, { parseApiError } from './client';
import { API_ENDPOINTS } from './endpoints';
import { ApiResponse } from './types';
import {
  StoredFile,
  FileFolder,
  FolderTreeNode,
  CreateFolderRequest,
  FilePermissionType,
  FileSearchResult,
  FileDownloadResponse,
} from '@/types/file';

/**
 * 파일 업로드
 */
export const uploadFile = async (
  courseId: string,
  file: File,
  folderId?: string,
  permission?: FilePermissionType,
): Promise<StoredFile> => {
  try {
    const formData = new FormData();
    formData.append('file', file);
    if (folderId) {
      formData.append('folderId', folderId);
    }
    if (permission) {
      formData.append('permission', permission);
    }

    const response = await apiClient.post<ApiResponse<StoredFile>>(
      API_ENDPOINTS.FILES.UPLOAD(courseId),
      formData,
      {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
      },
    );
    return response.data.data;
  } catch (error) {
    throw parseApiError(error);
  }
};

/**
 * 파일 목록 조회
 */
export const getFiles = async (
  courseId: string,
  folderId?: string,
): Promise<StoredFile[]> => {
  try {
    const response = await apiClient.get<ApiResponse<{ files: StoredFile[] }>>(
      API_ENDPOINTS.FILES.LIST(courseId),
      {
        params: { folderId },
      },
    );
    return response.data.data.files;
  } catch (error) {
    throw parseApiError(error);
  }
};

/**
 * 파일 상세 조회
 */
export const getFile = async (fileId: string): Promise<StoredFile> => {
  try {
    const response = await apiClient.get<ApiResponse<StoredFile>>(
      API_ENDPOINTS.FILES.DETAIL(fileId),
    );
    return response.data.data;
  } catch (error) {
    throw parseApiError(error);
  }
};

/**
 * 파일 삭제
 */
export const deleteFile = async (fileId: string): Promise<void> => {
  try {
    await apiClient.delete(API_ENDPOINTS.FILES.DELETE(fileId));
  } catch (error) {
    throw parseApiError(error);
  }
};

/**
 * 파일 다운로드 URL 생성
 */
export const downloadFile = async (fileId: string): Promise<FileDownloadResponse> => {
  try {
    const response = await apiClient.get<ApiResponse<FileDownloadResponse>>(
      API_ENDPOINTS.FILES.DOWNLOAD(fileId),
    );
    return response.data.data;
  } catch (error) {
    throw parseApiError(error);
  }
};

/**
 * 파일 검색
 */
export const searchFiles = async (
  courseId: string,
  query: string,
): Promise<FileSearchResult[]> => {
  try {
    const response = await apiClient.get<ApiResponse<{ results: FileSearchResult[] }>>(
      API_ENDPOINTS.FILES.SEARCH(courseId),
      {
        params: { q: query },
      },
    );
    return response.data.data.results;
  } catch (error) {
    throw parseApiError(error);
  }
};

/**
 * 파일 권한 업데이트
 */
export const updateFilePermission = async (
  fileId: string,
  permission: FilePermissionType,
): Promise<StoredFile> => {
  try {
    const response = await apiClient.patch<ApiResponse<StoredFile>>(
      API_ENDPOINTS.FILES.UPDATE_PERMISSION(fileId),
      { permission },
    );
    return response.data.data;
  } catch (error) {
    throw parseApiError(error);
  }
};

/**
 * 폴더 생성
 */
export const createFolder = async (
  request: CreateFolderRequest,
): Promise<FileFolder> => {
  try {
    const response = await apiClient.post<ApiResponse<FileFolder>>(
      API_ENDPOINTS.FILES.CREATE_FOLDER(request.courseId),
      request,
    );
    return response.data.data;
  } catch (error) {
    throw parseApiError(error);
  }
};

/**
 * 폴더 트리 조회
 */
export const getFolderTree = async (courseId: string): Promise<FolderTreeNode[]> => {
  try {
    const response = await apiClient.get<ApiResponse<{ tree: FolderTreeNode[] }>>(
      API_ENDPOINTS.FILES.FOLDER_TREE(courseId),
    );
    return response.data.data.tree;
  } catch (error) {
    throw parseApiError(error);
  }
};

/**
 * 폴더 삭제
 */
export const deleteFolder = async (folderId: string): Promise<void> => {
  try {
    await apiClient.delete(API_ENDPOINTS.FILES.DELETE_FOLDER(folderId));
  } catch (error) {
    throw parseApiError(error);
  }
};
