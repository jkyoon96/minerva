# Authentication System - Quick Start Guide

## 5-Minute Setup

### 1. Environment Setup

Create `.env.local`:
```env
NEXT_PUBLIC_API_URL=http://localhost:8000/api
```

### 2. Start Backend Server

```bash
# Backend 서버가 http://localhost:8000에서 실행 중이어야 합니다
```

### 3. Start Frontend

```bash
cd apps/frontend
npm install
npm run dev
```

### 4. Test Pages

- Login: http://localhost:3000/login
- Register: http://localhost:3000/register
- Forgot Password: http://localhost:3000/forgot-password

## Common Use Cases

### Protect a Page

**Option 1: Component Wrapper**
```tsx
// app/dashboard/page.tsx
import { AuthGuard } from '@/components/auth';

export default function DashboardPage() {
  return (
    <AuthGuard>
      <YourDashboardContent />
    </AuthGuard>
  );
}
```

**Option 2: HOC**
```tsx
// app/dashboard/page.tsx
import { withAuthGuard } from '@/components/auth';

function DashboardPage() {
  return <YourDashboardContent />;
}

export default withAuthGuard(DashboardPage);
```

### Access User Info

```tsx
'use client';

import { useAuthStore } from '@/stores/authStore';

export function UserProfile() {
  const { user, isAuthenticated } = useAuthStore();

  if (!isAuthenticated) return null;

  return <div>Hello, {user?.name}!</div>;
}
```

### Logout Button

```tsx
'use client';

import { useAuthStore } from '@/stores/authStore';
import { Button } from '@/components/ui/button';

export function LogoutButton() {
  const { logout, isLoading } = useAuthStore();

  return (
    <Button onClick={logout} disabled={isLoading}>
      Logout
    </Button>
  );
}
```

### Custom Login Form

```tsx
'use client';

import { useState } from 'react';
import { useAuthStore } from '@/stores/authStore';

export function MyLoginForm() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const { login, isLoading, error } = useAuthStore();

  const handleSubmit = async (e) => {
    e.preventDefault();
    await login({ email, password });
  };

  return (
    <form onSubmit={handleSubmit}>
      {error && <div>{error}</div>}
      <input value={email} onChange={(e) => setEmail(e.target.value)} />
      <input value={password} onChange={(e) => setPassword(e.target.value)} type="password" />
      <button disabled={isLoading}>Login</button>
    </form>
  );
}
```

## API Reference

### Auth Store

```typescript
import { useAuthStore } from '@/stores/authStore';

const {
  // State
  user,              // User | null
  isAuthenticated,   // boolean
  isLoading,         // boolean
  error,             // string | null

  // Actions
  login,             // (credentials) => Promise<void>
  register,          // (data) => Promise<void>
  logout,            // () => Promise<void>
  fetchProfile,      // () => Promise<void>
  clearError,        // () => void
} = useAuthStore();
```

### Validation Functions

```typescript
import {
  validateEmail,
  validatePassword,
  validatePasswordConfirm,
  validateName,
  getPasswordStrength,
} from '@/lib/validation';

// Returns error message or null
const emailError = validateEmail('test@example.com');
const passwordError = validatePassword('MyPass123!');
const confirmError = validatePasswordConfirm('pass1', 'pass2');
const nameError = validateName('John Doe');

// Returns 0-4
const strength = getPasswordStrength('MyPass123!');
```

### API Functions

```typescript
import * as authApi from '@/lib/api/auth';

// All functions return promises
await authApi.login({ email, password });
await authApi.register({ email, password, name, role });
await authApi.logout();
await authApi.getProfile();
await authApi.updateProfile({ name, bio });
await authApi.changePassword(currentPassword, newPassword);
await authApi.forgotPassword(email);
await authApi.resetPassword(token, newPassword);
```

## Component Props

### LoginForm

```typescript
<LoginForm
  redirectTo="/dashboard"  // Optional, default: "/dashboard"
/>
```

### RegisterForm

```typescript
<RegisterForm
  redirectTo="/dashboard"  // Optional, default: "/dashboard"
/>
```

### OAuthButtons

```typescript
<OAuthButtons
  disabled={false}  // Optional, default: false
/>
```

### AuthGuard

```typescript
<AuthGuard
  requireAuth={true}        // Optional, default: true
  redirectTo="/login"       // Optional, default: "/login"
>
  {children}
</AuthGuard>
```

## Keyboard Shortcuts

- `Tab`: Navigate between fields
- `Enter`: Submit form
- `Escape`: Close modals (if any)

## Troubleshooting

### Issue: "401 Unauthorized"
**Solution**: Check if backend is running and CORS is configured

### Issue: "Network Error"
**Solution**: Verify `NEXT_PUBLIC_API_URL` in `.env.local`

### Issue: Login successful but not redirecting
**Solution**: Check `router.push()` in LoginForm component

### Issue: Token not persisting
**Solution**: Check browser localStorage for `accessToken` and `refreshToken`

### Issue: Page keeps redirecting to login
**Solution**: Check if token is valid and not expired

## Best Practices

1. Always use `'use client'` for components using auth hooks
2. Handle loading states to prevent button double-clicks
3. Clear sensitive data on logout
4. Show user-friendly error messages
5. Validate on both client and server sides

## Example: Complete Protected Page

```tsx
// app/profile/page.tsx
'use client';

import { AuthGuard } from '@/components/auth';
import { useAuthStore } from '@/stores/authStore';
import { Button } from '@/components/ui/button';

function ProfileContent() {
  const { user, logout } = useAuthStore();

  return (
    <div>
      <h1>Profile</h1>
      <p>Name: {user?.name}</p>
      <p>Email: {user?.email}</p>
      <p>Role: {user?.role}</p>
      <Button onClick={logout}>Logout</Button>
    </div>
  );
}

export default function ProfilePage() {
  return (
    <AuthGuard>
      <ProfileContent />
    </AuthGuard>
  );
}
```

## Resources

- [Full Documentation](./README.md)
- [Implementation Summary](../../AUTHENTICATION_IMPLEMENTATION.md)
- [Backend API Docs](../../../../docs/design/api-design.yaml)
