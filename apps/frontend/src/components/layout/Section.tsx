import React from 'react';
import { cn } from '@/lib/utils';

interface SectionProps {
  title?: string;
  description?: string;
  actions?: React.ReactNode;
  children: React.ReactNode;
  className?: string;
}

/**
 * 콘텐츠 섹션 컴포넌트
 * - 제목, 설명, 액션이 있는 섹션
 * - 일관된 섹션 스타일 제공
 */
export function Section({ title, description, actions, children, className }: SectionProps) {
  return (
    <section className={cn('space-y-4', className)}>
      {(title || description || actions) && (
        <div className="flex items-start justify-between">
          <div>
            {title && <h2 className="text-xl font-semibold tracking-tight">{title}</h2>}
            {description && <p className="mt-1 text-sm text-muted-foreground">{description}</p>}
          </div>
          {actions && <div className="flex items-center gap-2">{actions}</div>}
        </div>
      )}
      <div>{children}</div>
    </section>
  );
}
