'use client';

import { useState, useEffect } from 'react';
import { useRouter } from 'next/navigation';
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Alert } from '@/components/ui/alert';
import { Loader2, ShieldCheck, ArrowLeft } from 'lucide-react';
import { Verify2FAForm } from '@/components/auth/two-factor';
import { useAuthStore } from '@/stores/authStore';
import { TwoFactorCodeMode } from '@/types/two-factor';

/**
 * 2FA 로그인 페이지
 * - TOTP 코드 입력
 * - 백업 코드 입력 (대체 수단)
 */
export default function TwoFactorLoginPage() {
  const router = useRouter();
  const {
    twoFactorRequired,
    temporaryToken,
    completeTwoFactorLogin,
    isLoading,
    error,
    clearError,
  } = useAuthStore();

  const [mode, setMode] = useState<TwoFactorCodeMode>(TwoFactorCodeMode.TOTP);
  const [backupCode, setBackupCode] = useState('');
  const [backupCodeError, setBackupCodeError] = useState('');

  // 2FA가 필요하지 않거나 임시 토큰이 없으면 로그인 페이지로 리다이렉트
  useEffect(() => {
    if (!twoFactorRequired || !temporaryToken) {
      router.push('/login');
    }
  }, [twoFactorRequired, temporaryToken, router]);

  const handleVerifyTOTP = async (code: string) => {
    try {
      await completeTwoFactorLogin(code, false);
      router.push('/dashboard');
    } catch (err) {
      // 에러는 authStore에서 처리됨
      console.error('2FA verification error:', err);
    }
  };

  const handleVerifyBackupCode = async (e: React.FormEvent) => {
    e.preventDefault();
    setBackupCodeError('');

    // 백업 코드 형식 검증 (일반적으로 8-12자의 영숫자)
    if (backupCode.length < 8) {
      setBackupCodeError('올바른 백업 코드를 입력해주세요.');
      return;
    }

    try {
      await completeTwoFactorLogin(backupCode, true);
      router.push('/dashboard');
    } catch (err: any) {
      setBackupCodeError(err.message || '백업 코드 인증에 실패했습니다.');
    }
  };

  const handleSwitchMode = () => {
    clearError();
    setBackupCode('');
    setBackupCodeError('');
    setMode(
      mode === TwoFactorCodeMode.TOTP
        ? TwoFactorCodeMode.BACKUP
        : TwoFactorCodeMode.TOTP,
    );
  };

  const handleBackToLogin = () => {
    router.push('/login');
  };

  return (
    <div className="min-h-screen flex items-center justify-center p-4">
      <Card className="w-full max-w-md">
        <CardHeader>
          <div className="flex items-center gap-2 mb-2">
            <ShieldCheck className="h-6 w-6 text-primary" />
            <CardTitle>2단계 인증</CardTitle>
          </div>
          <CardDescription>
            {mode === TwoFactorCodeMode.TOTP
              ? '인증 앱에 표시된 6자리 코드를 입력하세요'
              : '백업 코드를 입력하여 로그인하세요'}
          </CardDescription>
        </CardHeader>

        <CardContent>
          {mode === TwoFactorCodeMode.TOTP ? (
            // TOTP 코드 입력
            <Verify2FAForm
              onVerify={handleVerifyTOTP}
              onSwitchToBackup={handleSwitchMode}
              isLoading={isLoading}
              error={error}
              showBackupOption={true}
            />
          ) : (
            // 백업 코드 입력
            <form onSubmit={handleVerifyBackupCode} className="space-y-4">
              {(error || backupCodeError) && (
                <Alert variant="destructive">
                  <p className="text-sm">{error || backupCodeError}</p>
                </Alert>
              )}

              <div className="space-y-2">
                <Label htmlFor="backupCode">백업 코드</Label>
                <Input
                  id="backupCode"
                  type="text"
                  placeholder="백업 코드를 입력하세요"
                  value={backupCode}
                  onChange={(e) => setBackupCode(e.target.value.trim())}
                  disabled={isLoading}
                  className={backupCodeError ? 'border-red-500' : ''}
                />
                <p className="text-xs text-muted-foreground">
                  2FA 설정 시 받은 백업 코드 중 하나를 입력하세요
                </p>
              </div>

              <div className="flex flex-col gap-2">
                <Button type="submit" disabled={isLoading || !backupCode} className="w-full">
                  {isLoading ? (
                    <>
                      <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                      인증 중...
                    </>
                  ) : (
                    '인증'
                  )}
                </Button>

                <Button
                  type="button"
                  variant="ghost"
                  onClick={handleSwitchMode}
                  disabled={isLoading}
                  className="w-full"
                >
                  인증 앱 코드로 인증
                </Button>
              </div>
            </form>
          )}

          {/* 로그인 페이지로 돌아가기 */}
          <div className="mt-6 pt-6 border-t">
            <Button
              variant="outline"
              onClick={handleBackToLogin}
              disabled={isLoading}
              className="w-full"
            >
              <ArrowLeft className="mr-2 h-4 w-4" />
              로그인 화면으로 돌아가기
            </Button>
          </div>
        </CardContent>
      </Card>
    </div>
  );
}
