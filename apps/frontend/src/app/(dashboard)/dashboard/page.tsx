import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { BookOpen, Calendar, Users, TrendingUp } from 'lucide-react';

export default function DashboardPage() {
  const stats = [
    {
      title: '수강 중인 코스',
      value: '4',
      icon: BookOpen,
      description: '이번 학기',
    },
    {
      title: '다가오는 세션',
      value: '2',
      icon: Calendar,
      description: '오늘',
    },
    {
      title: '참여도 점수',
      value: '85%',
      icon: TrendingUp,
      description: '평균',
    },
    {
      title: '제출 대기',
      value: '3',
      icon: Users,
      description: '과제',
    },
  ];

  return (
    <div className="space-y-8">
      <div>
        <h1 className="text-3xl font-bold">대시보드</h1>
        <p className="text-muted-foreground">학습 현황을 한눈에 확인하세요</p>
      </div>

      <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-4">
        {stats.map((stat) => {
          const Icon = stat.icon;
          return (
            <Card key={stat.title}>
              <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                <CardTitle className="text-sm font-medium">{stat.title}</CardTitle>
                <Icon className="h-4 w-4 text-muted-foreground" />
              </CardHeader>
              <CardContent>
                <div className="text-2xl font-bold">{stat.value}</div>
                <p className="text-xs text-muted-foreground">{stat.description}</p>
              </CardContent>
            </Card>
          );
        })}
      </div>

      <div className="grid gap-4 md:grid-cols-2">
        <Card>
          <CardHeader>
            <CardTitle>최근 활동</CardTitle>
            <CardDescription>최근 7일간의 학습 활동</CardDescription>
          </CardHeader>
          <CardContent>
            <p className="text-sm text-muted-foreground">활동 내역이 여기에 표시됩니다.</p>
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <CardTitle>다가오는 일정</CardTitle>
            <CardDescription>예정된 세션 및 과제</CardDescription>
          </CardHeader>
          <CardContent>
            <p className="text-sm text-muted-foreground">일정이 여기에 표시됩니다.</p>
          </CardContent>
        </Card>
      </div>
    </div>
  );
}
