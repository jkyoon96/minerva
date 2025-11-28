# Setup Verification Checklist

FE-003 구현이 올바르게 완료되었는지 확인하는 체크리스트입니다.

## 📁 파일 구조 확인

### API Client
- [ ] `/src/lib/api/client.ts` - Axios 인스턴스
- [ ] `/src/lib/api/types.ts` - API 타입 정의
- [ ] `/src/lib/api/endpoints.ts` - 엔드포인트 상수
- [ ] `/src/lib/api/auth.ts` - 인증 API
- [ ] `/src/lib/api/courses.ts` - 코스 API
- [ ] `/src/lib/api/index.ts` - 통합 export

### Zustand Stores
- [ ] `/src/stores/authStore.ts` - 인증 스토어
- [ ] `/src/stores/uiStore.ts` - UI 스토어
- [ ] `/src/stores/courseStore.ts` - 코스 스토어
- [ ] `/src/stores/index.ts` - 통합 export

### React Query
- [ ] `/src/lib/queryClient.ts` - Query Client 설정
- [ ] `/src/lib/providers.tsx` - Providers 래퍼

### Custom Hooks
- [ ] `/src/hooks/useAuth.ts` - 인증 훅
- [ ] `/src/hooks/useCourses.ts` - 코스 훅
- [ ] `/src/hooks/useDebounce.ts` - Debounce 훅
- [ ] `/src/hooks/useLocalStorage.ts` - LocalStorage 훅
- [ ] `/src/hooks/useMediaQuery.ts` - MediaQuery 훅
- [ ] `/src/hooks/index.ts` - 통합 export

### Type Definitions
- [ ] `/src/types/auth.ts` - 인증 타입
- [ ] `/src/types/course.ts` - 코스 타입
- [ ] `/src/types/index.ts` - 업데이트됨

### Layout & Examples
- [ ] `/src/app/layout.tsx` - Providers 추가됨
- [ ] `/src/components/examples/CourseListExample.tsx` - 예제 컴포넌트

### Documentation
- [ ] `/STATE_MANAGEMENT.md` - 상세 가이드
- [ ] `/QUICK_REFERENCE.md` - 빠른 참조
- [ ] `/FE-003_IMPLEMENTATION_SUMMARY.md` - 구현 요약

## 📦 Dependencies 확인

`package.json`에 다음 패키지가 있는지 확인:

```bash
grep -E "(react-query|axios|zustand)" package.json
```

예상 출력:
```
"@tanstack/react-query": "^5.28.0",
"@tanstack/react-query-devtools": "^5.28.0",
"axios": "^1.6.7",
"zustand": "^4.5.0"
```

## 🔧 설치 확인

```bash
# 의존성 설치
npm install

# 또는
yarn install
```

설치 중 에러가 없어야 합니다.

## 🚀 실행 확인

```bash
# 개발 서버 실행
npm run dev
```

확인 사항:
- [ ] 컴파일 에러 없음
- [ ] TypeScript 에러 없음
- [ ] 서버가 `http://localhost:3000`에서 실행됨

## 🧪 타입 체크

```bash
npm run type-check
```

확인 사항:
- [ ] TypeScript 컴파일 에러 없음
- [ ] 타입 정의가 올바름

## 🌐 환경 변수

`.env.local` 파일 생성:

```bash
NEXT_PUBLIC_API_URL=http://localhost:8000/api
```

확인 사항:
- [ ] `.env.local` 파일 존재
- [ ] `NEXT_PUBLIC_API_URL` 설정됨

## 🔍 브라우저 확인

개발 서버 실행 후:

### React Query Devtools
- [ ] 우측 하단에 React Query Devtools 버튼 표시됨
- [ ] 버튼 클릭 시 Devtools 패널 열림

### Console 확인
브라우저 콘솔에서:

```javascript
// Zustand 스토어 확인
window.__ZUSTAND_STORES__ // 또는 개발자 도구에서 직접 확인
```

## 📝 코드 테스트

### 1. useAuth 훅 테스트

임시 테스트 페이지 생성 (`src/app/test/page.tsx`):

