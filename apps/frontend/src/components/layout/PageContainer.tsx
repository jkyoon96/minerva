import React from 'react';
import { cn } from '@/lib/utils';

interface PageContainerProps {
  children: React.ReactNode;
  maxWidth?: 'sm' | 'md' | 'lg' | 'xl' | '2xl' | 'full';
  className?: string;
}

const maxWidthMap = {
  sm: 'max-w-screen-sm',
  md: 'max-w-screen-md',
  lg: 'max-w-screen-lg',
  xl: 'max-w-screen-xl',
  '2xl': 'max-w-screen-2xl',
  full: 'max-w-full',
};

/**
 * 표준 페이지 컨테이너 컴포넌트
 * - 일관된 페이지 레이아웃 제공
 * - 최대 너비 옵션 지원
 */
export function PageContainer({ children, maxWidth = 'xl', className }: PageContainerProps) {
  return (
    <div className={cn('container mx-auto px-4 py-6', maxWidthMap[maxWidth], className)}>
      {children}
    </div>
  );
}
