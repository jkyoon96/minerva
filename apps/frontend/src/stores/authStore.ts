/**
 * 인증 상태 관리 스토어 (Zustand)
 * - 사용자 인증 상태
 * - 로그인/로그아웃 액션
 * - LocalStorage 영속화
 */

import { create } from 'zustand';
import { persist, createJSONStorage } from 'zustand/middleware';
import { User } from '@/types';
import * as authApi from '@/lib/api/auth';
import * as twoFactorApi from '@/lib/api/two-factor';
import { LoginRequest, RegisterRequest } from '@/lib/api/types';

interface AuthState {
  // 상태
  user: User | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  error: string | null;

  // 2FA 상태
  twoFactorRequired: boolean;
  temporaryToken: string | null;

  // 액션
  setUser: (user: User | null) => void;
  updateUser: (userData: Partial<User>) => void;
  setLoading: (isLoading: boolean) => void;
  setError: (error: string | null) => void;
  login: (credentials: LoginRequest) => Promise<void>;
  register: (data: RegisterRequest) => Promise<void>;
  logout: () => Promise<void>;
  fetchProfile: () => Promise<void>;
  clearError: () => void;

  // 2FA 액션
  setTwoFactorRequired: (required: boolean) => void;
  setTemporaryToken: (token: string | null) => void;
  completeTwoFactorLogin: (code: string, useBackupCode?: boolean) => Promise<void>;
}

export const useAuthStore = create<AuthState>()(
  persist(
    (set, get) => ({
      // 초기 상태
      user: null,
      isAuthenticated: false,
      isLoading: false,
      error: null,

      // 2FA 초기 상태
      twoFactorRequired: false,
      temporaryToken: null,

      // 사용자 설정
      setUser: (user) => {
        set({
          user,
          isAuthenticated: !!user,
        });
      },

      // 사용자 정보 업데이트 (부분 업데이트)
      updateUser: (userData) => {
        const { user } = get();
        if (user) {
          set({
            user: { ...user, ...userData },
          });
        }
      },

      // 로딩 상태 설정
      setLoading: (isLoading) => {
        set({ isLoading });
      },

      // 에러 설정
      setError: (error) => {
        set({ error });
      },

      // 에러 초기화
      clearError: () => {
        set({ error: null });
      },

      // 로그인
      login: async (credentials) => {
        set({ isLoading: true, error: null });
        try {
          const response = await authApi.login(credentials);

          // 2FA가 필요한 경우
          if ((response as any).requiresTwoFactor) {
            set({
              twoFactorRequired: true,
              temporaryToken: (response as any).temporaryToken,
              isLoading: false,
            });
            return;
          }

          // 일반 로그인 성공
          const { user, tokens } = response;

          // 토큰 저장
          localStorage.setItem('accessToken', tokens.accessToken);
          localStorage.setItem('refreshToken', tokens.refreshToken);

          set({
            user,
            isAuthenticated: true,
            isLoading: false,
            twoFactorRequired: false,
            temporaryToken: null,
          });
        } catch (error: any) {
          set({
            error: error.message || '로그인에 실패했습니다.',
            isLoading: false,
          });
          throw error;
        }
      },

      // 회원가입
      register: async (data) => {
        set({ isLoading: true, error: null });
        try {
          const { user, tokens } = await authApi.register(data);

          // 토큰 저장
          localStorage.setItem('accessToken', tokens.accessToken);
          localStorage.setItem('refreshToken', tokens.refreshToken);

          set({
            user,
            isAuthenticated: true,
            isLoading: false,
          });
        } catch (error: any) {
          set({
            error: error.message || '회원가입에 실패했습니다.',
            isLoading: false,
          });
          throw error;
        }
      },

      // 로그아웃
      logout: async () => {
        set({ isLoading: true });
        try {
          await authApi.logout();
        } catch (error) {
          console.error('Logout API error:', error);
        } finally {
          // 로컬 스토리지 정리
          localStorage.removeItem('accessToken');
          localStorage.removeItem('refreshToken');

          set({
            user: null,
            isAuthenticated: false,
            isLoading: false,
            error: null,
            twoFactorRequired: false,
            temporaryToken: null,
          });
        }
      },

      // 프로필 조회
      fetchProfile: async () => {
        set({ isLoading: true, error: null });
        try {
          const user = await authApi.getProfile();
          set({
            user,
            isAuthenticated: true,
            isLoading: false,
          });
        } catch (error: any) {
          set({
            error: error.message || '프로필 조회에 실패했습니다.',
            isLoading: false,
            user: null,
            isAuthenticated: false,
          });
          // 토큰 제거
          localStorage.removeItem('accessToken');
          localStorage.removeItem('refreshToken');
        }
      },

      // 2FA 필요 여부 설정
      setTwoFactorRequired: (required) => {
        set({ twoFactorRequired: required });
      },

      // 임시 토큰 설정
      setTemporaryToken: (token) => {
        set({ temporaryToken: token });
      },

      // 2FA 로그인 완료
      completeTwoFactorLogin: async (code, useBackupCode = false) => {
        set({ isLoading: true, error: null });
        try {
          const { temporaryToken } = get();
          if (!temporaryToken) {
            throw new Error('임시 토큰이 없습니다.');
          }

          const { user, tokens } = await twoFactorApi.loginWithTwoFactor({
            temporaryToken,
            code,
            useBackupCode,
          });

          // 토큰 저장
          localStorage.setItem('accessToken', tokens.accessToken);
          localStorage.setItem('refreshToken', tokens.refreshToken);

          set({
            user,
            isAuthenticated: true,
            isLoading: false,
            twoFactorRequired: false,
            temporaryToken: null,
          });
        } catch (error: any) {
          set({
            error: error.message || '2FA 인증에 실패했습니다.',
            isLoading: false,
          });
          throw error;
        }
      },
    }),
    {
      name: 'auth-storage', // localStorage 키 이름
      storage: createJSONStorage(() => localStorage),
      partialize: (state) => ({
        // user만 영속화 (isLoading, error는 제외)
        user: state.user,
        isAuthenticated: state.isAuthenticated,
      }),
    },
  ),
);
