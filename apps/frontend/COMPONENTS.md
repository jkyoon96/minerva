# EduForum 컴포넌트 라이브러리

FE-002: Common Component Library Setup - 완료된 shadcn/ui 기반 컴포넌트 라이브러리

## 📦 설치된 패키지

### Radix UI Primitives
```json
{
  "@radix-ui/react-avatar": "^1.0.4",
  "@radix-ui/react-checkbox": "^1.0.4",
  "@radix-ui/react-dialog": "^1.0.5",
  "@radix-ui/react-dropdown-menu": "^2.0.6",
  "@radix-ui/react-label": "^2.0.2",
  "@radix-ui/react-progress": "^1.0.3",
  "@radix-ui/react-radio-group": "^1.1.3",
  "@radix-ui/react-select": "^2.0.0",
  "@radix-ui/react-separator": "^1.0.3",
  "@radix-ui/react-slot": "^1.0.2",
  "@radix-ui/react-switch": "^1.0.3",
  "@radix-ui/react-tabs": "^1.0.4",
  "@radix-ui/react-toast": "^1.1.5",
  "@radix-ui/react-tooltip": "^1.0.7",
  "class-variance-authority": "^0.7.0"
}
```

## 🎨 UI Components (`src/components/ui/`)

### Form Controls
- **label.tsx** - 폼 레이블
- **input.tsx** - 텍스트 입력 (기존)
- **textarea.tsx** - 여러 줄 텍스트 입력
- **checkbox.tsx** - 체크박스
- **radio-group.tsx** - 라디오 버튼 그룹
- **switch.tsx** - 토글 스위치
- **select.tsx** - 드롭다운 선택

### Display Components
- **button.tsx** - 버튼 (기존)
- **card.tsx** - 카드 (기존)
- **badge.tsx** - 상태 배지
- **avatar.tsx** - 사용자 아바타
- **separator.tsx** - 구분선
- **skeleton.tsx** - 로딩 스켈레톤
- **progress.tsx** - 진행 표시줄

### Overlay Components
- **dialog.tsx** - 모달 다이얼로그
- **dropdown-menu.tsx** - 드롭다운 메뉴
- **sheet.tsx** - 사이드 패널/드로어
- **tooltip.tsx** - 툴팁

### Feedback Components
- **alert.tsx** - 알림 메시지
- **toast.tsx** - 토스트 알림
- **toaster.tsx** - 토스트 컨테이너
- **use-toast.ts** - 토스트 훅

### Navigation Components
- **tabs.tsx** - 탭 네비게이션

### Data Display
- **table.tsx** - 데이터 테이블

## 🔧 Common Components (`src/components/common/`)

### Layout Components
- **Header.tsx** - 페이지 헤더 (제목, 브레드크럼, 액션)
- **Footer.tsx** - 앱 푸터

### Feedback Components
- **LoadingSpinner.tsx** - 로딩 인디케이터 (sm/md/lg)
- **EmptyState.tsx** - 빈 상태 플레이스홀더
- **ErrorBoundary.tsx** - 에러 바운더리

### Interactive Components
- **ConfirmDialog.tsx** - 확인 다이얼로그
- **SearchInput.tsx** - 디바운스 검색 입력

### Data Components
- **DataTable.tsx** - 재사용 가능한 데이터 테이블 (정렬/페이징)
- **UserAvatar.tsx** - 사용자 아바타 (상태 표시)

## 📝 Form Components (`src/components/form/`)

검증 기능이 통합된 폼 컴포넌트들:

- **FormField.tsx** - 폼 필드 래퍼 (레이블 + 에러)
- **FormInput.tsx** - Input + 검증
- **FormSelect.tsx** - Select + 검증
- **FormTextarea.tsx** - Textarea + 검증

## 🏗️ Layout Components (`src/components/layout/`)

- **PageContainer.tsx** - 표준 페이지 래퍼 (최대 너비 옵션)
- **Section.tsx** - 콘텐츠 섹션 (제목, 설명, 액션)
- **Grid.tsx** - 반응형 그리드 레이아웃

## 🎨 테마 설정

### CSS 변수 (`src/styles/globals.css`)

```css
:root {
  /* 기본 색상 */
  --background, --foreground
  --card, --card-foreground
  --popover, --popover-foreground

  /* 의미론적 색상 */
  --primary, --primary-foreground
  --secondary, --secondary-foreground
  --muted, --muted-foreground
  --accent, --accent-foreground
  --destructive, --destructive-foreground

  /* 추가 색상 */
  --success, --success-foreground
  --warning, --warning-foreground
  --info, --info-foreground

  /* UI 요소 */
  --border, --input, --ring
  --radius
}
```

### 다크 모드 지원
모든 컴포넌트는 `.dark` 클래스를 통한 다크 모드를 지원합니다.

### 유틸리티 클래스
- `.scrollbar-thin` - 얇은 스크롤바
- `.line-clamp-1/2/3` - 텍스트 생략

## 📖 사용 예제

### 1. 폼 컴포넌트 사용

