/**
 * 세션 생성/수정 모달 컴포넌트
 */

'use client';

import * as React from 'react';
import { useState, useEffect } from 'react';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog';
import { Label } from '@/components/ui/label';
import { Input } from '@/components/ui/input';
import { Textarea } from '@/components/ui/textarea';
import { Button } from '@/components/ui/button';
import { useToast } from '@/components/ui/toast';
import { createSession, updateSession, CreateSessionRequest } from '@/lib/api/sessions';
import { Session } from '@/types';

interface SessionModalProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  courseId: string;
  session?: Session;
  mode: 'create' | 'edit';
}

export function SessionModal({ open, onOpenChange, courseId, session, mode }: SessionModalProps) {
  const [title, setTitle] = useState('');
  const [scheduledAt, setScheduledAt] = useState('');
  const [duration, setDuration] = useState('60');
  const [description, setDescription] = useState('');
  const { toast } = useToast();
  const queryClient = useQueryClient();

  useEffect(() => {
    if (session && mode === 'edit') {
      setTitle(session.title);
      setScheduledAt(new Date(session.scheduledAt).toISOString().slice(0, 16));
      setDuration(session.duration.toString());
      setDescription('');
    } else {
      setTitle('');
      setScheduledAt('');
      setDuration('60');
      setDescription('');
    }
  }, [session, mode, open]);

  const createMutation = useMutation({
    mutationFn: (data: CreateSessionRequest) => createSession(courseId, data),
    onSuccess: () => {
      toast({
        title: '세션 생성 완료',
        description: '새로운 세션이 생성되었습니다.',
      });
      queryClient.invalidateQueries({ queryKey: ['sessions', courseId] });
      onOpenChange(false);
    },
    onError: (error: any) => {
      toast({
        title: '세션 생성 실패',
        description: error.message || '세션 생성 중 오류가 발생했습니다.',
        variant: 'destructive',
      });
    },
  });

  const updateMutation = useMutation({
    mutationFn: (data: CreateSessionRequest) => updateSession(session!.id, data),
    onSuccess: () => {
      toast({
        title: '세션 수정 완료',
        description: '세션이 수정되었습니다.',
      });
      queryClient.invalidateQueries({ queryKey: ['sessions', courseId] });
      queryClient.invalidateQueries({ queryKey: ['session', session?.id] });
      onOpenChange(false);
    },
    onError: (error: any) => {
      toast({
        title: '세션 수정 실패',
        description: error.message || '세션 수정 중 오류가 발생했습니다.',
        variant: 'destructive',
      });
    },
  });

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();

    if (!title || !scheduledAt || !duration) {
      toast({
        title: '입력 오류',
        description: '모든 필수 항목을 입력해주세요.',
        variant: 'destructive',
      });
      return;
    }

    const data: CreateSessionRequest = {
      title,
      scheduledAt: new Date(scheduledAt).toISOString(),
      duration: parseInt(duration),
      description: description || undefined,
    };

    if (mode === 'create') {
      createMutation.mutate(data);
    } else {
      updateMutation.mutate(data);
    }
  };

  const isSubmitting = createMutation.isPending || updateMutation.isPending;

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="sm:max-w-[500px]">
        <DialogHeader>
          <DialogTitle>{mode === 'create' ? '새 세션 만들기' : '세션 수정'}</DialogTitle>
          <DialogDescription>
            {mode === 'create'
              ? '새로운 라이브 세션을 예약합니다'
              : '세션 정보를 수정합니다'}
          </DialogDescription>
        </DialogHeader>
        <form onSubmit={handleSubmit}>
          <div className="grid gap-4 py-4">
            <div className="grid gap-2">
              <Label htmlFor="title">
                세션 제목 <span className="text-destructive">*</span>
              </Label>
              <Input
                id="title"
                placeholder="예: 1주차 강의 - 소개"
                value={title}
                onChange={(e) => setTitle(e.target.value)}
                disabled={isSubmitting}
              />
            </div>

            <div className="grid gap-2">
              <Label htmlFor="scheduledAt">
                예정 일시 <span className="text-destructive">*</span>
              </Label>
              <Input
                id="scheduledAt"
                type="datetime-local"
                value={scheduledAt}
                onChange={(e) => setScheduledAt(e.target.value)}
                disabled={isSubmitting}
              />
            </div>

            <div className="grid gap-2">
              <Label htmlFor="duration">
                진행 시간 (분) <span className="text-destructive">*</span>
              </Label>
              <Input
                id="duration"
                type="number"
                placeholder="60"
                value={duration}
                onChange={(e) => setDuration(e.target.value)}
                min="1"
                disabled={isSubmitting}
              />
            </div>

            <div className="grid gap-2">
              <Label htmlFor="description">설명 (선택사항)</Label>
              <Textarea
                id="description"
                placeholder="세션에 대한 간단한 설명"
                rows={3}
                value={description}
                onChange={(e) => setDescription(e.target.value)}
                disabled={isSubmitting}
              />
            </div>
          </div>
          <DialogFooter>
            <Button
              type="button"
              variant="outline"
              onClick={() => onOpenChange(false)}
              disabled={isSubmitting}
            >
              취소
            </Button>
            <Button type="submit" disabled={isSubmitting}>
              {isSubmitting
                ? mode === 'create'
                  ? '생성 중...'
                  : '수정 중...'
                : mode === 'create'
                  ? '생성하기'
                  : '수정하기'}
            </Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  );
}
