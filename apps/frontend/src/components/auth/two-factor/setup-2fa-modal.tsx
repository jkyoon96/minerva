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
import { Loader2, Copy, Check, Download, Shield } from 'lucide-react';
import { QRCodeSVG } from 'qrcode.react';
import { setupTwoFactor, verifyTwoFactor } from '@/lib/api/two-factor';
import {
  TwoFactorSetupResponse,
  TwoFactorSetupStep,
} from '@/types/two-factor';

interface Setup2FAModalProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  onComplete: () => void;
}

/**
 * 2FA 설정 모달
 * - QR 코드 표시
 * - 시크릿 키 복사
 * - 코드 검증
 * - 백업 코드 표시
 */
export function Setup2FAModal({
  open,
  onOpenChange,
  onComplete,
}: Setup2FAModalProps) {
  const [step, setStep] = useState<TwoFactorSetupStep>(
    TwoFactorSetupStep.INITIAL,
  );
  const [setupData, setSetupData] = useState<TwoFactorSetupResponse | null>(
    null,
  );
  const [verificationCode, setVerificationCode] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [copiedSecret, setCopiedSecret] = useState(false);
  const [copiedBackupCodes, setCopiedBackupCodes] = useState(false);

  const handleStartSetup = async () => {
    setIsLoading(true);
    setError(null);
    try {
      const data = await setupTwoFactor();
      setSetupData(data);
      setStep(TwoFactorSetupStep.QR_CODE);
    } catch (err: any) {
      setError(err.message || '2FA 설정을 시작할 수 없습니다.');
    } finally {
      setIsLoading(false);
    }
  };

  const handleCopySecret = async () => {
    if (setupData?.secret) {
      await navigator.clipboard.writeText(setupData.secret);
      setCopiedSecret(true);
      setTimeout(() => setCopiedSecret(false), 2000);
    }
  };

  const handleVerifyCode = async () => {
    if (verificationCode.length !== 6) {
      setError('6자리 코드를 입력해주세요.');
      return;
    }

    setIsLoading(true);
    setError(null);
    try {
      await verifyTwoFactor({ code: verificationCode });
      setStep(TwoFactorSetupStep.BACKUP_CODES);
    } catch (err: any) {
      setError(err.message || '코드 검증에 실패했습니다.');
    } finally {
      setIsLoading(false);
    }
  };

  const handleCopyBackupCodes = async () => {
    if (setupData?.backupCodes) {
      const codes = setupData.backupCodes.join('\n');
      await navigator.clipboard.writeText(codes);
      setCopiedBackupCodes(true);
      setTimeout(() => setCopiedBackupCodes(false), 2000);
    }
  };

  const handleDownloadBackupCodes = () => {
    if (setupData?.backupCodes) {
      const codes = setupData.backupCodes.join('\n');
      const blob = new Blob([codes], { type: 'text/plain' });
      const url = URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = `eduforum-backup-codes-${Date.now()}.txt`;
      a.click();
      URL.revokeObjectURL(url);
    }
  };

  const handleComplete = () => {
    onComplete();
    handleClose();
  };

  const handleClose = () => {
    setStep(TwoFactorSetupStep.INITIAL);
    setSetupData(null);
    setVerificationCode('');
    setError(null);
    setCopiedSecret(false);
    setCopiedBackupCodes(false);
    onOpenChange(false);
  };

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="sm:max-w-md">
        <DialogHeader>
          <DialogTitle className="flex items-center gap-2">
            <Shield className="h-5 w-5" />
            2단계 인증 설정
          </DialogTitle>
          <DialogDescription>
            {step === TwoFactorSetupStep.INITIAL &&
              '앱 기반 2단계 인증을 설정합니다'}
            {step === TwoFactorSetupStep.QR_CODE &&
              'QR 코드를 스캔하거나 시크릿 키를 입력하세요'}
            {step === TwoFactorSetupStep.BACKUP_CODES &&
              '백업 코드를 안전한 곳에 보관하세요'}
          </DialogDescription>
        </DialogHeader>

        {error && (
          <Alert variant="destructive">
            <p className="text-sm">{error}</p>
          </Alert>
        )}

        {/* Step 1: 시작 */}
        {step === TwoFactorSetupStep.INITIAL && (
          <div className="space-y-4">
            <div className="rounded-lg border border-dashed border-muted-foreground/25 p-6">
              <h3 className="font-medium mb-2">필요한 앱</h3>
              <ul className="text-sm text-muted-foreground space-y-1">
                <li>• Google Authenticator</li>
                <li>• Microsoft Authenticator</li>
                <li>• Authy</li>
                <li>• 기타 TOTP 지원 앱</li>
              </ul>
            </div>

            <div className="space-y-2">
              <h3 className="font-medium text-sm">설정 단계</h3>
              <ol className="text-sm text-muted-foreground space-y-1">
                <li>1. 인증 앱에서 QR 코드 스캔</li>
                <li>2. 앱에 표시된 6자리 코드 입력</li>
                <li>3. 백업 코드 저장</li>
              </ol>
            </div>

            <Button onClick={handleStartSetup} disabled={isLoading} className="w-full">
              {isLoading ? (
                <>
                  <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                  설정 준비 중...
                </>
              ) : (
                '설정 시작'
              )}
            </Button>
          </div>
        )}

        {/* Step 2: QR 코드 및 시크릿 */}
        {step === TwoFactorSetupStep.QR_CODE && setupData && (
          <div className="space-y-4">
            {/* QR 코드 */}
            <div className="flex justify-center p-4 bg-white rounded-lg border">
              <QRCodeSVG value={setupData.qrCodeUrl} size={200} />
            </div>

            {/* 시크릿 키 */}
            <div className="space-y-2">
              <Label>시크릿 키 (수동 입력)</Label>
              <div className="flex gap-2">
                <Input
                  value={setupData.secret}
                  readOnly
                  className="font-mono text-sm"
                />
                <Button
                  type="button"
                  variant="outline"
                  size="icon"
                  onClick={handleCopySecret}
                >
                  {copiedSecret ? (
                    <Check className="h-4 w-4 text-green-600" />
                  ) : (
                    <Copy className="h-4 w-4" />
                  )}
                </Button>
              </div>
              <p className="text-xs text-muted-foreground">
                QR 코드를 스캔할 수 없는 경우 이 키를 앱에 입력하세요
              </p>
            </div>

            {/* 검증 코드 입력 */}
            <div className="space-y-2">
              <Label htmlFor="verificationCode">인증 코드</Label>
              <Input
                id="verificationCode"
                type="text"
                inputMode="numeric"
                maxLength={6}
                placeholder="000000"
                value={verificationCode}
                onChange={(e) => setVerificationCode(e.target.value)}
                disabled={isLoading}
              />
              <p className="text-xs text-muted-foreground">
                인증 앱에 표시된 6자리 코드를 입력하세요
              </p>
            </div>

            <Button
              onClick={handleVerifyCode}
              disabled={isLoading || verificationCode.length !== 6}
              className="w-full"
            >
              {isLoading ? (
                <>
                  <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                  검증 중...
                </>
              ) : (
                '확인'
              )}
            </Button>
          </div>
        )}

        {/* Step 3: 백업 코드 */}
        {step === TwoFactorSetupStep.BACKUP_CODES && setupData && (
          <div className="space-y-4">
            <Alert>
              <p className="text-sm font-medium">
                백업 코드를 안전한 곳에 보관하세요
              </p>
              <p className="text-sm text-muted-foreground mt-1">
                인증 앱을 사용할 수 없을 때 이 코드로 로그인할 수 있습니다.
                각 코드는 한 번만 사용할 수 있습니다.
              </p>
            </Alert>

            {/* 백업 코드 목록 */}
            <div className="rounded-lg bg-muted p-4 space-y-2">
              <div className="grid grid-cols-2 gap-2 font-mono text-sm">
                {setupData.backupCodes.map((code, index) => (
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
                onClick={handleCopyBackupCodes}
                className="flex-1"
              >
                {copiedBackupCodes ? (
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
                onClick={handleDownloadBackupCodes}
                className="flex-1"
              >
                <Download className="mr-2 h-4 w-4" />
                다운로드
              </Button>
            </div>

            <Button onClick={handleComplete} className="w-full">
              완료
            </Button>
          </div>
        )}
      </DialogContent>
    </Dialog>
  );
}