```tsx
import { FormInput, FormSelect, FormTextarea } from '@/components/form';
import { Button } from '@/components/ui/button';

function MyForm() {
  const [formData, setFormData] = useState({
    name: '',
    role: '',
    bio: '',
  });

  const [errors, setErrors] = useState({});

  return (
    <form>
      <FormInput
        label="이름"
        value={formData.name}
        onChange={(e) => setFormData({ ...formData, name: e.target.value })}
        error={errors.name}
        required
      />

      <FormSelect
        label="역할"
        options={[
          { label: '학생', value: 'student' },
          { label: '교수', value: 'professor' },
        ]}
        value={formData.role}
        onValueChange={(value) => setFormData({ ...formData, role: value })}
        error={errors.role}
        required
      />

      <FormTextarea
        label="자기소개"
        value={formData.bio}
        onChange={(e) => setFormData({ ...formData, bio: e.target.value })}
        description="간단한 자기소개를 작성해주세요"
      />

      <Button type="submit">제출</Button>
    </form>
  );
}
```

### 2. 레이아웃 컴포넌트 사용

```tsx
import { PageContainer, Section, Grid } from '@/components/layout';
import { Header } from '@/components/common';
import { Card, CardHeader, CardTitle, CardContent } from '@/components/ui/card';

function DashboardPage() {
  return (
    <>
      <Header
        title="대시보드"
        breadcrumbs={[
          { label: '홈', href: '/' },
          { label: '대시보드' },
        ]}
        description="학습 진행 상황을 확인하세요"
      />

      <PageContainer>
        <Section title="최근 활동">
          <Grid cols={3}>
            <Card>
              <CardHeader>
                <CardTitle>카드 1</CardTitle>
              </CardHeader>
              <CardContent>내용</CardContent>
            </Card>
            {/* 더 많은 카드... */}
          </Grid>
        </Section>
      </PageContainer>
    </>
  );
}
```

### 3. 데이터 테이블 사용

```tsx
import { DataTable, type Column } from '@/components/common/DataTable';
import { Badge } from '@/components/ui/badge';

interface Student {
  id: string;
  name: string;
  email: string;
  status: 'active' | 'inactive';
}

function StudentList() {
  const students: Student[] = [...];

  const columns: Column<Student>[] = [
    { key: 'name', header: '이름', sortable: true },
    { key: 'email', header: '이메일', sortable: true },
    {
      key: 'status',
      header: '상태',
      render: (student) => (
        <Badge variant={student.status === 'active' ? 'success' : 'secondary'}>
          {student.status === 'active' ? '활성' : '비활성'}
        </Badge>
      ),
    },
  ];

  return (
    <DataTable
      data={students}
      columns={columns}
      keyExtractor={(student) => student.id}
      onRowClick={(student) => console.log('Clicked:', student)}
    />
  );
}
```

### 4. 토스트 알림 사용

```tsx
import { useToast } from '@/components/ui/use-toast';
import { Button } from '@/components/ui/button';

function MyComponent() {
  const { toast } = useToast();

  const showSuccess = () => {
    toast({
      title: "성공!",
      description: "작업이 완료되었습니다.",
      variant: "success",
    });
  };

  return <Button onClick={showSuccess}>알림 표시</Button>;
}

// layout.tsx에 Toaster 추가 필요
import { Toaster } from '@/components/ui/toaster';

export default function RootLayout({ children }) {
  return (
    <html>
      <body>
        {children}
        <Toaster />
      </body>
    </html>
  );
}
```

### 5. 확인 다이얼로그 사용

```tsx
import { useState } from 'react';
import { ConfirmDialog } from '@/components/common';
import { Button } from '@/components/ui/button';

function DeleteButton({ itemId, onDelete }) {
  const [showConfirm, setShowConfirm] = useState(false);

  return (
    <>
      <Button variant="destructive" onClick={() => setShowConfirm(true)}>
        삭제
      </Button>

      <ConfirmDialog
        open={showConfirm}
        onOpenChange={setShowConfirm}
        title="정말 삭제하시겠습니까?"
        description="이 작업은 되돌릴 수 없습니다."
        variant="destructive"
        confirmLabel="삭제"
        onConfirm={() => {
          onDelete(itemId);
        }}
      />
    </>
  );
}
```

### 6. 검색 입력 사용

```tsx
import { useState } from 'react';
import { SearchInput } from '@/components/common';

function SearchableList() {
  const [searchQuery, setSearchQuery] = useState('');

  // 300ms 디바운스 후 자동으로 검색 실행
  const handleSearch = (query: string) => {
    console.log('Searching for:', query);
    // API 호출 등...
  };

  return (
    <SearchInput
      value={searchQuery}
      onChange={handleSearch}
      placeholder="학생 이름 검색..."
      debounceMs={300}
    />
  );
}
```

## 🎯 다음 단계

이제 다음 작업을 진행할 수 있습니다:

1. **FE-003**: Auth UI Implementation (인증 화면)
2. **FE-004**: Course Management UI (코스 관리)
3. **FE-005**: Live Session UI (실시간 세션)

## 📚 참고 자료

- [shadcn/ui Documentation](https://ui.shadcn.com/)
- [Radix UI Documentation](https://www.radix-ui.com/)
- [Tailwind CSS Documentation](https://tailwindcss.com/)
