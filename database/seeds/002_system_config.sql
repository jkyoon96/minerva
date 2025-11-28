-- ============================================
-- SEED 002: System Configuration Data
-- ============================================
-- Description: Inserts system configuration and notification settings
-- Author: System
-- Date: 2025-01-29

-- Notification settings for all existing users (역할별 맞춤 설정)
INSERT INTO analytics.notification_settings (
    user_id,
    email_enabled,
    push_enabled,
    alert_types,
    quiet_hours
)
SELECT
    u.id,
    TRUE,
    TRUE,
    CASE
        WHEN r.name = 'admin' THEN '["absence", "low_participation", "grade_drop", "assignment_due", "system_alert", "security_alert"]'::jsonb
        WHEN r.name = 'professor' THEN '["absence", "low_participation", "grade_drop", "assignment_due", "student_at_risk", "course_milestone"]'::jsonb
        WHEN r.name = 'ta' THEN '["assignment_due", "grade_posted", "session_reminder"]'::jsonb
        WHEN r.name = 'student' THEN '["assignment_due", "grade_posted", "session_reminder"]'::jsonb
        ELSE '["assignment_due"]'::jsonb
    END,
    CASE
        WHEN r.name = 'student' THEN '{"start": "21:00", "end": "09:00"}'::jsonb
        ELSE '{"start": "22:00", "end": "08:00"}'::jsonb
    END
FROM auth.users u
JOIN auth.user_roles ur ON u.id = ur.user_id
JOIN auth.roles r ON ur.role_id = r.id
WHERE NOT EXISTS (
    SELECT 1 FROM analytics.notification_settings ns WHERE ns.user_id = u.id
)
ON CONFLICT (user_id) DO NOTHING;

-- Comments
COMMENT ON TABLE analytics.notification_settings IS 'User notification preferences configured for all roles';

-- Rollback script
-- DELETE FROM analytics.notification_settings WHERE user_id IN (SELECT id FROM auth.users);
