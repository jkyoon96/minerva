'use client';

import { useState, useEffect } from 'react';
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { Alert } from '@/components/ui/alert';
import { Shield, ShieldCheck, Key, Loader2 } from 'lucide-react';
import { getTwoFactorStatus } from '@/lib/api/two-factor';
import { TwoFactorStatusResponse } from '@/types/two-factor';
import { format } from 'date-fns';
import { ko } from 'date-fns/locale';

interface TwoFactorStatusProps {
  onSetup: () => void;
  onDisable: () => void;
  onViewBackupCodes: () => void;
}

/**
 * 2FA 상태 카드
 * - 활성화/비활성화 상태 표시
 * - 백업 코드 수 표시
 * - 설정/비활성화/백업 코드 관리 버튼
 */
export function TwoFactorStatus({
  onSetup,
  onDisable,
  onViewBackupCodes,
}: TwoFactorStatusProps) {
  const [status, setStatus] = useState<TwoFactorStatusResponse | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const fetchStatus = async () => {
    setIsLoading(true);
    setError(null);
    try {
      const data = await getTwoFactorStatus();
      setStatus(data);
    } catch (err: any) {
      setError(err.message || '2FA 상태를 불러오는데 실패했습니다.');
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    fetchStatus();
  }, []);

  if (isLoading) {
    return (
      <Card>
        <CardContent className="flex items-center justify-center py-12">
          <Loader2 className="h-6 w-6 animate-spin text-muted-foreground" />
        </CardContent>
      </Card>
    );
  }

  if (error) {
    return (
      <Card>
        <CardContent className="py-6">
          <Alert variant="destructive">
            <p className="text-sm">{error}</p>
          </Alert>
          <Button onClick={fetchStatus} variant="outline" className="mt-4 w-full">
            다시 시도
          </Button>
        </CardContent>
      </Card>
    );
  }

  return (
    <Card>
      <CardHeader>
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-3">
            {status?.enabled ? (
              <ShieldCheck className="h-5 w-5 text-green-600" />
            ) : (
              <Shield className="h-5 w-5 text-muted-foreground" />
            )}
            <div>
              <CardTitle>2단계 인증 (2FA)</CardTitle>
              <CardDescription>
                앱 기반 인증 코드로 계정을 보호하세요
              </CardDescription>
            </div>
          </div>
          <Badge variant={status?.enabled ? 'default' : 'secondary'}>
            {status?.enabled ? '활성화됨' : '비활성화됨'}
          </Badge>
        </div>
      </CardHeader>

      <CardContent className="space-y-4">
        {status?.enabled ? (
          <>
            {/* 활성화 정보 */}
            <div className="rounded-lg bg-muted/50 p-4 space-y-2">
              <div className="flex items-center justify-between text-sm">
                <span className="text-muted-foreground">활성화 일시</span>
                <span className="font-medium">
                  {status.enabledAt
                    ? format(new Date(status.enabledAt), 'PPP', { locale: ko })
                    : '-'}
                </span>
              </div>
              <div className="flex items-center justify-between text-sm">
                <span className="text-muted-foreground">남은 백업 코드</span>
                <span className="font-medium">
                  {status.backupCodesRemaining}개
                </span>
              </div>
            </div>

            {/* 백업 코드 경고 */}
            {status.backupCodesRemaining <= 2 && (
              <Alert variant="destructive">
                <Key className="h-4 w-4" />
                <div>
                  <p className="text-sm font-medium">백업 코드가 부족합니다</p>
                  <p className="text-sm text-muted-foreground mt-1">
                    백업 코드를 재생성하여 계정을 안전하게 보호하세요
                  </p>
                </div>
              </Alert>
            )}

            {/* 관리 버튼 */}
            <div className="flex flex-col gap-2">
              <Button
                onClick={onViewBackupCodes}
                variant="outline"
                className="w-full"
              >
                <Key className="mr-2 h-4 w-4" />
                백업 코드 관리
              </Button>
              <Button onClick={onDisable} variant="outline" className="w-full">
                2FA 비활성화
              </Button>
            </div>
          </>
        ) : (
          <>
            {/* 비활성화 안내 */}
            <div className="rounded-lg border border-dashed border-muted-foreground/25 p-6 text-center">
              <Shield className="mx-auto h-12 w-12 text-muted-foreground mb-3" />
              <h3 className="font-medium mb-2">2단계 인증이 비활성화되어 있습니다</h3>
              <p className="text-sm text-muted-foreground mb-4">
                인증 앱을 사용하여 계정에 추가 보안 계층을 추가하세요.
                Google Authenticator, Authy 등을 사용할 수 있습니다.
              </p>
              <Button onClick={onSetup} className="w-full">
                <ShieldCheck className="mr-2 h-4 w-4" />
                2FA 설정하기
              </Button>
            </div>
          </>
        )}
      </CardContent>
    </Card>
  );
}
