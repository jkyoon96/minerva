/**
 * 초대 링크 관리 컴포넌트
 * 초대 링크 생성, 조회, 비활성화
 */

'use client';

import * as React from 'react';
import { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { Copy, Plus, Trash2, Link as LinkIcon } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog';
import { Label } from '@/components/ui/label';
import { RadioGroup, RadioGroupItem } from '@/components/ui/radio-group';
import { Input } from '@/components/ui/input';
import { useToast } from '@/components/ui/toast';
import { LoadingSpinner } from '@/components/common/LoadingSpinner';
import {
  getInviteLinks,
  createInviteLink,
  deactivateInviteLink,
  CreateInviteLinkRequest,
} from '@/lib/api/enrollments';
import { formatDateTime } from '@/lib/utils';

interface InviteLinkManagerProps {
  courseId: string;
}

export function InviteLinkManager({ courseId }: InviteLinkManagerProps) {
  const [createModalOpen, setCreateModalOpen] = useState(false);
  const [role, setRole] = useState<'student' | 'ta'>('student');
  const [maxUses, setMaxUses] = useState('');
  const { toast } = useToast();
  const queryClient = useQueryClient();

  const { data: inviteLinks, isLoading } = useQuery({
    queryKey: ['invite-links', courseId],
    queryFn: () => getInviteLinks(courseId),
  });

  const createMutation = useMutation({
    mutationFn: (data: CreateInviteLinkRequest) => createInviteLink(courseId, data),
    onSuccess: () => {
      toast({
        title: '초대 링크 생성 완료',
        description: '새로운 초대 링크가 생성되었습니다.',
      });
      queryClient.invalidateQueries({ queryKey: ['invite-links', courseId] });
      setCreateModalOpen(false);
      setRole('student');
      setMaxUses('');
    },
    onError: (error: any) => {
      toast({
        title: '초대 링크 생성 실패',
        description: error.message || '초대 링크 생성 중 오류가 발생했습니다.',
        variant: 'destructive',
      });
    },
  });

  const deactivateMutation = useMutation({
    mutationFn: deactivateInviteLink,
    onSuccess: () => {
      toast({
        title: '초대 링크 비활성화 완료',
        description: '초대 링크가 비활성화되었습니다.',
      });
      queryClient.invalidateQueries({ queryKey: ['invite-links', courseId] });
    },
    onError: (error: any) => {
      toast({
        title: '초대 링크 비활성화 실패',
        description: error.message || '초대 링크 비활성화 중 오류가 발생했습니다.',
        variant: 'destructive',
      });
    },
  });

  const handleCreateLink = () => {
    const data: CreateInviteLinkRequest = {
      role,
      maxUses: maxUses ? parseInt(maxUses) : undefined,
    };
    createMutation.mutate(data);
  };

  const handleCopyLink = (code: string) => {
    const inviteUrl = `${window.location.origin}/invite/${code}`;
    navigator.clipboard.writeText(inviteUrl);
    toast({
      title: '복사 완료',
      description: '초대 링크가 클립보드에 복사되었습니다.',
    });
  };

  if (isLoading) {
    return (
      <div className="flex items-center justify-center py-8">
        <LoadingSpinner />
      </div>
    );
  }

  return (
    <div className="space-y-4">
      <div className="flex items-center justify-between">
        <p className="text-sm text-muted-foreground">
          초대 링크를 생성하여 학생 및 조교를 코스에 초대하세요
        </p>
        <Button onClick={() => setCreateModalOpen(true)}>
          <Plus className="mr-2 h-4 w-4" />
          링크 생성
        </Button>
      </div>

      {/* 초대 링크 목록 */}
      {!inviteLinks || inviteLinks.length === 0 ? (
        <p className="text-sm text-muted-foreground">생성된 초대 링크가 없습니다</p>
      ) : (
        <div className="space-y-3">
          {inviteLinks.map((link) => (
            <div
              key={link.id}
              className="flex items-start justify-between rounded-lg border p-4"
            >
              <div className="flex-1">
                <div className="flex items-center gap-2">
                  <LinkIcon className="h-4 w-4 text-muted-foreground" />
                  <code className="rounded bg-muted px-2 py-1 text-sm font-mono">
                    {link.code}
                  </code>
                  <Badge variant={link.role === 'student' ? 'default' : 'secondary'}>
                    {link.role === 'student' ? '학생' : '조교'}
                  </Badge>
                  {link.isActive ? (
                    <Badge variant="outline" className="bg-green-50 text-green-700">
                      활성
                    </Badge>
                  ) : (
                    <Badge variant="outline" className="bg-gray-50 text-gray-700">
                      비활성
                    </Badge>
                  )}
                </div>
                <div className="mt-2 flex items-center gap-4 text-sm text-muted-foreground">
                  <span>사용: {link.usedCount}회</span>
                  {link.maxUses && <span>최대: {link.maxUses}회</span>}
                  <span>생성: {formatDateTime(link.createdAt)}</span>
                  {link.expiresAt && <span>만료: {formatDateTime(link.expiresAt)}</span>}
                </div>
              </div>
              <div className="flex gap-2">
                <Button
                  variant="outline"
                  size="sm"
                  onClick={() => handleCopyLink(link.code)}
                  disabled={!link.isActive}
                >
                  <Copy className="h-4 w-4" />
                </Button>
                <Button
                  variant="outline"
                  size="sm"
                  onClick={() => deactivateMutation.mutate(link.id)}
                  disabled={!link.isActive || deactivateMutation.isPending}
                >
                  <Trash2 className="h-4 w-4" />
                </Button>
              </div>
            </div>
          ))}
        </div>
      )}

      {/* 생성 모달 */}
      <Dialog open={createModalOpen} onOpenChange={setCreateModalOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>초대 링크 생성</DialogTitle>
            <DialogDescription>새로운 초대 링크를 생성합니다</DialogDescription>
          </DialogHeader>
          <div className="grid gap-4 py-4">
            <div className="grid gap-2">
              <Label>역할</Label>
              <RadioGroup value={role} onValueChange={(value) => setRole(value as any)}>
                <div className="flex items-center space-x-2">
                  <RadioGroupItem value="student" id="student" />
                  <Label htmlFor="student">학생</Label>
                </div>
                <div className="flex items-center space-x-2">
                  <RadioGroupItem value="ta" id="ta" />
                  <Label htmlFor="ta">조교</Label>
                </div>
              </RadioGroup>
            </div>
            <div className="grid gap-2">
              <Label htmlFor="maxUses">최대 사용 횟수 (선택사항)</Label>
              <Input
                id="maxUses"
                type="number"
                placeholder="제한 없음"
                value={maxUses}
                onChange={(e) => setMaxUses(e.target.value)}
                min="1"
              />
              <p className="text-sm text-muted-foreground">
                비워두면 사용 횟수 제한이 없습니다
              </p>
            </div>
          </div>
          <DialogFooter>
            <Button
              variant="outline"
              onClick={() => setCreateModalOpen(false)}
              disabled={createMutation.isPending}
            >
              취소
            </Button>
            <Button onClick={handleCreateLink} disabled={createMutation.isPending}>
              {createMutation.isPending ? '생성 중...' : '생성하기'}
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </div>
  );
}
