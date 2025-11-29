'use client';

import { useState, useEffect } from 'react';
import { UserListItem, UserSearchParams } from '@/types/admin';
import { UserRole } from '@/types';
import { UserList } from '@/components/admin/user-list';
import { UserSearch } from '@/components/admin/user-search';
import { UserRoleModal } from '@/components/admin/user-role-modal';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { useToast } from '@/hooks/use-toast';
import { getUsers, changeUserRole, changeUserStatus } from '@/lib/api/admin';
import { Download, RefreshCw } from 'lucide-react';
import {
  Pagination,
  PaginationContent,
  PaginationEllipsis,
  PaginationItem,
  PaginationLink,
  PaginationNext,
  PaginationPrevious,
} from '@/components/ui/pagination';

/**
 * 사용자 관리 페이지 (Admin 전용)
 * - 사용자 목록 조회
 * - 검색 및 필터링
 * - 역할 변경
 * - 상태 변경
 */
export default function AdminUsersPage() {
  const [users, setUsers] = useState<UserListItem[]>([]);
  const [total, setTotal] = useState(0);
  const [isLoading, setIsLoading] = useState(true);
  const [selectedUser, setSelectedUser] = useState<UserListItem | null>(null);
  const [isRoleModalOpen, setIsRoleModalOpen] = useState(false);
  const { toast } = useToast();

  // 검색/필터 상태
  const [searchParams, setSearchParams] = useState<UserSearchParams>({
    page: 1,
    limit: 20,
    sortBy: 'createdAt',
    sortOrder: 'desc',
  });

  // 사용자 목록 조회
  const fetchUsers = async () => {
    setIsLoading(true);
    try {
      const response = await getUsers(searchParams);
      setUsers(response.users);
      setTotal(response.total);
    } catch (error) {
      toast({
        title: '오류',
        description: '사용자 목록을 불러오는데 실패했습니다.',
        variant: 'destructive',
      });
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    fetchUsers();
  }, [searchParams]);

  // 검색
  const handleSearch = (query: string) => {
    setSearchParams((prev) => ({ ...prev, query, page: 1 }));
  };

  // 역할 필터
  const handleRoleFilter = (role: UserRole | 'all') => {
    setSearchParams((prev) => ({
      ...prev,
      role: role === 'all' ? undefined : role,
      page: 1,
    }));
  };

  // 상태 필터
  const handleStatusFilter = (status: 'active' | 'inactive' | 'suspended' | 'all') => {
    setSearchParams((prev) => ({
      ...prev,
      status: status === 'all' ? undefined : status,
      page: 1,
    }));
  };

  // 역할 변경 모달 열기
  const handleChangeRole = (user: UserListItem) => {
    setSelectedUser(user);
    setIsRoleModalOpen(true);
  };

  // 역할 변경 확정
  const handleConfirmRoleChange = async (
    userId: string,
    newRole: UserRole,
    reason?: string
  ) => {
    try {
      await changeUserRole(userId, newRole, reason);
      toast({
        title: '성공',
        description: '역할이 변경되었습니다.',
      });
      fetchUsers(); // 목록 새로고침
    } catch (error) {
      throw error; // 모달에서 처리
    }
  };

  // 상태 변경
  const handleChangeStatus = async (
    userId: string,
    status: 'active' | 'inactive' | 'suspended'
  ) => {
    try {
      await changeUserStatus(userId, status);
      toast({
        title: '성공',
        description: '사용자 상태가 변경되었습니다.',
      });
      fetchUsers();
    } catch (error) {
      toast({
        title: '오류',
        description: '상태 변경에 실패했습니다.',
        variant: 'destructive',
      });
    }
  };

  // 사용자 상세 보기 (미구현)
  const handleViewDetail = (user: UserListItem) => {
    toast({
      title: '사용자 상세',
      description: `${user.name}의 상세 정보를 보는 기능은 추후 구현 예정입니다.`,
    });
  };

  // 페이지 변경
  const handlePageChange = (page: number) => {
    setSearchParams((prev) => ({ ...prev, page }));
  };

  // 새로고침
  const handleRefresh = () => {
    fetchUsers();
  };

  const totalPages = Math.ceil(total / (searchParams.limit || 20));

  return (
    <div className="space-y-6">
      {/* 헤더 */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold">사용자 관리</h1>
          <p className="text-muted-foreground">
            전체 {total}명의 사용자
          </p>
        </div>
        <div className="flex gap-2">
          <Button variant="outline" onClick={handleRefresh} disabled={isLoading}>
            <RefreshCw className="mr-2 h-4 w-4" />
            새로고침
          </Button>
          <Button variant="outline">
            <Download className="mr-2 h-4 w-4" />
            내보내기
          </Button>
        </div>
      </div>

      {/* 검색 및 필터 */}
      <Card>
        <CardHeader>
          <CardTitle>검색 및 필터</CardTitle>
          <CardDescription>
            이름, 이메일로 검색하고 역할과 상태로 필터링할 수 있습니다.
          </CardDescription>
        </CardHeader>
        <CardContent>
          <UserSearch
            onSearch={handleSearch}
            onRoleFilter={handleRoleFilter}
            onStatusFilter={handleStatusFilter}
          />
        </CardContent>
      </Card>

      {/* 사용자 목록 */}
      <Card>
        <CardHeader>
          <CardTitle>사용자 목록</CardTitle>
          <CardDescription>
            {searchParams.query && `"${searchParams.query}" 검색 결과 - `}
            {total}명
          </CardDescription>
        </CardHeader>
        <CardContent>
          <UserList
            users={users}
            onChangeRole={handleChangeRole}
            onViewDetail={handleViewDetail}
            onChangeStatus={handleChangeStatus}
            isLoading={isLoading}
          />

          {/* 페이지네이션 */}
          {totalPages > 1 && (
            <div className="mt-4">
              <Pagination>
                <PaginationContent>
                  <PaginationItem>
                    <PaginationPrevious
                      onClick={() => handlePageChange(Math.max(1, (searchParams.page || 1) - 1))}
                      className={
                        (searchParams.page || 1) === 1
                          ? 'pointer-events-none opacity-50'
                          : 'cursor-pointer'
                      }
                    />
                  </PaginationItem>

                  {Array.from({ length: Math.min(5, totalPages) }, (_, i) => {
                    const page = i + 1;
                    return (
                      <PaginationItem key={page}>
                        <PaginationLink
                          onClick={() => handlePageChange(page)}
                          isActive={page === searchParams.page}
                          className="cursor-pointer"
                        >
                          {page}
                        </PaginationLink>
                      </PaginationItem>
                    );
                  })}

                  {totalPages > 5 && <PaginationEllipsis />}

                  <PaginationItem>
                    <PaginationNext
                      onClick={() =>
                        handlePageChange(Math.min(totalPages, (searchParams.page || 1) + 1))
                      }
                      className={
                        (searchParams.page || 1) === totalPages
                          ? 'pointer-events-none opacity-50'
                          : 'cursor-pointer'
                      }
                    />
                  </PaginationItem>
                </PaginationContent>
              </Pagination>
            </div>
          )}
        </CardContent>
      </Card>

      {/* 역할 변경 모달 */}
      <UserRoleModal
        user={selectedUser}
        isOpen={isRoleModalOpen}
        onClose={() => setIsRoleModalOpen(false)}
        onConfirm={handleConfirmRoleChange}
      />
    </div>
  );
}
