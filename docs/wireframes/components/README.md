# EduForum Wireframe Components

공통으로 사용되는 헤더, 사이드바 등의 컴포넌트 파일 모음입니다.

## 📁 파일 구조

```
components/
├── header-professor.html       # 교수용 헤더
├── header-student.html         # 학생용 헤더
├── sidebar-course-student.html # 학생용 코스 사이드바
└── README.md                   # 이 파일
```

## 🎓 교수용 헤더 (header-professor.html)

### 네비게이션 메뉴
| 메뉴 | 링크 |
|------|------|
| 대시보드 | `e2-course/dashboard.html` |
| 코스 관리 | `e2-course/crs-001-course-list.html` |
| 실시간 세미나 | `e3-live/live-001-session-list.html` |
| 성적 관리 | `e5-assessment/grade-001-overview.html` |
| 분석 | `e6-analytics/analytics-001-dashboard.html` |

### 사용자 메뉴
- 프로필 설정
- 계정 설정
- 도움말
- 로그아웃

### 기본 사용자 정보
- 이름: 김교수
- 이메일: professor.kim@university.edu
- 역할 뱃지: 교수 (badge-primary)

---

## 👩‍🎓 학생용 헤더 (header-student.html)

### 네비게이션 메뉴
| 메뉴 | 링크 |
|------|------|
| 대시보드 | `e2-course/dashboard-student.html` |
| 내 코스 | `e2-course/crs-001-course-list-student.html` |
| 학습 리포트 | `e6-analytics/report-001-personal.html` |
| 프로필 | `e1-auth/profile-001-settings.html` |

### 사용자 메뉴
- 프로필 설정
- 계정 설정
- 도움말
- 로그아웃

### 기본 사용자 정보
- 이름: 이영희
- 이메일: younghee.lee@university.edu
- 역할 뱃지: 학생 (badge-default)

---

## 📖 사용 방법

### 1. 헤더 복사
해당 역할의 헤더 파일 내용을 복사하여 HTML 파일의 `<body>` 시작 부분에 붙여넣습니다.

### 2. 현재 페이지 표시
현재 페이지에 해당하는 `nav-link`에 `active` 클래스를 추가합니다:

```html
<!-- 코스 관리 페이지인 경우 -->
<a href="../e2-course/crs-001-course-list.html" class="nav-link active">코스 관리</a>
```

### 3. 사용자 정보 수정
필요시 사용자 이름과 이메일을 수정합니다:

```html
<span class="text-sm font-medium">홍길동</span>
...
<div class="user-menu-name">홍길동</div>
<div class="user-menu-email">hong@university.edu</div>
```

### 4. 경로 확인
페이지 위치에 따라 상대 경로를 조정합니다:
- `e1-auth/` 폴더: `../e2-course/...`
- `e2-course/` 폴더: `crs-001-...` 또는 `../e3-live/...`
- 중첩 폴더: 경로 깊이에 맞게 `../` 추가

---

## 🔧 필수 의존성

헤더 컴포넌트를 사용하려면 다음 파일이 필요합니다:

```html
<head>
  <link rel="stylesheet" href="../css/variables.css">
  <link rel="stylesheet" href="../css/base.css">
  <link rel="stylesheet" href="../css/components.css">
  <script src="https://unpkg.com/lucide@latest/dist/umd/lucide.js"></script>
</head>
```

페이지 끝에 아이콘 초기화 스크립트를 추가합니다:

```html
<script>
  lucide.createIcons();
</script>
```

---

## 🎨 스타일 클래스

### 헤더 관련 클래스
| 클래스 | 설명 |
|--------|------|
| `.header` | 상단 고정 헤더 컨테이너 |
| `.navbar` | 네비게이션 바 레이아웃 |
| `.navbar-brand` | 로고 및 브랜드명 |
| `.navbar-nav` | 메인 네비게이션 링크 |
| `.nav-link` | 네비게이션 링크 |
| `.nav-link.active` | 현재 활성 페이지 |
| `.navbar-actions` | 우측 액션 버튼 영역 |

### 사용자 메뉴 클래스
| 클래스 | 설명 |
|--------|------|
| `.user-menu` | 사용자 메뉴 컨테이너 |
| `.user-menu-trigger` | 메뉴 열기 버튼 |
| `.user-menu-dropdown` | 드롭다운 메뉴 |
| `.user-menu-dropdown.show` | 메뉴 표시 상태 |
| `.user-menu-header` | 사용자 정보 헤더 |
| `.user-menu-item` | 메뉴 항목 |
| `.user-menu-item.destructive` | 위험 액션 (로그아웃) |
