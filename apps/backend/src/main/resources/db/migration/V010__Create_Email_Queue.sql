-- ================================================================================================
-- V010: 이메일 큐 스키마 생성
-- ================================================================================================
-- 작성일: 2024-11-29
-- 설명: 이메일 발송 작업 큐 테이블 생성
-- ================================================================================================

-- 이메일 작업 테이블
CREATE TABLE email_jobs (
    id BIGSERIAL PRIMARY KEY,
    recipient_email VARCHAR(255) NOT NULL,
    subject VARCHAR(500) NOT NULL,
    body TEXT,
    template_name VARCHAR(100),
    template_variables TEXT,
    is_html BOOLEAN NOT NULL DEFAULT true,
    from_name VARCHAR(100),
    reply_to VARCHAR(255),
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    retry_count INTEGER NOT NULL DEFAULT 0,
    max_retries INTEGER NOT NULL DEFAULT 3,
    scheduled_at TIMESTAMP,
    sent_at TIMESTAMP,
    error_message TEXT,
    message_id VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100) NOT NULL,
    updated_by VARCHAR(100) NOT NULL,

    CONSTRAINT chk_email_jobs_status CHECK (status IN ('PENDING', 'PROCESSING', 'RETRYING', 'SENT', 'FAILED')),
    CONSTRAINT chk_email_jobs_retry_count CHECK (retry_count >= 0),
    CONSTRAINT chk_email_jobs_max_retries CHECK (max_retries >= 0)
);

-- 인덱스 생성
CREATE INDEX idx_email_jobs_status ON email_jobs(status);
CREATE INDEX idx_email_jobs_scheduled_at ON email_jobs(scheduled_at);
CREATE INDEX idx_email_jobs_recipient_email ON email_jobs(recipient_email);
CREATE INDEX idx_email_jobs_created_at ON email_jobs(created_at);
CREATE INDEX idx_email_jobs_status_scheduled ON email_jobs(status, scheduled_at);

-- 코멘트 추가
COMMENT ON TABLE email_jobs IS '이메일 발송 작업 큐';
COMMENT ON COLUMN email_jobs.id IS '이메일 작업 ID';
COMMENT ON COLUMN email_jobs.recipient_email IS '수신자 이메일 주소';
COMMENT ON COLUMN email_jobs.subject IS '이메일 제목';
COMMENT ON COLUMN email_jobs.body IS '이메일 본문 (HTML 또는 Plain Text)';
COMMENT ON COLUMN email_jobs.template_name IS '템플릿 이름 (템플릿 사용 시)';
COMMENT ON COLUMN email_jobs.template_variables IS '템플릿 변수 (JSON 형식)';
COMMENT ON COLUMN email_jobs.is_html IS 'HTML 이메일 여부';
COMMENT ON COLUMN email_jobs.from_name IS '발신자 이름';
COMMENT ON COLUMN email_jobs.reply_to IS '답장 이메일 주소';
COMMENT ON COLUMN email_jobs.status IS '작업 상태 (PENDING, PROCESSING, RETRYING, SENT, FAILED)';
COMMENT ON COLUMN email_jobs.retry_count IS '재시도 횟수';
COMMENT ON COLUMN email_jobs.max_retries IS '최대 재시도 횟수';
COMMENT ON COLUMN email_jobs.scheduled_at IS '예약 발송 시간';
COMMENT ON COLUMN email_jobs.sent_at IS '실제 발송 시간';
COMMENT ON COLUMN email_jobs.error_message IS '에러 메시지 (실패 시)';
COMMENT ON COLUMN email_jobs.message_id IS '메시지 ID (SMTP/SendGrid에서 반환)';
COMMENT ON COLUMN email_jobs.created_at IS '생성 일시';
COMMENT ON COLUMN email_jobs.updated_at IS '수정 일시';
COMMENT ON COLUMN email_jobs.created_by IS '생성자';
COMMENT ON COLUMN email_jobs.updated_by IS '수정자';

-- 초기 데이터: 없음 (실제 사용 시 자동 생성됨)
