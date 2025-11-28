/**
 * 인증 관련 커스텀 훅
 * authStore를 사용하여 인증 상태 및 액션 제공
 */

import { useEffect } from 'react';
import { useAuthStore } from '@/stores/authStore';

export const useAuth = () => {
  const {
    user,
    isAuthenticated,
    isLoading,
    error,
    login,
    register,
    logout,
    fetchProfile,
    clearError,
  } = useAuthStore();

  // 앱 시작 시 토큰이 있으면 프로필 조회
  useEffect(() => {
    const token = localStorage.getItem('accessToken');
    if (token && !user && !isLoading) {
      fetchProfile();
    }
  }, []);

  return {
    // 상태
    user,
    isAuthenticated,
    isLoading,
    error,

    // 액션
    login,
    register,
    logout,
    fetchProfile,
    clearError,

    // 편의 기능
    isAdmin: user?.role === 'admin',
    isProfessor: user?.role === 'professor',
    isTA: user?.role === 'ta',
    isStudent: user?.role === 'student',
  };
};
