/**
 * 대시보드 알림 섹션
 * 최근 알림 목록 표시
 */

'use client';

import * as React from 'react';
import { Bell, FileText, BookOpen, Users, Info } from 'lucide-react';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { formatDateTime } from '@/lib/utils';
import { Notification, NotificationType } from '@/types/dashboard';
import { cn } from '@/lib/utils';

interface NotificationSectionProps {
  notifications: Notification[];
  onMarkAsRead?: (id: string) => void;
}

const notificationIcons: Record<NotificationType, typeof Bell> = {
  assignment: FileText,
  session: BookOpen,
  student: Users,
  system: Info,
};

const notificationColors: Record<NotificationType, string> = {
  assignment: 'text-blue-600',
  session: 'text-green-600',
  student: 'text-orange-600',
  system: 'text-gray-600',
};

export function NotificationSection({ notifications, onMarkAsRead }: NotificationSectionProps) {
  if (notifications.length === 0) {
    return (
      <Card>
        <CardHeader>
          <CardTitle>알림</CardTitle>
          <CardDescription>새로운 알림이 없습니다</CardDescription>
        </CardHeader>
      </Card>
    );
  }

  return (
    <Card>
      <CardHeader>
        <CardTitle>알림</CardTitle>
        <CardDescription>최근 알림 및 업데이트</CardDescription>
      </CardHeader>
      <CardContent>
        <div className="space-y-3">
          {notifications.map((notification) => {
            const Icon = notificationIcons[notification.type];
            const iconColor = notificationColors[notification.type];

            return (
              <div
                key={notification.id}
                className={cn(
                  'flex items-start gap-3 rounded-lg border p-3 transition-colors',
                  notification.read ? 'bg-background' : 'bg-accent/50',
                  'hover:bg-accent cursor-pointer',
                )}
                onClick={() => onMarkAsRead?.(notification.id)}
              >
                <div className={cn('mt-0.5', iconColor)}>
                  <Icon className="h-5 w-5" />
                </div>
                <div className="flex-1 space-y-1">
                  <div className="flex items-start justify-between gap-2">
                    <p className="font-medium leading-none">{notification.title}</p>
                    {!notification.read && (
                      <Badge variant="default" className="h-2 w-2 rounded-full p-0" />
                    )}
                  </div>
                  <p className="text-sm text-muted-foreground">{notification.message}</p>
                  <p className="text-xs text-muted-foreground">
                    {formatDateTime(notification.createdAt)}
                  </p>
                </div>
              </div>
            );
          })}
        </div>
      </CardContent>
    </Card>
  );
}
