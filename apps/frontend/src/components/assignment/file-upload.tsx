/**
 * 파일 업로드 컴포넌트
 * 과제 제출 시 파일 첨부
 */

'use client';

import * as React from 'react';
import { useState, useRef } from 'react';
import { Upload, File, X } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { cn } from '@/lib/utils';

interface FileUploadProps {
  onFilesChange: (files: File[]) => void;
  maxFiles?: number;
  maxSize?: number; // MB
  accept?: string;
  disabled?: boolean;
}

export function FileUpload({
  onFilesChange,
  maxFiles = 5,
  maxSize = 10,
  accept = '*/*',
  disabled = false,
}: FileUploadProps) {
  const [files, setFiles] = useState<File[]>([]);
  const fileInputRef = useRef<HTMLInputElement>(null);

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const selectedFiles = Array.from(e.target.files || []);
    addFiles(selectedFiles);
  };

  const addFiles = (newFiles: File[]) => {
    // 파일 개수 확인
    if (files.length + newFiles.length > maxFiles) {
      alert(`최대 ${maxFiles}개의 파일만 업로드할 수 있습니다.`);
      return;
    }

    // 파일 크기 확인
    const oversizedFiles = newFiles.filter((file) => file.size > maxSize * 1024 * 1024);
    if (oversizedFiles.length > 0) {
      alert(`파일 크기는 ${maxSize}MB를 초과할 수 없습니다.`);
      return;
    }

    const updatedFiles = [...files, ...newFiles];
    setFiles(updatedFiles);
    onFilesChange(updatedFiles);
  };

  const removeFile = (index: number) => {
    const updatedFiles = files.filter((_, i) => i !== index);
    setFiles(updatedFiles);
    onFilesChange(updatedFiles);
  };

  const handleDrop = (e: React.DragEvent<HTMLDivElement>) => {
    e.preventDefault();
    if (disabled) return;
    const droppedFiles = Array.from(e.dataTransfer.files);
    addFiles(droppedFiles);
  };

  const handleDragOver = (e: React.DragEvent<HTMLDivElement>) => {
    e.preventDefault();
  };

  const formatFileSize = (bytes: number) => {
    if (bytes < 1024) return bytes + ' B';
    if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(2) + ' KB';
    return (bytes / (1024 * 1024)).toFixed(2) + ' MB';
  };

  return (
    <div className="space-y-4">
      {/* 드래그 앤 드롭 영역 */}
      <div
        className={cn(
          'flex flex-col items-center justify-center rounded-lg border-2 border-dashed p-8 text-center transition-colors',
          disabled
            ? 'cursor-not-allowed opacity-50'
            : 'cursor-pointer hover:border-muted-foreground/50',
          'border-muted-foreground/25',
        )}
        onDrop={handleDrop}
        onDragOver={handleDragOver}
        onClick={() => !disabled && fileInputRef.current?.click()}
      >
        <Upload className="h-12 w-12 text-muted-foreground" />
        <p className="mt-4 text-sm font-medium">파일을 드래그하거나 클릭하여 선택</p>
        <p className="mt-1 text-sm text-muted-foreground">
          최대 {maxFiles}개, 파일당 {maxSize}MB까지
        </p>
        <input
          ref={fileInputRef}
          type="file"
          accept={accept}
          multiple
          onChange={handleFileChange}
          className="hidden"
          disabled={disabled}
        />
      </div>

      {/* 선택된 파일 목록 */}
      {files.length > 0 && (
        <div className="space-y-2">
          <p className="text-sm font-medium">선택된 파일 ({files.length})</p>
          {files.map((file, index) => (
            <div
              key={index}
              className="flex items-center justify-between rounded-lg border p-3"
            >
              <div className="flex items-center gap-3">
                <File className="h-5 w-5 text-muted-foreground" />
                <div>
                  <p className="text-sm font-medium">{file.name}</p>
                  <p className="text-xs text-muted-foreground">{formatFileSize(file.size)}</p>
                </div>
              </div>
              <Button
                variant="ghost"
                size="sm"
                onClick={() => removeFile(index)}
                disabled={disabled}
              >
                <X className="h-4 w-4" />
              </Button>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
