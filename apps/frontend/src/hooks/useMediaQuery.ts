/**
 * 미디어 쿼리 훅
 * CSS 미디어 쿼리를 React에서 사용
 * 반응형 UI에 유용
 */

import { useState, useEffect } from 'react';

export function useMediaQuery(query: string): boolean {
  const [matches, setMatches] = useState(false);

  useEffect(() => {
    // SSR 대응
    if (typeof window === 'undefined') {
      return;
    }

    const mediaQuery = window.matchMedia(query);

    // 초기값 설정
    setMatches(mediaQuery.matches);

    // 변경 감지
    const handler = (event: MediaQueryListEvent) => {
      setMatches(event.matches);
    };

    // 이벤트 리스너 등록
    // addEventListener 사용 (구형 브라우저 호환성)
    if (mediaQuery.addEventListener) {
      mediaQuery.addEventListener('change', handler);
    } else {
      // fallback for older browsers
      mediaQuery.addListener(handler);
    }

    // 클린업
    return () => {
      if (mediaQuery.removeEventListener) {
        mediaQuery.removeEventListener('change', handler);
      } else {
        mediaQuery.removeListener(handler);
      }
    };
  }, [query]);

  return matches;
}

// 일반적인 미디어 쿼리 헬퍼
export const useIsMobile = () => useMediaQuery('(max-width: 768px)');
export const useIsTablet = () =>
  useMediaQuery('(min-width: 769px) and (max-width: 1024px)');
export const useIsDesktop = () => useMediaQuery('(min-width: 1025px)');
export const useIsDarkMode = () => useMediaQuery('(prefers-color-scheme: dark)');
