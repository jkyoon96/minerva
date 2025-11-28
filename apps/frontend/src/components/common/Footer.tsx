import React from 'react';
import Link from 'next/link';
import { cn } from '@/lib/utils';

interface FooterProps {
  className?: string;
}

/**
 * 애플리케이션 푸터 컴포넌트
 */
export function Footer({ className }: FooterProps) {
  const currentYear = new Date().getFullYear();

  return (
    <footer className={cn('border-t bg-background', className)}>
      <div className="container mx-auto px-4 py-8">
        <div className="grid grid-cols-1 gap-8 md:grid-cols-4">
          {/* 회사 정보 */}
          <div>
            <h3 className="mb-3 text-sm font-semibold">EduForum</h3>
            <p className="text-sm text-muted-foreground">
              미네르바 대학의 Active Learning Forum을 참고한 대화형 온라인 학습 플랫폼
            </p>
          </div>

          {/* 제품 */}
          <div>
            <h3 className="mb-3 text-sm font-semibold">제품</h3>
            <ul className="space-y-2 text-sm">
              <li>
                <Link href="/features" className="text-muted-foreground hover:text-foreground">
                  주요 기능
                </Link>
              </li>
              <li>
                <Link href="/pricing" className="text-muted-foreground hover:text-foreground">
                  요금제
                </Link>
              </li>
              <li>
                <Link href="/roadmap" className="text-muted-foreground hover:text-foreground">
                  로드맵
                </Link>
              </li>
            </ul>
          </div>

          {/* 지원 */}
          <div>
            <h3 className="mb-3 text-sm font-semibold">지원</h3>
            <ul className="space-y-2 text-sm">
              <li>
                <Link href="/docs" className="text-muted-foreground hover:text-foreground">
                  문서
                </Link>
              </li>
              <li>
                <Link href="/support" className="text-muted-foreground hover:text-foreground">
                  고객 지원
                </Link>
              </li>
              <li>
                <Link href="/faq" className="text-muted-foreground hover:text-foreground">
                  자주 묻는 질문
                </Link>
              </li>
            </ul>
          </div>

          {/* 법적 정보 */}
          <div>
            <h3 className="mb-3 text-sm font-semibold">법적 정보</h3>
            <ul className="space-y-2 text-sm">
              <li>
                <Link href="/privacy" className="text-muted-foreground hover:text-foreground">
                  개인정보처리방침
                </Link>
              </li>
              <li>
                <Link href="/terms" className="text-muted-foreground hover:text-foreground">
                  이용약관
                </Link>
              </li>
            </ul>
          </div>
        </div>

        <div className="mt-8 border-t pt-8 text-center text-sm text-muted-foreground">
          <p>&copy; {currentYear} EduForum. All rights reserved.</p>
        </div>
      </div>
    </footer>
  );
}
