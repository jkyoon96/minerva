# EduForum Frontend - Quick Start Guide

## 빠른 시작

### 1. 개발 환경 설정

```bash
cd /mnt/d/Development/git/minerva/apps/frontend

# 의존성 설치
npm install

# 환경 변수 설정
cp .env.example .env.local

# .env.local 편집
NEXT_PUBLIC_API_URL=http://localhost:8000/api

# 개발 서버 실행
npm run dev
```

개발 서버: http://localhost:3000

### 2. 프로젝트 구조

```
apps/frontend/src/
├── app/                    # Next.js 14 App Router 페이지
│   ├── (auth)/            # 인증 페이지 (로그인, 회원가입)
│   ├── (dashboard)/       # 대시보드 페이지
│   │   ├── professor/     # 교수 페이지
│   │   ├── student/       # 학생 페이지
│   │   └── courses/       # 코스 상세 페이지
│   └── (marketing)/       # 랜딩 페이지
├── components/            # React 컴포넌트
│   ├── ui/               # shadcn/ui 기본 컴포넌트
│   ├── common/           # 공통 컴포넌트
│   ├── dashboard/        # 대시보드 컴포넌트
│   ├── course/           # 코스 관련 컴포넌트
│   ├── session/          # 세션 관련 컴포넌트
│   ├── assignment/       # 과제 관련 컴포넌트
│   └── enrollment/       # 수강 관리 컴포넌트
├── lib/                   # 유틸리티 및 설정
│   └── api/              # API 클라이언트
├── stores/               # Zustand 상태 관리
├── types/                # TypeScript 타입 정의
└── styles/               # 전역 스타일
```

### 3. 주요 기능별 라우트

#### 교수용
- `/professor` - 교수 대시보드
- `/professor/courses` - 코스 목록
- `/professor/courses/new` - 새 코스 만들기
- `/courses/[courseId]` - 코스 상세
- `/courses/[courseId]/settings` - 코스 설정

#### 학생용
- `/student` - 학생 대시보드
- `/student/courses` - 수강 코스 목록

#### 공통
- `/login` - 로그인
- `/register` - 회원가입

### 4. 자주 사용하는 컴포넌트

#### UI 컴포넌트
```tsx
import { Button } from '@/components/ui/button';
import { Card, CardHeader, CardTitle, CardContent } from '@/components/ui/card';
import { Input } from '@/components/ui/input';
import { Badge } from '@/components/ui/badge';
```

#### 커스텀 컴포넌트
```tsx
import { StatCard } from '@/components/ui/stat-card';
import { CourseCard } from '@/components/course';
import { FileUpload } from '@/components/assignment';
```

### 5. API 사용 예시

```tsx
import { useQuery, useMutation } from '@tanstack/react-query';
import { getCourses, createCourse } from '@/lib/api/courses';

function CoursesPage() {
  // 데이터 조회
  const { data, isLoading } = useQuery({
    queryKey: ['courses'],
    queryFn: getCourses,
  });

  // 데이터 변경
  const createMutation = useMutation({
    mutationFn: createCourse,
    onSuccess: () => {
      // 성공 처리
    },
  });

  return (
    <div>
      {isLoading ? <LoadingSpinner /> : <CourseList courses={data?.courses} />}
    </div>
  );
}
```

### 6. 상태 관리

```tsx
import { useAuthStore } from '@/stores/authStore';
import { useCourseStore } from '@/stores/courseStore';

function Component() {
  // 인증 상태
  const { user, isAuthenticated } = useAuthStore();

  // 코스 상태
  const { courses, setCourses } = useCourseStore();
}
```

### 7. 타입 안전성

```tsx
import { Course, User } from '@/types';
import { CourseFormData } from '@/types/course';
import { ProfessorDashboardData } from '@/lib/api/dashboard';

function handleSubmit(data: CourseFormData) {
  // TypeScript가 타입 체크
}
```

### 8. 스타일링 패턴

```tsx
import { cn } from '@/lib/utils';

function Component({ className, variant }) {
  return (
    <div className={cn(
      'base-classes',
      variant === 'primary' && 'variant-classes',
      className
    )}>
      내용
    </div>
  );
}
```

### 9. 폼 처리

```tsx
import { useForm } from 'react-hook-form';

function FormComponent() {
  const { register, handleSubmit, formState: { errors } } = useForm();

  const onSubmit = (data) => {
    // 폼 제출 처리
  };

  return (
    <form onSubmit={handleSubmit(onSubmit)}>
      <Input {...register('field', { required: true })} />
      {errors.field && <span>필수 항목입니다</span>}
    </form>
  );
}
```

### 10. Toast 알림

```tsx
import { useToast } from '@/components/ui/toast';

function Component() {
  const { toast } = useToast();

  const handleAction = () => {
    toast({
      title: '성공',
      description: '작업이 완료되었습니다.',
    });
  };
}
```

## 개발 명령어

```bash
# 개발 서버
npm run dev

# 프로덕션 빌드
npm run build

# 프로덕션 서버
npm start

# 타입 체크
npm run type-check

# 린트
npm run lint

# 포맷팅
npm run format
```

## 디버깅 팁

### React Query Devtools
개발 모드에서 자동으로 활성화됩니다.
- 화면 하단의 React Query 아이콘 클릭
- 쿼리 상태 및 캐시 확인 가능

### Console Logging
```tsx
console.log('Debug:', data);
```

### Network 요청 확인
브라우저 개발자 도구 > Network 탭에서 API 요청 확인

## 문제 해결

### 빌드 오류
```bash
# node_modules 삭제 후 재설치
rm -rf node_modules
npm install
```

### 타입 오류
```bash
# TypeScript 캐시 삭제
rm -rf .next
npm run type-check
```

### API 연결 오류
- `.env.local`의 `NEXT_PUBLIC_API_URL` 확인
- Backend 서버 실행 상태 확인

## 참고 자료

- [Next.js 14 문서](https://nextjs.org/docs)
- [shadcn/ui 컴포넌트](https://ui.shadcn.com/)
- [TanStack Query](https://tanstack.com/query/latest)
- [Tailwind CSS](https://tailwindcss.com/)
- [E2 구현 상세 문서](./E2-COURSE-MANAGEMENT-README.md)

## 도움이 필요한 경우

1. 기존 코드 참고
2. TypeScript 타입 정의 확인
3. shadcn/ui 문서 참조
4. Backend API 스펙 확인
