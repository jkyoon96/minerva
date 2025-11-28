import React from 'react';
import Link from 'next/link';
import { ChevronRight } from 'lucide-react';
import { cn } from '@/lib/utils';

interface BreadcrumbItem {
  label: string;
  href?: string;
}

interface HeaderProps {
  title: string;
  breadcrumbs?: BreadcrumbItem[];
  description?: string;
  actions?: React.ReactNode;
  className?: string;
}

/**
 * 페이지 헤더 컴포넌트
 * - 제목, 브레드크럼, 설명, 액션 버튼을 포함
 */
export function Header({ title, breadcrumbs, description, actions, className }: HeaderProps) {
  return (
    <header className={cn('border-b bg-background', className)}>
      <div className="container mx-auto px-4 py-4">
        {/* 브레드크럼 */}
        {breadcrumbs && breadcrumbs.length > 0 && (
          <nav className="mb-2 flex items-center space-x-1 text-sm text-muted-foreground">
            {breadcrumbs.map((item, index) => (
              <React.Fragment key={index}>
                {index > 0 && <ChevronRight className="h-4 w-4" />}
                {item.href ? (
                  <Link
                    href={item.href}
                    className="hover:text-foreground transition-colors"
                  >
                    {item.label}
                  </Link>
                ) : (
                  <span className="text-foreground font-medium">{item.label}</span>
                )}
              </React.Fragment>
            ))}
          </nav>
        )}

        {/* 제목과 액션 */}
        <div className="flex items-start justify-between">
          <div>
            <h1 className="text-2xl font-bold tracking-tight">{title}</h1>
            {description && <p className="mt-1 text-sm text-muted-foreground">{description}</p>}
          </div>
          {actions && <div className="flex items-center gap-2">{actions}</div>}
        </div>
      </div>
    </header>
  );
}
