/**
 * CSV 파일 업로드 컴포넌트
 * 수강생 일괄 등록을 위한 CSV 파일 업로드
 */

'use client';

import * as React from 'react';
import { useState, useRef } from 'react';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { Upload, FileText, AlertCircle, CheckCircle } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Alert, AlertDescription, AlertTitle } from '@/components/ui/alert';
import { Badge } from '@/components/ui/badge';
import { useToast } from '@/components/ui/toast';
import { uploadEnrollmentCsv } from '@/lib/api/enrollments';

interface CsvUploadProps {
  courseId: string;
}

export function CsvUpload({ courseId }: CsvUploadProps) {
  const [file, setFile] = useState<File | null>(null);
  const fileInputRef = useRef<HTMLInputElement>(null);
  const { toast } = useToast();
  const queryClient = useQueryClient();

  const uploadMutation = useMutation({
    mutationFn: (file: File) => uploadEnrollmentCsv(courseId, file),
    onSuccess: (result) => {
      toast({
        title: 'CSV 업로드 완료',
        description: `${result.success}명 등록 성공, ${result.failed}명 실패`,
      });
      queryClient.invalidateQueries({ queryKey: ['enrollments', courseId] });
      setFile(null);
      if (fileInputRef.current) {
        fileInputRef.current.value = '';
      }
    },
    onError: (error: any) => {
      toast({
        title: 'CSV 업로드 실패',
        description: error.message || 'CSV 파일 업로드 중 오류가 발생했습니다.',
        variant: 'destructive',
      });
    },
  });

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const selectedFile = e.target.files?.[0];
    if (selectedFile) {
      if (selectedFile.type !== 'text/csv' && !selectedFile.name.endsWith('.csv')) {
        toast({
          title: '파일 형식 오류',
          description: 'CSV 파일만 업로드 가능합니다.',
          variant: 'destructive',
        });
        return;
      }
      setFile(selectedFile);
    }
  };

  const handleUpload = () => {
    if (!file) {
      toast({
        title: '파일 선택 필요',
        description: '업로드할 CSV 파일을 선택해주세요.',
        variant: 'destructive',
      });
      return;
    }
    uploadMutation.mutate(file);
  };

  const handleDrop = (e: React.DragEvent<HTMLDivElement>) => {
    e.preventDefault();
    const droppedFile = e.dataTransfer.files[0];
    if (droppedFile) {
      if (droppedFile.type !== 'text/csv' && !droppedFile.name.endsWith('.csv')) {
        toast({
          title: '파일 형식 오류',
          description: 'CSV 파일만 업로드 가능합니다.',
          variant: 'destructive',
        });
        return;
      }
      setFile(droppedFile);
    }
  };

  const handleDragOver = (e: React.DragEvent<HTMLDivElement>) => {
    e.preventDefault();
  };

  return (
    <div className="space-y-4">
      {/* CSV 형식 안내 */}
      <Alert>
        <AlertCircle className="h-4 w-4" />
        <AlertTitle>CSV 파일 형식</AlertTitle>
        <AlertDescription>
          <div className="mt-2 space-y-1 text-sm">
            <p>CSV 파일은 다음 형식을 따라야 합니다:</p>
            <code className="block rounded bg-muted p-2 font-mono">
              email,name,studentId
              <br />
              student1@example.com,홍길동,2024001
              <br />
              student2@example.com,김철수,2024002
            </code>
            <p className="mt-2 text-muted-foreground">
              첫 번째 줄은 헤더이며, 각 행은 학생 정보를 나타냅니다.
            </p>
          </div>
        </AlertDescription>
      </Alert>

      {/* 파일 업로드 영역 */}
      <div
        className="flex flex-col items-center justify-center rounded-lg border-2 border-dashed border-muted-foreground/25 p-8 text-center transition-colors hover:border-muted-foreground/50"
        onDrop={handleDrop}
        onDragOver={handleDragOver}
      >
        <Upload className="h-12 w-12 text-muted-foreground" />
        <p className="mt-4 text-sm font-medium">CSV 파일을 드래그하거나 클릭하여 선택</p>
        <p className="mt-1 text-sm text-muted-foreground">최대 파일 크기: 10MB</p>
        <input
          ref={fileInputRef}
          type="file"
          accept=".csv"
          onChange={handleFileChange}
          className="hidden"
          id="csv-upload"
        />
        <Button
          variant="outline"
          className="mt-4"
          onClick={() => fileInputRef.current?.click()}
          disabled={uploadMutation.isPending}
        >
          파일 선택
        </Button>
      </div>

      {/* 선택된 파일 정보 */}
      {file && (
        <div className="flex items-center justify-between rounded-lg border p-4">
          <div className="flex items-center gap-3">
            <FileText className="h-8 w-8 text-primary" />
            <div>
              <p className="font-medium">{file.name}</p>
              <p className="text-sm text-muted-foreground">
                {(file.size / 1024).toFixed(2)} KB
              </p>
            </div>
          </div>
          <Button
            onClick={handleUpload}
            disabled={uploadMutation.isPending}
          >
            {uploadMutation.isPending ? '업로드 중...' : '업로드'}
          </Button>
        </div>
      )}

      {/* 업로드 결과 */}
      {uploadMutation.isSuccess && uploadMutation.data && (
        <Alert>
          <CheckCircle className="h-4 w-4" />
          <AlertTitle>업로드 완료</AlertTitle>
          <AlertDescription>
            <div className="mt-2 space-y-2">
              <div className="flex items-center gap-2">
                <Badge variant="default">{uploadMutation.data.success}명 성공</Badge>
                {uploadMutation.data.failed > 0 && (
                  <Badge variant="destructive">{uploadMutation.data.failed}명 실패</Badge>
                )}
              </div>
              {uploadMutation.data.errors.length > 0 && (
                <div className="mt-4">
                  <p className="font-medium">오류 목록:</p>
                  <div className="mt-2 max-h-40 space-y-1 overflow-y-auto">
                    {uploadMutation.data.errors.map((error, index) => (
                      <div key={index} className="text-sm">
                        <span className="font-mono">{error.email}</span>
                        <span className="text-muted-foreground"> - {error.reason}</span>
                      </div>
                    ))}
                  </div>
                </div>
              )}
            </div>
          </AlertDescription>
        </Alert>
      )}
    </div>
  );
}
