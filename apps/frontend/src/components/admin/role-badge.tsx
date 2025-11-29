'use client';

import { UserRole } from '@/types';
import { Badge } from '@/components/ui/badge';
import { Shield, GraduationCap, UserCog, User } from 'lucide-react';

interface RoleBadgeProps {
  role: UserRole;
  showIcon?: boolean;
}

/**
 * 역할 뱃지 컴포넌트
 * - ADMIN: 빨간색 (destructive)
 * - PROFESSOR: 파란색 (info)
 * - TA: 초록색 (success)
 * - STUDENT: 회색 (secondary)
 */
export function RoleBadge({ role, showIcon = false }: RoleBadgeProps) {
  const getRoleConfig = (role: UserRole) => {
    switch (role) {
      case 'admin':
        return {
          label: 'ADMIN',
          variant: 'destructive' as const,
          icon: Shield,
        };
      case 'professor':
        return {
          label: 'PROFESSOR',
          variant: 'info' as const,
          icon: GraduationCap,
        };
      case 'ta':
        return {
          label: 'TA',
          variant: 'success' as const,
          icon: UserCog,
        };
      case 'student':
        return {
          label: 'STUDENT',
          variant: 'secondary' as const,
          icon: User,
        };
      default:
        return {
          label: 'UNKNOWN',
          variant: 'outline' as const,
          icon: User,
        };
    }
  };

  const config = getRoleConfig(role);
  const Icon = config.icon;

  return (
    <Badge variant={config.variant} className="gap-1">
      {showIcon && <Icon className="h-3 w-3" />}
      {config.label}
    </Badge>
  );
}

/**
 * 역할 표시명 가져오기 헬퍼 함수
 */
export function getRoleDisplayName(role: UserRole): string {
  switch (role) {
    case 'admin':
      return '관리자';
    case 'professor':
      return '교수';
    case 'ta':
      return '조교';
    case 'student':
      return '학생';
    default:
      return '알 수 없음';
  }
}

/**
 * 역할 설명 가져오기 헬퍼 함수
 */
export function getRoleDescription(role: UserRole): string {
  switch (role) {
    case 'admin':
      return '시스템 전체 관리 권한';
    case 'professor':
      return '코스 생성 및 관리 권한';
    case 'ta':
      return '코스 운영 지원 권한';
    case 'student':
      return '코스 수강 및 참여 권한';
    default:
      return '';
  }
}
