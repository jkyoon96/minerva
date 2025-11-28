import Link from 'next/link';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { GraduationCap, Video, Users, BarChart } from 'lucide-react';

export default function HomePage() {
  const features = [
    {
      icon: Video,
      title: '실시간 화상 세미나',
      description: '최대 50명이 참여하는 HD 화상 강의와 화면 공유 기능',
    },
    {
      icon: Users,
      title: '액티브 러닝 도구',
      description: '투표, 퀴즈, 분반 토론으로 학생 참여도 향상',
    },
    {
      icon: BarChart,
      title: '학습 분석',
      description: '실시간 참여도 측정 및 AI 기반 학습 분석',
    },
  ];

  return (
    <div className="flex min-h-screen flex-col">
      {/* 네비게이션 */}
      <nav className="border-b">
        <div className="container flex h-16 items-center justify-between px-4">
          <div className="flex items-center space-x-2">
            <GraduationCap className="h-6 w-6" />
            <span className="text-xl font-bold">EduForum</span>
          </div>
          <div className="flex gap-4">
            <Link href="/login">
              <Button variant="ghost">로그인</Button>
            </Link>
            <Link href="/register">
              <Button>시작하기</Button>
            </Link>
          </div>
        </div>
      </nav>

      {/* 히어로 섹션 */}
      <section className="flex flex-1 items-center justify-center bg-gradient-to-b from-background to-secondary/20 py-20">
        <div className="container px-4 text-center">
          <h1 className="mb-6 text-5xl font-bold tracking-tight">
            대화형 온라인 학습 플랫폼
          </h1>
          <p className="mb-8 text-xl text-muted-foreground">
            미네르바 대학의 Active Learning Forum을 참고한
            <br />
            차세대 교육 플랫폼
          </p>
          <div className="flex justify-center gap-4">
            <Link href="/register">
              <Button size="lg">무료로 시작하기</Button>
            </Link>
            <Link href="/features">
              <Button size="lg" variant="outline">
                자세히 알아보기
              </Button>
            </Link>
          </div>
        </div>
      </section>

      {/* 기능 섹션 */}
      <section className="py-20">
        <div className="container px-4">
          <h2 className="mb-12 text-center text-3xl font-bold">주요 기능</h2>
          <div className="grid gap-6 md:grid-cols-3">
            {features.map((feature) => {
              const Icon = feature.icon;
              return (
                <Card key={feature.title}>
                  <CardHeader>
                    <div className="mb-4 flex h-12 w-12 items-center justify-center rounded-lg bg-primary text-primary-foreground">
                      <Icon className="h-6 w-6" />
                    </div>
                    <CardTitle>{feature.title}</CardTitle>
                    <CardDescription>{feature.description}</CardDescription>
                  </CardHeader>
                </Card>
              );
            })}
          </div>
        </div>
      </section>

      {/* 푸터 */}
      <footer className="border-t py-8">
        <div className="container px-4 text-center text-sm text-muted-foreground">
          © 2025 EduForum. All rights reserved.
        </div>
      </footer>
    </div>
  );
}
