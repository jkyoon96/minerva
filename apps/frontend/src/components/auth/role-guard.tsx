'use client';

import { useEffect, ReactNode } from 'react';
import { useRouter } from 'next/navigation';
import { useAuthStore } from '@/stores/authStore';
import { UserRole } from '@/types';
import { Loader2 } from 'lucide-react';

interface RoleGuardProps {
  children: ReactNode;
  allowedRoles: UserRole[];
  fallbackPath?: string;
}

/**
 * 역할 기반 라우팅 가드 컴포넌트
 * - 지정된 역할을 가진 사용자만 접근 가능
 * - 권한이 없으면 /403 페이지로 리다이렉트
 * - 로그인하지 않았으면 /login 페이지로 리다이렉트
 */
export function RoleGuard({
  children,
  allowedRoles,
  fallbackPath = '/403',
}: RoleGuardProps) {
  const router = useRouter();
  const { user, isAuthenticated, isLoading, fetchProfile } = useAuthStore();

  useEffect(() => {
    // 초기 로드 시 프로필 확인
    const checkAuth = async () => {
      const token = localStorage.getItem('accessToken');

      // 토큰이 있지만 사용자 정보가 없으면 프로필 조회
      if (token && !isAuthenticated && !isLoading) {
        try {
          await fetchProfile();
        } catch (error) {
          console.error('Failed to fetch profile:', error);
        }
      }
    };

    checkAuth();
  }, []);

  useEffect(() => {
    // 로딩이 끝난 후 인증 및 권한 확인
    if (!isLoading) {
      // 인증되지 않은 경우 로그인 페이지로
      if (!isAuthenticated) {
        router.push('/login');
        return;
      }

      // 사용자 역할이 허용된 역할에 포함되지 않는 경우 403 페이지로
      if (user && !allowedRoles.includes(user.role)) {
        router.push(fallbackPath);
        return;
      }
    }
  }, [isAuthenticated, isLoading, user, allowedRoles, router, fallbackPath]);

  // 로딩 중 표시
  if (isLoading) {
    return (
      <div className="flex min-h-screen items-center justify-center">
        <Loader2 className="h-8 w-8 animate-spin text-primary" />
      </div>
    );
  }

  // 인증되지 않았거나 권한이 없으면 null 반환
  if (!isAuthenticated || (user && !allowedRoles.includes(user.role))) {
    return null;
  }

  return <>{children}</>;
}

/**
 * HOC 방식으로 사용할 수 있는 RoleGuard
 */
export function withRoleGuard<P extends object>(
  Component: React.ComponentType<P>,
  allowedRoles: UserRole[],
  fallbackPath?: string
) {
  return function RoleGuardedComponent(props: P) {
    return (
      <RoleGuard allowedRoles={allowedRoles} fallbackPath={fallbackPath}>
        <Component {...props} />
      </RoleGuard>
    );
  };
}

/**
 * Admin 전용 가드 (편의 컴포넌트)
 */
export function AdminGuard({ children }: { children: ReactNode }) {
  return <RoleGuard allowedRoles={['admin']}>{children}</RoleGuard>;
}

/**
 * Professor 또는 Admin 가드
 */
export function ProfessorGuard({ children }: { children: ReactNode }) {
  return <RoleGuard allowedRoles={['admin', 'professor']}>{children}</RoleGuard>;
}

/**
 * TA 또는 상위 권한 가드
 */
export function TAGuard({ children }: { children: ReactNode }) {
  return (
    <RoleGuard allowedRoles={['admin', 'professor', 'ta']}>{children}</RoleGuard>
  );
}
