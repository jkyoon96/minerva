/**
 * 코스 생성 페이지
 */

'use client';

import * as React from 'react';
import { useRouter } from 'next/navigation';
import { useMutation } from '@tanstack/react-query';
import { ArrowLeft } from 'lucide-react';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { useToast } from '@/components/ui/toast';
import { CourseForm } from '@/components/course/course-form';
import { createCourse } from '@/lib/api/courses';
import { CourseFormData } from '@/types/course';
import Link from 'next/link';

export default function NewCoursePage() {
  const router = useRouter();
  const { toast } = useToast();

  const createMutation = useMutation({
    mutationFn: createCourse,
    onSuccess: (course) => {
      toast({
        title: '코스 생성 완료',
        description: '새로운 코스가 성공적으로 생성되었습니다.',
      });
      router.push(`/courses/${course.id}`);
    },
    onError: (error: any) => {
      toast({
        title: '코스 생성 실패',
        description: error.message || '코스 생성 중 오류가 발생했습니다.',
        variant: 'destructive',
      });
    },
  });

  const handleSubmit = (data: CourseFormData) => {
    createMutation.mutate(data);
  };

  return (
    <div className="mx-auto max-w-2xl space-y-6">
      {/* 헤더 */}
      <div>
        <Button variant="ghost" size="sm" asChild className="mb-4">
          <Link href="/professor/courses">
            <ArrowLeft className="mr-2 h-4 w-4" />
            코스 목록으로
          </Link>
        </Button>
        <h1 className="text-3xl font-bold">새 코스 만들기</h1>
        <p className="text-muted-foreground">새로운 코스를 생성하고 학생들을 초대하세요</p>
      </div>

      {/* 폼 카드 */}
      <Card>
        <CardHeader>
          <CardTitle>코스 정보</CardTitle>
          <CardDescription>코스의 기본 정보를 입력하세요</CardDescription>
        </CardHeader>
        <CardContent>
          <CourseForm
            onSubmit={handleSubmit}
            onCancel={() => router.back()}
            submitLabel="코스 생성"
            isSubmitting={createMutation.isPending}
          />
        </CardContent>
      </Card>
    </div>
  );
}
