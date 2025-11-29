/**
 * 코스 파일 관리 페이지
 * 파일 업로드, 목록, 폴더 트리 관리
 */

'use client';

import { use, useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { ArrowLeft, Upload, Grid, List, Search } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { LoadingSpinner } from '@/components/common/LoadingSpinner';
import { FileUploadDropzone } from '@/components/file/file-upload-dropzone';
import { FileList } from '@/components/file/file-list';
import { FileCard } from '@/components/file/file-card';
import { FolderTree } from '@/components/file/folder-tree';
import { FilePreviewModal } from '@/components/file/file-preview-modal';
import { FilePermissionModal } from '@/components/file/file-permission-modal';
import {
  getFiles,
  getFolderTree,
  uploadFile,
  deleteFile,
  downloadFile,
  createFolder,
  deleteFolder,
  updateFilePermission,
} from '@/lib/api/files';
import { StoredFile, FilePermissionType } from '@/types/file';
import { useToast } from '@/hooks/use-toast';
import Link from 'next/link';

interface CourseFilesPageProps {
  params: Promise<{
    courseId: string;
  }>;
}

type ViewMode = 'list' | 'grid';

export default function CourseFilesPage({ params }: CourseFilesPageProps) {
  const { courseId } = use(params);
  const queryClient = useQueryClient();
  const { toast } = useToast();

  const [viewMode, setViewMode] = useState<ViewMode>('list');
  const [selectedFolderId, setSelectedFolderId] = useState<string | undefined>(undefined);
  const [searchQuery, setSearchQuery] = useState('');
  const [previewFile, setPreviewFile] = useState<StoredFile | null>(null);
  const [permissionFile, setPermissionFile] = useState<StoredFile | null>(null);
  const [selectedFileIds, setSelectedFileIds] = useState<string[]>([]);

  // 파일 목록 조회
  const { data: files, isLoading: filesLoading } = useQuery({
    queryKey: ['files', courseId, selectedFolderId],
    queryFn: () => getFiles(courseId, selectedFolderId),
  });

  // 폴더 트리 조회
  const { data: folderTree, isLoading: foldersLoading } = useQuery({
    queryKey: ['folderTree', courseId],
    queryFn: () => getFolderTree(courseId),
  });

  // 파일 업로드 mutation
  const uploadMutation = useMutation({
    mutationFn: (file: File) => uploadFile(courseId, file, selectedFolderId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['files', courseId] });
      queryClient.invalidateQueries({ queryKey: ['folderTree', courseId] });
      toast({
        title: '업로드 완료',
        description: '파일이 성공적으로 업로드되었습니다.',
      });
    },
    onError: (error) => {
      toast({
        title: '업로드 실패',
        description: error instanceof Error ? error.message : '파일 업로드 중 오류가 발생했습니다.',
        variant: 'destructive',
      });
    },
  });

  // 파일 삭제 mutation
  const deleteMutation = useMutation({
    mutationFn: deleteFile,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['files', courseId] });
      queryClient.invalidateQueries({ queryKey: ['folderTree', courseId] });
      toast({
        title: '삭제 완료',
        description: '파일이 삭제되었습니다.',
      });
    },
    onError: (error) => {
      toast({
        title: '삭제 실패',
        description: error instanceof Error ? error.message : '파일 삭제 중 오류가 발생했습니다.',
        variant: 'destructive',
      });
    },
  });

  // 폴더 생성 mutation
  const createFolderMutation = useMutation({
    mutationFn: ({ parentId, name }: { parentId?: string; name: string }) =>
      createFolder({ courseId, parentId, name }),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['folderTree', courseId] });
      toast({
        title: '폴더 생성 완료',
        description: '새 폴더가 생성되었습니다.',
      });
    },
    onError: (error) => {
      toast({
        title: '폴더 생성 실패',
        description: error instanceof Error ? error.message : '폴더 생성 중 오류가 발생했습니다.',
        variant: 'destructive',
      });
    },
  });

  // 폴더 삭제 mutation
  const deleteFolderMutation = useMutation({
    mutationFn: deleteFolder,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['folderTree', courseId] });
      toast({
        title: '폴더 삭제 완료',
        description: '폴더가 삭제되었습니다.',
      });
    },
    onError: (error) => {
      toast({
        title: '폴더 삭제 실패',
        description: error instanceof Error ? error.message : '폴더 삭제 중 오류가 발생했습니다.',
        variant: 'destructive',
      });
    },
  });

  // 권한 업데이트 mutation
  const permissionMutation = useMutation({
    mutationFn: ({ fileId, permission }: { fileId: string; permission: FilePermissionType }) =>
      updateFilePermission(fileId, permission),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['files', courseId] });
      toast({
        title: '권한 업데이트 완료',
        description: '파일 권한이 변경되었습니다.',
      });
    },
    onError: (error) => {
      toast({
        title: '권한 업데이트 실패',
        description: error instanceof Error ? error.message : '권한 업데이트 중 오류가 발생했습니다.',
        variant: 'destructive',
      });
    },
  });

  // 핸들러들
  const handleUpload = async (uploadFiles: File[]) => {
    for (const file of uploadFiles) {
      await uploadMutation.mutateAsync(file);
    }
  };

  const handleDownload = async (file: StoredFile) => {
    try {
      const downloadResponse = await downloadFile(file.id);
      window.open(downloadResponse.url, '_blank');
    } catch (error) {
      toast({
        title: '다운로드 실패',
        description: error instanceof Error ? error.message : '파일 다운로드 중 오류가 발생했습니다.',
        variant: 'destructive',
      });
    }
  };

  const handleDelete = async (file: StoredFile) => {
    if (confirm(`"${file.originalName}" 파일을 삭제하시겠습니까?`)) {
      await deleteMutation.mutateAsync(file.id);
    }
  };

  const handlePermission = (file: StoredFile) => {
    setPermissionFile(file);
  };

  const handlePermissionSave = async (file: StoredFile, permission: FilePermissionType) => {
    await permissionMutation.mutateAsync({ fileId: file.id, permission });
  };

  const handlePreview = (file: StoredFile) => {
    setPreviewFile(file);
  };

  const handleFolderCreate = async (parentId: string | undefined, name: string) => {
    await createFolderMutation.mutateAsync({ parentId, name });
  };

  const handleFolderDelete = async (folderId: string) => {
    await deleteFolderMutation.mutateAsync(folderId);
  };

  // 검색 필터링
  const filteredFiles = files?.filter((file) =>
    file.originalName.toLowerCase().includes(searchQuery.toLowerCase()),
  );

  if (filesLoading || foldersLoading) {
    return (
      <div className="flex min-h-[400px] items-center justify-center">
        <LoadingSpinner />
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* 헤더 */}
      <div>
        <Button variant="ghost" size="sm" asChild className="mb-4">
          <Link href={`/courses/${courseId}`}>
            <ArrowLeft className="mr-2 h-4 w-4" />
            코스로 돌아가기
          </Link>
        </Button>
        <div className="flex items-start justify-between">
          <div>
            <h1 className="text-3xl font-bold">파일 관리</h1>
            <p className="mt-1 text-muted-foreground">
              코스 자료를 업로드하고 관리합니다
            </p>
          </div>
        </div>
      </div>

      <div className="grid grid-cols-1 gap-6 lg:grid-cols-4">
        {/* 좌측: 폴더 트리 */}
        <div className="lg:col-span-1">
          <Card>
            <CardHeader>
              <CardTitle className="text-base">폴더</CardTitle>
            </CardHeader>
            <CardContent>
              <FolderTree
                folders={folderTree || []}
                selectedFolderId={selectedFolderId}
                onFolderSelect={setSelectedFolderId}
                onFolderCreate={handleFolderCreate}
                onFolderDelete={handleFolderDelete}
              />
            </CardContent>
          </Card>
        </div>

        {/* 우측: 파일 업로드 및 목록 */}
        <div className="space-y-6 lg:col-span-3">
          {/* 파일 업로드 */}
          <Card>
            <CardHeader>
              <CardTitle className="flex items-center gap-2">
                <Upload className="h-5 w-5" />
                파일 업로드
              </CardTitle>
              <CardDescription>
                파일을 드래그하거나 클릭하여 업로드하세요
              </CardDescription>
            </CardHeader>
            <CardContent>
              <FileUploadDropzone onUpload={handleUpload} />
            </CardContent>
          </Card>

          {/* 파일 목록 */}
          <Card>
            <CardHeader>
              <div className="flex items-center justify-between">
                <CardTitle>파일 목록</CardTitle>
                <div className="flex items-center gap-2">
                  {/* 검색 */}
                  <div className="relative">
                    <Search className="absolute left-2.5 top-2.5 h-4 w-4 text-muted-foreground" />
                    <Input
                      type="search"
                      placeholder="파일 검색..."
                      className="w-[200px] pl-8"
                      value={searchQuery}
                      onChange={(e) => setSearchQuery(e.target.value)}
                    />
                  </div>

                  {/* 뷰 모드 전환 */}
                  <div className="flex items-center rounded-md border">
                    <Button
                      variant={viewMode === 'list' ? 'secondary' : 'ghost'}
                      size="sm"
                      onClick={() => setViewMode('list')}
                      className="rounded-r-none"
                    >
                      <List className="h-4 w-4" />
                    </Button>
                    <Button
                      variant={viewMode === 'grid' ? 'secondary' : 'ghost'}
                      size="sm"
                      onClick={() => setViewMode('grid')}
                      className="rounded-l-none"
                    >
                      <Grid className="h-4 w-4" />
                    </Button>
                  </div>
                </div>
              </div>
            </CardHeader>
            <CardContent>
              {viewMode === 'list' ? (
                <FileList
                  files={filteredFiles || []}
                  onDownload={handleDownload}
                  onDelete={handleDelete}
                  onPermission={handlePermission}
                  onPreview={handlePreview}
                  selectable
                  onSelectionChange={setSelectedFileIds}
                />
              ) : (
                <div className="grid grid-cols-1 gap-4 md:grid-cols-2 xl:grid-cols-3">
                  {filteredFiles?.map((file) => (
                    <FileCard
                      key={file.id}
                      file={file}
                      onDownload={handleDownload}
                      onDelete={handleDelete}
                      onPermission={handlePermission}
                      onPreview={handlePreview}
                      selectable
                      selected={selectedFileIds.includes(file.id)}
                      onSelect={(checked) => {
                        setSelectedFileIds((prev) =>
                          checked
                            ? [...prev, file.id]
                            : prev.filter((id) => id !== file.id),
                        );
                      }}
                    />
                  ))}
                </div>
              )}
            </CardContent>
          </Card>
        </div>
      </div>

      {/* 파일 미리보기 모달 */}
      <FilePreviewModal
        file={previewFile}
        open={!!previewFile}
        onClose={() => setPreviewFile(null)}
        onDownload={handleDownload}
      />

      {/* 파일 권한 설정 모달 */}
      <FilePermissionModal
        file={permissionFile}
        open={!!permissionFile}
        onClose={() => setPermissionFile(null)}
        onSave={handlePermissionSave}
      />
    </div>
  );
}
