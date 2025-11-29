'use client';

import { useState, useEffect } from 'react';
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogDescription,
} from '@/components/ui/dialog';
import { Button } from '@/components/ui/button';
import { Download, X, ZoomIn, ZoomOut, RotateCw } from 'lucide-react';
import { StoredFile } from '@/types/file';
import { cn } from '@/lib/utils';

interface FilePreviewModalProps {
  file: StoredFile | null;
  open: boolean;
  onClose: () => void;
  onDownload?: (file: StoredFile) => void;
}

function formatFileSize(bytes: number): string {
  if (bytes === 0) return '0 Bytes';
  const k = 1024;
  const sizes = ['Bytes', 'KB', 'MB', 'GB'];
  const i = Math.floor(Math.log(bytes) / Math.log(k));
  return Math.round((bytes / Math.pow(k, i)) * 100) / 100 + ' ' + sizes[i];
}

function getFileType(filename: string): string {
  const ext = filename.split('.').pop()?.toLowerCase() || '';

  if (['jpg', 'jpeg', 'png', 'gif', 'webp', 'svg'].includes(ext)) {
    return 'image';
  }
  if (['pdf'].includes(ext)) {
    return 'pdf';
  }
  if (['mp4', 'mov', 'avi', 'webm'].includes(ext)) {
    return 'video';
  }
  if (['mp3', 'wav', 'ogg'].includes(ext)) {
    return 'audio';
  }
  if (['txt', 'md', 'log'].includes(ext)) {
    return 'text';
  }
  if (['json', 'xml', 'html', 'css', 'js', 'ts', 'tsx', 'jsx', 'py', 'java'].includes(ext)) {
    return 'code';
  }
  return 'unknown';
}

export function FilePreviewModal({
  file,
  open,
  onClose,
  onDownload,
}: FilePreviewModalProps) {
  const [zoom, setZoom] = useState(100);
  const [rotation, setRotation] = useState(0);

  useEffect(() => {
    if (!open) {
      setZoom(100);
      setRotation(0);
    }
  }, [open]);

  if (!file) return null;

  const fileType = getFileType(file.originalName);
  const previewUrl = `/api/files/${file.id}/preview`;

  const handleZoomIn = () => {
    setZoom((prev) => Math.min(prev + 25, 200));
  };

  const handleZoomOut = () => {
    setZoom((prev) => Math.max(prev - 25, 25));
  };

  const handleRotate = () => {
    setRotation((prev) => (prev + 90) % 360);
  };

  const handleDownload = () => {
    onDownload?.(file);
  };

  const renderPreview = () => {
    switch (fileType) {
      case 'image':
        return (
          <div className="flex items-center justify-center overflow-auto p-4">
            <img
              src={previewUrl}
              alt={file.originalName}
              className="max-h-full max-w-full object-contain"
              style={{
                transform: `scale(${zoom / 100}) rotate(${rotation}deg)`,
                transition: 'transform 0.2s ease-in-out',
              }}
            />
          </div>
        );

      case 'pdf':
        return (
          <iframe
            src={previewUrl}
            className="h-full w-full"
            title={file.originalName}
          />
        );

      case 'video':
        return (
          <div className="flex items-center justify-center p-4">
            <video
              src={previewUrl}
              controls
              className="max-h-full max-w-full"
            >
              브라우저가 비디오 재생을 지원하지 않습니다.
            </video>
          </div>
        );

      case 'audio':
        return (
          <div className="flex items-center justify-center p-8">
            <audio src={previewUrl} controls className="w-full max-w-md">
              브라우저가 오디오 재생을 지원하지 않습니다.
            </audio>
          </div>
        );

      case 'text':
      case 'code':
        return (
          <iframe
            src={previewUrl}
            className="h-full w-full"
            title={file.originalName}
          />
        );

      default:
        return (
          <div className="flex h-full items-center justify-center p-8">
            <div className="text-center">
              <p className="mb-4 text-muted-foreground">
                이 파일 형식은 미리보기를 지원하지 않습니다.
              </p>
              <Button onClick={handleDownload}>
                <Download className="mr-2 h-4 w-4" />
                다운로드
              </Button>
            </div>
          </div>
        );
    }
  };

  const showImageControls = fileType === 'image';

  return (
    <Dialog open={open} onOpenChange={onClose}>
      <DialogContent className="max-w-4xl h-[80vh] flex flex-col">
        <DialogHeader>
          <DialogTitle className="flex items-center justify-between">
            <span className="truncate">{file.originalName}</span>
            <div className="flex items-center gap-2">
              {showImageControls && (
                <>
                  <Button
                    variant="outline"
                    size="sm"
                    onClick={handleZoomOut}
                    disabled={zoom <= 25}
                  >
                    <ZoomOut className="h-4 w-4" />
                  </Button>
                  <span className="text-sm text-muted-foreground">
                    {zoom}%
                  </span>
                  <Button
                    variant="outline"
                    size="sm"
                    onClick={handleZoomIn}
                    disabled={zoom >= 200}
                  >
                    <ZoomIn className="h-4 w-4" />
                  </Button>
                  <Button
                    variant="outline"
                    size="sm"
                    onClick={handleRotate}
                  >
                    <RotateCw className="h-4 w-4" />
                  </Button>
                </>
              )}
              {onDownload && (
                <Button
                  variant="outline"
                  size="sm"
                  onClick={handleDownload}
                >
                  <Download className="mr-2 h-4 w-4" />
                  다운로드
                </Button>
              )}
            </div>
          </DialogTitle>
          <DialogDescription className="flex items-center gap-4 text-sm">
            <span>{formatFileSize(file.size)}</span>
            {file.uploader && (
              <>
                <span>•</span>
                <span>업로더: {file.uploader.name}</span>
              </>
            )}
            <span>•</span>
            <span>다운로드: {file.downloadCount}회</span>
          </DialogDescription>
        </DialogHeader>

        <div className="flex-1 overflow-hidden rounded-lg border bg-muted">
          {renderPreview()}
        </div>
      </DialogContent>
    </Dialog>
  );
}
