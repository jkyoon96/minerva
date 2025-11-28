/**
 * 코스 설정 페이지
 * 코스 정보 수정, 초대 링크 관리, 수강생 관리
 */

'use client';

import * as React from 'react';
import { use } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { ArrowLeft, Link as LinkIcon, Users, Upload } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { LoadingSpinner } from '@/components/common/LoadingSpinner';
import { useToast } from '@/components/ui/toast';
import { CourseForm } from '@/components/course/course-form';
import { InviteLinkManager } from '@/components/course/invite-link-manager';
import { CsvUpload } from '@/components/enrollment/csv-upload';
import { getCourse, updateCourse } from '@/lib/api/courses';
import { CourseFormData } from '@/types/course';
import Link from 'next/link';
import { useRouter } from 'next/navigation';

interface CourseSettingsPageProps {
  params: Promise<{
    courseId: string;
  }>;
}

export default function CourseSettingsPage({ params }: CourseSettingsPageProps) {
  const { courseId } = use(params);
  const router = useRouter();
  const { toast } = useToast();
  const queryClient = useQueryClient();

  const { data: course, isLoading } = useQuery({
    queryKey: ['course', courseId],
    queryFn: () => getCourse(courseId),
  });

  const updateMutation = useMutation({
    mutationFn: (data: CourseFormData) => updateCourse(courseId, data),
    onSuccess: () => {
      toast({
        title: '코스 업데이트 완료',
        description: '코스 정보가 성공적으로 업데이트되었습니다.',
      });
      queryClient.invalidateQueries({ queryKey: ['course', courseId] });
    },
    onError: (error: any) => {
      toast({
        title: '코스 업데이트 실패',
        description: error.message || '코스 업데이트 중 오류가 발생했습니다.',
        variant: 'destructive',
      });
    },
  });

  if (isLoading) {
    return (
      <div className="flex min-h-[400px] items-center justify-center">
        <LoadingSpinner />
      </div>
    );
  }

  if (!course) {
    return (
      <div className="text-center">
        <p className="text-muted-foreground">코스를 찾을 수 없습니다.</p>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* 헤더 */}
      <div>
        <Button variant="ghost" size="sm" asChild className="mb-4">
          <Link href={`/courses/${courseId}`}>
            <ArrowLeft className="mr-2 h-4 w-4" />
            코스 상세로
          </Link>
        </Button>
        <h1 className="text-3xl font-bold">코스 설정</h1>
        <p className="text-muted-foreground">{course.title}</p>
      </div>

      {/* 탭 */}
      <Tabs defaultValue="general" className="space-y-4">
        <TabsList>
          <TabsTrigger value="general">기본 정보</TabsTrigger>
          <TabsTrigger value="invites">
            <LinkIcon className="mr-2 h-4 w-4" />
            초대 링크
          </TabsTrigger>
          <TabsTrigger value="enrollments">
            <Users className="mr-2 h-4 w-4" />
            수강생 관리
          </TabsTrigger>
        </TabsList>

        {/* 기본 정보 탭 */}
        <TabsContent value="general">
          <Card>
            <CardHeader>
              <CardTitle>기본 정보</CardTitle>
              <CardDescription>코스의 기본 정보를 수정합니다</CardDescription>
            </CardHeader>
            <CardContent>
              <CourseForm
                initialData={{
                  title: course.title,
                  code: course.code,
                  semester: course.semester,
                  description: course.description,
                }}
                onSubmit={(data) => updateMutation.mutate(data)}
                onCancel={() => router.back()}
                submitLabel="저장하기"
                isSubmitting={updateMutation.isPending}
              />
            </CardContent>
          </Card>
        </TabsContent>

        {/* 초대 링크 탭 */}
        <TabsContent value="invites">
          <Card>
            <CardHeader>
              <CardTitle>초대 링크 관리</CardTitle>
              <CardDescription>학생 및 조교 초대 링크를 생성하고 관리합니다</CardDescription>
            </CardHeader>
            <CardContent>
              <InviteLinkManager courseId={courseId} />
            </CardContent>
          </Card>
        </TabsContent>

        {/* 수강생 관리 탭 */}
        <TabsContent value="enrollments">
          <div className="space-y-4">
            <Card>
              <CardHeader>
                <CardTitle>CSV 파일로 수강생 등록</CardTitle>
                <CardDescription>CSV 파일을 업로드하여 여러 학생을 한 번에 등록합니다</CardDescription>
              </CardHeader>
              <CardContent>
                <CsvUpload courseId={courseId} />
              </CardContent>
            </Card>

            <Card>
              <CardHeader>
                <CardTitle>수강생 목록</CardTitle>
                <CardDescription>현재 등록된 수강생을 확인하고 관리합니다</CardDescription>
              </CardHeader>
              <CardContent>
                <Button asChild>
                  <Link href={`/courses/${courseId}/enrollments`}>
                    <Users className="mr-2 h-4 w-4" />
                    수강생 목록 보기
                  </Link>
                </Button>
              </CardContent>
            </Card>
          </div>
        </TabsContent>
      </Tabs>
    </div>
  );
}
