# State Management & API Client Documentation

EduForum 프론트엔드의 상태 관리 및 API 통신 가이드입니다.

## 목차

- [개요](#개요)
- [기술 스택](#기술-스택)
- [폴더 구조](#폴더-구조)
- [API Client](#api-client)
- [Zustand Stores](#zustand-stores)
- [React Query](#react-query)
- [Custom Hooks](#custom-hooks)
- [사용 예제](#사용-예제)

## 개요

이 프로젝트는 다음과 같은 상태 관리 전략을 사용합니다:

- **Zustand**: 전역 상태 관리 (인증, UI, 코스 상태)
- **React Query**: 서버 상태 관리 및 캐싱
- **Axios**: HTTP 클라이언트
- **Custom Hooks**: 로직 재사용 및 추상화

## 기술 스택

```json
{
  "@tanstack/react-query": "^5.28.0",
  "@tanstack/react-query-devtools": "^5.28.0",
  "axios": "^1.6.7",
  "zustand": "^4.5.0"
}
```

## 폴더 구조

```
src/
├── lib/
│   ├── api/
│   │   ├── client.ts          # Axios 인스턴스 + 인터셉터
│   │   ├── types.ts           # API 타입 정의
│   │   ├── endpoints.ts       # API 엔드포인트 상수
│   │   ├── auth.ts            # 인증 API 함수
│   │   ├── courses.ts         # 코스 API 함수
│   │   └── index.ts           # API 모듈 통합 export
│   ├── queryClient.ts         # React Query 설정
│   └── providers.tsx          # 앱 프로바이더 래퍼
├── stores/
│   ├── authStore.ts           # 인증 상태 스토어
│   ├── uiStore.ts             # UI 상태 스토어
│   └── courseStore.ts         # 코스 상태 스토어
├── hooks/
│   ├── useAuth.ts             # 인증 훅
│   ├── useCourses.ts          # 코스 훅 (React Query)
│   ├── useDebounce.ts         # Debounce 훅
│   ├── useLocalStorage.ts     # LocalStorage 훅
│   ├── useMediaQuery.ts       # 미디어 쿼리 훅
│   └── index.ts               # 훅 통합 export
└── types/
    ├── auth.ts                # 인증 관련 타입
    ├── course.ts              # 코스 관련 타입
    └── index.ts               # 타입 통합 export
```

## API Client

### 기본 설정

`src/lib/api/client.ts` - Axios 기반 API 클라이언트

**주요 기능:**
- 자동 JWT 토큰 첨부
- 401 에러 시 자동 토큰 갱신
- 에러 핸들링 및 파싱
- TypeScript 타입 지원

**사용 예시:**

```typescript
import apiClient from '@/lib/api/client';

// GET 요청
const response = await apiClient.get('/courses');

// POST 요청
const response = await apiClient.post('/auth/login', {
  email: 'user@example.com',
  password: 'password',
});
```

### API 함수

#### 인증 API (`src/lib/api/auth.ts`)

```typescript
import * as authApi from '@/lib/api/auth';

// 로그인
await authApi.login({ email, password });

// 회원가입
await authApi.register({ email, password, name, role });

// 로그아웃
await authApi.logout();

// 프로필 조회
const user = await authApi.getProfile();

// 프로필 업데이트
await authApi.updateProfile({ name: 'New Name' });
```

#### 코스 API (`src/lib/api/courses.ts`)

```typescript
import * as coursesApi from '@/lib/api/courses';

// 코스 목록 조회
const { courses, pagination } = await coursesApi.getCourses({
  page: 1,
  limit: 10,
  search: '데이터베이스',
});

// 코스 상세 조회
const course = await coursesApi.getCourse('course-id');

// 코스 생성
const newCourse = await coursesApi.createCourse({
  title: 'Database Systems',
  code: 'CS101',
  semester: '2024-1',
});

// 코스 업데이트
await coursesApi.updateCourse('course-id', { title: 'New Title' });

// 코스 삭제
await coursesApi.deleteCourse('course-id');
```

## Zustand Stores

### Auth Store

전역 인증 상태 관리 (LocalStorage 영속화)

```typescript
import { useAuthStore } from '@/stores/authStore';

function MyComponent() {
  const { user, isAuthenticated, login, logout } = useAuthStore();

  return (
    <div>
      {isAuthenticated ? (
        <p>Welcome, {user.name}!</p>
      ) : (
        <button onClick={() => login({ email, password })}>Login</button>
      )}
    </div>
  );
}
```

**상태:**
- `user` - 현재 사용자 정보
- `isAuthenticated` - 인증 여부
- `isLoading` - 로딩 상태
- `error` - 에러 메시지

**액션:**
- `login()` - 로그인
- `register()` - 회원가입
- `logout()` - 로그아웃
- `fetchProfile()` - 프로필 조회

### UI Store

UI 상태 관리 (사이드바, 테마, 알림)

```typescript
import { useUiStore } from '@/stores/uiStore';

function MyComponent() {
  const { sidebarOpen, toggleSidebar, addNotification } = useUiStore();

  const handleSuccess = () => {
    addNotification({
      type: 'success',
      title: '성공',
      message: '작업이 완료되었습니다.',
      duration: 3000,
    });
  };

  return <button onClick={toggleSidebar}>Toggle Sidebar</button>;
}
```

**상태:**
- `sidebarOpen` - 사이드바 열림/닫힘
- `theme` - 테마 설정 (light/dark/system)
- `notifications` - 알림 목록

**액션:**
- `toggleSidebar()` - 사이드바 토글
- `setTheme()` - 테마 변경
- `addNotification()` - 알림 추가
- `removeNotification()` - 알림 제거

### Course Store

코스 상태 관리

```typescript
import { useCourseStore } from '@/stores/courseStore';

function CourseList() {
  const { courses, selectedCourse, selectCourseById } = useCourseStore();

  return (
    <div>
      {courses.map((course) => (
        <div key={course.id} onClick={() => selectCourseById(course.id)}>
          {course.title}
        </div>
      ))}
    </div>
  );
}
```

**상태:**
- `courses` - 코스 목록
- `selectedCourse` - 선택된 코스
- `searchQuery` - 검색어
- `selectedSemester` - 선택된 학기

**액션:**
- `setCourses()` - 코스 목록 설정
- `selectCourseById()` - 코스 선택
- `setSearchQuery()` - 검색어 설정
- `addCourse()` - 코스 추가
- `updateCourse()` - 코스 업데이트
- `removeCourse()` - 코스 제거

## React Query

### Query Client 설정

`src/lib/queryClient.ts` - React Query 클라이언트 설정

**기본 옵션:**
- `staleTime`: 5분 - 데이터가 stale 상태로 간주되는 시간
- `gcTime`: 30분 - 캐시 유지 시간
- `refetchOnWindowFocus`: false - 윈도우 포커스 시 자동 refetch 비활성화
- `retry`: 1 - 실패 시 재시도 횟수

### Devtools

개발 환경에서 React Query Devtools가 자동으로 활성화됩니다.

```typescript
// src/lib/providers.tsx에 포함됨
<ReactQueryDevtools initialIsOpen={false} position="bottom-right" />
```

## Custom Hooks

### useAuth

인증 관련 통합 훅

```typescript
import { useAuth } from '@/hooks';

function MyComponent() {
  const {
    user,
    isAuthenticated,
    isLoading,
    login,
    logout,
    isAdmin,
    isProfessor,
  } = useAuth();

  return <div>{isAuthenticated && <p>Hello, {user.name}</p>}</div>;
}
```

### useCourses

코스 목록 조회 (React Query)

```typescript
import { useCourses } from '@/hooks';

function CourseList() {
  const { data, isLoading, error } = useCourses({
    page: 1,
    limit: 10,
    search: '데이터베이스',
  });

  if (isLoading) return <div>Loading...</div>;
  if (error) return <div>Error: {error.message}</div>;

  return (
    <div>
      {data?.courses.map((course) => (
        <div key={course.id}>{course.title}</div>
      ))}
    </div>
  );
}
```

### useCreateCourse

코스 생성 Mutation

```typescript
import { useCreateCourse } from '@/hooks';

function CreateCourseForm() {
  const createCourse = useCreateCourse();

  const handleSubmit = async (data) => {
    await createCourse.mutateAsync(data);
    // 성공 시 자동으로 알림 표시 및 캐시 무효화
  };

  return (
    <form onSubmit={handleSubmit}>
      {/* 폼 필드 */}
      <button type="submit" disabled={createCourse.isPending}>
        {createCourse.isPending ? '생성 중...' : '코스 생성'}
      </button>
    </form>
  );
}
```

### useDebounce

검색어 입력 디바운싱

```typescript
import { useDebounce } from '@/hooks';
import { useState } from 'react';

function SearchInput() {
  const [search, setSearch] = useState('');
  const debouncedSearch = useDebounce(search, 500); // 500ms 지연

  // debouncedSearch를 사용하여 API 호출
  const { data } = useCourses({ search: debouncedSearch });

  return <input value={search} onChange={(e) => setSearch(e.target.value)} />;
}
```

### useMediaQuery

반응형 UI

```typescript
import { useIsMobile, useIsTablet, useIsDesktop } from '@/hooks';

function ResponsiveComponent() {
  const isMobile = useIsMobile();
  const isTablet = useIsTablet();
  const isDesktop = useIsDesktop();

  return (
    <div>
      {isMobile && <p>Mobile View</p>}
      {isTablet && <p>Tablet View</p>}
      {isDesktop && <p>Desktop View</p>}
    </div>
  );
}
```

## 사용 예제

### 로그인 페이지

```typescript
'use client';

import { useState } from 'react';
import { useRouter } from 'next/navigation';
import { useAuth } from '@/hooks';

export default function LoginPage() {
  const router = useRouter();
  const { login, isLoading, error } = useAuth();
  const [formData, setFormData] = useState({
    email: '',
    password: '',
  });

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    try {
      await login(formData);
      router.push('/dashboard');
    } catch (err) {
      // 에러는 useAuth에서 처리됨
      console.error('Login failed:', err);
    }
  };

  return (
    <form onSubmit={handleSubmit}>
      <input
        type="email"
        value={formData.email}
        onChange={(e) => setFormData({ ...formData, email: e.target.value })}
      />
      <input
        type="password"
        value={formData.password}
        onChange={(e) => setFormData({ ...formData, password: e.target.value })}
      />
      <button type="submit" disabled={isLoading}>
        {isLoading ? '로그인 중...' : '로그인'}
      </button>
      {error && <p className="text-red-500">{error}</p>}
    </form>
  );
}
```

### 코스 목록 페이지

```typescript
'use client';

import { useState } from 'react';
import { useCourses, useDebounce } from '@/hooks';

export default function CoursesPage() {
  const [search, setSearch] = useState('');
  const debouncedSearch = useDebounce(search, 500);

  const { data, isLoading, error } = useCourses({
    search: debouncedSearch,
    page: 1,
    limit: 10,
  });

  if (isLoading) return <div>Loading courses...</div>;
  if (error) return <div>Error: {error.message}</div>;

  return (
    <div>
      <input
        type="search"
        placeholder="코스 검색..."
        value={search}
        onChange={(e) => setSearch(e.target.value)}
      />

      <div>
        {data?.courses.map((course) => (
          <div key={course.id}>
            <h3>{course.title}</h3>
            <p>{course.code} - {course.semester}</p>
          </div>
        ))}
      </div>

      <div>
        Total: {data?.pagination.total} courses
        (Page {data?.pagination.page} of {data?.pagination.totalPages})
      </div>
    </div>
  );
}
```

### 알림 표시

```typescript
'use client';

import { useUiStore } from '@/stores/uiStore';
import { useEffect } from 'react';

export function NotificationList() {
  const { notifications, removeNotification } = useUiStore();

  return (
    <div className="fixed top-4 right-4 z-50 space-y-2">
      {notifications.map((notification) => (
        <div
          key={notification.id}
          className={`p-4 rounded-lg shadow-lg ${
            notification.type === 'success' ? 'bg-green-500' :
            notification.type === 'error' ? 'bg-red-500' :
            notification.type === 'warning' ? 'bg-yellow-500' :
            'bg-blue-500'
          } text-white`}
        >
          <h4 className="font-bold">{notification.title}</h4>
          {notification.message && <p>{notification.message}</p>}
          <button onClick={() => removeNotification(notification.id)}>
            닫기
          </button>
        </div>
      ))}
    </div>
  );
}
```

## 환경 변수

`.env.local` 파일에 다음 변수를 설정하세요:

```bash
NEXT_PUBLIC_API_URL=http://localhost:8000/api
```

## 개발 팁

1. **React Query Devtools**: 개발 환경에서 우측 하단 버튼을 클릭하여 쿼리 상태를 확인할 수 있습니다.

2. **에러 핸들링**: API 에러는 자동으로 파싱되어 `ApiError` 타입으로 반환됩니다.

3. **토큰 갱신**: 401 에러 발생 시 자동으로 리프레시 토큰을 사용하여 액세스 토큰을 갱신합니다.

4. **캐시 무효화**: Mutation 성공 시 관련 쿼리 캐시가 자동으로 무효화됩니다.

5. **알림**: `useUiStore`의 `addNotification`을 사용하여 사용자에게 피드백을 제공하세요.

## 참고 자료

- [React Query 공식 문서](https://tanstack.com/query/latest/docs/react/overview)
- [Zustand 공식 문서](https://docs.pmnd.rs/zustand/getting-started/introduction)
- [Axios 공식 문서](https://axios-http.com/docs/intro)
