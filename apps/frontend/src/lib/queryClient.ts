/**
 * React Query 클라이언트 설정
 */

import { QueryClient } from '@tanstack/react-query';

export const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      // 기본 옵션
      staleTime: 1000 * 60 * 5, // 5분 - 데이터가 stale 상태로 간주되는 시간
      gcTime: 1000 * 60 * 30, // 30분 (이전 cacheTime) - 캐시 유지 시간
      refetchOnWindowFocus: false, // 윈도우 포커스 시 자동 refetch 비활성화
      refetchOnReconnect: true, // 재연결 시 refetch
      retry: 1, // 실패 시 재시도 횟수
    },
    mutations: {
      // Mutation 기본 옵션
      retry: 0, // Mutation은 재시도하지 않음
    },
  },
});
