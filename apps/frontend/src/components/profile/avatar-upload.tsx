'use client';

import { useState, useRef, ChangeEvent } from 'react';
import { Avatar, AvatarImage, AvatarFallback } from '@/components/ui/avatar';
import { Button } from '@/components/ui/button';
import { Alert } from '@/components/ui/alert';
import { Upload, X, Loader2, User } from 'lucide-react';
import { cn } from '@/lib/utils';

interface AvatarUploadProps {
  currentAvatar?: string;
  userName: string;
  onUpload: (file: File) => Promise<void>;
  onDelete: () => Promise<void>;
  isUploading?: boolean;
  className?: string;
}

const MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
const ALLOWED_TYPES = ['image/jpeg', 'image/png', 'image/jpg'];

export function AvatarUpload({
  currentAvatar,
  userName,
  onUpload,
  onDelete,
  isUploading = false,
  className,
}: AvatarUploadProps) {
  const [preview, setPreview] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [isDragging, setIsDragging] = useState(false);
  const fileInputRef = useRef<HTMLInputElement>(null);

  const validateFile = (file: File): string | null => {
    if (!ALLOWED_TYPES.includes(file.type)) {
      return 'JPG, PNG 파일만 업로드할 수 있습니다.';
    }

    if (file.size > MAX_FILE_SIZE) {
      return '파일 크기는 5MB를 초과할 수 없습니다.';
    }

    return null;
  };

  const handleFileChange = async (e: ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (!file) return;

    setError(null);

    const validationError = validateFile(file);
    if (validationError) {
      setError(validationError);
      return;
    }

    // 미리보기 생성
    const reader = new FileReader();
    reader.onloadend = () => {
      setPreview(reader.result as string);
    };
    reader.readAsDataURL(file);

    // 업로드
    try {
      await onUpload(file);
      setPreview(null);
    } catch (err) {
      setError(err instanceof Error ? err.message : '업로드 중 오류가 발생했습니다.');
      setPreview(null);
    }

    // 입력 초기화
    if (fileInputRef.current) {
      fileInputRef.current.value = '';
    }
  };

  const handleDragOver = (e: React.DragEvent) => {
    e.preventDefault();
    setIsDragging(true);
  };

  const handleDragLeave = (e: React.DragEvent) => {
    e.preventDefault();
    setIsDragging(false);
  };

  const handleDrop = async (e: React.DragEvent) => {
    e.preventDefault();
    setIsDragging(false);
    setError(null);

    const file = e.dataTransfer.files[0];
    if (!file) return;

    const validationError = validateFile(file);
    if (validationError) {
      setError(validationError);
      return;
    }

    // 미리보기 생성
    const reader = new FileReader();
    reader.onloadend = () => {
      setPreview(reader.result as string);
    };
    reader.readAsDataURL(file);

    // 업로드
    try {
      await onUpload(file);
      setPreview(null);
    } catch (err) {
      setError(err instanceof Error ? err.message : '업로드 중 오류가 발생했습니다.');
      setPreview(null);
    }
  };

  const handleDeleteClick = async () => {
    setError(null);
    setPreview(null);

    try {
      await onDelete();
    } catch (err) {
      setError(err instanceof Error ? err.message : '삭제 중 오류가 발생했습니다.');
    }
  };

  const handleUploadClick = () => {
    fileInputRef.current?.click();
  };

  const displayAvatar = preview || currentAvatar;
  const initials = userName
    .split(' ')
    .map((n) => n[0])
    .join('')
    .toUpperCase()
    .slice(0, 2);

  return (
    <div className={cn('space-y-4', className)}>
      <div className="flex flex-col items-center space-y-4">
        {/* 아바타 미리보기 */}
        <div
          className={cn(
            'relative rounded-full border-4 border-background shadow-lg transition-all',
            isDragging && 'ring-2 ring-primary ring-offset-2',
          )}
          onDragOver={handleDragOver}
          onDragLeave={handleDragLeave}
          onDrop={handleDrop}
        >
          <Avatar className="h-32 w-32">
            {displayAvatar ? (
              <AvatarImage src={displayAvatar} alt={userName} />
            ) : (
              <AvatarFallback className="bg-primary text-primary-foreground text-2xl">
                {initials || <User className="h-12 w-12" />}
              </AvatarFallback>
            )}
          </Avatar>

          {isUploading && (
            <div className="absolute inset-0 flex items-center justify-center rounded-full bg-black/50">
              <Loader2 className="h-8 w-8 animate-spin text-white" />
            </div>
          )}
        </div>

        {/* 업로드/삭제 버튼 */}
        <div className="flex gap-2">
          <Button
            type="button"
            variant="outline"
            size="sm"
            onClick={handleUploadClick}
            disabled={isUploading}
          >
            <Upload className="mr-2 h-4 w-4" />
            사진 업로드
          </Button>

          {currentAvatar && !isUploading && (
            <Button
              type="button"
              variant="outline"
              size="sm"
              onClick={handleDeleteClick}
            >
              <X className="mr-2 h-4 w-4" />
              삭제
            </Button>
          )}
        </div>

        {/* 파일 입력 (숨김) */}
        <input
          ref={fileInputRef}
          type="file"
          accept={ALLOWED_TYPES.join(',')}
          onChange={handleFileChange}
          className="hidden"
        />

        {/* 안내 텍스트 */}
        <p className="text-center text-xs text-muted-foreground">
          JPG, PNG 파일 (최대 5MB)
          <br />
          드래그 앤 드롭으로 업로드할 수 있습니다
        </p>
      </div>

      {/* 에러 메시지 */}
      {error && (
        <Alert variant="destructive">
          <p className="text-sm">{error}</p>
        </Alert>
      )}
    </div>
  );
}
