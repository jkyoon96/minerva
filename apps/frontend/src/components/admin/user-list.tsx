'use client';

import { useState } from 'react';
import { UserListItem } from '@/types/admin';
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { RoleBadge } from './role-badge';
import { UserAvatar } from '@/components/common/UserAvatar';
import { MoreVertical, Edit, Trash2, Ban, CheckCircle } from 'lucide-react';
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu';
import { formatDistanceToNow } from 'date-fns';
import { ko } from 'date-fns/locale';

interface UserListProps {
  users: UserListItem[];
  onChangeRole: (user: UserListItem) => void;
  onViewDetail: (user: UserListItem) => void;
  onChangeStatus?: (userId: string, status: 'active' | 'inactive' | 'suspended') => void;
  onDelete?: (userId: string) => void;
  isLoading?: boolean;
}

/**
 * 사용자 목록 테이블 컴포넌트
 * - 이름, 이메일, 역할, 상태, 가입일, 마지막 로그인, 액션
 * - 역할 변경, 상세 보기, 상태 변경, 삭제
 */
export function UserList({
  users,
  onChangeRole,
  onViewDetail,
  onChangeStatus,
  onDelete,
  isLoading = false,
}: UserListProps) {
  const getStatusBadge = (status: UserListItem['status']) => {
    switch (status) {
      case 'active':
        return <Badge variant="success">활성</Badge>;
      case 'inactive':
        return <Badge variant="secondary">비활성</Badge>;
      case 'suspended':
        return <Badge variant="destructive">정지</Badge>;
      default:
        return <Badge variant="outline">알 수 없음</Badge>;
    }
  };

  const formatDate = (dateString: string) => {
    try {
      return formatDistanceToNow(new Date(dateString), {
        addSuffix: true,
        locale: ko,
      });
    } catch {
      return '-';
    }
  };

  if (isLoading) {
    return (
      <div className="flex items-center justify-center py-12">
        <div className="text-center">
          <div className="h-8 w-8 animate-spin rounded-full border-4 border-primary border-t-transparent mx-auto mb-4" />
          <p className="text-sm text-muted-foreground">로딩 중...</p>
        </div>
      </div>
    );
  }

  if (users.length === 0) {
    return (
      <div className="rounded-lg border border-dashed py-12 text-center">
        <p className="text-muted-foreground">사용자가 없습니다.</p>
      </div>
    );
  }

  return (
    <div className="rounded-md border">
      <Table>
        <TableHeader>
          <TableRow>
            <TableHead className="w-[250px]">사용자</TableHead>
            <TableHead>이메일</TableHead>
            <TableHead>역할</TableHead>
            <TableHead>상태</TableHead>
            <TableHead>가입일</TableHead>
            <TableHead>마지막 로그인</TableHead>
            <TableHead className="w-[80px]">액션</TableHead>
          </TableRow>
        </TableHeader>
        <TableBody>
          {users.map((user) => (
            <TableRow
              key={user.id}
              className="cursor-pointer hover:bg-muted/50"
              onClick={() => onViewDetail(user)}
            >
              {/* 사용자 */}
              <TableCell>
                <div className="flex items-center gap-3">
                  <UserAvatar user={user} size="sm" />
                  <div className="font-medium">{user.name}</div>
                </div>
              </TableCell>

              {/* 이메일 */}
              <TableCell className="text-muted-foreground">
                {user.email}
              </TableCell>

              {/* 역할 */}
              <TableCell>
                <RoleBadge role={user.role} />
              </TableCell>

              {/* 상태 */}
              <TableCell>{getStatusBadge(user.status)}</TableCell>

              {/* 가입일 */}
              <TableCell className="text-sm text-muted-foreground">
                {formatDate(user.createdAt)}
              </TableCell>

              {/* 마지막 로그인 */}
              <TableCell className="text-sm text-muted-foreground">
                {user.lastLoginAt ? formatDate(user.lastLoginAt) : '-'}
              </TableCell>

              {/* 액션 */}
              <TableCell onClick={(e) => e.stopPropagation()}>
                <DropdownMenu>
                  <DropdownMenuTrigger asChild>
                    <Button variant="ghost" size="sm">
                      <MoreVertical className="h-4 w-4" />
                      <span className="sr-only">액션 메뉴</span>
                    </Button>
                  </DropdownMenuTrigger>
                  <DropdownMenuContent align="end">
                    <DropdownMenuLabel>작업</DropdownMenuLabel>
                    <DropdownMenuItem onClick={() => onViewDetail(user)}>
                      상세 보기
                    </DropdownMenuItem>
                    <DropdownMenuItem onClick={() => onChangeRole(user)}>
                      <Edit className="mr-2 h-4 w-4" />
                      역할 변경
                    </DropdownMenuItem>

                    {onChangeStatus && (
                      <>
                        <DropdownMenuSeparator />
                        <DropdownMenuLabel>상태 변경</DropdownMenuLabel>
                        {user.status !== 'active' && (
                          <DropdownMenuItem
                            onClick={() => onChangeStatus(user.id, 'active')}
                          >
                            <CheckCircle className="mr-2 h-4 w-4" />
                            활성화
                          </DropdownMenuItem>
                        )}
                        {user.status !== 'suspended' && (
                          <DropdownMenuItem
                            onClick={() => onChangeStatus(user.id, 'suspended')}
                          >
                            <Ban className="mr-2 h-4 w-4" />
                            정지
                          </DropdownMenuItem>
                        )}
                      </>
                    )}

                    {onDelete && (
                      <>
                        <DropdownMenuSeparator />
                        <DropdownMenuItem
                          onClick={() => onDelete(user.id)}
                          className="text-destructive"
                        >
                          <Trash2 className="mr-2 h-4 w-4" />
                          삭제
                        </DropdownMenuItem>
                      </>
                    )}
                  </DropdownMenuContent>
                </DropdownMenu>
              </TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>
    </div>
  );
}
