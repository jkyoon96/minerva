'use client';

import { useState, useEffect } from 'react';
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogDescription,
  DialogFooter,
} from '@/components/ui/dialog';
import { Button } from '@/components/ui/button';
import { Label } from '@/components/ui/label';
import { RadioGroup, RadioGroupItem } from '@/components/ui/radio-group';
import { Alert } from '@/components/ui/alert';
import { Globe, Users, Lock, ShieldCheck } from 'lucide-react';
import { StoredFile, FilePermissionType } from '@/types/file';

interface FilePermissionModalProps {
  file: StoredFile | null;
  open: boolean;
  onClose: () => void;
  onSave: (file: StoredFile, permission: FilePermissionType) => Promise<void>;
}

const PERMISSION_OPTIONS: Array<{
  value: FilePermissionType;
  label: string;
  description: string;
  icon: typeof Globe;
}> = [
  {
    value: 'public',
    label: '공개',
    description: '누구나 이 파일에 접근할 수 있습니다',
    icon: Globe,
  },
  {
    value: 'enrolled',
    label: '수강생',
    description: '코스에 등록된 사용자만 접근할 수 있습니다',
    icon: Users,
  },
  {
    value: 'instructors',
    label: '교수진',
    description: '교수와 조교만 접근할 수 있습니다',
    icon: ShieldCheck,
  },
  {
    value: 'private',
    label: '비공개',
    description: '업로더만 접근할 수 있습니다',
    icon: Lock,
  },
];

export function FilePermissionModal({
  file,
  open,
  onClose,
  onSave,
}: FilePermissionModalProps) {
  const [permission, setPermission] = useState<FilePermissionType>('enrolled');
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (file) {
      setPermission(file.permission);
    }
    setError(null);
  }, [file, open]);

  const handleSave = async () => {
    if (!file) return;

    setIsLoading(true);
    setError(null);

    try {
      await onSave(file, permission);
      onClose();
    } catch (err) {
      setError(err instanceof Error ? err.message : '권한 설정 중 오류가 발생했습니다.');
    } finally {
      setIsLoading(false);
    }
  };

  if (!file) return null;

  return (
    <Dialog open={open} onOpenChange={onClose}>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>파일 권한 설정</DialogTitle>
          <DialogDescription>
            {file.originalName}의 접근 권한을 설정합니다.
          </DialogDescription>
        </DialogHeader>

        <div className="space-y-4 py-4">
          <RadioGroup
            value={permission}
            onValueChange={(value) => setPermission(value as FilePermissionType)}
          >
            <div className="space-y-3">
              {PERMISSION_OPTIONS.map((option) => {
                const Icon = option.icon;
                return (
                  <div
                    key={option.value}
                    className="flex items-start space-x-3 rounded-lg border p-4 transition-colors hover:bg-accent"
                  >
                    <RadioGroupItem
                      value={option.value}
                      id={option.value}
                      className="mt-1"
                    />
                    <div className="flex flex-1 items-start gap-3">
                      <div className="rounded-md bg-primary/10 p-2">
                        <Icon className="h-4 w-4 text-primary" />
                      </div>
                      <div className="flex-1 space-y-1">
                        <Label
                          htmlFor={option.value}
                          className="cursor-pointer text-base font-medium"
                        >
                          {option.label}
                        </Label>
                        <p className="text-sm text-muted-foreground">
                          {option.description}
                        </p>
                      </div>
                    </div>
                  </div>
                );
              })}
            </div>
          </RadioGroup>

          {error && (
            <Alert variant="destructive">
              <p className="text-sm">{error}</p>
            </Alert>
          )}
        </div>

        <DialogFooter>
          <Button
            variant="outline"
            onClick={onClose}
            disabled={isLoading}
          >
            취소
          </Button>
          <Button
            onClick={handleSave}
            disabled={isLoading || permission === file.permission}
          >
            {isLoading ? '저장 중...' : '저장'}
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}
