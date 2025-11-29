'use client';

import { useState } from 'react';
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
import { Separator } from '@/components/ui/separator';
import { Loader2, Key, Lock, ShieldCheck } from 'lucide-react';
import {
  TwoFactorStatus,
  Setup2FAModal,
  BackupCodesModal,
  Disable2FAModal,
} from '@/components/auth/two-factor';
import { changePassword } from '@/lib/api/auth';
import { validatePassword } from '@/lib/validation';

/**
 * 보안 설정 페이지
 * - 2FA 설정 관리
 * - 비밀번호 변경
 */
export default function SecuritySettingsPage() {
  // 2FA 모달 상태
  const [setupModalOpen, setSetupModalOpen] = useState(false);
  const [backupCodesModalOpen, setBackupCodesModalOpen] = useState(false);
  const [disableModalOpen, setDisableModalOpen] = useState(false);
  const [twoFactorRefreshKey, setTwoFactorRefreshKey] = useState(0);

  // 비밀번호 변경 상태
  const [passwordForm, setPasswordForm] = useState({
    currentPassword: '',
    newPassword: '',
    confirmPassword: '',
  });
  const [passwordErrors, setPasswordErrors] = useState<{
    currentPassword?: string;
    newPassword?: string;
    confirmPassword?: string;
  }>({});
  const [isChangingPassword, setIsChangingPassword] = useState(false);
  const [passwordChangeSuccess, setPasswordChangeSuccess] = useState(false);
  const [passwordError, setPasswordError] = useState<string | null>(null);

  // 2FA 핸들러
  const handleSetupComplete = () => {
    setTwoFactorRefreshKey((prev) => prev + 1);
  };

  const handleDisableComplete = () => {
    setTwoFactorRefreshKey((prev) => prev + 1);
  };

  // 비밀번호 변경 핸들러
  const validatePasswordForm = (): boolean => {
    const errors: typeof passwordErrors = {};

    if (!passwordForm.currentPassword) {
      errors.currentPassword = '현재 비밀번호를 입력해주세요.';
    }

    const newPasswordError = validatePassword(passwordForm.newPassword);
    if (newPasswordError) {
      errors.newPassword = newPasswordError;
    }

    if (passwordForm.newPassword !== passwordForm.confirmPassword) {
      errors.confirmPassword = '비밀번호가 일치하지 않습니다.';
    }

    if (passwordForm.currentPassword === passwordForm.newPassword) {
      errors.newPassword = '현재 비밀번호와 다른 비밀번호를 입력해주세요.';
    }

    setPasswordErrors(errors);
    return Object.keys(errors).length === 0;
  };

  const handlePasswordChange = async (e: React.FormEvent) => {
    e.preventDefault();
    setPasswordError(null);
    setPasswordChangeSuccess(false);

    if (!validatePasswordForm()) {
      return;
    }

    setIsChangingPassword(true);
    try {
      await changePassword(
        passwordForm.currentPassword,
        passwordForm.newPassword,
      );

      setPasswordChangeSuccess(true);
      setPasswordForm({
        currentPassword: '',
        newPassword: '',
        confirmPassword: '',
      });

      // 3초 후 성공 메시지 숨김
      setTimeout(() => {
        setPasswordChangeSuccess(false);
      }, 3000);
    } catch (err: any) {
      setPasswordError(err.message || '비밀번호 변경에 실패했습니다.');
    } finally {
      setIsChangingPassword(false);
    }
  };

  const handlePasswordInputChange = (field: keyof typeof passwordForm) => (
    e: React.ChangeEvent<HTMLInputElement>,
  ) => {
    setPasswordForm((prev) => ({
      ...prev,
      [field]: e.target.value,
    }));

    // 에러 초기화
    if (passwordErrors[field]) {
      setPasswordErrors((prev) => ({
        ...prev,
        [field]: undefined,
      }));
    }
  };

  return (
    <div className="container max-w-4xl py-8 space-y-8">
      {/* 헤더 */}
      <div>
        <h1 className="text-3xl font-bold flex items-center gap-2">
          <Lock className="h-8 w-8" />
          보안 설정
        </h1>
        <p className="text-muted-foreground mt-2">
          계정 보안 설정을 관리하고 2단계 인증을 설정하세요
        </p>
      </div>

      {/* 2단계 인증 */}
      <TwoFactorStatus
        key={twoFactorRefreshKey}
        onSetup={() => setSetupModalOpen(true)}
        onDisable={() => setDisableModalOpen(true)}
        onViewBackupCodes={() => setBackupCodesModalOpen(true)}
      />

      <Separator />

      {/* 비밀번호 변경 */}
      <Card>
        <CardHeader>
          <div className="flex items-center gap-2">
            <Key className="h-5 w-5" />
            <CardTitle>비밀번호 변경</CardTitle>
          </div>
          <CardDescription>
            정기적으로 비밀번호를 변경하여 계정을 안전하게 보호하세요
          </CardDescription>
        </CardHeader>
        <CardContent>
          <form onSubmit={handlePasswordChange} className="space-y-4">
            {passwordChangeSuccess && (
              <Alert>
                <ShieldCheck className="h-4 w-4" />
                <p className="text-sm font-medium">
                  비밀번호가 성공적으로 변경되었습니다
                </p>
              </Alert>
            )}

            {passwordError && (
              <Alert variant="destructive">
                <p className="text-sm">{passwordError}</p>
              </Alert>
            )}

            <div className="space-y-2">
              <Label htmlFor="currentPassword">현재 비밀번호</Label>
              <Input
                id="currentPassword"
                type="password"
                value={passwordForm.currentPassword}
                onChange={handlePasswordInputChange('currentPassword')}
                disabled={isChangingPassword}
                className={
                  passwordErrors.currentPassword ? 'border-red-500' : ''
                }
              />
              {passwordErrors.currentPassword && (
                <p className="text-sm text-red-500">
                  {passwordErrors.currentPassword}
                </p>
              )}
            </div>

            <div className="space-y-2">
              <Label htmlFor="newPassword">새 비밀번호</Label>
              <Input
                id="newPassword"
                type="password"
                value={passwordForm.newPassword}
                onChange={handlePasswordInputChange('newPassword')}
                disabled={isChangingPassword}
                className={passwordErrors.newPassword ? 'border-red-500' : ''}
              />
              {passwordErrors.newPassword && (
                <p className="text-sm text-red-500">
                  {passwordErrors.newPassword}
                </p>
              )}
              <p className="text-xs text-muted-foreground">
                최소 8자 이상, 영문/숫자/특수문자 포함
              </p>
            </div>

            <div className="space-y-2">
              <Label htmlFor="confirmPassword">새 비밀번호 확인</Label>
              <Input
                id="confirmPassword"
                type="password"
                value={passwordForm.confirmPassword}
                onChange={handlePasswordInputChange('confirmPassword')}
                disabled={isChangingPassword}
                className={
                  passwordErrors.confirmPassword ? 'border-red-500' : ''
                }
              />
              {passwordErrors.confirmPassword && (
                <p className="text-sm text-red-500">
                  {passwordErrors.confirmPassword}
                </p>
              )}
            </div>

            <Button type="submit" disabled={isChangingPassword}>
              {isChangingPassword ? (
                <>
                  <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                  변경 중...
                </>
              ) : (
                '비밀번호 변경'
              )}
            </Button>
          </form>
        </CardContent>
      </Card>

      {/* 2FA 모달들 */}
      <Setup2FAModal
        open={setupModalOpen}
        onOpenChange={setSetupModalOpen}
        onComplete={handleSetupComplete}
      />
      <BackupCodesModal
        open={backupCodesModalOpen}
        onOpenChange={setBackupCodesModalOpen}
      />
      <Disable2FAModal
        open={disableModalOpen}
        onOpenChange={setDisableModalOpen}
        onDisabled={handleDisableComplete}
      />
    </div>
  );
}
