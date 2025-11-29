'use client';

import Link from 'next/link';
import { usePathname } from 'next/navigation';
import { cn } from '@/lib/utils';
import { Users, Shield, BarChart3, Settings, ChevronLeft } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Separator } from '@/components/ui/separator';

const adminSidebarItems = [
  {
    title: '사용자 관리',
    href: '/admin/users',
    icon: Users,
    description: '사용자 목록 및 역할 관리',
  },
  {
    title: '역할 관리',
    href: '/admin/roles',
    icon: Shield,
    description: '역할별 권한 설정',
  },
  {
    title: '통계',
    href: '/admin/statistics',
    icon: BarChart3,
    description: '시스템 통계 및 분석',
  },
  {
    title: '시스템 설정',
    href: '/admin/settings',
    icon: Settings,
    description: '시스템 전체 설정',
  },
];

/**
 * Admin 전용 사이드바 컴포넌트
 * - Admin 페이지 네비게이션
 * - 대시보드로 돌아가기
 */
export function AdminSidebar() {
  const pathname = usePathname();

  return (
    <aside className="fixed left-0 top-16 h-[calc(100vh-4rem)] w-64 border-r bg-background">
      <div className="flex h-full flex-col">
        {/* 헤더 */}
        <div className="p-4">
          <h2 className="text-lg font-semibold">관리자 패널</h2>
          <p className="text-sm text-muted-foreground">시스템 관리 및 설정</p>
        </div>

        <Separator />

        {/* 네비게이션 */}
        <nav className="flex-1 space-y-1 p-4">
          {adminSidebarItems.map((item) => {
            const Icon = item.icon;
            const isActive = pathname === item.href;

            return (
              <Link
                key={item.href}
                href={item.href}
                className={cn(
                  'flex flex-col gap-1 rounded-lg px-3 py-3 transition-colors',
                  isActive
                    ? 'bg-primary text-primary-foreground'
                    : 'hover:bg-accent hover:text-accent-foreground'
                )}
              >
                <div className="flex items-center gap-3">
                  <Icon className="h-4 w-4" />
                  <span className="text-sm font-medium">{item.title}</span>
                </div>
                <p
                  className={cn(
                    'ml-7 text-xs',
                    isActive ? 'text-primary-foreground/80' : 'text-muted-foreground'
                  )}
                >
                  {item.description}
                </p>
              </Link>
            );
          })}
        </nav>

        <Separator />

        {/* 푸터 - 대시보드로 돌아가기 */}
        <div className="p-4">
          <Button variant="outline" className="w-full justify-start" asChild>
            <Link href="/dashboard">
              <ChevronLeft className="mr-2 h-4 w-4" />
              대시보드로 돌아가기
            </Link>
          </Button>
        </div>
      </div>
    </aside>
  );
}
