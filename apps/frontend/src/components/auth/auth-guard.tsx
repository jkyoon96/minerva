'use client';

import { useEffect, ReactNode } from 'react';
import { useRouter } from 'next/navigation';
import { useAuthStore } from '@/stores/authStore';
import { Loader2 } from 'lucide-react';

interface AuthGuardProps {
  children: ReactNode;
  redirectTo?: string;
  requireAuth?: boolean;
}

/**
 * 인증 가드 컴포넌트
 * - requireAuth가 true일 때: 인증된 사용자만 접근 가능
 * - requireAuth가 false일 때: 비인증 사용자만 접근 가능 (로그인/회원가입 페이지용)
 */
export function AuthGuard({
  children,
  redirectTo = '/login',
  requireAuth = true,
}: AuthGuardProps) {
  const router = useRouter();
  const { isAuthenticated, isLoading, fetchProfile } = useAuthStore();

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
    // 로딩이 끝난 후 인증 상태 확인
    if (!isLoading) {
      if (requireAuth && !isAuthenticated) {
        // 인증이 필요한데 인증되지 않은 경우
        router.push(redirectTo);
      } else if (!requireAuth && isAuthenticated) {
        // 비인증 페이지인데 인증된 경우 (로그인/회원가입 페이지)
        router.push('/dashboard');
      }
    }
  }, [isAuthenticated, isLoading, requireAuth, router, redirectTo]);

  // 로딩 중이거나 리다이렉트가 필요한 경우 로딩 표시
  if (isLoading) {
    return (
      <div className="flex min-h-screen items-center justify-center">
        <Loader2 className="h-8 w-8 animate-spin text-primary" />
      </div>
    );
  }

  // 인증 상태가 요구사항과 맞지 않으면 null 반환
  if (requireAuth && !isAuthenticated) {
    return null;
  }

  if (!requireAuth && isAuthenticated) {
    return null;
  }

  return <>{children}</>;
}

/**
 * HOC 방식으로 사용할 수 있는 AuthGuard
 */
export function withAuthGuard<P extends object>(
  Component: React.ComponentType<P>,
  options: Omit<AuthGuardProps, 'children'> = {}
) {
  return function AuthGuardedComponent(props: P) {
    return (
      <AuthGuard {...options}>
        <Component {...props} />
      </AuthGuard>
    );
  };
}
