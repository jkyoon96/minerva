# FE-003: State Management & API Client Implementation

## Overview

This implementation provides a complete state management and API client solution for the EduForum Next.js 14 frontend application.

## What's Included

### 🎯 Core Features

1. **API Client System**
   - Axios-based HTTP client with automatic JWT token management
   - Request/Response interceptors
   - Automatic token refresh on 401 errors
   - Type-safe API functions for authentication and courses
   - Centralized error handling

2. **State Management**
   - **Zustand Stores**: Global state (auth, UI, courses)
   - **React Query**: Server state management and caching
   - **LocalStorage Persistence**: User preferences and session data

3. **Custom Hooks**
   - `useAuth` - Authentication management
   - `useCourses` - Course data fetching and mutations
   - `useDebounce` - Input debouncing
   - `useLocalStorage` - LocalStorage as React state
   - `useMediaQuery` - Responsive design helpers

4. **TypeScript Types**
   - Complete type definitions for API requests/responses
   - Form data types
   - State types
   - Utility types

## 📁 File Structure

```
apps/frontend/
├── src/
│   ├── lib/
│   │   ├── api/                    # API client and functions
│   │   │   ├── client.ts          # Axios instance
│   │   │   ├── types.ts           # API types
│   │   │   ├── endpoints.ts       # Endpoint constants
│   │   │   ├── auth.ts            # Auth API
│   │   │   ├── courses.ts         # Course API
│   │   │   └── index.ts           # Exports
│   │   ├── queryClient.ts         # React Query config
│   │   └── providers.tsx          # App providers
│   ├── stores/                     # Zustand stores
│   │   ├── authStore.ts           # Authentication
│   │   ├── uiStore.ts             # UI state
│   │   ├── courseStore.ts         # Course state
│   │   └── index.ts               # Exports
│   ├── hooks/                      # Custom hooks
│   │   ├── useAuth.ts
│   │   ├── useCourses.ts
│   │   ├── useDebounce.ts
│   │   ├── useLocalStorage.ts
│   │   ├── useMediaQuery.ts
│   │   └── index.ts
│   └── types/                      # Type definitions
│       ├── auth.ts
│       ├── course.ts
│       └── index.ts
├── STATE_MANAGEMENT.md             # Detailed documentation
├── QUICK_REFERENCE.md              # Quick reference guide
├── FE-003_IMPLEMENTATION_SUMMARY.md # Implementation details
└── verify-setup.md                 # Setup verification

Total: 29 new files, 3 updated files, 1,579 lines of code
```

## 🚀 Quick Start

### 1. Install Dependencies

```bash
npm install
```

New dependencies:
- `@tanstack/react-query`: Server state management
- `@tanstack/react-query-devtools`: Development tools
- `axios`: HTTP client

### 2. Setup Environment

Create `.env.local`:

```bash
NEXT_PUBLIC_API_URL=http://localhost:8000/api
```

### 3. Run Development Server

```bash
npm run dev
```

### 4. Verify Setup

Open browser and check:
- React Query Devtools button (bottom-right)
- No TypeScript errors
- No console errors

## 📖 Usage Examples

### Authentication

```typescript
import { useAuth } from '@/hooks';

function LoginPage() {
  const { login, isLoading, error } = useAuth();

  const handleLogin = async () => {
    await login({
      email: 'user@example.com',
      password: 'password'
    });
  };

  return <button onClick={handleLogin}>Login</button>;
}
```

### Fetching Courses

```typescript
import { useCourses } from '@/hooks';

function CourseList() {
  const { data, isLoading } = useCourses({
    page: 1,
    limit: 10
  });

  if (isLoading) return <div>Loading...</div>;

  return (
    <div>
      {data?.courses.map(course => (
        <div key={course.id}>{course.title}</div>
      ))}
    </div>
  );
}
```

### Creating a Course

```typescript
import { useCreateCourse } from '@/hooks';

function CreateCourse() {
  const createCourse = useCreateCourse();

  const handleSubmit = async (formData) => {
    await createCourse.mutateAsync({
      title: 'New Course',
      code: 'CS101',
      semester: '2024-1',
    });
    // Success notification shown automatically
  };

  return <form onSubmit={handleSubmit}>...</form>;
}
```

### UI State (Notifications)

```typescript
import { useUiStore } from '@/stores';

function MyComponent() {
  const { addNotification } = useUiStore();

  const handleSuccess = () => {
    addNotification({
      type: 'success',
      title: 'Success',
      message: 'Operation completed',
      duration: 3000,
    });
  };

  return <button onClick={handleSuccess}>Do Something</button>;
}
```

