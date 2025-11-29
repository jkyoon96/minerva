'use client';

import { useState, useRef, useEffect } from 'react';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Alert } from '@/components/ui/alert';
import { Loader2 } from 'lucide-react';

interface Verify2FAFormProps {
  onVerify: (code: string) => Promise<void>;
  onSwitchToBackup?: () => void;
  isLoading?: boolean;
  error?: string | null;
  showBackupOption?: boolean;
}

/**
 * 6자리 TOTP 코드 입력 폼
 * - 6개의 개별 숫자 입력 박스
 * - 자동 포커스 이동
 * - 붙여넣기 지원
 */
export function Verify2FAForm({
  onVerify,
  onSwitchToBackup,
  isLoading = false,
  error,
  showBackupOption = true,
}: Verify2FAFormProps) {
  const [code, setCode] = useState<string[]>(Array(6).fill(''));
  const inputRefs = useRef<(HTMLInputElement | null)[]>([]);

  // 첫 번째 입력 필드에 포커스
  useEffect(() => {
    inputRefs.current[0]?.focus();
  }, []);

  const handleChange = (index: number, value: string) => {
    // 숫자만 허용
    if (value && !/^\d$/.test(value)) {
      return;
    }

    const newCode = [...code];
    newCode[index] = value;
    setCode(newCode);

    // 자동 포커스 이동
    if (value && index < 5) {
      inputRefs.current[index + 1]?.focus();
    }

    // 6자리 완성 시 자동 제출
    if (value && index === 5) {
      const fullCode = newCode.join('');
      if (fullCode.length === 6) {
        handleSubmit(fullCode);
      }
    }
  };

  const handleKeyDown = (index: number, e: React.KeyboardEvent) => {
    if (e.key === 'Backspace' && !code[index] && index > 0) {
      // 빈 필드에서 백스페이스 시 이전 필드로 이동
      inputRefs.current[index - 1]?.focus();
    }
  };

  const handlePaste = (e: React.ClipboardEvent) => {
    e.preventDefault();
    const pastedData = e.clipboardData.getData('text').trim();

    // 6자리 숫자인지 확인
    if (/^\d{6}$/.test(pastedData)) {
      const newCode = pastedData.split('');
      setCode(newCode);

      // 마지막 필드에 포커스
      inputRefs.current[5]?.focus();

      // 자동 제출
      handleSubmit(pastedData);
    }
  };

  const handleSubmit = async (fullCode?: string) => {
    const codeToSubmit = fullCode || code.join('');

    if (codeToSubmit.length !== 6) {
      return;
    }

    await onVerify(codeToSubmit);
  };

  const handleReset = () => {
    setCode(Array(6).fill(''));
    inputRefs.current[0]?.focus();
  };

  return (
    <div className="space-y-4">
      {error && (
        <Alert variant="destructive">
          <p className="text-sm">{error}</p>
        </Alert>
      )}

      <div className="space-y-2">
        <Label>인증 코드</Label>
        <p className="text-sm text-muted-foreground">
          인증 앱에 표시된 6자리 코드를 입력하세요
        </p>

        {/* 6자리 코드 입력 */}
        <div className="flex justify-center gap-2" onPaste={handlePaste}>
          {code.map((digit, index) => (
            <Input
              key={index}
              ref={(el) => (inputRefs.current[index] = el)}
              type="text"
              inputMode="numeric"
              maxLength={1}
              value={digit}
              onChange={(e) => handleChange(index, e.target.value)}
              onKeyDown={(e) => handleKeyDown(index, e)}
              disabled={isLoading}
              className="w-12 h-12 text-center text-lg font-semibold"
              aria-label={`코드 ${index + 1}번째 자리`}
            />
          ))}
        </div>
      </div>

      <div className="flex flex-col gap-2">
        <Button
          onClick={() => handleSubmit()}
          disabled={isLoading || code.join('').length !== 6}
          className="w-full"
        >
          {isLoading ? (
            <>
              <Loader2 className="mr-2 h-4 w-4 animate-spin" />
              인증 중...
            </>
          ) : (
            '인증'
          )}
        </Button>

        <Button
          type="button"
          variant="outline"
          onClick={handleReset}
          disabled={isLoading}
          className="w-full"
        >
          초기화
        </Button>

        {showBackupOption && onSwitchToBackup && (
          <Button
            type="button"
            variant="ghost"
            onClick={onSwitchToBackup}
            disabled={isLoading}
            className="w-full"
          >
            백업 코드로 인증
          </Button>
        )}
      </div>
    </div>
  );
}
