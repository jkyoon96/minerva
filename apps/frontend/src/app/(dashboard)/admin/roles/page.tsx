'use client';

import { useState, useEffect } from 'react';
import { RoleInfo, RoleStatistics } from '@/types/admin';
import { UserRole } from '@/types';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { useToast } from '@/hooks/use-toast';
import { getRoles, getRoleStatistics } from '@/lib/api/admin';
import { Shield, GraduationCap, UserCog, User, Users } from 'lucide-react';
import { RoleBadge, getRoleDisplayName, getRoleDescription } from '@/components/admin/role-badge';
import { Progress } from '@/components/ui/progress';
import { Skeleton } from '@/components/ui/skeleton';

/**
 * 역할 관리 페이지 (Admin 전용)
 * - 역할 목록 및 통계 표시
 * - 역할별 사용자 수
 */
export default function AdminRolesPage() {
  const [roles, setRoles] = useState<RoleInfo[]>([]);
  const [statistics, setStatistics] = useState<RoleStatistics | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const { toast } = useToast();

  useEffect(() => {
    fetchRolesData();
  }, []);

  const fetchRolesData = async () => {
    setIsLoading(true);
    try {
      const [rolesData, statsData] = await Promise.all([
        getRoles(),
        getRoleStatistics(),
      ]);
      setRoles(rolesData);
      setStatistics(statsData);
    } catch (error) {
      toast({
        title: '오류',
        description: '역할 정보를 불러오는데 실패했습니다.',
        variant: 'destructive',
      });
    } finally {
      setIsLoading(false);
    }
  };

  const getRoleIcon = (role: UserRole) => {
    switch (role) {
      case 'admin':
        return Shield;
      case 'professor':
        return GraduationCap;
      case 'ta':
        return UserCog;
      case 'student':
        return User;
    }
  };

  const getRolePercentage = (count: number) => {
    if (!statistics || statistics.totalUsers === 0) return 0;
    return Math.round((count / statistics.totalUsers) * 100);
  };

  if (isLoading) {
    return (
      <div className="space-y-6">
        <div>
          <Skeleton className="h-10 w-48 mb-2" />
          <Skeleton className="h-6 w-32" />
        </div>
        <div className="grid gap-6 md:grid-cols-2 lg:grid-cols-4">
          {[1, 2, 3, 4].map((i) => (
            <Skeleton key={i} className="h-40" />
          ))}
        </div>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* 헤더 */}
      <div>
        <h1 className="text-3xl font-bold">역할 관리</h1>
        <p className="text-muted-foreground">
          역할별 사용자 수 및 권한 정보
        </p>
      </div>

      {/* 통계 카드 */}
      {statistics && (
        <div className="grid gap-6 md:grid-cols-2 lg:grid-cols-4">
          <Card>
            <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
              <CardTitle className="text-sm font-medium">전체 사용자</CardTitle>
              <Users className="h-4 w-4 text-muted-foreground" />
            </CardHeader>
            <CardContent>
              <div className="text-2xl font-bold">{statistics.totalUsers}</div>
              <p className="text-xs text-muted-foreground">
                활성: {statistics.activeUsers} / 비활성: {statistics.inactiveUsers}
              </p>
            </CardContent>
          </Card>

          <Card>
            <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
              <CardTitle className="text-sm font-medium">관리자</CardTitle>
              <Shield className="h-4 w-4 text-destructive" />
            </CardHeader>
            <CardContent>
              <div className="text-2xl font-bold">{statistics.byRole.admin}</div>
              <p className="text-xs text-muted-foreground">
                전체의 {getRolePercentage(statistics.byRole.admin)}%
              </p>
            </CardContent>
          </Card>

          <Card>
            <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
              <CardTitle className="text-sm font-medium">교수</CardTitle>
              <GraduationCap className="h-4 w-4 text-blue-500" />
            </CardHeader>
            <CardContent>
              <div className="text-2xl font-bold">{statistics.byRole.professor}</div>
              <p className="text-xs text-muted-foreground">
                전체의 {getRolePercentage(statistics.byRole.professor)}%
              </p>
            </CardContent>
          </Card>

          <Card>
            <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
              <CardTitle className="text-sm font-medium">학생</CardTitle>
              <User className="h-4 w-4 text-muted-foreground" />
            </CardHeader>
            <CardContent>
              <div className="text-2xl font-bold">{statistics.byRole.student}</div>
              <p className="text-xs text-muted-foreground">
                전체의 {getRolePercentage(statistics.byRole.student)}%
              </p>
            </CardContent>
          </Card>
        </div>
      )}

      {/* 역할 목록 */}
      <Card>
        <CardHeader>
          <CardTitle>역할 목록</CardTitle>
          <CardDescription>
            시스템에서 사용 가능한 역할과 권한 정보
          </CardDescription>
        </CardHeader>
        <CardContent>
          <div className="space-y-6">
            {roles.map((role) => {
              const Icon = getRoleIcon(role.role);
              const percentage = getRolePercentage(role.userCount);

              return (
                <div key={role.role} className="space-y-2">
                  <div className="flex items-center justify-between">
                    <div className="flex items-center gap-3">
                      <div className="flex h-10 w-10 items-center justify-center rounded-lg bg-muted">
                        <Icon className="h-5 w-5" />
                      </div>
                      <div>
                        <div className="flex items-center gap-2">
                          <p className="font-medium">{role.displayName}</p>
                          <RoleBadge role={role.role} />
                        </div>
                        <p className="text-sm text-muted-foreground">
                          {role.description}
                        </p>
                      </div>
                    </div>
                    <div className="text-right">
                      <p className="text-2xl font-bold">{role.userCount}</p>
                      <p className="text-xs text-muted-foreground">{percentage}%</p>
                    </div>
                  </div>
                  <Progress value={percentage} className="h-2" />
                </div>
              );
            })}
          </div>
        </CardContent>
      </Card>

      {/* 권한 정보 */}
      <Card>
        <CardHeader>
          <CardTitle>역할별 권한</CardTitle>
          <CardDescription>
            각 역할이 가진 주요 권한 설명
          </CardDescription>
        </CardHeader>
        <CardContent>
          <div className="space-y-4">
            <div className="rounded-lg border p-4">
              <div className="mb-2 flex items-center gap-2">
                <Shield className="h-5 w-5 text-destructive" />
                <h3 className="font-semibold">{getRoleDisplayName('admin')}</h3>
              </div>
              <ul className="ml-7 space-y-1 text-sm text-muted-foreground">
                <li>• 시스템 전체 설정 관리</li>
                <li>• 사용자 역할 변경 및 관리</li>
                <li>• 모든 코스 및 데이터 접근</li>
                <li>• 통계 및 분석 조회</li>
              </ul>
            </div>

            <div className="rounded-lg border p-4">
              <div className="mb-2 flex items-center gap-2">
                <GraduationCap className="h-5 w-5 text-blue-500" />
                <h3 className="font-semibold">{getRoleDisplayName('professor')}</h3>
              </div>
              <ul className="ml-7 space-y-1 text-sm text-muted-foreground">
                <li>• 코스 생성 및 관리</li>
                <li>• 수강생 등록 및 관리</li>
                <li>• 과제 및 평가 관리</li>
                <li>• 실시간 세미나 진행</li>
              </ul>
            </div>

            <div className="rounded-lg border p-4">
              <div className="mb-2 flex items-center gap-2">
                <UserCog className="h-5 w-5 text-green-500" />
                <h3 className="font-semibold">{getRoleDisplayName('ta')}</h3>
              </div>
              <ul className="ml-7 space-y-1 text-sm text-muted-foreground">
                <li>• 코스 운영 지원</li>
                <li>• 과제 채점 및 피드백</li>
                <li>• 실시간 세미나 지원</li>
                <li>• 학생 질문 답변</li>
              </ul>
            </div>

            <div className="rounded-lg border p-4">
              <div className="mb-2 flex items-center gap-2">
                <User className="h-5 w-5 text-muted-foreground" />
                <h3 className="font-semibold">{getRoleDisplayName('student')}</h3>
              </div>
              <ul className="ml-7 space-y-1 text-sm text-muted-foreground">
                <li>• 코스 수강 및 참여</li>
                <li>• 과제 제출</li>
                <li>• 실시간 세미나 참여</li>
                <li>• 질문 및 토론 참여</li>
              </ul>
            </div>
          </div>
        </CardContent>
      </Card>
    </div>
  );
}
