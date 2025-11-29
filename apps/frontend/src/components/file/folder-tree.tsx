'use client';

import { useState } from 'react';
import { Button } from '@/components/ui/button';
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu';
import { Input } from '@/components/ui/input';
import {
  Folder,
  FolderOpen,
  ChevronRight,
  ChevronDown,
  MoreVertical,
  Plus,
  Trash2,
  Edit,
} from 'lucide-react';
import { FolderTreeNode } from '@/types/file';
import { cn } from '@/lib/utils';

interface FolderTreeProps {
  folders: FolderTreeNode[];
  selectedFolderId?: string;
  onFolderSelect?: (folderId: string | undefined) => void;
  onFolderCreate?: (parentId: string | undefined, name: string) => Promise<void>;
  onFolderDelete?: (folderId: string) => Promise<void>;
  className?: string;
}

interface FolderNodeProps {
  node: FolderTreeNode;
  level: number;
  selectedFolderId?: string;
  onSelect?: (folderId: string | undefined) => void;
  onCreate?: (parentId: string | undefined, name: string) => Promise<void>;
  onDelete?: (folderId: string) => Promise<void>;
}

function FolderNode({
  node,
  level,
  selectedFolderId,
  onSelect,
  onCreate,
  onDelete,
}: FolderNodeProps) {
  const [isExpanded, setIsExpanded] = useState(node.isExpanded ?? false);
  const [isCreating, setIsCreating] = useState(false);
  const [newFolderName, setNewFolderName] = useState('');
  const isSelected = selectedFolderId === node.id;
  const hasChildren = node.children.length > 0;

  const handleToggle = (e: React.MouseEvent) => {
    e.stopPropagation();
    setIsExpanded(!isExpanded);
  };

  const handleSelect = () => {
    onSelect?.(node.id);
  };

  const handleCreateClick = (e: React.MouseEvent) => {
    e.stopPropagation();
    setIsCreating(true);
    setIsExpanded(true);
  };

  const handleCreateSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!newFolderName.trim()) return;

    try {
      await onCreate?.(node.id, newFolderName.trim());
      setNewFolderName('');
      setIsCreating(false);
    } catch (error) {
      console.error('Failed to create folder:', error);
    }
  };

  const handleCreateCancel = () => {
    setNewFolderName('');
    setIsCreating(false);
  };

  const handleDelete = async (e: React.MouseEvent) => {
    e.stopPropagation();
    if (confirm(`"${node.name}" 폴더를 삭제하시겠습니까?`)) {
      try {
        await onDelete?.(node.id);
      } catch (error) {
        console.error('Failed to delete folder:', error);
      }
    }
  };

  return (
    <div>
      {/* 폴더 아이템 */}
      <div
        className={cn(
          'group flex items-center gap-2 rounded-md px-2 py-1.5 text-sm transition-colors hover:bg-accent',
          isSelected && 'bg-accent font-medium',
        )}
        style={{ paddingLeft: `${level * 16 + 8}px` }}
        onClick={handleSelect}
      >
        {/* 펼치기/접기 버튼 */}
        <Button
          variant="ghost"
          size="sm"
          className="h-5 w-5 p-0 hover:bg-transparent"
          onClick={handleToggle}
          disabled={!hasChildren && !isCreating}
        >
          {hasChildren || isCreating ? (
            isExpanded ? (
              <ChevronDown className="h-4 w-4" />
            ) : (
              <ChevronRight className="h-4 w-4" />
            )
          ) : (
            <div className="h-4 w-4" />
          )}
        </Button>

        {/* 폴더 아이콘 */}
        {isExpanded ? (
          <FolderOpen className="h-4 w-4 flex-shrink-0 text-muted-foreground" />
        ) : (
          <Folder className="h-4 w-4 flex-shrink-0 text-muted-foreground" />
        )}

        {/* 폴더 이름 */}
        <span className="flex-1 truncate">{node.name}</span>

        {/* 파일 개수 */}
        {node.fileCount > 0 && (
          <span className="text-xs text-muted-foreground">{node.fileCount}</span>
        )}

        {/* 메뉴 버튼 */}
        <div onClick={(e) => e.stopPropagation()}>
          <DropdownMenu>
            <DropdownMenuTrigger asChild>
              <Button
                variant="ghost"
                size="sm"
                className="h-6 w-6 p-0 opacity-0 transition-opacity group-hover:opacity-100"
              >
                <MoreVertical className="h-3 w-3" />
                <span className="sr-only">메뉴 열기</span>
              </Button>
            </DropdownMenuTrigger>
            <DropdownMenuContent align="end">
              {onCreate && (
                <DropdownMenuItem onClick={handleCreateClick}>
                  <Plus className="mr-2 h-4 w-4" />
                  하위 폴더 생성
                </DropdownMenuItem>
              )}
              {onDelete && (
                <>
                  <DropdownMenuSeparator />
                  <DropdownMenuItem
                    onClick={handleDelete}
                    className="text-destructive focus:text-destructive"
                  >
                    <Trash2 className="mr-2 h-4 w-4" />
                    삭제
                  </DropdownMenuItem>
                </>
              )}
            </DropdownMenuContent>
          </DropdownMenu>
        </div>
      </div>

      {/* 새 폴더 생성 입력 */}
      {isCreating && isExpanded && (
        <form
          onSubmit={handleCreateSubmit}
          className="flex items-center gap-2 py-1"
          style={{ paddingLeft: `${(level + 1) * 16 + 32}px` }}
        >
          <Folder className="h-4 w-4 flex-shrink-0 text-muted-foreground" />
          <Input
            type="text"
            value={newFolderName}
            onChange={(e) => setNewFolderName(e.target.value)}
            placeholder="폴더 이름"
            className="h-7 text-sm"
            autoFocus
            onBlur={handleCreateCancel}
            onKeyDown={(e) => {
              if (e.key === 'Escape') {
                handleCreateCancel();
              }
            }}
          />
        </form>
      )}

      {/* 하위 폴더들 */}
      {isExpanded && hasChildren && (
        <div>
          {node.children.map((child) => (
            <FolderNode
              key={child.id}
              node={child}
              level={level + 1}
              selectedFolderId={selectedFolderId}
              onSelect={onSelect}
              onCreate={onCreate}
              onDelete={onDelete}
            />
          ))}
        </div>
      )}
    </div>
  );
}

