'use client';

import { useState, useEffect } from 'react';
import { Card, CardHeader, CardTitle, CardContent } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Alert } from '@/components/ui/alert';
import { Separator } from '@/components/ui/separator';
import { Loader2, Mail, Lock } from 'lucide-react';
import { AvatarUpload } from '@/components/profile/avatar-upload';
import { ProfileForm } from '@/components/profile/profile-form';
import { PasswordChangeModal } from '@/components/profile/password-change-modal';
import { EmailChangeModal } from '@/components/profile/email-change-modal';
import { useAuthStore } from '@/stores/authStore';
import {
  getProfile,
  updateProfile,
  uploadAvatar,
  deleteAvatar,
  changeEmail,
  changePassword,
} from '@/lib/api/profile';
import { Profile, ProfileUpdateRequest, EmailChangeRequest, PasswordChangeRequest } from '@/types/profile';

export default function ProfilePage() {
  const { user, updateUser } = useAuthStore();
  const [profile, setProfile] = useState<Profile | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [isUpdating, setIsUpdating] = useState(false);
  const [isUploadingAvatar, setIsUploadingAvatar] = useState(false);
  const [error, setError] = useState<string | null>(null);

  // 모달 상태
  const [passwordModalOpen, setPasswordModalOpen] = useState(false);
  const [emailModalOpen, setEmailModalOpen] = useState(false);

  // 프로필 조회
  useEffect(() => {
    const fetchProfile = async () => {
      try {
        setIsLoading(true);
        setError(null);
        const data = await getProfile();
        setProfile(data);
      } catch (err) {
        setError(err instanceof Error ? err.message : '프로필을 불러오는 중 오류가 발생했습니다.');
      } finally {
        setIsLoading(false);
      }
    };

    fetchProfile();
  }, []);

  // 프로필 업데이트
  const handleProfileUpdate = async (data: ProfileUpdateRequest) => {
    try {
      setIsUpdating(true);
      setError(null);
      const updatedProfile = await updateProfile(data);
      setProfile(updatedProfile);

      // authStore 업데이트
      if (user) {
        updateUser({
          ...user,
          name: updatedProfile.name,
          bio: updatedProfile.bio,
        });
      }
    } catch (err) {
      const errorMessage = err instanceof Error ? err.message : '프로필 업데이트 중 오류가 발생했습니다.';
      setError(errorMessage);
      throw err;
    } finally {
      setIsUpdating(false);
    }
  };

  // 아바타 업로드
  const handleAvatarUpload = async (file: File) => {
    try {
      setIsUploadingAvatar(true);
      setError(null);
      const avatarUrl = await uploadAvatar(file);

      // 프로필 업데이트
      setProfile((prev) => prev ? { ...prev, avatar: avatarUrl } : null);

      // authStore 업데이트
      if (user) {
        updateUser({
          ...user,
          avatar: avatarUrl,
        });
      }
    } catch (err) {
      const errorMessage = err instanceof Error ? err.message : '아바타 업로드 중 오류가 발생했습니다.';
      setError(errorMessage);
      throw err;
    } finally {
      setIsUploadingAvatar(false);
    }
  };

  // 아바타 삭제
  const handleAvatarDelete = async () => {
    try {
      setIsUploadingAvatar(true);
      setError(null);
      await deleteAvatar();

      // 프로필 업데이트
      setProfile((prev) => prev ? { ...prev, avatar: undefined } : null);

      // authStore 업데이트
      if (user) {
        updateUser({
          ...user,
          avatar: undefined,
        });
      }
    } catch (err) {
      const errorMessage = err instanceof Error ? err.message : '아바타 삭제 중 오류가 발생했습니다.';
      setError(errorMessage);
      throw err;
    } finally {
      setIsUploadingAvatar(false);
    }
  };

  // 이메일 변경
  const handleEmailChange = async (data: EmailChangeRequest) => {
    try {
      setError(null);
      await changeEmail(data);
    } catch (err) {
      const errorMessage = err instanceof Error ? err.message : '이메일 변경 중 오류가 발생했습니다.';
      setError(errorMessage);
      throw err;
    }
  };

  // 비밀번호 변경
  const handlePasswordChange = async (data: PasswordChangeRequest) => {
    try {
      setError(null);
      await changePassword(data);
    } catch (err) {
      const errorMessage = err instanceof Error ? err.message : '비밀번호 변경 중 오류가 발생했습니다.';
      setError(errorMessage);
      throw err;
    }
  };

  if (isLoading) {
    return (
      <div className="flex h-[50vh] items-center justify-center">
        <Loader2 className="h-8 w-8 animate-spin text-muted-foreground" />
      </div>
    );
  }

  if (!profile) {
    return (
      <div className="container max-w-4xl py-8">
        <Alert variant="destructive">
          <p>프로필을 불러올 수 없습니다.</p>
        </Alert>
      </div>
    );
  }

  return (
    <div className="container max-w-4xl py-8">
      <div className="mb-8">
        <h1 className="text-3xl font-bold">프로필 설정</h1>
        <p className="mt-2 text-muted-foreground">
          프로필 정보를 관리하고 계정 보안을 설정할 수 있습니다.
        </p>
      </div>

      {error && (
        <Alert variant="destructive" className="mb-6">
          <p>{error}</p>
        </Alert>
      )}

      <div className="space-y-6">
        {/* 프로필 사진 */}
        <Card>
          <CardHeader>
            <CardTitle>프로필 사진</CardTitle>
          </CardHeader>
          <CardContent>
            <AvatarUpload
              currentAvatar={profile.avatar}
              userName={profile.name}
              onUpload={handleAvatarUpload}
              onDelete={handleAvatarDelete}
              isUploading={isUploadingAvatar}
            />
          </CardContent>
        </Card>

        {/* 기본 정보 */}
        <Card>
          <CardHeader>
            <CardTitle>기본 정보</CardTitle>
          </CardHeader>
          <CardContent>
            <ProfileForm
              initialData={{
                name: profile.name,
                bio: profile.bio,
              }}
              onSubmit={handleProfileUpdate}
              isLoading={isUpdating}
            />
          </CardContent>
        </Card>

        {/* 계정 보안 */}
        <Card>
          <CardHeader>
            <CardTitle>계정 보안</CardTitle>
          </CardHeader>
          <CardContent className="space-y-4">
            {/* 이메일 */}
            <div className="flex items-center justify-between">
              <div>
                <div className="flex items-center gap-2">
                  <Mail className="h-4 w-4 text-muted-foreground" />
                  <span className="font-medium">이메일</span>
                </div>
                <p className="mt-1 text-sm text-muted-foreground">
                  {profile.email}
                </p>
              </div>
              <Button
                variant="outline"
                size="sm"
                onClick={() => setEmailModalOpen(true)}
              >
                변경
              </Button>
            </div>

            <Separator />

            {/* 비밀번호 */}
            <div className="flex items-center justify-between">
              <div>
                <div className="flex items-center gap-2">
                  <Lock className="h-4 w-4 text-muted-foreground" />
                  <span className="font-medium">비밀번호</span>
                </div>
                <p className="mt-1 text-sm text-muted-foreground">
                  ••••••••
                </p>
              </div>
              <Button
                variant="outline"
                size="sm"
                onClick={() => setPasswordModalOpen(true)}
              >
                변경
              </Button>
            </div>
          </CardContent>
        </Card>
      </div>

      {/* 비밀번호 변경 모달 */}
      <PasswordChangeModal
        open={passwordModalOpen}
        onClose={() => setPasswordModalOpen(false)}
        onSubmit={handlePasswordChange}
      />

      {/* 이메일 변경 모달 */}
      <EmailChangeModal
        open={emailModalOpen}
        currentEmail={profile.email}
        onClose={() => setEmailModalOpen(false)}
        onSubmit={handleEmailChange}
      />
    </div>
  );
}
