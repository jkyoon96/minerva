/**
 * 코스 등록 모달 컴포넌트
 * 초대 코드 입력을 통한 수강 신청
 */

'use client';

import * as React from 'react';
import { useState } from 'react';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog';
import { Input } from '@/components/ui/input';
import { Button } from '@/components/ui/button';
import { Label } from '@/components/ui/label';
import { useToast } from '@/components/ui/toast';
import { enrollWithInviteCode } from '@/lib/api/enrollments';

interface EnrollModalProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
}

export function EnrollModal({ open, onOpenChange }: EnrollModalProps) {
  const [inviteCode, setInviteCode] = useState('');
  const { toast } = useToast();
  const queryClient = useQueryClient();

  const enrollMutation = useMutation({
    mutationFn: enrollWithInviteCode,
    onSuccess: () => {
      toast({
        title: '수강 신청 완료',
        description: '코스에 성공적으로 등록되었습니다.',
      });
      queryClient.invalidateQueries({ queryKey: ['courses'] });
      queryClient.invalidateQueries({ queryKey: ['student-dashboard'] });
      onOpenChange(false);
      setInviteCode('');
    },
    onError: (error: any) => {
      toast({
        title: '수강 신청 실패',
        description: error.message || '올바른 초대 코드를 입력해주세요.',
        variant: 'destructive',
      });
    },
  });

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!inviteCode.trim()) {
      toast({
        title: '입력 오류',
        description: '초대 코드를 입력해주세요.',
        variant: 'destructive',
      });
      return;
    }
    enrollMutation.mutate(inviteCode.trim());
  };

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>코스 등록</DialogTitle>
          <DialogDescription>
            교수님께 받은 초대 코드를 입력하여 코스에 등록하세요.
          </DialogDescription>
        </DialogHeader>
        <form onSubmit={handleSubmit}>
          <div className="grid gap-4 py-4">
            <div className="grid gap-2">
              <Label htmlFor="inviteCode">초대 코드</Label>
              <Input
                id="inviteCode"
                placeholder="예: ABC123XYZ"
                value={inviteCode}
                onChange={(e) => setInviteCode(e.target.value)}
                disabled={enrollMutation.isPending}
              />
              <p className="text-sm text-muted-foreground">
                초대 코드는 대소문자를 구분합니다.
              </p>
            </div>
          </div>
          <DialogFooter>
            <Button
              type="button"
              variant="outline"
              onClick={() => onOpenChange(false)}
              disabled={enrollMutation.isPending}
            >
              취소
            </Button>
            <Button type="submit" disabled={enrollMutation.isPending}>
              {enrollMutation.isPending ? '등록 중...' : '등록하기'}
            </Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  );
}
