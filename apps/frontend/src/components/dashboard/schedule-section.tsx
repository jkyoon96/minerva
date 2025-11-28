/**
 * 대시보드 일정 섹션
 * 오늘의 세션 및 과제 일정 표시
 */

'use client';

import * as React from 'react';
import { Clock, BookOpen, FileText } from 'lucide-react';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { formatDateTime } from '@/lib/utils';
import { ScheduleItem } from '@/types/dashboard';

interface ScheduleSectionProps {
  items: ScheduleItem[];
}

export function ScheduleSection({ items }: ScheduleSectionProps) {
  if (items.length === 0) {
    return (
      <Card>
        <CardHeader>
          <CardTitle>오늘 일정</CardTitle>
          <CardDescription>오늘 예정된 세션 및 과제가 없습니다</CardDescription>
        </CardHeader>
      </Card>
    );
  }

  return (
    <Card>
      <CardHeader>
        <CardTitle>오늘 일정</CardTitle>
        <CardDescription>오늘 예정된 세션 및 과제</CardDescription>
      </CardHeader>
      <CardContent>
        <div className="space-y-4">
          {items.map((item) => (
            <div key={item.id} className="flex items-start gap-4 rounded-lg border p-4">
              <div className="rounded-lg bg-primary/10 p-2">
                {item.type === 'session' ? (
                  <BookOpen className="h-5 w-5 text-primary" />
                ) : (
                  <FileText className="h-5 w-5 text-primary" />
                )}
              </div>
              <div className="flex-1">
                <div className="flex items-start justify-between">
                  <div>
                    <h4 className="font-semibold">{item.title}</h4>
                    <p className="text-sm text-muted-foreground">{item.courseTitle}</p>
                  </div>
                  <Badge variant={item.type === 'session' ? 'default' : 'outline'}>
                    {item.type === 'session' ? '세션' : '과제'}
                  </Badge>
                </div>
                <div className="mt-2 flex items-center gap-2 text-sm text-muted-foreground">
                  <Clock className="h-4 w-4" />
                  <span>{formatDateTime(item.scheduledAt)}</span>
                  {item.type === 'session' && <span>({item.duration}분)</span>}
                </div>
              </div>
            </div>
          ))}
        </div>
      </CardContent>
    </Card>
  );
}
