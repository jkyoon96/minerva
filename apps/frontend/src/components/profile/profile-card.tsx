'use client';

import { Avatar, AvatarImage, AvatarFallback } from '@/components/ui/avatar';
import { Card, CardHeader, CardContent } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { User as UserIcon, Mail, Calendar } from 'lucide-react';
import { Profile } from '@/types/profile';
import { UserRole } from '@/types';
import { formatDate } from '@/lib/utils';

interface ProfileCardProps {
  profile: Profile;
  className?: string;
}

const getRoleBadgeVariant = (
  role: UserRole,
): 'default' | 'secondary' | 'outline' | 'destructive' => {
  switch (role) {
    case 'admin':
      return 'destructive';
    case 'professor':
      return 'default';
    case 'ta':
      return 'secondary';
    case 'student':
      return 'outline';
    default:
      return 'outline';
  }
};

const getRoleText = (role: UserRole): string => {
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
      return role;
  }
};

export function ProfileCard({ profile, className }: ProfileCardProps) {
  const initials = profile.name
    .split(' ')
    .map((n) => n[0])
    .join('')
    .toUpperCase()
    .slice(0, 2);

  return (
    <Card className={className}>
      <CardHeader>
        <div className="flex items-center gap-4">
          {/* 아바타 */}
          <Avatar className="h-16 w-16">
            {profile.avatar ? (
              <AvatarImage src={profile.avatar} alt={profile.name} />
            ) : (
              <AvatarFallback className="bg-primary text-primary-foreground text-lg">
                {initials || <UserIcon className="h-8 w-8" />}
              </AvatarFallback>
            )}
          </Avatar>

          {/* 이름과 역할 */}
          <div className="flex-1">
            <div className="flex items-center gap-2">
              <h2 className="text-xl font-semibold">{profile.name}</h2>
              <Badge variant={getRoleBadgeVariant(profile.role)}>
                {getRoleText(profile.role)}
              </Badge>
            </div>
            <div className="mt-1 flex items-center gap-1 text-sm text-muted-foreground">
              <Mail className="h-4 w-4" />
              <span>{profile.email}</span>
            </div>
          </div>
        </div>
      </CardHeader>

      <CardContent className="space-y-4">
        {/* 소개 */}
        {profile.bio && (
          <div>
            <h3 className="mb-2 text-sm font-medium">소개</h3>
            <p className="text-sm text-muted-foreground">{profile.bio}</p>
          </div>
        )}

        {/* 가입일 */}
        <div className="flex items-center gap-2 text-sm text-muted-foreground">
          <Calendar className="h-4 w-4" />
          <span>가입일: {formatDate(profile.createdAt)}</span>
        </div>
      </CardContent>
    </Card>
  );
}
