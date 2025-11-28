# Quick Reference - State Management & API

빠른 참조를 위한 요약 가이드

## 📦 설치

```bash
npm install
# or
yarn install
```

## 🚀 시작하기

### 1. 환경 변수 설정

`.env.local` 생성:

```bash
NEXT_PUBLIC_API_URL=http://localhost:8000/api
```

### 2. 개발 서버 실행

```bash
npm run dev
```

## 🔑 인증

### 로그인

```typescript
import { useAuth } from '@/hooks';

const { login, isLoading, error } = useAuth();

await login({ email: 'user@example.com', password: 'password123' });
```

### 로그아웃

```typescript
const { logout } = useAuth();
await logout();
```

### 현재 사용자 정보

```typescript
const { user, isAuthenticated, isProfessor, isStudent } = useAuth();

if (isAuthenticated) {
  console.log(`Hello, ${user.name}!`);
}
```

## 📚 코스

### 코스 목록 조회

```typescript
import { useCourses } from '@/hooks';

const { data, isLoading, error } = useCourses({
  page: 1,
  limit: 10,
  search: '데이터베이스',
});
```

### 코스 생성

```typescript
import { useCreateCourse } from '@/hooks';

const createCourse = useCreateCourse();

await createCourse.mutateAsync({
  title: 'Database Systems',
  code: 'CS101',
  semester: '2024-1',
  description: 'Introduction to databases',
});
```

### 코스 업데이트

```typescript
import { useUpdateCourse } from '@/hooks';

const updateCourse = useUpdateCourse();

await updateCourse.mutateAsync({
  id: 'course-id',
  data: { title: 'New Title' },
});
```

### 코스 삭제

```typescript
import { useDeleteCourse } from '@/hooks';

const deleteCourse = useDeleteCourse();
await deleteCourse.mutateAsync('course-id');
```

## 🎨 UI 상태

### 사이드바

```typescript
import { useUiStore } from '@/stores';

const { sidebarOpen, toggleSidebar, setSidebarOpen } = useUiStore();

// 토글
toggleSidebar();

// 직접 설정
setSidebarOpen(true);
```

### 테마

```typescript
const { theme, setTheme } = useUiStore();

setTheme('dark'); // 'light' | 'dark' | 'system'
```

### 알림

```typescript
const { addNotification } = useUiStore();

addNotification({
  type: 'success', // 'success' | 'error' | 'warning' | 'info'
  title: '성공',
  message: '작업이 완료되었습니다.',
  duration: 3000, // 밀리초 (optional)
});
```

## 🔧 유틸리티 훅

### Debounce (검색어 입력 등)

```typescript
import { useDebounce } from '@/hooks';

const [search, setSearch] = useState('');
const debouncedSearch = useDebounce(search, 500); // 500ms 지연
```

### LocalStorage

```typescript
import { useLocalStorage } from '@/hooks';

const [value, setValue, removeValue] = useLocalStorage('key', 'defaultValue');
```

### 반응형

```typescript
import { useIsMobile, useIsTablet, useIsDesktop } from '@/hooks';

const isMobile = useIsMobile();
const isTablet = useIsTablet();
const isDesktop = useIsDesktop();
```

## 📡 직접 API 호출

```typescript
import { authApi, coursesApi } from '@/lib/api';

// 인증
const { user, tokens } = await authApi.login({ email, password });
const profile = await authApi.getProfile();

// 코스
const { courses, pagination } = await coursesApi.getCourses({ page: 1 });
const course = await coursesApi.getCourse('course-id');
const newCourse = await coursesApi.createCourse(data);
```

## 🎯 타입

```typescript
import type {
  User,
  UserRole,
  Course,
  Session,
  LoginFormData,
  RegisterFormData,
  CourseFormData,
} from '@/types';
```

## 📊 React Query

### Query Keys

```typescript
import { COURSE_QUERY_KEYS } from '@/hooks/useCourses';

// 모든 코스
COURSE_QUERY_KEYS.lists();

// 특정 파라미터의 코스 목록
COURSE_QUERY_KEYS.list({ page: 1, limit: 10 });

// 특정 코스 상세
COURSE_QUERY_KEYS.detail('course-id');
```

### 캐시 무효화

