'use client';

import { useState } from 'react';
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table';
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
  Image,
  Video,
  Music,
  Archive,
  Download,
  Trash2,
  MoreVertical,
  Lock,
  Eye,
  ArrowUpDown,
  ArrowUp,
  ArrowDown,
} from 'lucide-react';
import { StoredFile, FileSortBy, FileSortOrder } from '@/types/file';
import { cn } from '@/lib/utils';

interface FileListProps {
  files: StoredFile[];
  onDownload?: (file: StoredFile) => void;
  onDelete?: (file: StoredFile) => void;
  onPermission?: (file: StoredFile) => void;
  onPreview?: (file: StoredFile) => void;
  selectable?: boolean;
  onSelectionChange?: (selectedIds: string[]) => void;
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
  jpg: Image,
  jpeg: Image,
  png: Image,
  gif: Image,
  webp: Image,
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

export function FileList({
  files,
  onDownload,
  onDelete,
  onPermission,
  onPreview,
  selectable = false,
  onSelectionChange,
  className,
}: FileListProps) {
  const [selectedIds, setSelectedIds] = useState<string[]>([]);
  const [sortBy, setSortBy] = useState<FileSortBy>('uploadedAt');
  const [sortOrder, setSortOrder] = useState<FileSortOrder>('desc');

  const handleSelectAll = (checked: boolean) => {
    const newSelectedIds = checked ? files.map((f) => f.id) : [];
    setSelectedIds(newSelectedIds);
    onSelectionChange?.(newSelectedIds);
  };

  const handleSelect = (fileId: string, checked: boolean) => {
    const newSelectedIds = checked
      ? [...selectedIds, fileId]
      : selectedIds.filter((id) => id !== fileId);
    setSelectedIds(newSelectedIds);
    onSelectionChange?.(newSelectedIds);
  };

  const handleSort = (field: FileSortBy) => {
    if (sortBy === field) {
      setSortOrder(sortOrder === 'asc' ? 'desc' : 'asc');
    } else {
      setSortBy(field);
      setSortOrder('asc');
    }
  };

  const sortedFiles = [...files].sort((a, b) => {
    let comparison = 0;

    switch (sortBy) {
      case 'name':
        comparison = a.originalName.localeCompare(b.originalName);
        break;
      case 'size':
        comparison = a.size - b.size;
        break;
      case 'uploadedAt':
        comparison = new Date(a.createdAt).getTime() - new Date(b.createdAt).getTime();
        break;
      case 'downloads':
        comparison = a.downloadCount - b.downloadCount;
        break;
    }

    return sortOrder === 'asc' ? comparison : -comparison;
  });

  const SortIcon = ({ field }: { field: FileSortBy }) => {
    if (sortBy !== field) {
      return <ArrowUpDown className="ml-2 h-4 w-4" />;
    }
    return sortOrder === 'asc' ? (
      <ArrowUp className="ml-2 h-4 w-4" />
    ) : (
      <ArrowDown className="ml-2 h-4 w-4" />
    );
  };

  const isAllSelected = files.length > 0 && selectedIds.length === files.length;
  const isSomeSelected = selectedIds.length > 0 && selectedIds.length < files.length;

  if (files.length === 0) {
    return (
      <div className="flex flex-col items-center justify-center rounded-lg border border-dashed p-12 text-center">
        <File className="mb-4 h-12 w-12 text-muted-foreground" />
        <h3 className="mb-1 text-lg font-semibold">파일이 없습니다</h3>
        <p className="text-sm text-muted-foreground">
          파일을 업로드하여 시작하세요
        </p>
      </div>
    );
  }

  return (
    <div className={cn('rounded-md border', className)}>
      <Table>
        <TableHeader>
          <TableRow>
            {selectable && (
              <TableHead className="w-[50px]">
                <Checkbox
                  checked={isAllSelected}
                  onCheckedChange={handleSelectAll}
                  aria-label="모두 선택"
                  className={cn(isSomeSelected && 'data-[state=checked]:bg-muted')}
                />
              </TableHead>
            )}
            <TableHead>
              <Button
                variant="ghost"
                size="sm"
                onClick={() => handleSort('name')}
                className="-ml-3 h-8 data-[state=on]:bg-accent"
              >
                파일명
                <SortIcon field="name" />
              </Button>
            </TableHead>
            <TableHead>
              <Button
                variant="ghost"
                size="sm"
                onClick={() => handleSort('size')}
                className="-ml-3 h-8 data-[state=on]:bg-accent"
              >
                크기
                <SortIcon field="size" />
              </Button>
            </TableHead>
            <TableHead>
              <Button
                variant="ghost"
                size="sm"
                onClick={() => handleSort('uploadedAt')}
                className="-ml-3 h-8 data-[state=on]:bg-accent"
              >
                업로드 날짜
                <SortIcon field="uploadedAt" />
              </Button>
            </TableHead>
            <TableHead>업로더</TableHead>
            <TableHead>
              <Button
                variant="ghost"
                size="sm"
                onClick={() => handleSort('downloads')}
                className="-ml-3 h-8 data-[state=on]:bg-accent"
              >
                다운로드
                <SortIcon field="downloads" />
              </Button>
            </TableHead>
            <TableHead className="w-[70px]"></TableHead>
          </TableRow>
        </TableHeader>
        <TableBody>
          {sortedFiles.map((file) => {
            const Icon = getFileIcon(file.originalName);
            const isSelected = selectedIds.includes(file.id);

            return (
              <TableRow key={file.id} className={cn(isSelected && 'bg-muted/50')}>
                {selectable && (
                  <TableCell>
                    <Checkbox
                      checked={isSelected}
                      onCheckedChange={(checked) =>
                        handleSelect(file.id, checked as boolean)
                      }
                      aria-label={`${file.originalName} 선택`}
                    />
                  </TableCell>
                )}
                <TableCell>
                  <div className="flex items-center gap-3">
                    <Icon className="h-5 w-5 flex-shrink-0 text-muted-foreground" />
                    <div className="min-w-0 flex-1">
                      <p className="truncate font-medium">{file.originalName}</p>
                      {file.permission !== 'public' && (
                        <div className="flex items-center gap-1 text-xs text-muted-foreground">
                          <Lock className="h-3 w-3" />
                          <span>제한됨</span>
                        </div>
                      )}
                    </div>
                  </div>
                </TableCell>
                <TableCell className="text-sm text-muted-foreground">
                  {formatFileSize(file.size)}
                </TableCell>
                <TableCell className="text-sm text-muted-foreground">
                  {formatDate(file.createdAt)}
                </TableCell>
                <TableCell className="text-sm">
                  {file.uploader?.name || '알 수 없음'}
                </TableCell>
                <TableCell className="text-sm text-muted-foreground">
                  {file.downloadCount}회
                </TableCell>
                <TableCell>
                  <DropdownMenu>
                    <DropdownMenuTrigger asChild>
                      <Button
                        variant="ghost"
                        size="sm"
                        className="h-8 w-8 p-0"
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
                </TableCell>
              </TableRow>
            );
          })}
        </TableBody>
      </Table>
    </div>
  );
}
