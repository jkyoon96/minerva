'use client';

import { useState } from 'react';
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Alert } from '@/components/ui/alert';
import { Loader2, ShieldOff, AlertTriangle } from 'lucide-react';
import { disableTwoFactor } from '@/lib/api/two-factor';

interface Disable2FAModalProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  onDisabled: () => void;
}

/**
 * 2FA 비활성화 모달
 * - 비밀번호 확인
 * - 경고 메시지 표시
 */
export function Disable2FAModal({
  open,
  onOpenChange,
  onDisabled,
}: Disable2FAModalProps) {
  const [password, setPassword] = useState('');
  const [showConfirm, setShowConfirm] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleNext = () => {
    if (!password) {
      setError('비밀번호를 입력해주세요.');
      return;
    }
    setError(null);
    setShowConfirm(true);
  };

  const handleDisable = async () => {
    setIsLoading(true);
    setError(null);
    try {
      await disableTwoFactor({ password });
      onDisabled();
      handleClose();
    } catch (err: any) {
      setError(err.message || '2FA 비활성화에 실패했습니다.');
    } finally {
      setIsLoading(false);
    }
  };

  const handleClose = () => {
    setPassword('');
    setShowConfirm(false);
    setError(null);
    onOpenChange(false);
  };

  return (
    <Dialog open={open} onOpenChange={handleClose}>
      <DialogContent className="sm:max-w-md">
        <DialogHeader>
          <DialogTitle className="flex items-center gap-2">
            <ShieldOff className="h-5 w-5" />
            2단계 인증 비활성화
          </DialogTitle>
          <DialogDescription>
            계정의 2단계 인증을 비활성화합니다
          </DialogDescription>
        </DialogHeader>

        {error && (
          <Alert variant="destructive">
            <p className="text-sm">{error}</p>
          </Alert>
        )}

        {!showConfirm ? (
          <>
            {/* 경고 메시지 */}
            <Alert variant="destructive">
              <AlertTriangle className="h-4 w-4" />
              <div>
                <p className="text-sm font-medium">보안 위험</p>
                <p className="text-sm text-muted-foreground mt-1">
                  2단계 인증을 비활성화하면 계정 보안이 약화됩니다.
                  정말 비활성화하시겠습니까?
                </p>
              </div>
            </Alert>

            {/* 비밀번호 입력 */}
            <div className="space-y-2">
              <Label htmlFor="password">비밀번호</Label>
              <Input
                id="password"
                type="password"
                placeholder="본인 확인을 위해 비밀번호를 입력하세요"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                disabled={isLoading}
              />
              <p className="text-xs text-muted-foreground">
                계속하려면 현재 비밀번호를 입력해주세요
              </p>
            </div>

            <div className="flex gap-2">
              <Button
                onClick={handleClose}
                variant="outline"
                className="flex-1"
              >
                취소
              </Button>
              <Button
                onClick={handleNext}
                disabled={!password}
                variant="destructive"
                className="flex-1"
              >
                다음
              </Button>
            </div>
          </>
        ) : (
          <>
            {/* 최종 확인 */}
            <div className="space-y-4">
              <Alert variant="destructive">
                <AlertTriangle className="h-4 w-4" />
                <div>
                  <p className="text-sm font-medium">
                    정말로 2단계 인증을 비활성화하시겠습니까?
                  </p>
                  <p className="text-sm text-muted-foreground mt-2">
                    비활성화 시 다음 항목이 삭제됩니다:
                  </p>
                  <ul className="text-sm text-muted-foreground mt-1 space-y-1">
                    <li>• 설정된 인증 앱 연결</li>
                    <li>• 모든 백업 코드</li>
                  </ul>
                </div>
              </Alert>

              <div className="rounded-lg bg-muted/50 p-4">
                <p className="text-sm text-muted-foreground">
                  계정에 다시 로그인할 때는 이메일과 비밀번호만 필요합니다.
                  언제든지 2단계 인증을 다시 활성화할 수 있습니다.
                </p>
              </div>
            </div>

            <div className="flex gap-2">
              <Button
                onClick={() => setShowConfirm(false)}
                variant="outline"
                className="flex-1"
              >
                이전
              </Button>
              <Button
                onClick={handleDisable}
                disabled={isLoading}
                variant="destructive"
                className="flex-1"
              >
                {isLoading ? (
                  <>
                    <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                    비활성화 중...
                  </>
                ) : (
                  '비활성화'
                )}
              </Button>
            </div>
          </>
        )}
      </DialogContent>
    </Dialog>
  );
}
