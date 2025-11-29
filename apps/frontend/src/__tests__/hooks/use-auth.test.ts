import { renderHook, act, waitFor } from '@testing-library/react';
import { useAuthStore } from '@/stores/authStore';
import * as authApi from '@/lib/api/auth';

// Mock the auth API
jest.mock('@/lib/api/auth');
const mockedAuthApi = authApi as jest.Mocked<typeof authApi>;

describe('useAuthStore', () => {
  beforeEach(() => {
    // Reset store state before each test
    const { result } = renderHook(() => useAuthStore());
    act(() => {
      result.current.setUser(null);
      result.current.setError(null);
      result.current.setLoading(false);
      result.current.setTwoFactorRequired(false);
      result.current.setTemporaryToken(null);
    });

    // Clear localStorage
    localStorage.clear();

    // Clear all mocks
    jest.clearAllMocks();
  });

  describe('Initial State', () => {
    it('should have correct initial state', () => {
      const { result } = renderHook(() => useAuthStore());

      expect(result.current.user).toBeNull();
      expect(result.current.isAuthenticated).toBe(false);
      expect(result.current.isLoading).toBe(false);
      expect(result.current.error).toBeNull();
      expect(result.current.twoFactorRequired).toBe(false);
      expect(result.current.temporaryToken).toBeNull();
    });
  });

  describe('setUser', () => {
    it('should set user and isAuthenticated to true', () => {
      const { result } = renderHook(() => useAuthStore());
      const mockUser = {
        id: 1,
        email: 'test@minerva.edu',
        name: 'Test User',
        role: 'STUDENT',
      };

      act(() => {
        result.current.setUser(mockUser);
      });

      expect(result.current.user).toEqual(mockUser);
      expect(result.current.isAuthenticated).toBe(true);
    });

    it('should set isAuthenticated to false when user is null', () => {
      const { result } = renderHook(() => useAuthStore());

      act(() => {
        result.current.setUser(null);
      });

      expect(result.current.user).toBeNull();
      expect(result.current.isAuthenticated).toBe(false);
    });
  });

  describe('login', () => {
    it('should successfully login with valid credentials', async () => {
      const { result } = renderHook(() => useAuthStore());
      const mockLoginResponse = {
        user: {
          id: 1,
          email: 'test@minerva.edu',
          name: 'Test User',
          role: 'STUDENT',
        },
        tokens: {
          accessToken: 'mock-access-token',
          refreshToken: 'mock-refresh-token',
        },
      };

      mockedAuthApi.login.mockResolvedValueOnce(mockLoginResponse);

      await act(async () => {
        await result.current.login({
          email: 'test@minerva.edu',
          password: 'password123',
        });
      });

      expect(result.current.user).toEqual(mockLoginResponse.user);
      expect(result.current.isAuthenticated).toBe(true);
      expect(result.current.isLoading).toBe(false);
      expect(result.current.error).toBeNull();
      expect(localStorage.getItem('accessToken')).toBe('mock-access-token');
      expect(localStorage.getItem('refreshToken')).toBe('mock-refresh-token');
    });

    it('should handle 2FA requirement', async () => {
      const { result } = renderHook(() => useAuthStore());
      const mock2FAResponse = {
        requiresTwoFactor: true,
        temporaryToken: 'temp-token',
        user: {
          id: 1,
          email: 'test@minerva.edu',
          name: 'Test User',
        },
      };

      mockedAuthApi.login.mockResolvedValueOnce(mock2FAResponse as any);

      await act(async () => {
        await result.current.login({
          email: 'test@minerva.edu',
          password: 'password123',
        });
      });

      expect(result.current.twoFactorRequired).toBe(true);
      expect(result.current.temporaryToken).toBe('temp-token');
      expect(result.current.isAuthenticated).toBe(false);
      expect(result.current.isLoading).toBe(false);
    });

    it('should handle login error', async () => {
      const { result } = renderHook(() => useAuthStore());
      const mockError = new Error('Invalid credentials');

      mockedAuthApi.login.mockRejectedValueOnce(mockError);

      await act(async () => {
        try {
          await result.current.login({
            email: 'test@minerva.edu',
            password: 'wrong-password',
          });
        } catch (error) {
          // Expected to throw
        }
      });

      expect(result.current.error).toBe('Invalid credentials');
      expect(result.current.isAuthenticated).toBe(false);
      expect(result.current.isLoading).toBe(false);
    });
  });

  describe('logout', () => {
    it('should successfully logout and clear state', async () => {
      const { result } = renderHook(() => useAuthStore());

      // Set up logged in state
      act(() => {
        result.current.setUser({
          id: 1,
          email: 'test@minerva.edu',
          name: 'Test User',
          role: 'STUDENT',
        });
      });
      localStorage.setItem('accessToken', 'token');
      localStorage.setItem('refreshToken', 'refresh');

      mockedAuthApi.logout.mockResolvedValueOnce(undefined);

      await act(async () => {
        await result.current.logout();
      });

      expect(result.current.user).toBeNull();
      expect(result.current.isAuthenticated).toBe(false);
      expect(result.current.isLoading).toBe(false);
      expect(localStorage.getItem('accessToken')).toBeNull();
      expect(localStorage.getItem('refreshToken')).toBeNull();
    });
  });

  describe('updateUser', () => {
    it('should update user data partially', () => {
      const { result } = renderHook(() => useAuthStore());

      act(() => {
        result.current.setUser({
          id: 1,
          email: 'test@minerva.edu',
          name: 'Test User',
          role: 'STUDENT',
        });
      });

      act(() => {
        result.current.updateUser({ name: 'Updated Name' });
      });

      expect(result.current.user?.name).toBe('Updated Name');
      expect(result.current.user?.email).toBe('test@minerva.edu');
    });

    it('should not update if user is null', () => {
      const { result } = renderHook(() => useAuthStore());

      act(() => {
        result.current.updateUser({ name: 'Updated Name' });
      });

      expect(result.current.user).toBeNull();
    });
  });

  describe('clearError', () => {
    it('should clear error', () => {
      const { result } = renderHook(() => useAuthStore());

      act(() => {
        result.current.setError('Some error');
      });

      expect(result.current.error).toBe('Some error');

      act(() => {
        result.current.clearError();
      });

      expect(result.current.error).toBeNull();
    });
  });
});
