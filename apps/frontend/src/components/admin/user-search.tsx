'use client';

import { useState } from 'react';
import { Search, X } from 'lucide-react';
import { Input } from '@/components/ui/input';
import { Button } from '@/components/ui/button';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select';
import { UserRole } from '@/types';
import { UserStatus } from '@/types/admin';
import { getRoleDisplayName } from './role-badge';

interface UserSearchProps {
  onSearch: (query: string) => void;
  onRoleFilter: (role: UserRole | 'all') => void;
  onStatusFilter: (status: UserStatus | 'all') => void;
  initialQuery?: string;
  initialRole?: UserRole | 'all';
  initialStatus?: UserStatus | 'all';
}

/**
 * 사용자 검색 및 필터 컴포넌트
 * - 검색어 입력 (이름/이메일)
 * - 역할 필터
 * - 상태 필터
 */
export function UserSearch({
  onSearch,
  onRoleFilter,
  onStatusFilter,
  initialQuery = '',
  initialRole = 'all',
  initialStatus = 'all',
}: UserSearchProps) {
  const [query, setQuery] = useState(initialQuery);
  const [role, setRole] = useState<UserRole | 'all'>(initialRole);
  const [status, setStatus] = useState<UserStatus | 'all'>(initialStatus);

  const handleSearch = () => {
    onSearch(query);
  };

  const handleClear = () => {
    setQuery('');
    onSearch('');
  };

  const handleRoleChange = (value: string) => {
    const newRole = value as UserRole | 'all';
    setRole(newRole);
    onRoleFilter(newRole);
  };

  const handleStatusChange = (value: string) => {
    const newStatus = value as UserStatus | 'all';
    setStatus(newStatus);
    onStatusFilter(newStatus);
  };

  const handleKeyDown = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === 'Enter') {
      handleSearch();
    }
  };

  return (
    <div className="flex flex-col gap-4 sm:flex-row sm:items-center">
      {/* 검색어 입력 */}
      <div className="relative flex-1">
        <Search className="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
        <Input
          type="text"
          placeholder="이름 또는 이메일로 검색..."
          value={query}
          onChange={(e) => setQuery(e.target.value)}
          onKeyDown={handleKeyDown}
          className="pl-9 pr-9"
        />
        {query && (
          <button
            onClick={handleClear}
            className="absolute right-3 top-1/2 -translate-y-1/2 text-muted-foreground hover:text-foreground"
          >
            <X className="h-4 w-4" />
          </button>
        )}
      </div>

      {/* 역할 필터 */}
      <Select value={role} onValueChange={handleRoleChange}>
        <SelectTrigger className="w-full sm:w-[180px]">
          <SelectValue placeholder="역할 선택" />
        </SelectTrigger>
        <SelectContent>
          <SelectItem value="all">모든 역할</SelectItem>
          <SelectItem value="admin">{getRoleDisplayName('admin')}</SelectItem>
          <SelectItem value="professor">{getRoleDisplayName('professor')}</SelectItem>
          <SelectItem value="ta">{getRoleDisplayName('ta')}</SelectItem>
          <SelectItem value="student">{getRoleDisplayName('student')}</SelectItem>
        </SelectContent>
      </Select>

      {/* 상태 필터 */}
      <Select value={status} onValueChange={handleStatusChange}>
        <SelectTrigger className="w-full sm:w-[180px]">
          <SelectValue placeholder="상태 선택" />
        </SelectTrigger>
        <SelectContent>
          <SelectItem value="all">모든 상태</SelectItem>
          <SelectItem value="active">활성</SelectItem>
          <SelectItem value="inactive">비활성</SelectItem>
          <SelectItem value="suspended">정지</SelectItem>
        </SelectContent>
      </Select>

      {/* 검색 버튼 */}
      <Button onClick={handleSearch} className="w-full sm:w-auto">
        <Search className="mr-2 h-4 w-4" />
        검색
      </Button>
    </div>
  );
}
