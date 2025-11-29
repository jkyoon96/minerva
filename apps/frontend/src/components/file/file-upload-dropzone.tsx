'use client';

import { useState, useRef, useCallback } from 'react';
import { Button } from '@/components/ui/button';
import { Progress } from '@/components/ui/progress';
import { Alert } from '@/components/ui/alert';
import { Upload, X, File, FileText, Image, Video, Music, Archive, Loader2 } from 'lucide-react';
import { cn } from '@/lib/utils';
import { FileUploadProgress } from '@/types/file';

interface FileUploadDropzoneProps {
  onUpload: (files: File[]) => Promise<void>;
  maxFileSize?: number; // 바이트 단위
  maxFiles?: number;
  acceptedTypes?: string[];
  className?: string;
}

const MAX_FILE_SIZE = 100 * 1024 * 1024; // 100MB
const MAX_FILES = 10;

const FILE_ICONS: Record<string, typeof FileText> = {
  'application/pdf': FileText,
  'application/msword': FileText,
  'application/vnd.openxmlformats-officedocument.wordprocessingml.document': FileText,
  'application/vnd.ms-powerpoint': FileText,
  'application/vnd.openxmlformats-officedocument.presentationml.presentation': FileText,
  'application/vnd.ms-excel': FileText,
  'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet': FileText,
  'image/': Image,
  'video/': Video,
  'audio/': Music,
  'application/zip': Archive,
  'application/x-zip-compressed': Archive,
  'application/x-rar-compressed': Archive,
  'application/x-7z-compressed': Archive,
};

function getFileIcon(mimeType: string) {
  const Icon = Object.entries(FILE_ICONS).find(([type]) => mimeType.startsWith(type))?.[1];
  return Icon || File;
}

function formatFileSize(bytes: number): string {
  if (bytes === 0) return '0 Bytes';
  const k = 1024;
  const sizes = ['Bytes', 'KB', 'MB', 'GB'];
  const i = Math.floor(Math.log(bytes) / Math.log(k));
  return Math.round((bytes / Math.pow(k, i)) * 100) / 100 + ' ' + sizes[i];
}

