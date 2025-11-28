/**
 * UI 상태 관리 스토어 (Zustand)
 * - 사이드바 열림/닫힘
 * - 테마 설정
 * - 알림 관리
 */

import { create } from 'zustand';
import { persist, createJSONStorage } from 'zustand/middleware';

export type Theme = 'light' | 'dark' | 'system';

export interface Notification {
  id: string;
  type: 'success' | 'error' | 'warning' | 'info';
  title: string;
  message?: string;
  duration?: number; // 밀리초, undefined면 자동으로 사라지지 않음
  createdAt: number;
}

interface UiState {
  // 상태
  sidebarOpen: boolean;
  theme: Theme;
  notifications: Notification[];

  // 액션
  toggleSidebar: () => void;
  setSidebarOpen: (open: boolean) => void;
  setTheme: (theme: Theme) => void;
  addNotification: (
    notification: Omit<Notification, 'id' | 'createdAt'>,
  ) => void;
  removeNotification: (id: string) => void;
  clearNotifications: () => void;
}

export const useUiStore = create<UiState>()(
  persist(
    (set, get) => ({
      // 초기 상태
      sidebarOpen: true,
      theme: 'system',
      notifications: [],

      // 사이드바 토글
      toggleSidebar: () => {
        set((state) => ({ sidebarOpen: !state.sidebarOpen }));
      },

      // 사이드바 열림/닫힘 설정
      setSidebarOpen: (open) => {
        set({ sidebarOpen: open });
      },

      // 테마 설정
      setTheme: (theme) => {
        set({ theme });

        // 실제 테마 적용
        if (typeof window !== 'undefined') {
          const root = window.document.documentElement;

          if (theme === 'system') {
            const systemTheme = window.matchMedia('(prefers-color-scheme: dark)')
              .matches
              ? 'dark'
              : 'light';
            root.classList.toggle('dark', systemTheme === 'dark');
          } else {
            root.classList.toggle('dark', theme === 'dark');
          }
        }
      },

      // 알림 추가
      addNotification: (notification) => {
        const id = `notification-${Date.now()}-${Math.random()}`;
        const newNotification: Notification = {
          ...notification,
          id,
          createdAt: Date.now(),
        };

        set((state) => ({
          notifications: [...state.notifications, newNotification],
        }));

        // 자동 제거 (duration이 설정된 경우)
        if (notification.duration) {
          setTimeout(() => {
            get().removeNotification(id);
          }, notification.duration);
        }
      },

      // 알림 제거
      removeNotification: (id) => {
        set((state) => ({
          notifications: state.notifications.filter((n) => n.id !== id),
        }));
      },

      // 모든 알림 제거
      clearNotifications: () => {
        set({ notifications: [] });
      },
    }),
    {
      name: 'ui-storage',
      storage: createJSONStorage(() => localStorage),
      partialize: (state) => ({
        // notifications는 영속화하지 않음
        sidebarOpen: state.sidebarOpen,
        theme: state.theme,
      }),
    },
  ),
);
