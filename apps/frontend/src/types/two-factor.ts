/**
 * 2FA (Two-Factor Authentication) 관련 타입 정의
 */

/**
 * 2FA 설정 응답
 */
export interface TwoFactorSetupResponse {
  secret: string; // Base32 encoded secret
  qrCodeUrl: string; // otpauth:// URL for QR code
  backupCodes: string[]; // 10개의 백업 코드
}

/**
 * 2FA 검증 요청
 */
export interface TwoFactorVerifyRequest {
  code: string; // 6자리 TOTP 코드
}

/**
 * 2FA 상태 응답
 */
export interface TwoFactorStatusResponse {
  enabled: boolean;
  enabledAt?: string; // ISO 8601 timestamp
  backupCodesRemaining: number; // 남은 백업 코드 수
}

/**
 * 백업 코드 응답
 */
export interface BackupCodesResponse {
  backupCodes: string[]; // 새로 생성된 백업 코드 10개
}

/**
 * 2FA 로그인 요청
 */
export interface TwoFactorLoginRequest {
  temporaryToken: string; // 1차 인증 후 받은 임시 토큰
  code: string; // 6자리 TOTP 코드
  useBackupCode?: boolean; // 백업 코드 사용 여부
}

/**
 * 2FA 비활성화 요청
 */
export interface TwoFactorDisableRequest {
  password: string; // 본인 확인용 비밀번호
}

/**
 * 2FA 설정 단계
 */
export enum TwoFactorSetupStep {
  INITIAL = 'initial', // 시작 화면
  QR_CODE = 'qr_code', // QR 코드 표시
  VERIFY = 'verify', // 코드 검증
  BACKUP_CODES = 'backup_codes', // 백업 코드 표시
  COMPLETE = 'complete', // 완료
}

/**
 * 2FA 코드 입력 모드
 */
export enum TwoFactorCodeMode {
  TOTP = 'totp', // 앱 인증 코드
  BACKUP = 'backup', // 백업 코드
}
