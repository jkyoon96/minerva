'use client';

import { useState } from 'react';
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog';
import { Button } from '@/components/ui/button';
import { Label } from '@/components/ui/label';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select';
import { Textarea } from '@/components/ui/textarea';
import { UserRole } from '@/types';
import { UserListItem } from '@/types/admin';
import { RoleBadge, getRoleDisplayName, getRoleDescription } from './role-badge';
import { UserAvatar } from '@/components/common/UserAvatar';
import { Loader2, AlertTriangle } from 'lucide-react';
import { Alert, AlertDescription } from '@/components/ui/alert';

interface UserRoleModalProps {
  user: UserListItem | null;
  isOpen: boolean;
  onClose: () => void;
  onConfirm: (userId: string, newRole: UserRole, reason?: string) => Promise<void>;
}

const roles: UserRole[] = ['admin', 'professor', 'ta', 'student'];

/**
 * 역할 변경 모달 컴포넌트
 * - 사용자 정보 표시
 * - 새 역할 선택
 * - 변경 사유 입력 (선택)
 */
export function UserRoleModal({
  user,
  isOpen,
  onClose,
  onConfirm,
}: UserRoleModalProps) {
  const [selectedRole, setSelectedRole] = useState<UserRole | ''>('');
  const [reason, setReason] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  // 모달이 열릴 때 초기화
  const handleOpenChange = (open: boolean) => {
    if (!open) {
      handleClose();
    }
  };

  const handleClose = () => {
    setSelectedRole('');
    setReason('');
    setError(null);
    setIsSubmitting(false);
    onClose();
  };

  const handleSubmit = async () => {
    if (!user || !selectedRole) return;

    // 동일한 역할로 변경 시도
    if (selectedRole === user.role) {
      setError('현재 역할과 동일합니다.');
      return;
    }

    setIsSubmitting(true);
    setError(null);

    try {
      await onConfirm(user.id, selectedRole, reason || undefined);
      handleClose();
    } catch (err) {
      setError(err instanceof Error ? err.message : '역할 변경에 실패했습니다.');
    } finally {
      setIsSubmitting(false);
    }
  };

  if (!user) return null;

  return (
    <Dialog open={isOpen} onOpenChange={handleOpenChange}>
      <DialogContent className="sm:max-w-[500px]">
        <DialogHeader>
          <DialogTitle>역할 변경</DialogTitle>
          <DialogDescription>
            사용자의 역할을 변경합니다. 변경된 역할은 즉시 적용됩니다.
          </DialogDescription>
        </DialogHeader>

        <div className="space-y-6 py-4">
          {/* 사용자 정보 */}
          <div className="flex items-center gap-4 rounded-lg border p-4">
            <UserAvatar user={user} size="lg" />
            <div className="flex-1">
              <p className="font-medium">{user.name}</p>
              <p className="text-sm text-muted-foreground">{user.email}</p>
            </div>
            <RoleBadge role={user.role} showIcon />
          </div>

          {/* 에러 메시지 */}
          {error && (
            <Alert variant="destructive">
              <AlertTriangle className="h-4 w-4" />
              <AlertDescription>{error}</AlertDescription>
            </Alert>
          )}

          {/* 새 역할 선택 */}
          <div className="space-y-2">
            <Label htmlFor="role">새 역할</Label>
            <Select
              value={selectedRole}
              onValueChange={(value) => setSelectedRole(value as UserRole)}
            >
              <SelectTrigger id="role">
                <SelectValue placeholder="역할을 선택하세요" />
              </SelectTrigger>
              <SelectContent>
                {roles.map((role) => (
                  <SelectItem key={role} value={role}>
                    <div className="flex items-center gap-2">
                      <span>{getRoleDisplayName(role)}</span>
                      <span className="text-xs text-muted-foreground">
                        ({getRoleDescription(role)})
                      </span>
                    </div>
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
            {selectedRole && selectedRole !== user.role && (
              <p className="text-sm text-muted-foreground">
                {user.role === 'student' && selectedRole === 'professor' && (
                  '학생 → 교수: 코스 생성 및 관리 권한이 부여됩니다.'
                )}
                {user.role === 'professor' && selectedRole === 'admin' && (
                  '교수 → 관리자: 시스템 전체 관리 권한이 부여됩니다.'
                )}
                {user.role === 'admin' && selectedRole !== 'admin' && (
                  <span className="text-destructive">
                    경고: 관리자 권한이 해제됩니다.
                  </span>
                )}
              </p>
            )}
          </div>

          {/* 변경 사유 (선택) */}
          <div className="space-y-2">
            <Label htmlFor="reason">변경 사유 (선택)</Label>
            <Textarea
              id="reason"
              placeholder="역할 변경 사유를 입력하세요..."
              value={reason}
              onChange={(e) => setReason(e.target.value)}
              rows={3}
              maxLength={500}
            />
            <p className="text-xs text-muted-foreground">
              {reason.length}/500
            </p>
          </div>
        </div>

        <DialogFooter>
          <Button variant="outline" onClick={handleClose} disabled={isSubmitting}>
            취소
          </Button>
          <Button
            onClick={handleSubmit}
            disabled={!selectedRole || isSubmitting}
          >
            {isSubmitting ? (
              <>
                <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                변경 중...
              </>
            ) : (
              '변경'
            )}
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}
