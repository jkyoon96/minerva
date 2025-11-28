/**
 * 과제 상세 정보 컴포넌트
 * 과제 정보 및 제출 폼
 */

'use client';

import * as React from 'react';
import { useState } from 'react';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { Calendar, Clock, FileText, CheckCircle } from 'lucide-react';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { Textarea } from '@/components/ui/textarea';
import { Label } from '@/components/ui/label';
import { useToast } from '@/components/ui/toast';
import { FileUpload } from './file-upload';
import { submitAssignment } from '@/lib/api/assignments';
import { Assignment, AssignmentSubmission } from '@/lib/api/assignments';
import { formatDateTime } from '@/lib/utils';

interface AssignmentDetailProps {
  assignment: Assignment;
  submission?: AssignmentSubmission;
}

export function AssignmentDetail({ assignment, submission }: AssignmentDetailProps) {
  const [content, setContent] = useState(submission?.content || '');
  const [files, setFiles] = useState<File[]>([]);
  const { toast } = useToast();
  const queryClient = useQueryClient();

  const submitMutation = useMutation({
    mutationFn: () =>
      submitAssignment(assignment.id, {
        content,
        attachments: files.map((f) => f.name), // 실제로는 업로드된 파일 URL
      }),
    onSuccess: () => {
      toast({
        title: '과제 제출 완료',
        description: '과제가 성공적으로 제출되었습니다.',
      });
      queryClient.invalidateQueries({ queryKey: ['assignment', assignment.id] });
      setContent('');
      setFiles([]);
    },
    onError: (error: any) => {
      toast({
        title: '과제 제출 실패',
        description: error.message || '과제 제출 중 오류가 발생했습니다.',
        variant: 'destructive',
      });
    },
  });

  const handleSubmit = () => {
    if (!content.trim() && files.length === 0) {
      toast({
        title: '입력 오류',
        description: '과제 내용을 입력하거나 파일을 첨부해주세요.',
        variant: 'destructive',
      });
      return;
    }
    submitMutation.mutate();
  };

  const isOverdue = new Date(assignment.dueDate) < new Date();
  const isSubmitted = !!submission;

  return (
    <div className="space-y-6">
      {/* 과제 정보 */}
      <Card>
        <CardHeader>
          <div className="flex items-start justify-between">
            <div className="flex-1">
              <CardTitle className="text-2xl">{assignment.title}</CardTitle>
              <CardDescription className="mt-2">{assignment.description}</CardDescription>
            </div>
            <Badge variant={isOverdue ? 'destructive' : 'default'}>
              {isOverdue ? '마감됨' : '진행 중'}
            </Badge>
          </div>
        </CardHeader>
        <CardContent>
          <div className="grid gap-4 sm:grid-cols-2">
            <div className="flex items-center gap-2 text-sm">
              <Calendar className="h-4 w-4 text-muted-foreground" />
              <span className="text-muted-foreground">마감일:</span>
              <span className="font-medium">{formatDateTime(assignment.dueDate)}</span>
            </div>
            <div className="flex items-center gap-2 text-sm">
              <Clock className="h-4 w-4 text-muted-foreground" />
              <span className="text-muted-foreground">배점:</span>
              <span className="font-medium">{assignment.maxGrade}점</span>
            </div>
          </div>

          {assignment.attachments && assignment.attachments.length > 0 && (
            <div className="mt-4">
              <p className="text-sm font-medium">첨부 파일</p>
              <div className="mt-2 space-y-1">
                {assignment.attachments.map((file, index) => (
                  <a
                    key={index}
                    href={file}
                    target="_blank"
                    rel="noopener noreferrer"
                    className="flex items-center gap-2 text-sm text-primary hover:underline"
                  >
                    <FileText className="h-4 w-4" />
                    {file}
                  </a>
                ))}
              </div>
            </div>
          )}
        </CardContent>
      </Card>

      {/* 제출 현황 */}
      {isSubmitted ? (
        <Card>
          <CardHeader>
            <div className="flex items-center gap-2">
              <CheckCircle className="h-5 w-5 text-green-600" />
              <CardTitle>제출 완료</CardTitle>
            </div>
            <CardDescription>
              {formatDateTime(submission.submittedAt)}에 제출되었습니다
            </CardDescription>
          </CardHeader>
          <CardContent>
            {submission.content && (
              <div className="mb-4">
                <p className="text-sm font-medium">제출 내용</p>
                <p className="mt-2 whitespace-pre-wrap text-sm text-muted-foreground">
                  {submission.content}
                </p>
              </div>
            )}
            {submission.grade !== undefined && (
              <div className="rounded-lg border border-green-200 bg-green-50 p-4">
                <p className="text-sm font-medium">채점 결과</p>
                <div className="mt-2 flex items-center gap-2">
                  <span className="text-2xl font-bold text-green-700">
                    {submission.grade}점
                  </span>
                  <span className="text-sm text-muted-foreground">
                    / {assignment.maxGrade}점
                  </span>
                </div>
                {submission.feedback && (
                  <p className="mt-2 text-sm text-muted-foreground">{submission.feedback}</p>
                )}
              </div>
            )}
          </CardContent>
        </Card>
      ) : (
        <Card>
          <CardHeader>
            <CardTitle>과제 제출</CardTitle>
            <CardDescription>과제 내용을 작성하고 파일을 첨부하세요</CardDescription>
          </CardHeader>
          <CardContent className="space-y-4">
            <div className="grid gap-2">
              <Label htmlFor="content">제출 내용</Label>
              <Textarea
                id="content"
                placeholder="과제 내용을 입력하세요"
                rows={8}
                value={content}
                onChange={(e) => setContent(e.target.value)}
                disabled={submitMutation.isPending || isOverdue}
              />
            </div>

            <div className="grid gap-2">
              <Label>파일 첨부</Label>
              <FileUpload
                onFilesChange={setFiles}
                disabled={submitMutation.isPending || isOverdue}
              />
            </div>

            <Button
              onClick={handleSubmit}
              disabled={submitMutation.isPending || isOverdue}
              className="w-full"
            >
              {submitMutation.isPending ? '제출 중...' : '제출하기'}
            </Button>
          </CardContent>
        </Card>
      )}
    </div>
  );
}
