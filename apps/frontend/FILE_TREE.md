# FE-003 Implementation File Tree

Complete file structure for the state management and API client implementation.

```
apps/frontend/
├── src/
│   ├── lib/
│   │   ├── api/                           # API Client Layer
│   │   │   ├── client.ts                  # ✨ Axios instance + interceptors
│   │   │   ├── types.ts                   # ✨ API request/response types
│   │   │   ├── endpoints.ts               # ✨ API endpoint constants
│   │   │   ├── auth.ts                    # ✨ Authentication API functions
│   │   │   ├── courses.ts                 # ✨ Course API functions
│   │   │   └── index.ts                   # ✨ API exports
│   │   ├── queryClient.ts                 # ✨ React Query configuration
│   │   ├── providers.tsx                  # ✨ App providers wrapper
│   │   └── utils.ts                       # (existing)
│   │
│   ├── stores/                            # Zustand State Management
│   │   ├── authStore.ts                   # ✨ Authentication state
│   │   ├── uiStore.ts                     # ✨ UI state (sidebar, theme, notifications)
│   │   ├── courseStore.ts                 # ✨ Course state (list, filters, pagination)
│   │   └── index.ts                       # ✨ Store exports
│   │
│   ├── hooks/                             # Custom React Hooks
│   │   ├── useAuth.ts                     # ✨ Authentication hook
│   │   ├── useCourses.ts                  # ✨ Course data hooks (React Query)
│   │   ├── useDebounce.ts                 # ✨ Debounce utility hook
│   │   ├── useLocalStorage.ts             # ✨ LocalStorage hook
│   │   ├── useMediaQuery.ts               # ✨ Media query hook
│   │   └── index.ts                       # ✨ Hook exports
│   │
│   ├── types/                             # TypeScript Type Definitions
│   │   ├── auth.ts                        # ✨ Authentication types
│   │   ├── course.ts                      # ✨ Course types
│   │   └── index.ts                       # 🔄 Updated with new exports
│   │
│   ├── components/
│   │   ├── examples/
│   │   │   └── CourseListExample.tsx     # ✨ Example component
│   │   ├── common/                        # (existing)
│   │   └── ui/                            # (existing)
│   │
│   └── app/
│       ├── layout.tsx                     # 🔄 Updated with Providers
│       ├── (auth)/                        # (existing)
│       ├── (dashboard)/                   # (existing)
│       └── (marketing)/                   # (existing)
│
├── package.json                           # 🔄 Updated with new dependencies
├── .env.example                           # (existing)
│
└── Documentation/
    ├── STATE_MANAGEMENT.md                # ✨ Comprehensive guide (13K+ words)
    ├── QUICK_REFERENCE.md                 # ✨ Quick reference with snippets
    ├── FE-003_IMPLEMENTATION_SUMMARY.md   # ✨ Implementation details
    ├── FE-003_README.md                   # ✨ Overview & quick start
    ├── verify-setup.md                    # ✨ Setup verification checklist
    ├── QUICK_START.md                     # (existing)
    └── README.md                          # (existing)
```

## Legend

- ✨ **New file** - Created in FE-003 implementation
- 🔄 **Updated** - Modified in FE-003 implementation
- (existing) - Pre-existing file

## Statistics

### Files
- **29** new files created
- **3** files updated
- **1,579** lines of code added

### By Category

#### API Layer (6 files)
```
src/lib/api/
├── client.ts (170 lines)
├── types.ts (120 lines)
├── endpoints.ts (75 lines)
├── auth.ts (145 lines)
├── courses.ts (135 lines)
└── index.ts (7 lines)
```

#### State Management (4 files)
```
src/stores/
├── authStore.ts (170 lines)
├── uiStore.ts (130 lines)
├── courseStore.ts (145 lines)
└── index.ts (7 lines)
```

#### Custom Hooks (6 files)
```
src/hooks/
├── useAuth.ts (55 lines)
├── useCourses.ts (220 lines)
├── useDebounce.ts (25 lines)
├── useLocalStorage.ts (80 lines)
├── useMediaQuery.ts (50 lines)
└── index.ts (20 lines)
```

#### Type Definitions (3 files)
```
src/types/
├── auth.ts (55 lines)
├── course.ts (75 lines)
└── index.ts (updated)
```

#### Configuration & Setup (2 files)
```
src/lib/
├── queryClient.ts (20 lines)
└── providers.tsx (30 lines)
```

#### Documentation (5 files)
```
root/
├── STATE_MANAGEMENT.md (13,204 bytes)
├── QUICK_REFERENCE.md (7,999 bytes)
├── FE-003_IMPLEMENTATION_SUMMARY.md (11,192 bytes)
├── FE-003_README.md (9,500 bytes)
└── verify-setup.md (8,500 bytes)
```

## Key Directories

### `/src/lib/api/` - API Client Layer
All API communication logic, including:
- HTTP client configuration
- Request/response interceptors
- Token management
- API function definitions
- Type definitions for API

### `/src/stores/` - Global State
Zustand stores for client-side state:
- User authentication state
- UI preferences and settings
- Course data cache
- Persistent state with localStorage

### `/src/hooks/` - Custom Hooks
Reusable React hooks:
- Authentication management
- Data fetching with React Query
- Utility functions (debounce, localStorage, etc.)
- Responsive design helpers

### `/src/types/` - Type Definitions
TypeScript type definitions:
- Domain models (User, Course, etc.)
- API request/response types
- Form data types
- Utility types

## Import Patterns

### API Functions
```typescript
import { authApi, coursesApi } from '@/lib/api';
```

### Stores
```typescript
import { useAuthStore, useUiStore, useCourseStore } from '@/stores';
```

### Hooks
```typescript
import { useAuth, useCourses, useDebounce } from '@/hooks';
```

### Types
```typescript
import type { User, Course, LoginFormData } from '@/types';
```

## Dependencies Added

```json
{
  "@tanstack/react-query": "^5.28.0",
  "@tanstack/react-query-devtools": "^5.28.0",
  "axios": "^1.6.7"
}
```

Note: `zustand: ^4.5.0` was already present in the project.

---

**Generated**: 2025-11-29
**Task**: FE-003
**Status**: Complete ✅