```typescript
import { useQueryClient } from '@tanstack/react-query';
import { COURSE_QUERY_KEYS } from '@/hooks/useCourses';

const queryClient = useQueryClient();

// 모든 코스 목록 캐시 무효화
queryClient.invalidateQueries({ queryKey: COURSE_QUERY_KEYS.lists() });

// 특정 코스 캐시 무효화
queryClient.invalidateQueries({ queryKey: COURSE_QUERY_KEYS.detail('course-id') });
```

## 🐛 디버깅

### React Query Devtools

개발 환경에서 자동으로 활성화됩니다. 우측 하단 버튼 클릭.

### Console Logs

```typescript
// Zustand 상태 확인
console.log(useAuthStore.getState());
console.log(useCourseStore.getState());

// React Query 캐시 확인
console.log(queryClient.getQueryCache());
```

## 🔒 보호된 라우트 예제

```typescript
'use client';

import { useAuth } from '@/hooks';
import { useRouter } from 'next/navigation';
import { useEffect } from 'react';

export default function ProtectedPage() {
  const { isAuthenticated, isLoading } = useAuth();
  const router = useRouter();

  useEffect(() => {
    if (!isLoading && !isAuthenticated) {
      router.push('/login');
    }
  }, [isAuthenticated, isLoading, router]);

  if (isLoading) return <div>Loading...</div>;
  if (!isAuthenticated) return null;

  return <div>Protected Content</div>;
}
```

## 📝 폼 예제 (with validation)

```typescript
'use client';

import { useState } from 'react';
import { useCreateCourse } from '@/hooks';

export default function CreateCourseForm() {
  const createCourse = useCreateCourse();
  const [formData, setFormData] = useState({
    title: '',
    code: '',
    semester: '',
    description: '',
  });
  const [errors, setErrors] = useState<Record<string, string>>({});

  const validate = () => {
    const newErrors: Record<string, string> = {};
    if (!formData.title) newErrors.title = '제목은 필수입니다.';
    if (!formData.code) newErrors.code = '코스 코드는 필수입니다.';
    if (!formData.semester) newErrors.semester = '학기는 필수입니다.';
    return newErrors;
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    const newErrors = validate();
    if (Object.keys(newErrors).length > 0) {
      setErrors(newErrors);
      return;
    }

    try {
      await createCourse.mutateAsync(formData);
      // 성공 시 자동으로 알림 표시됨
      setFormData({ title: '', code: '', semester: '', description: '' });
      setErrors({});
    } catch (error) {
      // 에러는 자동으로 처리됨
    }
  };

  return (
    <form onSubmit={handleSubmit} className="space-y-4">
      <div>
        <input
          type="text"
          placeholder="코스 제목"
          value={formData.title}
          onChange={(e) => setFormData({ ...formData, title: e.target.value })}
          className="w-full px-4 py-2 border rounded"
        />
        {errors.title && <p className="text-red-500 text-sm">{errors.title}</p>}
      </div>

      <button
        type="submit"
        disabled={createCourse.isPending}
        className="px-4 py-2 bg-blue-500 text-white rounded disabled:opacity-50"
      >
        {createCourse.isPending ? '생성 중...' : '코스 생성'}
      </button>
    </form>
  );
}
```

## 🌐 API 엔드포인트

```typescript
import { API_ENDPOINTS } from '@/lib/api/endpoints';

// 인증
API_ENDPOINTS.AUTH.LOGIN; // /auth/login
API_ENDPOINTS.AUTH.REGISTER; // /auth/register
API_ENDPOINTS.AUTH.PROFILE; // /auth/profile

// 코스
API_ENDPOINTS.COURSES.LIST; // /courses
API_ENDPOINTS.COURSES.DETAIL('id'); // /courses/:id
API_ENDPOINTS.COURSES.ENROLLMENTS('id'); // /courses/:id/enrollments
```

## 💡 팁

1. **에러 핸들링**: API 에러는 자동으로 파싱되고 알림으로 표시됩니다.
2. **토큰 갱신**: 401 에러 시 자동으로 리프레시 토큰으로 갱신을 시도합니다.
3. **캐시**: React Query가 자동으로 데이터를 캐싱하므로 불필요한 API 호출이 줄어듭니다.
4. **타입 안전성**: TypeScript를 활용하여 타입 에러를 사전에 방지합니다.
5. **개발 도구**: React Query Devtools로 쿼리 상태를 실시간으로 확인할 수 있습니다.
