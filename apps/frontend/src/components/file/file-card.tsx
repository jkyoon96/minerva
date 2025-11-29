'use client';

import { useState } from 'react';
import { Card, CardContent } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu';
import { Checkbox } from '@/components/ui/checkbox';
import {
  FileText,
  File,
  Image as ImageIcon,
  Video,
  Music,
  Archive,
  Download,
  Trash2,
  MoreVertical,
  Lock,
  Eye,
} from 'lucide-react';
import { StoredFile } from '@/types/file';
import { cn } from '@/lib/utils';

interface FileCardProps {
  file: StoredFile;
  onDownload?: (file: StoredFile) => void;
  onDelete?: (file: StoredFile) => void;
  onPermission?: (file: StoredFile) => void;
  onPreview?: (file: StoredFile) => void;
  selectable?: boolean;
  selected?: boolean;
  onSelect?: (checked: boolean) => void;
  className?: string;
}

const FILE_ICONS: Record<string, typeof FileText> = {
  pdf: FileText,
  doc: FileText,
  docx: FileText,
  ppt: FileText,
  pptx: FileText,
  xls: FileText,
  xlsx: FileText,
  jpg: ImageIcon,
  jpeg: ImageIcon,
  png: ImageIcon,
  gif: ImageIcon,
  webp: ImageIcon,
  mp4: Video,
  mov: Video,
  avi: Video,
  mp3: Music,
  wav: Music,
  zip: Archive,
  rar: Archive,
  '7z': Archive,
};

function getFileIcon(filename: string) {
  const ext = filename.split('.').pop()?.toLowerCase() || '';
  return FILE_ICONS[ext] || File;
}

function formatFileSize(bytes: number): string {
  if (bytes === 0) return '0 Bytes';
  const k = 1024;
  const sizes = ['Bytes', 'KB', 'MB', 'GB'];
  const i = Math.floor(Math.log(bytes) / Math.log(k));
  return Math.round((bytes / Math.pow(k, i)) * 100) / 100 + ' ' + sizes[i];
}

function formatDate(dateString: string): string {
  const date = new Date(dateString);
  const now = new Date();
  const diff = now.getTime() - date.getTime();
  const days = Math.floor(diff / (1000 * 60 * 60 * 24));

  if (days === 0) {
    return '오늘';
  } else if (days === 1) {
    return '어제';
  } else if (days < 7) {
    return `${days}일 전`;
  } else {
    return date.toLocaleDateString('ko-KR');
  }
}

function isImageFile(filename: string): boolean {
  const ext = filename.split('.').pop()?.toLowerCase() || '';
  return ['jpg', 'jpeg', 'png', 'gif', 'webp'].includes(ext);
}

export function FileCard({
  file,
  onDownload,
  onDelete,
  onPermission,
  onPreview,
  selectable = false,
  selected = false,
  onSelect,
  className,
}: FileCardProps) {
  const [imageError, setImageError] = useState(false);
  const Icon = getFileIcon(file.originalName);
  const showImage = isImageFile(file.originalName) && !imageError;

  const handleCardClick = () => {
    if (onPreview) {
      onPreview(file);
    }
  };

  return (
    <Card
      className={cn(
        'group relative overflow-hidden transition-all hover:shadow-md',
        selected && 'ring-2 ring-primary',
        onPreview && 'cursor-pointer',
        className,
      )}
      onClick={handleCardClick}
    >
      {/* 선택 체크박스 */}
      {selectable && (
        <div
          className="absolute left-3 top-3 z-10"
          onClick={(e) => e.stopPropagation()}
        >
          <Checkbox
            checked={selected}
            onCheckedChange={onSelect}
            aria-label={`${file.originalName} 선택`}
            className="bg-background"
          />
        </div>
      )}

      {/* 메뉴 버튼 */}
      <div
        className="absolute right-3 top-3 z-10"
        onClick={(e) => e.stopPropagation()}
      >
        <DropdownMenu>
          <DropdownMenuTrigger asChild>
            <Button
              variant="secondary"
              size="sm"
              className="h-8 w-8 p-0 opacity-0 transition-opacity group-hover:opacity-100"
            >
              <MoreVertical className="h-4 w-4" />
              <span className="sr-only">메뉴 열기</span>
            </Button>
          </DropdownMenuTrigger>
          <DropdownMenuContent align="end">
            {onPreview && (
              <DropdownMenuItem onClick={() => onPreview(file)}>
                <Eye className="mr-2 h-4 w-4" />
                미리보기
              </DropdownMenuItem>
            )}
            {onDownload && (
              <DropdownMenuItem onClick={() => onDownload(file)}>
                <Download className="mr-2 h-4 w-4" />
                다운로드
              </DropdownMenuItem>
            )}
            {onPermission && (
              <DropdownMenuItem onClick={() => onPermission(file)}>
                <Lock className="mr-2 h-4 w-4" />
                권한 설정
              </DropdownMenuItem>
            )}
            {onDelete && (
              <>
                <DropdownMenuSeparator />
                <DropdownMenuItem
                  onClick={() => onDelete(file)}
                  className="text-destructive focus:text-destructive"
                >
                  <Trash2 className="mr-2 h-4 w-4" />
                  삭제
                </DropdownMenuItem>
              </>
            )}
          </DropdownMenuContent>
        </DropdownMenu>
      </div>

      <CardContent className="p-0">
        {/* 파일 미리보기/아이콘 영역 */}
        <div className="flex h-40 items-center justify-center bg-muted">
          {showImage ? (
            <img
              src={`/api/files/${file.id}/thumbnail`}
              alt={file.originalName}
              className="h-full w-full object-cover"
              onError={() => setImageError(true)}
            />
          ) : (
            <Icon className="h-16 w-16 text-muted-foreground" />
          )}
        </div>

        {/* 파일 정보 */}
        <div className="space-y-2 p-4">
          <div className="flex items-start justify-between gap-2">
            <div className="min-w-0 flex-1">
              <h3
                className="truncate text-sm font-medium"
                title={file.originalName}
              >
                {file.originalName}
              </h3>
              {file.permission !== 'public' && (
                <div className="mt-1 flex items-center gap-1 text-xs text-muted-foreground">
                  <Lock className="h-3 w-3" />
                  <span>제한됨</span>
                </div>
              )}
            </div>
          </div>

          <div className="flex items-center justify-between text-xs text-muted-foreground">
            <span>{formatFileSize(file.size)}</span>
            <span>{formatDate(file.createdAt)}</span>
          </div>

          {file.uploader && (
            <div className="flex items-center gap-2 border-t pt-2">
              <div className="flex h-6 w-6 items-center justify-center rounded-full bg-primary text-xs text-primary-foreground">
                {file.uploader.name.charAt(0).toUpperCase()}
              </div>
              <span className="truncate text-xs text-muted-foreground">
                {file.uploader.name}
              </span>
            </div>
          )}

          {file.downloadCount > 0 && (
            <div className="flex items-center gap-1 text-xs text-muted-foreground">
              <Download className="h-3 w-3" />
              <span>{file.downloadCount}회 다운로드</span>
            </div>
          )}
        </div>
      </CardContent>
    </Card>
  );
}
