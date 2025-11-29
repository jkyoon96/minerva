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
import { Alert } from '@/components/ui/alert';
import { Loader2, Copy, Check, Download, Key, AlertTriangle } from 'lucide-react';
import { regenerateBackupCodes } from '@/lib/api/two-factor';
import { BackupCodesResponse } from '@/types/two-factor';

interface BackupCodesModalProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  currentCodesRemaining?: number;
}

/**
 * 백업 코드 모달
 * - 백업 코드 재생성
 * - 백업 코드 표시
 * - 복사/다운로드 기능
 */
export function BackupCodesModal({
  open,
  onOpenChange,
  currentCodesRemaining = 0,
}: BackupCodesModalProps) {
  const [backupCodes, setBackupCodes] = useState<string[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [copiedCodes, setCopiedCodes] = useState(false);
  const [showConfirm, setShowConfirm] = useState(false);

  const handleRegenerateConfirm = () => {
    setShowConfirm(true);
  };

  const handleRegenerate = async () => {
    setIsLoading(true);
    setError(null);
    try {
      const data: BackupCodesResponse = await regenerateBackupCodes();
      setBackupCodes(data.backupCodes);
      setShowConfirm(false);
    } catch (err: any) {
      setError(err.message || '백업 코드 재생성에 실패했습니다.');
    } finally {
      setIsLoading(false);
    }
  };

  const handleCopyCodes = async () => {
    if (backupCodes.length > 0) {
      const codes = backupCodes.join('\n');
      await navigator.clipboard.writeText(codes);
      setCopiedCodes(true);
      setTimeout(() => setCopiedCodes(false), 2000);
    }
  };

  const handleDownloadCodes = () => {
    if (backupCodes.length > 0) {
      const codes = backupCodes.join('\n');
      const blob = new Blob([codes], { type: 'text/plain' });
      const url = URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = `eduforum-backup-codes-${Date.now()}.txt`;
      a.click();
      URL.revokeObjectURL(url);
    }
  };

  const handleClose = () => {
    setBackupCodes([]);
    setError(null);
    setShowConfirm(false);
    setCopiedCodes(false);
    onOpenChange(false);
  };

  return (
    <Dialog open={open} onOpenChange={handleClose}>
      <DialogContent className="sm:max-w-md">
        <DialogHeader>
          <DialogTitle className="flex items-center gap-2">
            <Key className="h-5 w-5" />
            백업 코드 관리
          </DialogTitle>
          <DialogDescription>
            인증 앱을 사용할 수 없을 때 백업 코드로 로그인할 수 있습니다
          </DialogDescription>
        </DialogHeader>

        {error && (
          <Alert variant="destructive">
            <p className="text-sm">{error}</p>
          </Alert>
        )}

        {/* 현재 백업 코드 상태 */}
        {backupCodes.length === 0 && !showConfirm && (
          <div className="space-y-4">
            <div className="rounded-lg bg-muted/50 p-4 space-y-3">
              <div className="flex items-center justify-between">
                <span className="text-sm text-muted-foreground">
                  남은 백업 코드
                </span>
                <span className="text-lg font-semibold">
                  {currentCodesRemaining}개
                </span>
              </div>

              {currentCodesRemaining <= 2 && (
                <Alert variant="destructive" className="mt-2">
                  <AlertTriangle className="h-4 w-4" />
                  <div>
                    <p className="text-sm font-medium">백업 코드가 부족합니다</p>
                    <p className="text-sm text-muted-foreground mt-1">
                      새로운 백업 코드를 생성하는 것을 권장합니다
                    </p>
                  </div>
                </Alert>
              )}
            </div>

            <Alert>
              <p className="text-sm font-medium">백업 코드 재생성 시 주의사항</p>
              <ul className="text-sm text-muted-foreground mt-2 space-y-1">
                <li>• 기존 백업 코드는 모두 무효화됩니다</li>
                <li>• 새로운 10개의 백업 코드가 생성됩니다</li>
                <li>• 각 코드는 한 번만 사용할 수 있습니다</li>
              </ul>
            </Alert>

            <Button
              onClick={handleRegenerateConfirm}
              disabled={isLoading}
              className="w-full"
            >
              백업 코드 재생성
            </Button>
          </div>
        )}

        {/* 재생성 확인 */}
        {showConfirm && backupCodes.length === 0 && (
          <div className="space-y-4">
            <Alert variant="destructive">
              <AlertTriangle className="h-4 w-4" />
              <div>
                <p className="text-sm font-medium">정말 재생성하시겠습니까?</p>
                <p className="text-sm text-muted-foreground mt-1">
                  기존 백업 코드는 모두 사용할 수 없게 됩니다.
                  새로운 백업 코드를 안전한 곳에 보관해야 합니다.
                </p>
              </div>
            </Alert>

            <div className="flex gap-2">
              <Button
                onClick={() => setShowConfirm(false)}
                variant="outline"
                className="flex-1"
              >
                취소
              </Button>
              <Button
                onClick={handleRegenerate}
                disabled={isLoading}
                className="flex-1"
              >
                {isLoading ? (
                  <>
                    <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                    재생성 중...
                  </>
                ) : (
                  '재생성'
                )}
              </Button>
            </div>
          </div>
        )}

        {/* 새 백업 코드 표시 */}
        {backupCodes.length > 0 && (
          <div className="space-y-4">
            <Alert>
              <p className="text-sm font-medium">
                새로운 백업 코드가 생성되었습니다
              </p>
              <p className="text-sm text-muted-foreground mt-1">
                안전한 곳에 보관하세요. 각 코드는 한 번만 사용할 수 있습니다.
              </p>
            </Alert>

            {/* 백업 코드 목록 */}
            <div className="rounded-lg bg-muted p-4 space-y-2">
              <div className="grid grid-cols-2 gap-2 font-mono text-sm">
                {backupCodes.map((code, index) => (
                  <div
                    key={index}
                    className="px-3 py-2 bg-background rounded border"
                  >
                    {code}
                  </div>
                ))}
              </div>
            </div>

            {/* 액션 버튼 */}
            <div className="flex gap-2">
              <Button
                variant="outline"
                onClick={handleCopyCodes}
                className="flex-1"
              >
                {copiedCodes ? (
                  <>
                    <Check className="mr-2 h-4 w-4 text-green-600" />
                    복사됨
                  </>
                ) : (
                  <>
                    <Copy className="mr-2 h-4 w-4" />
                    복사
                  </>
                )}
              </Button>
              <Button
                variant="outline"
                onClick={handleDownloadCodes}
                className="flex-1"
              >
                <Download className="mr-2 h-4 w-4" />
                다운로드
              </Button>
            </div>

            <Button onClick={handleClose} className="w-full">
              완료
            </Button>
          </div>
        )}
      </DialogContent>
    </Dialog>
  );
}
