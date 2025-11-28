# E2 Course Management Frontend Implementation

EduForum 프로젝트의 E2 코스 관리 시스템 Frontend UI 구현 문서입니다.

## 개요

이 구현은 P0-MVP 우선순위에 따라 교수 대시보드, 학생 대시보드, 코스 관리, 세션 관리, 과제 관리 기능을 포함합니다.

## 기술 스택

- **Framework**: Next.js 14 (App Router)
- **Language**: TypeScript
- **Styling**: Tailwind CSS
- **UI Components**: shadcn/ui (Radix UI)
- **State Management**: Zustand
- **Data Fetching**: TanStack Query (React Query)
- **HTTP Client**: Axios
- **Icons**: Lucide React

## 디렉토리 구조

```
apps/frontend/src/
├── app/
│   └── (dashboard)/
│       ├── professor/
│       │   ├── page.tsx                    # 교수 대시보드
│       │   └── courses/
│       │       ├── page.tsx                # 교수 코스 목록
│       │       └── new/
│       │           └── page.tsx            # 코스 생성
│       ├── student/
│       │   ├── page.tsx                    # 학생 대시보드
│       │   └── courses/
│       │       └── page.tsx                # 학생 코스 목록
│       └── courses/
│           └── [courseId]/
│               ├── page.tsx                # 코스 상세
│               └── settings/
│                   └── page.tsx            # 코스 설정
├── components/
│   ├── ui/
│   │   └── stat-card.tsx                   # 통계 카드 컴포넌트
│   ├── dashboard/
│   │   ├── schedule-section.tsx           # 일정 섹션
│   │   └── notification-section.tsx       # 알림 섹션
│   ├── course/
│   │   ├── course-card.tsx                # 코스 카드
│   │   ├── course-form.tsx                # 코스 폼
│   │   ├── enroll-modal.tsx               # 수강 신청 모달
│   │   └── invite-link-manager.tsx        # 초대 링크 관리
│   ├── session/
│   │   └── session-modal.tsx              # 세션 생성/수정 모달
│   ├── assignment/
│   │   ├── file-upload.tsx                # 파일 업로드
│   │   └── assignment-detail.tsx          # 과제 상세
│   └── enrollment/
│       └── csv-upload.tsx                 # CSV 업로드
├── lib/
│   └── api/
│       ├── client.ts                      # Axios 클라이언트
│       ├── endpoints.ts                   # API 엔드포인트
│       ├── dashboard.ts                   # 대시보드 API
│       ├── sessions.ts                    # 세션 API
│       ├── assignments.ts                 # 과제 API
│       └── enrollments.ts                 # 수강 관리 API
└── types/
    ├── dashboard.ts                       # 대시보드 타입
    ├── assignment.ts                      # 과제 타입
    └── session.ts                         # 세션 타입
```

## 주요 기능

### 1. 교수 대시보드 (#49, #50, #51, #52)
**경로**: `/professor`

**기능**:
- 통계 카드: 전체 코스, 학생, 과제, 세션 수
- 오늘 일정: 당일 예정된 세션 및 과제
- 알림: 최근 알림 및 업데이트
- 채점 대기: 채점이 필요한 과제 목록
- 위험 학생: 주의가 필요한 학생 목록

**사용 컴포넌트**:
- `StatCard` - 통계 정보 표시
- `ScheduleSection` - 일정 목록
- `NotificationSection` - 알림 목록

### 2. 학생 대시보드 (#57, #58, #59)
**경로**: `/student`

**기능**:
- 통계 카드: 수강 코스, 완료 과제, 평균 점수
- 오늘 일정: 당일 예정된 세션 및 과제
- 마감 임박 과제: 제출이 필요한 과제
- 참여도: 코스별 출석률 및 참여도
- 최근 성적: 최근 채점된 과제

**사용 컴포넌트**:
- `StatCard` - 통계 정보 표시
- `ScheduleSection` - 일정 목록
- `Progress` - 참여도 시각화

### 3. 학생 코스 목록 (#62, #63)
**경로**: `/student/courses`

**기능**:
- 수강 중인 코스 카드 목록
- 코스 검색
- 초대 코드를 통한 코스 등록

**사용 컴포넌트**:
- `CourseCard` - 코스 정보 표시
- `EnrollModal` - 수강 신청 모달

### 4. 교수 코스 관리 (#78, #79, #80)
**경로**: `/professor/courses`, `/professor/courses/new`

**기능**:
- 코스 목록 조회 (필터링: 상태, 검색)
- 새 코스 생성
- 코스 카드 표시

**사용 컴포넌트**:
- `CourseCard` - 코스 정보 표시
- `CourseForm` - 코스 생성/수정 폼

### 5. 코스 상세 (#72, #73)
**경로**: `/courses/[courseId]`

**기능**:
- 세션 목록 탭
- 과제 목록 탭
- 수강생 목록 탭
- 각 항목 상세보기

**사용 컴포넌트**:
- `Tabs` - 탭 인터페이스
- `Card` - 목록 표시

### 6. 코스 설정 (#84, #85)
**경로**: `/courses/[courseId]/settings`

**기능**:
- 기본 정보 수정
- 초대 링크 생성/관리
- CSV 파일로 수강생 일괄 등록

