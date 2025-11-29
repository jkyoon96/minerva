'use client';

import { useState, useEffect } from 'react';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Textarea } from '@/components/ui/textarea';
import { Alert } from '@/components/ui/alert';
import { Loader2 } from 'lucide-react';
import { validateName, validateRequired } from '@/lib/validation';
import { ProfileUpdateRequest } from '@/types/profile';

interface ProfileFormProps {
  initialData: {
    name: string;
    bio?: string;
  };
  onSubmit: (data: ProfileUpdateRequest) => Promise<void>;
  isLoading?: boolean;
}

export function ProfileForm({
  initialData,
  onSubmit,
  isLoading = false,
}: ProfileFormProps) {
  const [formData, setFormData] = useState({
    name: initialData.name || '',
    bio: initialData.bio || '',
  });

  const [errors, setErrors] = useState<{
    name?: string;
    bio?: string;
  }>({});

  const [success, setSuccess] = useState(false);
  const [hasChanges, setHasChanges] = useState(false);

  // 변경사항 감지
  useEffect(() => {
    const changed =
      formData.name !== initialData.name ||
      formData.bio !== (initialData.bio || '');
    setHasChanges(changed);
  }, [formData, initialData]);

  const validateForm = (): boolean => {
    const newErrors: typeof errors = {};

    const nameError = validateName(formData.name);
    if (nameError) newErrors.name = nameError;

    if (formData.bio && formData.bio.length > 500) {
      newErrors.bio = '소개는 500자를 초과할 수 없습니다.';
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
        name: formData.name,
        bio: formData.bio || undefined,
      });

      setSuccess(true);
      setHasChanges(false);

      // 성공 메시지 3초 후 숨김
      setTimeout(() => setSuccess(false), 3000);
    } catch (err) {
      // 에러는 부모 컴포넌트에서 처리
    }
  };

  const handleChange = (
    field: keyof typeof formData,
    value: string,
  ) => {
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

  const handleReset = () => {
    setFormData({
      name: initialData.name || '',
      bio: initialData.bio || '',
    });
    setErrors({});
    setSuccess(false);
    setHasChanges(false);
  };

  return (
    <form onSubmit={handleSubmit} className="space-y-6">
      {success && (
        <Alert>
          <p className="text-sm">프로필이 성공적으로 업데이트되었습니다.</p>
        </Alert>
      )}

      {/* 이름 */}
      <div className="space-y-2">
        <Label htmlFor="name">
          이름 <span className="text-red-500">*</span>
        </Label>
        <Input
          id="name"
          type="text"
          placeholder="홍길동"
          value={formData.name}
          onChange={(e) => handleChange('name', e.target.value)}
          disabled={isLoading}
          className={errors.name ? 'border-red-500' : ''}
        />
        {errors.name && <p className="text-sm text-red-500">{errors.name}</p>}
      </div>

      {/* 소개 */}
      <div className="space-y-2">
        <Label htmlFor="bio">소개</Label>
        <Textarea
          id="bio"
          placeholder="자신을 소개해주세요"
          value={formData.bio}
          onChange={(e) => handleChange('bio', e.target.value)}
          disabled={isLoading}
          rows={4}
          className={errors.bio ? 'border-red-500' : ''}
        />
        <div className="flex items-center justify-between">
          <p className="text-xs text-muted-foreground">
            {errors.bio && <span className="text-red-500">{errors.bio}</span>}
          </p>
          <p className="text-xs text-muted-foreground">
            {formData.bio.length} / 500
          </p>
        </div>
      </div>

      {/* 버튼 */}
      <div className="flex gap-2">
        <Button
          type="submit"
          disabled={isLoading || !hasChanges}
        >
          {isLoading ? (
            <>
              <Loader2 className="mr-2 h-4 w-4 animate-spin" />
              저장 중...
            </>
          ) : (
            '변경사항 저장'
          )}
        </Button>

        {hasChanges && !isLoading && (
          <Button
            type="button"
            variant="outline"
            onClick={handleReset}
          >
            취소
          </Button>
        )}
      </div>
    </form>
  );
}
