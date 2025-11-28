'use client';

import { useState, useEffect } from 'react';
import { useRouter, useSearchParams } from 'next/navigation';
import Link from 'next/link';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Alert } from '@/components/ui/alert';
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from '@/components/ui/card';
import { resetPassword } from '@/lib/api/auth';
import {
  validatePassword,
  validatePasswordConfirm,
  getPasswordStrength,
  getPasswordStrengthText,
  getPasswordStrengthColor,
} from '@/lib/validation';
import { Loader2, CheckCircle2 } from 'lucide-react';

export default function ResetPasswordPage() {
  const router = useRouter();
  const searchParams = useSearchParams();
  const token = searchParams.get('token');

  const [password, setPassword] = useState('');
  const [passwordConfirm, setPasswordConfirm] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState(false);
  const [showPasswordStrength, setShowPasswordStrength] = useState(false);

  const [errors, setErrors] = useState<{
    password?: string;
    passwordConfirm?: string;
  }>({});

  const passwordStrength = getPasswordStrength(password);

  useEffect(() => {
    if (!token) {
      setError('유효하지 않은 재설정 링크입니다.');
    }
  }, [token]);

  const validateForm = (): boolean => {
    const newErrors: typeof errors = {};

    const passwordError = validatePassword(password);
    if (passwordError) newErrors.password = passwordError;

    const passwordConfirmError = validatePasswordConfirm(
      password,
      passwordConfirm
    );
    if (passwordConfirmError)
      newErrors.passwordConfirm = passwordConfirmError;

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);

    if (!token) {
      setError('유효하지 않은 재설정 링크입니다.');
      return;
    }

    if (!validateForm()) {
      return;
    }

    setIsLoading(true);

    try {
      await resetPassword(token, password);
      setSuccess(true);

      // 3초 후 로그인 페이지로 이동
      setTimeout(() => {
        router.push('/login');
      }, 3000);
    } catch (err: any) {
      setError(
        err.message ||
          '비밀번호 재설정에 실패했습니다. 링크가 만료되었을 수 있습니다.'
      );
    } finally {
      setIsLoading(false);
    }
  };

  if (success) {
    return (
      <Card className="w-full max-w-md">
        <CardHeader>
          <div className="flex justify-center mb-4">
            <CheckCircle2 className="h-12 w-12 text-green-500" />
          </div>
          <CardTitle className="text-center">
            비밀번호가 재설정되었습니다
          </CardTitle>
          <CardDescription className="text-center">
            새로운 비밀번호로 로그인할 수 있습니다.
          </CardDescription>
        </CardHeader>
        <CardContent>
          <p className="text-sm text-muted-foreground text-center">
            잠시 후 로그인 페이지로 이동합니다...
          </p>
          <div className="mt-4">
            <Link href="/login">
              <Button className="w-full">로그인 페이지로 이동</Button>
            </Link>
          </div>
        </CardContent>
      </Card>
    );
  }

  return (
    <Card className="w-full max-w-md">
      <CardHeader>
        <CardTitle>비밀번호 재설정</CardTitle>
        <CardDescription>새로운 비밀번호를 입력해주세요.</CardDescription>
      </CardHeader>
      <CardContent>
        <form onSubmit={handleSubmit} className="space-y-4">
          {error && (
            <Alert variant="destructive">
              <p className="text-sm">{error}</p>
            </Alert>
          )}

          <div className="space-y-2">
            <Label htmlFor="password">새 비밀번호</Label>
            <Input
              id="password"
              type="password"
              value={password}
              onChange={(e) => {
                setPassword(e.target.value);
                setErrors((prev) => ({ ...prev, password: undefined }));
              }}
              disabled={isLoading || !token}
              className={errors.password ? 'border-red-500' : ''}
              onFocus={() => setShowPasswordStrength(true)}
            />
            {errors.password && (
              <p className="text-sm text-red-500">{errors.password}</p>
            )}

            {showPasswordStrength && password && (
              <div className="space-y-1">
                <div className="flex gap-1">
                  {[0, 1, 2, 3].map((i) => (
                    <div
                      key={i}
                      className={`h-1 flex-1 rounded ${
                        i < passwordStrength
                          ? getPasswordStrengthColor(passwordStrength)
                          : 'bg-gray-300'
                      }`}
                    />
                  ))}
                </div>
                <p className="text-xs text-muted-foreground">
                  비밀번호 강도: {getPasswordStrengthText(passwordStrength)}
                </p>
              </div>
            )}
          </div>

          <div className="space-y-2">
            <Label htmlFor="passwordConfirm">새 비밀번호 확인</Label>
            <Input
              id="passwordConfirm"
              type="password"
              value={passwordConfirm}
              onChange={(e) => {
                setPasswordConfirm(e.target.value);
                setErrors((prev) => ({
                  ...prev,
                  passwordConfirm: undefined,
                }));
              }}
              disabled={isLoading || !token}
              className={errors.passwordConfirm ? 'border-red-500' : ''}
            />
            {errors.passwordConfirm && (
              <p className="text-sm text-red-500">{errors.passwordConfirm}</p>
            )}
          </div>

          <Button
            type="submit"
            className="w-full"
            disabled={isLoading || !token}
          >
            {isLoading ? (
              <>
                <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                재설정 중...
              </>
            ) : (
              '비밀번호 재설정'
            )}
          </Button>
        </form>

        <div className="mt-6 text-center">
          <Link href="/login" className="text-sm text-primary hover:underline">
            로그인 페이지로 돌아가기
          </Link>
        </div>
      </CardContent>
    </Card>
  );
}