export function FolderTree({
  folders,
  selectedFolderId,
  onFolderSelect,
  onFolderCreate,
  onFolderDelete,
  className,
}: FolderTreeProps) {
  const [isCreatingRoot, setIsCreatingRoot] = useState(false);
  const [newFolderName, setNewFolderName] = useState('');

  const handleRootSelect = () => {
    onFolderSelect?.(undefined);
  };

  const handleCreateRootClick = () => {
    setIsCreatingRoot(true);
  };

  const handleCreateRootSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!newFolderName.trim()) return;

    try {
      await onFolderCreate?.(undefined, newFolderName.trim());
      setNewFolderName('');
      setIsCreatingRoot(false);
    } catch (error) {
      console.error('Failed to create folder:', error);
    }
  };

  const handleCreateRootCancel = () => {
    setNewFolderName('');
    setIsCreatingRoot(false);
  };

  return (
    <div className={cn('space-y-1', className)}>
      {/* 루트 (전체 파일) */}
      <div
        className={cn(
          'flex items-center gap-2 rounded-md px-2 py-1.5 text-sm transition-colors hover:bg-accent',
          selectedFolderId === undefined && 'bg-accent font-medium',
        )}
        onClick={handleRootSelect}
      >
        <FolderOpen className="h-4 w-4 flex-shrink-0 text-muted-foreground" />
        <span className="flex-1">전체 파일</span>
      </div>

      {/* 폴더 트리 */}
      {folders.map((folder) => (
        <FolderNode
          key={folder.id}
          node={folder}
          level={0}
          selectedFolderId={selectedFolderId}
          onSelect={onFolderSelect}
          onCreate={onFolderCreate}
          onDelete={onFolderDelete}
        />
      ))}

      {/* 새 루트 폴더 생성 입력 */}
      {isCreatingRoot && (
        <form
          onSubmit={handleCreateRootSubmit}
          className="flex items-center gap-2 px-2 py-1"
        >
          <Folder className="h-4 w-4 flex-shrink-0 text-muted-foreground" />
          <Input
            type="text"
            value={newFolderName}
            onChange={(e) => setNewFolderName(e.target.value)}
            placeholder="폴더 이름"
            className="h-7 text-sm"
            autoFocus
            onBlur={handleCreateRootCancel}
            onKeyDown={(e) => {
              if (e.key === 'Escape') {
                handleCreateRootCancel();
              }
            }}
          />
        </form>
      )}

      {/* 새 폴더 버튼 */}
      {onFolderCreate && !isCreatingRoot && (
        <Button
          variant="ghost"
          size="sm"
          className="w-full justify-start gap-2 text-sm"
          onClick={handleCreateRootClick}
        >
          <Plus className="h-4 w-4" />
          새 폴더
        </Button>
      )}
    </div>
  );
}
