/**
 * 코스 생성/수정 폼 컴포넌트
 */

'use client';

import * as React from 'react';
import { useForm } from 'react-hook-form';
import { Label } from '@/components/ui/label';
import { Input } from '@/components/ui/input';
import { Textarea } from '@/components/ui/textarea';
import { Button } from '@/components/ui/button';
import { CourseFormData } from '@/types/course';

interface CourseFormProps {
  initialData?: Partial<CourseFormData>;
  onSubmit: (data: CourseFormData) => void;
  onCancel?: () => void;
  submitLabel?: string;
  isSubmitting?: boolean;
}

export function CourseForm({
  initialData,
  onSubmit,
  onCancel,
  submitLabel = '생성하기',
  isSubmitting = false,
}: CourseFormProps) {
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<CourseFormData>({
    defaultValues: initialData || {
      title: '',
      code: '',
      semester: '',
      description: '',
    },
  });

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="space-y-6">
      <div className="grid gap-4">
        <div className="grid gap-2">
          <Label htmlFor="title">
            코스 제목 <span className="text-destructive">*</span>
          </Label>
          <Input
            id="title"
            placeholder="예: 데이터 구조와 알고리즘"
            {...register('title', {
              required: '코스 제목을 입력해주세요',
            })}
            disabled={isSubmitting}
          />
          {errors.title && <p className="text-sm text-destructive">{errors.title.message}</p>}
        </div>

        <div className="grid gap-2">
          <Label htmlFor="code">
            코스 코드 <span className="text-destructive">*</span>
          </Label>
          <Input
            id="code"
            placeholder="예: CS101"
            {...register('code', {
              required: '코스 코드를 입력해주세요',
            })}
            disabled={isSubmitting}
          />
          {errors.code && <p className="text-sm text-destructive">{errors.code.message}</p>}
        </div>

        <div className="grid gap-2">
          <Label htmlFor="semester">
            학기 <span className="text-destructive">*</span>
          </Label>
          <Input
            id="semester"
            placeholder="예: 2024-1"
            {...register('semester', {
              required: '학기를 입력해주세요',
            })}
            disabled={isSubmitting}
          />
          {errors.semester && <p className="text-sm text-destructive">{errors.semester.message}</p>}
        </div>

        <div className="grid gap-2">
          <Label htmlFor="description">코스 설명</Label>
          <Textarea
            id="description"
            placeholder="코스에 대한 간단한 설명을 입력하세요"
            rows={4}
            {...register('description')}
            disabled={isSubmitting}
          />
        </div>
      </div>

      <div className="flex gap-2">
        {onCancel && (
          <Button type="button" variant="outline" onClick={onCancel} disabled={isSubmitting}>
            취소
          </Button>
        )}
        <Button type="submit" disabled={isSubmitting}>
          {isSubmitting ? '처리 중...' : submitLabel}
        </Button>
      </div>
    </form>
  );
}