## 🔑 Key Features

### Automatic Token Management

- JWT tokens automatically attached to requests
- Automatic token refresh on 401 errors
- Token storage in localStorage
- Automatic logout on refresh failure

### Error Handling

- All API errors parsed to `ApiError` type
- Automatic error notifications for mutations
- Network error detection
- Type-safe error messages

### Caching & Performance

- React Query automatic caching (5min stale time)
- Cache invalidation on mutations
- Optimistic updates ready
- Debounced search inputs

### Type Safety

- Full TypeScript support
- Type inference for API responses
- Type-safe store actions
- IntelliSense support

### Developer Experience

- React Query Devtools in development
- Clear error messages
- Console-friendly debugging
- Comprehensive documentation

## 📚 Documentation

1. **STATE_MANAGEMENT.md** (13,000+ words)
   - Complete architecture guide
   - API documentation
   - Store documentation
   - Hook documentation
   - Usage examples

2. **QUICK_REFERENCE.md**
   - Code snippets
   - Common patterns
   - Quick examples
   - Tips & tricks

3. **FE-003_IMPLEMENTATION_SUMMARY.md**
   - What was implemented
   - File listing
   - Feature summary
   - Next steps

4. **verify-setup.md**
   - Setup verification checklist
   - Troubleshooting guide
   - Test examples

## 🎨 Architecture

### State Management Strategy

```
┌─────────────────────────────────────────┐
│         Application State               │
├─────────────────────────────────────────┤
│                                         │
│  ┌──────────────┐  ┌─────────────────┐ │
│  │   Zustand    │  │  React Query    │ │
│  │  (Global)    │  │   (Server)      │ │
│  ├──────────────┤  ├─────────────────┤ │
│  │ • Auth       │  │ • Courses       │ │
│  │ • UI         │  │ • Sessions      │ │
│  │ • Course     │  │ • Assignments   │ │
│  └──────────────┘  └─────────────────┘ │
│                                         │
└─────────────────────────────────────────┘
           ▲                    ▲
           │                    │
    ┌──────┴──────┐      ┌─────┴─────┐
    │ localStorage │      │ API Cache │
    └─────────────┘      └───────────┘
```

### Data Flow

```
Component
    ↓
Custom Hook (useAuth, useCourses)
    ↓
Store (Zustand) / Query (React Query)
    ↓
API Client (Axios)
    ↓
Backend API
```

## 🔧 Configuration

### React Query

```typescript
// src/lib/queryClient.ts
{
  staleTime: 5 * 60 * 1000,    // 5 minutes
  gcTime: 30 * 60 * 1000,      // 30 minutes
  refetchOnWindowFocus: false,
  retry: 1,
}
```

### API Client

```typescript
// src/lib/api/client.ts
{
  baseURL: process.env.NEXT_PUBLIC_API_URL,
  timeout: 30000,              // 30 seconds
  withCredentials: true,
}
```

## 🧪 Testing

### Manual Testing

See `verify-setup.md` for comprehensive testing checklist.

### Integration Testing (Future)

Ready for:
- Vitest unit tests
- React Testing Library integration tests
- Playwright E2E tests

## 🔒 Security Features

- JWT token in Authorization header
- Automatic token refresh
- Secure token storage (localStorage)
- CORS support (withCredentials)
- Request timeout protection

## 🚦 Next Steps

### Recommended Additions

1. **Additional API Modules**
   - Sessions API (`src/lib/api/sessions.ts`)
   - Assignments API (`src/lib/api/assignments.ts`)
   - Polls API (`src/lib/api/polls.ts`)

2. **Real-time Features**
   - Socket.io integration
   - WebSocket hooks
   - Real-time notifications

3. **Error Boundaries**
   - React Error Boundary
   - Error logging
   - Fallback UI

4. **Testing**
   - Unit tests for stores
   - Integration tests for hooks
   - E2E tests for flows

5. **Performance**
   - Code splitting
   - Lazy loading
   - Image optimization

## 📊 Metrics

- **Files Created**: 29
- **Files Updated**: 3
- **Lines of Code**: 1,579
- **Dependencies Added**: 3
- **Documentation Pages**: 4
- **Example Components**: 1

## 🤝 Contributing

When adding new features:

1. Follow existing patterns
2. Add TypeScript types
3. Include error handling
4. Update documentation
5. Add usage examples

## 📝 License

Part of the EduForum project.

## 🙏 Credits

Implemented for FE-003 task as part of the EduForum MVP development.

---

**Implementation Date**: 2025-11-29
**Task ID**: FE-003
**Status**: ✅ Complete
