'use client';

import { useRouter } from 'next/navigation';
import { Button } from '@/components/ui/button';
import { Card, CardContent } from '@/components/ui/card';
import { ShieldX, Home, ArrowLeft } from 'lucide-react';
import { useAuthStore } from '@/stores/authStore';

/**
 * 403 Forbidden 페이지
 * - 권한 없음 안내
 * - 대시보드로 돌아가기 버튼
 * - 이전 페이지로 돌아가기 버튼
 */
export default function ForbiddenPage() {
  const router = useRouter();
  const { user } = useAuthStore();

  const handleGoBack = () => {
    router.back();
  };

  const handleGoHome = () => {
    router.push('/dashboard');
  };

  return (
    <div className="flex min-h-screen items-center justify-center bg-background p-4">
      <Card className="w-full max-w-md">
        <CardContent className="pt-6">
          <div className="flex flex-col items-center text-center">
            {/* 아이콘 */}
            <div className="mb-6 flex h-20 w-20 items-center justify-center rounded-full bg-destructive/10">
              <ShieldX className="h-10 w-10 text-destructive" />
            </div>

            {/* 에러 코드 */}
            <div className="mb-4">
              <h1 className="text-6xl font-bold text-destructive">403</h1>
              <h2 className="mt-2 text-2xl font-semibold">접근 권한이 없습니다</h2>
            </div>

            {/* 설명 */}
            <p className="mb-8 text-muted-foreground">
              이 페이지에 접근할 권한이 없습니다.
              <br />
              {user ? (
                <>
                  현재 계정({user.email})으로는 이 기능을 사용할 수 없습니다.
                  <br />
                  관리자에게 문의하세요.
                </>
              ) : (
                '로그인 후 다시 시도하세요.'
              )}
            </p>

            {/* 버튼 */}
            <div className="flex w-full flex-col gap-3 sm:flex-row">
              <Button
                variant="outline"
                className="flex-1"
                onClick={handleGoBack}
              >
                <ArrowLeft className="mr-2 h-4 w-4" />
                이전 페이지
              </Button>
              <Button
                className="flex-1"
                onClick={handleGoHome}
              >
                <Home className="mr-2 h-4 w-4" />
                대시보드로 돌아가기
              </Button>
            </div>

            {/* 추가 정보 */}
            <div className="mt-8 rounded-lg bg-muted p-4 text-left">
              <h3 className="mb-2 font-semibold">이 페이지가 계속 표시되는 경우:</h3>
              <ul className="space-y-1 text-sm text-muted-foreground">
                <li>• 필요한 권한이 있는지 관리자에게 확인하세요</li>
                <li>• 다른 계정으로 로그인해 보세요</li>
                <li>• 페이지 URL이 올바른지 확인하세요</li>
                <li>• 기술 지원이 필요하면 help@eduforum.com으로 문의하세요</li>
              </ul>
            </div>
          </div>
        </CardContent>
      </Card>
    </div>
  );
}
