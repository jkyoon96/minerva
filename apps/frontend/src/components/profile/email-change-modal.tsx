'use client';

import { useState } from 'react';
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogFooter,
  DialogDescription,
} from '@/components/ui/dialog';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Alert } from '@/components/ui/alert';
import { Loader2, Eye, EyeOff, Mail } from 'lucide-react';
import { validateEmail, validateRequired } from '@/lib/validation';
import { EmailChangeRequest } from '@/types/profile';

interface EmailChangeModalProps {
  open: boolean;
  currentEmail: string;
  onClose: () => void;
  onSubmit: (data: EmailChangeRequest) => Promise<void>;
  isLoading?: boolean;
}

export function EmailChangeModal({
  open,
  currentEmail,
  onClose,
  onSubmit,
  isLoading = false,
}: EmailChangeModalProps) {
  const [step, setStep] = useState<'input' | 'sent'>('input');
  const [formData, setFormData] = useState({
    newEmail: '',
    currentPassword: '',
  });

  const [showPassword, setShowPassword] = useState(false);

  const [errors, setErrors] = useState<{
    newEmail?: string;
    currentPassword?: string;
  }>({});

  const validateForm = (): boolean => {
    const newErrors: typeof errors = {};

    const emailError = validateEmail(formData.newEmail);
    if (emailError) newErrors.newEmail = emailError;

    // 현재 이메일과 같은지 확인
    if (formData.newEmail === currentEmail) {
      newErrors.newEmail = '현재 이메일과 동일합니다.';
    }

    const passwordError = validateRequired(
      formData.currentPassword,
      '현재 비밀번호',
    );
    if (passwordError) newErrors.currentPassword = passwordError;

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!validateForm()) {
      return;
    }

    try {
      await onSubmit({
        newEmail: formData.newEmail,
        currentPassword: formData.currentPassword,
      });

      // 성공 시 인증 이메일 발송 안내로 전환
      setStep('sent');
    } catch (err) {
      // 에러는 부모 컴포넌트에서 처리
    }
  };

  const handleClose = () => {
    setFormData({
      newEmail: '',
      currentPassword: '',
    });
    setErrors({});
    setStep('input');
    setShowPassword(false);
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

  return (
    <Dialog open={open} onOpenChange={handleClose}>
      <DialogContent className="sm:max-w-md">
        <DialogHeader>
          <DialogTitle>이메일 변경</DialogTitle>
          <DialogDescription>
            {step === 'input'
              ? '새로운 이메일 주소와 현재 비밀번호를 입력해주세요.'
              : '인증 이메일이 발송되었습니다.'}
          </DialogDescription>
        </DialogHeader>

        {step === 'input' ? (
          <form onSubmit={handleSubmit} className="space-y-4">
            {/* 현재 이메일 (읽기 전용) */}
            <div className="space-y-2">
              <Label>현재 이메일</Label>
              <Input value={currentEmail} disabled className="bg-muted" />
            </div>

            {/* 새 이메일 */}
            <div className="space-y-2">
              <Label htmlFor="newEmail">새 이메일</Label>
              <Input
                id="newEmail"
                type="email"
                placeholder="new@example.com"
                value={formData.newEmail}
                onChange={(e) => handleChange('newEmail', e.target.value)}
                disabled={isLoading}
                className={errors.newEmail ? 'border-red-500' : ''}
              />
              {errors.newEmail && (
                <p className="text-sm text-red-500">{errors.newEmail}</p>
              )}
            </div>

            {/* 현재 비밀번호 */}
            <div className="space-y-2">
              <Label htmlFor="currentPassword">현재 비밀번호</Label>
              <div className="relative">
                <Input
                  id="currentPassword"
                  type={showPassword ? 'text' : 'password'}
                  value={formData.currentPassword}
                  onChange={(e) =>
                    handleChange('currentPassword', e.target.value)
                  }
                  disabled={isLoading}
                  className={errors.currentPassword ? 'border-red-500' : ''}
                />
                <button
                  type="button"
                  onClick={() => setShowPassword(!showPassword)}
                  className="absolute right-3 top-1/2 -translate-y-1/2 text-muted-foreground hover:text-foreground"
                >
                  {showPassword ? (
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

            {/* 안내 메시지 */}
            <Alert>
              <p className="text-xs">
                보안을 위해 현재 비밀번호를 입력해야 합니다. 변경 후 새 이메일로
                인증 링크가 발송됩니다.
              </p>
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
                    처리 중...
                  </>
                ) : (
                  '이메일 변경'
                )}
              </Button>
            </DialogFooter>
          </form>
        ) : (
          <div className="space-y-4 py-4">
            <div className="flex flex-col items-center space-y-4 text-center">
              <div className="rounded-full bg-primary/10 p-4">
                <Mail className="h-8 w-8 text-primary" />
              </div>

              <div className="space-y-2">
                <h3 className="font-semibold">인증 이메일이 발송되었습니다</h3>
                <p className="text-sm text-muted-foreground">
                  <span className="font-medium">{formData.newEmail}</span>로
                  인증 링크가 발송되었습니다.
                  <br />
                  이메일의 링크를 클릭하여 이메일 변경을 완료해주세요.
                </p>
              </div>

              <Alert>
                <p className="text-xs">
                  이메일이 도착하지 않았다면 스팸 메일함을 확인해주세요.
                  <br />
                  인증 링크는 24시간 동안 유효합니다.
                </p>
              </Alert>
            </div>

            <DialogFooter>
              <Button onClick={handleClose} className="w-full">
                확인
              </Button>
            </DialogFooter>
          </div>
        )}
      </DialogContent>
    </Dialog>
  );
}
