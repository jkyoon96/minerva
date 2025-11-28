'use client';

import { useState } from 'react';
import { useRouter } from 'next/navigation';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Alert } from '@/components/ui/alert';
import { useAuthStore } from '@/stores/authStore';
import {
  validateEmail,
  validatePassword,
  validatePasswordConfirm,
  validateName,
  getPasswordStrength,
  getPasswordStrengthText,
  getPasswordStrengthColor,
} from '@/lib/validation';
import { Loader2 } from 'lucide-react';
import { UserRole } from '@/types';

interface RegisterFormProps {
  redirectTo?: string;
}

export function RegisterForm({ redirectTo = '/dashboard' }: RegisterFormProps) {
  const router = useRouter();
  const { register, isLoading, error, clearError } = useAuthStore();

  const [formData, setFormData] = useState({
    email: '',
    password: '',
    passwordConfirm: '',
    name: '',
    role: 'student' as UserRole,
  });

  const [errors, setErrors] = useState<{
    email?: string;
    password?: string;
    passwordConfirm?: string;
    name?: string;
  }>({});

  const [showPasswordStrength, setShowPasswordStrength] = useState(false);
  const passwordStrength = getPasswordStrength(formData.password);

  const validateForm = (): boolean => {
    const newErrors: typeof errors = {};

    const emailError = validateEmail(formData.email);
    if (emailError) newErrors.email = emailError;

    const passwordError = validatePassword(formData.password);
    if (passwordError) newErrors.password = passwordError;

    const passwordConfirmError = validatePasswordConfirm(
      formData.password,
      formData.passwordConfirm
    );
    if (passwordConfirmError) newErrors.passwordConfirm = passwordConfirmError;

    const nameError = validateName(formData.name);
    if (nameError) newErrors.name = nameError;

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    clearError();

    if (!validateForm()) {
      return;
    }

    try {
      await register({
        email: formData.email,
        password: formData.password,
        name: formData.name,
        role: formData.role,
      });

      // 회원가입 성공 시 리다이렉트
      router.push(redirectTo);
    } catch (err) {
      // 에러는 authStore에서 처리됨
      console.error('Register error:', err);
    }
  };

  const handleChange = (
    field: keyof typeof formData
  ) => (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    setFormData((prev) => ({
      ...prev,
      [field]: e.target.value,
    }));

    // 에러 초기화
    if (errors[field as keyof typeof errors]) {
      setErrors((prev) => ({
        ...prev,
        [field]: undefined,
      }));
    }

    // 비밀번호 필드 포커스 시 강도 표시
    if (field === 'password') {
      setShowPasswordStrength(true);
    }
  };

  return (
    <form onSubmit={handleSubmit} className="space-y-4">
      {error && (
        <Alert variant="destructive">
          <p className="text-sm">{error}</p>
        </Alert>
      )}

      <div className="space-y-2">
        <Label htmlFor="name">이름</Label>
        <Input
          id="name"
          type="text"
          placeholder="홍길동"
          value={formData.name}
          onChange={handleChange('name')}
          disabled={isLoading}
          className={errors.name ? 'border-red-500' : ''}
        />
        {errors.name && <p className="text-sm text-red-500">{errors.name}</p>}
      </div>

      <div className="space-y-2">
        <Label htmlFor="email">이메일</Label>
        <Input
          id="email"
          type="email"
          placeholder="name@example.com"
          value={formData.email}
          onChange={handleChange('email')}
          disabled={isLoading}
          className={errors.email ? 'border-red-500' : ''}
        />
        {errors.email && <p className="text-sm text-red-500">{errors.email}</p>}
      </div>

      <div className="space-y-2">
        <Label htmlFor="password">비밀번호</Label>
        <Input
          id="password"
          type="password"
          value={formData.password}
          onChange={handleChange('password')}
          disabled={isLoading}
          className={errors.password ? 'border-red-500' : ''}
          onFocus={() => setShowPasswordStrength(true)}
        />
        {errors.password && (
          <p className="text-sm text-red-500">{errors.password}</p>
        )}

        {showPasswordStrength && formData.password && (
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
        <Label htmlFor="passwordConfirm">비밀번호 확인</Label>
        <Input
          id="passwordConfirm"
          type="password"
          value={formData.passwordConfirm}
          onChange={handleChange('passwordConfirm')}
          disabled={isLoading}
          className={errors.passwordConfirm ? 'border-red-500' : ''}
        />
        {errors.passwordConfirm && (
          <p className="text-sm text-red-500">{errors.passwordConfirm}</p>
        )}
      </div>

      <div className="space-y-2">
        <Label htmlFor="role">역할</Label>
        <select
          id="role"
          value={formData.role}
          onChange={handleChange('role')}
          disabled={isLoading}
          className="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring"
        >
          <option value="student">학생</option>
          <option value="professor">교수</option>
          <option value="ta">조교</option>
        </select>
      </div>

      <Button type="submit" className="w-full" disabled={isLoading}>
        {isLoading ? (
          <>
            <Loader2 className="mr-2 h-4 w-4 animate-spin" />
            회원가입 중...
          </>
        ) : (
          '회원가입'
        )}
      </Button>
    </form>
  );
}
