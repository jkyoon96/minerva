'use client';

import { useState } from 'react';
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogFooter,
} from '@/components/ui/dialog';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Alert } from '@/components/ui/alert';
import { Progress } from '@/components/ui/progress';
import { Loader2, Eye, EyeOff, CheckCircle2 } from 'lucide-react';
import {
  validatePassword,
  validatePasswordConfirm,
  validateRequired,
  getPasswordStrength,
  getPasswordStrengthText,
  getPasswordStrengthColor,
} from '@/lib/validation';
import { PasswordChangeRequest } from '@/types/profile';

interface PasswordChangeModalProps {
  open: boolean;
  onClose: () => void;
  onSubmit: (data: PasswordChangeRequest) => Promise<void>;
  isLoading?: boolean;
}

export function PasswordChangeModal({
  open,
  onClose,
  onSubmit,
  isLoading = false,
}: PasswordChangeModalProps) {
  const [formData, setFormData] = useState({
    currentPassword: '',
    newPassword: '',
    newPasswordConfirm: '',
  });

  const [showPasswords, setShowPasswords] = useState({
    current: false,
    new: false,
    confirm: false,
  });

  const [errors, setErrors] = useState<{
    currentPassword?: string;
    newPassword?: string;
    newPasswordConfirm?: string;
  }>({});

  const [success, setSuccess] = useState(false);

  const passwordStrength = getPasswordStrength(formData.newPassword);
  const strengthText = getPasswordStrengthText(passwordStrength);
  const strengthColor = getPasswordStrengthColor(passwordStrength);

  const validateForm = (): boolean => {
    const newErrors: typeof errors = {};

    const currentPasswordError = validateRequired(
      formData.currentPassword,
      '현재 비밀번호',
    );
    if (currentPasswordError) newErrors.currentPassword = currentPasswordError;

    const newPasswordError = validatePassword(formData.newPassword);
    if (newPasswordError) newErrors.newPassword = newPasswordError;

    const confirmError = validatePasswordConfirm(
      formData.newPassword,
      formData.newPasswordConfirm,
    );
    if (confirmError) newErrors.newPasswordConfirm = confirmError;

    // 현재 비밀번호와 새 비밀번호가 같은지 확인
    if (
      formData.currentPassword &&
      formData.newPassword &&
      formData.currentPassword === formData.newPassword
    ) {
      newErrors.newPassword = '새 비밀번호는 현재 비밀번호와 달라야 합니다.';
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setSuccess(false);

    if (!validateForm()) {
      return;
    }

    try {
      await onSubmit({
        currentPassword: formData.currentPassword,
        newPassword: formData.newPassword,
      });

      setSuccess(true);

      // 2초 후 모달 닫기
      setTimeout(() => {
        handleClose();
      }, 2000);
    } catch (err) {
      // 에러는 부모 컴포넌트에서 처리
    }
  };

  const handleClose = () => {
    setFormData({
      currentPassword: '',
      newPassword: '',
      newPasswordConfirm: '',
    });
    setErrors({});
    setSuccess(false);
    setShowPasswords({
      current: false,
      new: false,
      confirm: false,
    });
    onClose();
  };

  const handleChange = (field: keyof typeof formData, value: string) => {
    setFormData((prev) => ({
      ...prev,
      [field]: value,
    }));

    // 에러 초기화
    if (errors[field]) {
      setErrors((prev) => ({
        ...prev,
        [field]: undefined,
      }));
    }
  };

  const togglePasswordVisibility = (field: keyof typeof showPasswords) => {
    setShowPasswords((prev) => ({
      ...prev,
      [field]: !prev[field],
    }));
  };

  return (
    <Dialog open={open} onOpenChange={handleClose}>
      <DialogContent className="sm:max-w-md">
        <DialogHeader>
          <DialogTitle>비밀번호 변경</DialogTitle>
        </DialogHeader>

        {success ? (
          <div className="py-8 text-center">
            <CheckCircle2 className="mx-auto mb-4 h-16 w-16 text-green-500" />
            <p className="text-lg font-medium">
              비밀번호가 성공적으로 변경되었습니다.
            </p>
          </div>
        ) : (
          <form onSubmit={handleSubmit} className="space-y-4">
            {/* 현재 비밀번호 */}
            <div className="space-y-2">
              <Label htmlFor="currentPassword">현재 비밀번호</Label>
              <div className="relative">
                <Input
                  id="currentPassword"
                  type={showPasswords.current ? 'text' : 'password'}
                  value={formData.currentPassword}
                  onChange={(e) =>
                    handleChange('currentPassword', e.target.value)
                  }
                  disabled={isLoading}
                  className={errors.currentPassword ? 'border-red-500' : ''}
                />
                <button
                  type="button"
                  onClick={() => togglePasswordVisibility('current')}
                  className="absolute right-3 top-1/2 -translate-y-1/2 text-muted-foreground hover:text-foreground"
                >
                  {showPasswords.current ? (
                    <EyeOff className="h-4 w-4" />
                  ) : (
                    <Eye className="h-4 w-4" />
                  )}
                </button>
              </div>
              {errors.currentPassword && (
                <p className="text-sm text-red-500">{errors.currentPassword}</p>
              )}
            </div>

            {/* 새 비밀번호 */}
            <div className="space-y-2">
              <Label htmlFor="newPassword">새 비밀번호</Label>
              <div className="relative">
                <Input
                  id="newPassword"
                  type={showPasswords.new ? 'text' : 'password'}
                  value={formData.newPassword}
                  onChange={(e) => handleChange('newPassword', e.target.value)}
                  disabled={isLoading}
                  className={errors.newPassword ? 'border-red-500' : ''}
                />
                <button
                  type="button"
                  onClick={() => togglePasswordVisibility('new')}
                  className="absolute right-3 top-1/2 -translate-y-1/2 text-muted-foreground hover:text-foreground"
                >
                  {showPasswords.new ? (
                    <EyeOff className="h-4 w-4" />
                  ) : (
                    <Eye className="h-4 w-4" />
                  )}
                </button>
              </div>
              {errors.newPassword && (
                <p className="text-sm text-red-500">{errors.newPassword}</p>
              )}

              {/* 비밀번호 강도 표시 */}
              {formData.newPassword && !errors.newPassword && (
                <div className="space-y-1">
                  <div className="flex items-center justify-between text-xs">
                    <span className="text-muted-foreground">비밀번호 강도</span>
                    <span className="font-medium">{strengthText}</span>
                  </div>
                  <Progress value={(passwordStrength / 4) * 100} className="h-2" />
                </div>
              )}
            </div>

            {/* 새 비밀번호 확인 */}
            <div className="space-y-2">
              <Label htmlFor="newPasswordConfirm">새 비밀번호 확인</Label>
              <div className="relative">
                <Input
                  id="newPasswordConfirm"
                  type={showPasswords.confirm ? 'text' : 'password'}
                  value={formData.newPasswordConfirm}
                  onChange={(e) =>
                    handleChange('newPasswordConfirm', e.target.value)
                  }
                  disabled={isLoading}
                  className={errors.newPasswordConfirm ? 'border-red-500' : ''}
                />
                <button
                  type="button"
                  onClick={() => togglePasswordVisibility('confirm')}
                  className="absolute right-3 top-1/2 -translate-y-1/2 text-muted-foreground hover:text-foreground"
                >
                  {showPasswords.confirm ? (
                    <EyeOff className="h-4 w-4" />
                  ) : (
                    <Eye className="h-4 w-4" />
                  )}
                </button>
              </div>
              {errors.newPasswordConfirm && (
                <p className="text-sm text-red-500">
                  {errors.newPasswordConfirm}
                </p>
              )}
            </div>

            {/* 비밀번호 요구사항 안내 */}
            <Alert>
              <p className="text-xs">
                비밀번호는 다음 요구사항을 충족해야 합니다:
              </p>
              <ul className="mt-2 space-y-1 text-xs">
                <li>• 최소 8자 이상</li>
                <li>• 대문자, 소문자, 숫자, 특수문자 포함</li>
              </ul>
            </Alert>

            <DialogFooter>
              <Button
                type="button"
                variant="outline"
                onClick={handleClose}
                disabled={isLoading}
              >
                취소
              </Button>
              <Button type="submit" disabled={isLoading}>
                {isLoading ? (
                  <>
                    <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                    변경 중...
                  </>
                ) : (
                  '비밀번호 변경'
                )}
              </Button>
            </DialogFooter>
          </form>
        )}
      </DialogContent>
    </Dialog>
  );
}
