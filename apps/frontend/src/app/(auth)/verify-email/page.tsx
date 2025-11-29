/**
 * 이메일 인증 완료 페이지
 * URL 파라미터의 토큰으로 이메일 인증 처리
 */

'use client';

import { useEffect, useState } from 'react';
import { useSearchParams, useRouter } from 'next/navigation';
import Link from 'next/link';
import {
  Card,
  CardContent,
  CardDescription,
  CardFooter,
  CardHeader,
  CardTitle,
} from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Alert } from '@/components/ui/alert';
import { LoadingSpinner } from '@/components/common/LoadingSpinner';
import { CheckCircle2, XCircle, Mail } from 'lucide-react';
import { verifyEmail } from '@/lib/api/auth';

type VerificationStatus = 'verifying' | 'success' | 'error';

export default function VerifyEmailPage() {
  const searchParams = useSearchParams();
  const router = useRouter();
  const [status, setStatus] = useState<VerificationStatus>('verifying');
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const token = searchParams.get('token');

    if (!token) {
      setStatus('error');
      setError('인증 토큰이 없습니다. 이메일의 링크를 다시 확인해주세요.');
      return;
    }

    // 이메일 인증 처리
    const verify = async () => {
      try {
        await verifyEmail(token);
        setStatus('success');
      } catch (err) {
        setStatus('error');
        if (err instanceof Error) {
          setError(err.message);
        } else {
          setError('이메일 인증에 실패했습니다. 토큰이 만료되었거나 유효하지 않습니다.');
        }
      }
    };

    verify();
  }, [searchParams]);

  const handleGoToLogin = () => {
    router.push('/login');
  };

  return (
    <Card className="w-full max-w-md">
      <CardHeader className="text-center">
        {status === 'verifying' && (
          <>
            <div className="mx-auto mb-4 flex h-16 w-16 items-center justify-center rounded-full bg-primary/10">
              <Mail className="h-8 w-8 text-primary" />
            </div>
            <CardTitle>이메일 인증 중...</CardTitle>
            <CardDescription>잠시만 기다려주세요</CardDescription>
          </>
        )}

        {status === 'success' && (
          <>
            <div className="mx-auto mb-4 flex h-16 w-16 items-center justify-center rounded-full bg-green-100">
              <CheckCircle2 className="h-8 w-8 text-green-600" />
            </div>
            <CardTitle>이메일 인증 완료</CardTitle>
            <CardDescription>
              이메일 인증이 성공적으로 완료되었습니다
            </CardDescription>
          </>
        )}

        {status === 'error' && (
          <>
            <div className="mx-auto mb-4 flex h-16 w-16 items-center justify-center rounded-full bg-red-100">
              <XCircle className="h-8 w-8 text-red-600" />
            </div>
            <CardTitle>이메일 인증 실패</CardTitle>
            <CardDescription>
              이메일 인증 중 문제가 발생했습니다
            </CardDescription>
          </>
        )}
      </CardHeader>

      <CardContent>
        {status === 'verifying' && (
          <div className="flex justify-center py-8">
            <LoadingSpinner />
          </div>
        )}

        {status === 'success' && (
          <div className="space-y-4 text-center">
            <p className="text-sm text-muted-foreground">
              이제 EduForum의 모든 기능을 사용하실 수 있습니다.
            </p>
            <p className="text-sm text-muted-foreground">
              로그인하여 시작해보세요.
            </p>
          </div>
        )}

        {status === 'error' && error && (
          <Alert variant="destructive">
            <p className="text-sm">{error}</p>
          </Alert>
        )}
      </CardContent>

      <CardFooter className="flex flex-col gap-2">
        {status === 'success' && (
          <Button onClick={handleGoToLogin} className="w-full">
            로그인하기
          </Button>
        )}

        {status === 'error' && (
          <>
            <Button onClick={handleGoToLogin} className="w-full">
              로그인 페이지로 이동
            </Button>
            <Button variant="outline" asChild className="w-full">
              <Link href="/register">다시 회원가입</Link>
            </Button>
          </>
        )}

        {status === 'verifying' && (
          <Button variant="outline" asChild className="w-full" disabled>
            <Link href="/login">로그인</Link>
          </Button>
        )}
      </CardFooter>
    </Card>
  );
}
