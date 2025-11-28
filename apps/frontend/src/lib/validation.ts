/**
 * 폼 유효성 검사 유틸리티
 */

/**
 * 이메일 유효성 검사
 */
export const validateEmail = (email: string): string | null => {
  if (!email) {
    return '이메일을 입력해주세요.';
  }

  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  if (!emailRegex.test(email)) {
    return '올바른 이메일 형식이 아닙니다.';
  }

  return null;
};

/**
 * 비밀번호 유효성 검사
 */
export const validatePassword = (password: string): string | null => {
  if (!password) {
    return '비밀번호를 입력해주세요.';
  }

  if (password.length < 8) {
    return '비밀번호는 최소 8자 이상이어야 합니다.';
  }

  if (!/[A-Z]/.test(password)) {
    return '비밀번호는 대문자를 포함해야 합니다.';
  }

  if (!/[a-z]/.test(password)) {
    return '비밀번호는 소문자를 포함해야 합니다.';
  }

  if (!/[0-9]/.test(password)) {
    return '비밀번호는 숫자를 포함해야 합니다.';
  }

  if (!/[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]/.test(password)) {
    return '비밀번호는 특수문자를 포함해야 합니다.';
  }

  return null;
};

/**
 * 비밀번호 확인 검사
 */
export const validatePasswordConfirm = (
  password: string,
  passwordConfirm: string
): string | null => {
  if (!passwordConfirm) {
    return '비밀번호 확인을 입력해주세요.';
  }

  if (password !== passwordConfirm) {
    return '비밀번호가 일치하지 않습니다.';
  }

  return null;
};

/**
 * 이름 유효성 검사
 */
export const validateName = (name: string): string | null => {
  if (!name) {
    return '이름을 입력해주세요.';
  }

  if (name.length < 2) {
    return '이름은 최소 2자 이상이어야 합니다.';
  }

  if (name.length > 50) {
    return '이름은 50자를 초과할 수 없습니다.';
  }

  return null;
};

/**
 * 필수 입력 검사
 */
export const validateRequired = (value: string, fieldName: string): string | null => {
  if (!value || value.trim() === '') {
    return `${fieldName}을(를) 입력해주세요.`;
  }
  return null;
};

/**
 * 비밀번호 강도 계산 (0-4)
 */
export const getPasswordStrength = (password: string): number => {
  let strength = 0;

  if (password.length >= 8) strength++;
  if (password.length >= 12) strength++;
  if (/[A-Z]/.test(password) && /[a-z]/.test(password)) strength++;
  if (/[0-9]/.test(password)) strength++;
  if (/[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]/.test(password)) strength++;

  return Math.min(strength, 4);
};

/**
 * 비밀번호 강도 텍스트
 */
export const getPasswordStrengthText = (strength: number): string => {
  switch (strength) {
    case 0:
    case 1:
      return '매우 약함';
    case 2:
      return '약함';
    case 3:
      return '보통';
    case 4:
      return '강함';
    default:
      return '';
  }
};

/**
 * 비밀번호 강도 색상
 */
export const getPasswordStrengthColor = (strength: number): string => {
  switch (strength) {
    case 0:
    case 1:
      return 'bg-red-500';
    case 2:
      return 'bg-orange-500';
    case 3:
      return 'bg-yellow-500';
    case 4:
      return 'bg-green-500';
    default:
      return 'bg-gray-300';
  }
};