**사용 컴포넌트**:
- `CourseForm` - 코스 정보 수정
- `InviteLinkManager` - 초대 링크 관리
- `CsvUpload` - CSV 업로드

### 7. 초대 링크 관리 (#95, #96)
**컴포넌트**: `InviteLinkManager`

**기능**:
- 학생/조교용 초대 링크 생성
- 사용 횟수 제한 설정
- 링크 복사 및 비활성화
- 링크 사용 현황 표시

### 8. 세션 관리 (#101, #102, #103)
**컴포넌트**: `SessionModal`

**기능**:
- 세션 생성 (제목, 일시, 진행 시간)
- 세션 수정
- 세션 삭제

### 9. 과제 관리 (#67, #68, #69)
**컴포넌트**: `AssignmentDetail`, `FileUpload`

**기능**:
- 과제 정보 표시
- 과제 제출 (내용, 파일 첨부)
- 제출 현황 확인
- 채점 결과 표시

### 10. CSV 업로드 (#89, #90, #91)
**컴포넌트**: `CsvUpload`

**기능**:
- CSV 파일 드래그 앤 드롭
- 파일 형식 검증
- 업로드 결과 표시 (성공/실패 건수)
- 오류 상세 정보

## API 통합

### API 클라이언트
모든 API 호출은 `/lib/api/` 아래의 모듈을 통해 이루어집니다.

```typescript
import { getProfessorDashboard } from '@/lib/api/dashboard';
import { getCourses } from '@/lib/api/courses';
import { createSession } from '@/lib/api/sessions';
```

### 인증
- JWT 토큰 자동 첨부 (Axios interceptor)
- 토큰 만료 시 자동 갱신
- 401 오류 시 로그인 페이지로 리다이렉트

### 에러 처리
```typescript
try {
  const data = await apiCall();
} catch (error) {
  // parseApiError로 에러 메시지 추출
  toast({
    title: '오류 발생',
    description: error.message,
    variant: 'destructive',
  });
}
```

## 상태 관리

### React Query
서버 상태 관리에 TanStack Query 사용:

```typescript
const { data, isLoading } = useQuery({
  queryKey: ['courses', courseId],
  queryFn: () => getCourse(courseId),
});
```

### Zustand
클라이언트 상태 관리:

```typescript
const { courses, setCourses } = useCourseStore();
const { user, isAuthenticated } = useAuthStore();
```

## 스타일링

### Tailwind CSS
- 유틸리티 클래스 사용
- 반응형 디자인 (sm:, md:, lg: 브레이크포인트)
- 다크 모드 지원 준비

### shadcn/ui
- Radix UI 기반 접근성 컴포넌트
- 커스터마이징 가능한 디자인
- 일관된 UI/UX

## 사용 예시

### 코스 생성
```tsx
import { CourseForm } from '@/components/course';
import { createCourse } from '@/lib/api/courses';

function CreateCoursePage() {
  const handleSubmit = async (data) => {
    const course = await createCourse(data);
    router.push(`/courses/${course.id}`);
  };

  return <CourseForm onSubmit={handleSubmit} />;
}
```

### 대시보드 데이터 로딩
```tsx
import { useQuery } from '@tanstack/react-query';
import { getProfessorDashboard } from '@/lib/api/dashboard';

function ProfessorDashboard() {
  const { data, isLoading } = useQuery({
    queryKey: ['professor-dashboard'],
    queryFn: getProfessorDashboard,
  });

  if (isLoading) return <LoadingSpinner />;

  return <DashboardLayout data={data} />;
}
```

## 환경 변수

`.env.local` 파일에 다음 변수를 설정하세요:

```env
NEXT_PUBLIC_API_URL=http://localhost:8000/api
```

## 개발 가이드

### 컴포넌트 추가
1. `/components/` 아래 적절한 디렉토리에 컴포넌트 생성
2. `index.ts`에 export 추가
3. 타입 정의는 `/types/`에 추가

### API 엔드포인트 추가
1. `/lib/api/endpoints.ts`에 경로 추가
2. 해당 API 모듈에 함수 구현
3. 타입 정의는 `/lib/api/types.ts` 또는 해당 모듈에 추가

### 페이지 추가
1. `/app/(dashboard)/` 아래에 페이지 생성
2. 적절한 레이아웃 사용
3. SEO를 위한 메타데이터 추가

## 테스트

```bash
# 타입 체크
npm run type-check

# 린트
npm run lint

# 빌드
npm run build
```

## 다음 단계

- [ ] E3: 실시간 세미나 UI 구현
- [ ] E4: 액티브 러닝 도구 UI 구현
- [ ] E5: 평가 및 피드백 UI 구현
- [ ] E6: 학습 분석 UI 구현
- [ ] 단위 테스트 추가
- [ ] E2E 테스트 추가
- [ ] 성능 최적화
- [ ] 접근성 개선

## 관련 문서

- [Backend API 문서](/apps/backend/README.md)
- [와이어프레임](/docs/wireframes/)
- [데이터베이스 설계](/docs/database/)
- [API 설계](/docs/api-spec/)

## 기여

개발 시 다음 사항을 준수해주세요:

1. TypeScript strict mode 사용
2. ESLint/Prettier 규칙 준수
3. 접근성 고려 (ARIA 레이블, 키보드 네비게이션)
4. 반응형 디자인
5. 에러 처리 및 로딩 상태
6. 코드 주석 및 문서화