export function FileUploadDropzone({
  onUpload,
  maxFileSize = MAX_FILE_SIZE,
  maxFiles = MAX_FILES,
  acceptedTypes,
  className,
}: FileUploadDropzoneProps) {
  const [uploadProgress, setUploadProgress] = useState<FileUploadProgress[]>([]);
  const [isDragging, setIsDragging] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [isUploading, setIsUploading] = useState(false);
  const fileInputRef = useRef<HTMLInputElement>(null);

  const validateFiles = useCallback(
    (files: File[]): string | null => {
      if (files.length > maxFiles) {
        return `최대 ${maxFiles}개의 파일만 업로드할 수 있습니다.`;
      }

      for (const file of files) {
        if (file.size > maxFileSize) {
          return `파일 크기는 ${formatFileSize(maxFileSize)}를 초과할 수 없습니다. (${file.name})`;
        }

        if (acceptedTypes && !acceptedTypes.some((type) => file.type.match(type))) {
          return `지원하지 않는 파일 형식입니다. (${file.name})`;
        }
      }

      return null;
    },
    [maxFileSize, maxFiles, acceptedTypes],
  );

  const handleFiles = useCallback(
    async (files: File[]) => {
      setError(null);

      const validationError = validateFiles(files);
      if (validationError) {
        setError(validationError);
        return;
      }

      // 업로드 진행 상태 초기화
      const initialProgress: FileUploadProgress[] = files.map((file) => ({
        file,
        progress: 0,
        status: 'pending',
      }));
      setUploadProgress(initialProgress);
      setIsUploading(true);

      try {
        // 업로드 시뮬레이션 (실제로는 onUpload에서 진행률 콜백 받아야 함)
        for (let i = 0; i < files.length; i++) {
          setUploadProgress((prev) =>
            prev.map((p, idx) =>
              idx === i ? { ...p, status: 'uploading' as const } : p,
            ),
          );

          // 진행률 업데이트 시뮬레이션
          for (let progress = 0; progress <= 100; progress += 20) {
            await new Promise((resolve) => setTimeout(resolve, 100));
            setUploadProgress((prev) =>
              prev.map((p, idx) =>
                idx === i ? { ...p, progress } : p,
              ),
            );
          }

          setUploadProgress((prev) =>
            prev.map((p, idx) =>
              idx === i ? { ...p, status: 'success' as const, progress: 100 } : p,
            ),
          );
        }

        await onUpload(files);

        // 성공 후 초기화
        setTimeout(() => {
          setUploadProgress([]);
        }, 2000);
      } catch (err) {
        setError(err instanceof Error ? err.message : '업로드 중 오류가 발생했습니다.');
        setUploadProgress((prev) =>
          prev.map((p) => ({ ...p, status: 'error' as const })),
        );
      } finally {
        setIsUploading(false);
      }

      // 입력 초기화
      if (fileInputRef.current) {
        fileInputRef.current.value = '';
      }
    },
    [onUpload, validateFiles],
  );

  const handleFileChange = useCallback(
    async (e: React.ChangeEvent<HTMLInputElement>) => {
      const files = Array.from(e.target.files || []);
      if (files.length === 0) return;
      await handleFiles(files);
    },
    [handleFiles],
  );

  const handleDragOver = useCallback((e: React.DragEvent) => {
    e.preventDefault();
    setIsDragging(true);
  }, []);

  const handleDragLeave = useCallback((e: React.DragEvent) => {
    e.preventDefault();
    setIsDragging(false);
  }, []);

  const handleDrop = useCallback(
    async (e: React.DragEvent) => {
      e.preventDefault();
      setIsDragging(false);

      const files = Array.from(e.dataTransfer.files);
      if (files.length === 0) return;
      await handleFiles(files);
    },
    [handleFiles],
  );

  const handleUploadClick = useCallback(() => {
    fileInputRef.current?.click();
  }, []);

  const handleRemoveFile = useCallback((index: number) => {
    setUploadProgress((prev) => prev.filter((_, i) => i !== index));
  }, []);

  return (
    <div className={cn('space-y-4', className)}>
      {/* 드래그 앤 드롭 영역 */}
      <div
        className={cn(
          'relative rounded-lg border-2 border-dashed p-8 text-center transition-colors',
          isDragging
            ? 'border-primary bg-primary/5'
            : 'border-muted-foreground/25 hover:border-muted-foreground/50',
          isUploading && 'pointer-events-none opacity-50',
        )}
        onDragOver={handleDragOver}
        onDragLeave={handleDragLeave}
        onDrop={handleDrop}
      >
        <div className="flex flex-col items-center space-y-4">
          <div className="rounded-full bg-muted p-4">
            <Upload className="h-8 w-8 text-muted-foreground" />
          </div>

          <div className="space-y-2">
            <p className="text-base font-medium">
              파일을 드래그하여 놓거나 클릭하여 선택하세요
            </p>
            <p className="text-sm text-muted-foreground">
              최대 {maxFiles}개 파일, 파일당 최대 {formatFileSize(maxFileSize)}
            </p>
          </div>

          <Button
            type="button"
            variant="outline"
            size="sm"
            onClick={handleUploadClick}
            disabled={isUploading}
          >
            파일 선택
          </Button>
        </div>

        {/* 파일 입력 (숨김) */}
        <input
          ref={fileInputRef}
          type="file"
          accept={acceptedTypes?.join(',')}
          onChange={handleFileChange}
          multiple={maxFiles > 1}
          className="hidden"
        />
      </div>

      {/* 업로드 진행 상태 */}
      {uploadProgress.length > 0 && (
        <div className="space-y-2">
          {uploadProgress.map((item, index) => {
            const Icon = getFileIcon(item.file.type);
            return (
              <div
                key={index}
                className="flex items-center gap-3 rounded-lg border bg-card p-3"
              >
                <div className="flex-shrink-0">
                  <Icon className="h-6 w-6 text-muted-foreground" />
                </div>

                <div className="flex-1 space-y-1">
                  <div className="flex items-center justify-between">
                    <p className="text-sm font-medium">{item.file.name}</p>
                    <p className="text-xs text-muted-foreground">
                      {formatFileSize(item.file.size)}
                    </p>
                  </div>

                  {item.status === 'uploading' && (
                    <Progress value={item.progress} className="h-1" />
                  )}

                  {item.status === 'success' && (
                    <p className="text-xs text-green-600">업로드 완료</p>
                  )}

                  {item.status === 'error' && (
                    <p className="text-xs text-destructive">업로드 실패</p>
                  )}
                </div>

                <div className="flex-shrink-0">
                  {item.status === 'uploading' && (
                    <Loader2 className="h-4 w-4 animate-spin text-muted-foreground" />
                  )}
                  {item.status !== 'uploading' && (
                    <Button
                      type="button"
                      variant="ghost"
                      size="sm"
                      onClick={() => handleRemoveFile(index)}
                      className="h-8 w-8 p-0"
                    >
                      <X className="h-4 w-4" />
                    </Button>
                  )}
                </div>
              </div>
            );
          })}
        </div>
      )}

      {/* 에러 메시지 */}
      {error && (
        <Alert variant="destructive">
          <p className="text-sm">{error}</p>
        </Alert>
      )}
    </div>
  );
}