```typescript
'use client';

import { useAuth } from '@/hooks';

export default function TestPage() {
  const { user, isAuthenticated } = useAuth();

  return (
    <div className="p-4">
      <h1>Auth Test</h1>
      <pre>{JSON.stringify({ user, isAuthenticated }, null, 2)}</pre>
    </div>
  );
}
```

확인 사항:
- [ ] 페이지가 렌더링됨
- [ ] user와 isAuthenticated 값이 표시됨
- [ ] 타입 에러 없음

### 2. useCourses 훅 테스트

```typescript
'use client';

import { useCourses } from '@/hooks';

export default function TestPage() {
  const { data, isLoading, error } = useCourses({ page: 1, limit: 10 });

  if (isLoading) return <div>Loading...</div>;
  if (error) return <div>Error: {error.message}</div>;

  return (
    <div className="p-4">
      <h1>Courses Test</h1>
      <pre>{JSON.stringify(data, null, 2)}</pre>
    </div>
  );
}
```

확인 사항:
- [ ] 페이지가 렌더링됨
- [ ] 로딩 상태 표시됨
- [ ] API 호출이 시도됨 (Network 탭)
- [ ] React Query Devtools에서 쿼리 확인 가능

### 3. UI Store 테스트

```typescript
'use client';

import { useUiStore } from '@/stores';

export default function TestPage() {
  const { sidebarOpen, toggleSidebar, addNotification } = useUiStore();

  return (
    <div className="p-4 space-y-4">
      <h1>UI Store Test</h1>

      <div>
        <p>Sidebar Open: {sidebarOpen ? 'Yes' : 'No'}</p>
        <button onClick={toggleSidebar} className="px-4 py-2 bg-blue-500 text-white rounded">
          Toggle Sidebar
        </button>
      </div>

      <button
        onClick={() => addNotification({
          type: 'success',
          title: 'Test',
          message: 'This is a test notification',
          duration: 3000,
        })}
        className="px-4 py-2 bg-green-500 text-white rounded"
      >
        Add Notification
      </button>
    </div>
  );
}
```

확인 사항:
- [ ] 버튼 클릭 시 사이드바 상태 변경됨
- [ ] 알림 추가 시 알림이 표시됨
- [ ] 3초 후 알림이 자동으로 사라짐

## 🔐 API Client 테스트

브라우저 콘솔에서:

```javascript
// API 엔드포인트 확인
import { API_ENDPOINTS } from '@/lib/api/endpoints';
console.log(API_ENDPOINTS);

// API Client 확인
import apiClient from '@/lib/api/client';
console.log(apiClient.defaults.baseURL); // http://localhost:8000/api
```

## ⚠️ 일반적인 문제 해결

### 1. "Cannot find module" 에러

```bash
# node_modules 삭제 후 재설치
rm -rf node_modules package-lock.json
npm install
```

### 2. TypeScript 에러

```bash
# TypeScript 캐시 클리어
rm -rf .next
npm run type-check
```

### 3. React Query Devtools가 표시되지 않음

확인:
- `NODE_ENV`가 'development'인지
- `src/lib/providers.tsx`가 올바르게 import 되었는지
- 브라우저 콘솔에 에러가 없는지

### 4. API 호출 실패

확인:
- `.env.local`의 `NEXT_PUBLIC_API_URL`이 올바른지
- 백엔드 서버가 실행 중인지 (`http://localhost:8000`)
- CORS 설정이 올바른지

## ✅ 최종 확인

모든 항목을 확인한 후:

- [ ] 파일 구조 완료
- [ ] Dependencies 설치 완료
- [ ] 타입 체크 통과
- [ ] 개발 서버 실행 가능
- [ ] 브라우저에서 정상 작동
- [ ] React Query Devtools 작동
- [ ] 예제 코드 실행 가능

## 🎉 완료!

모든 항목이 체크되었다면 FE-003 구현이 성공적으로 완료되었습니다!

## 📚 다음 단계

1. **STATE_MANAGEMENT.md** 읽기
2. **QUICK_REFERENCE.md**로 빠른 참조
3. 실제 페이지에 적용 시작
4. 추가 API 엔드포인트 구현 (세션, 과제 등)

---

문제가 발생하면 위 문서들을 참고하거나, 각 파일의 주석을 확인하세요.
