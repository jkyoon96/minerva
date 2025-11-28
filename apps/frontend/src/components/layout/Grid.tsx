import React from 'react';
import { cn } from '@/lib/utils';

interface GridProps {
  children: React.ReactNode;
  cols?: 1 | 2 | 3 | 4 | 5 | 6;
  gap?: 2 | 4 | 6 | 8;
  className?: string;
}

const colsMap = {
  1: 'grid-cols-1',
  2: 'grid-cols-1 md:grid-cols-2',
  3: 'grid-cols-1 md:grid-cols-2 lg:grid-cols-3',
  4: 'grid-cols-1 md:grid-cols-2 lg:grid-cols-4',
  5: 'grid-cols-1 md:grid-cols-3 lg:grid-cols-5',
  6: 'grid-cols-1 md:grid-cols-3 lg:grid-cols-6',
};

const gapMap = {
  2: 'gap-2',
  4: 'gap-4',
  6: 'gap-6',
  8: 'gap-8',
};

/**
 * 반응형 그리드 레이아웃 컴포넌트
 * - 다양한 열 개수 옵션
 * - 반응형 디자인 자동 적용
 */
export function Grid({ children, cols = 3, gap = 4, className }: GridProps) {
  return <div className={cn('grid', colsMap[cols], gapMap[gap], className)}>{children}</div>;
}
