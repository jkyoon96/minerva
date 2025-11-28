import React from 'react';
import { User } from 'lucide-react';
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';
import { Badge } from '@/components/ui/badge';
import { cn } from '@/lib/utils';

interface UserAvatarProps {
  src?: string;
  name?: string;
  size?: 'sm' | 'md' | 'lg';
  status?: 'online' | 'offline' | 'busy' | 'away';
  showStatus?: boolean;
  className?: string;
}

const sizeMap = {
  sm: 'h-8 w-8',
  md: 'h-10 w-10',
  lg: 'h-12 w-12',
};

const statusColorMap = {
  online: 'bg-green-500',
  offline: 'bg-gray-400',
  busy: 'bg-red-500',
  away: 'bg-yellow-500',
};

/**
 * 사용자 아바타 컴포넌트
 * - 프로필 이미지 또는 이니셜 표시
 * - 온라인 상태 표시 지원
 */
export function UserAvatar({
  src,
  name = '',
  size = 'md',
  status,
  showStatus = false,
  className,
}: UserAvatarProps) {
  const getInitials = (name: string) => {
    if (!name) return '';
    const parts = name.split(' ');
    if (parts.length === 1) return parts[0].charAt(0).toUpperCase();
    return (parts[0].charAt(0) + parts[parts.length - 1].charAt(0)).toUpperCase();
  };

  return (
    <div className={cn('relative inline-block', className)}>
      <Avatar className={sizeMap[size]}>
        <AvatarImage src={src} alt={name} />
        <AvatarFallback>
          {name ? getInitials(name) : <User className="h-4 w-4" />}
        </AvatarFallback>
      </Avatar>
      {showStatus && status && (
        <span
          className={cn(
            'absolute bottom-0 right-0 block h-3 w-3 rounded-full border-2 border-background',
            statusColorMap[status]
          )}
        />
      )}
    </div>
  );
}
