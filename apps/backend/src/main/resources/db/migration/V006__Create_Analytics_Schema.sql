-- ================================================
-- V006: Create Analytics Schema for E6 Learning Analytics
-- ================================================

-- Create analytics schema
CREATE SCHEMA IF NOT EXISTS analytics;

-- Set search path
SET search_path TO analytics, public;

-- ================================================
-- Analytics Snapshots Table
-- ================================================
CREATE TABLE IF NOT EXISTS analytics.analytics_snapshots (
    id BIGSERIAL PRIMARY KEY,
    session_id BIGINT,
    course_id BIGINT NOT NULL,
    snapshot_time TIMESTAMP WITH TIME ZONE NOT NULL,
    total_participants INTEGER DEFAULT 0,
    active_participants INTEGER DEFAULT 0,
    avg_engagement_score NUMERIC(5,2),
    total_interactions INTEGER DEFAULT 0,
    poll_responses INTEGER DEFAULT 0,
    quiz_attempts INTEGER DEFAULT 0,
    chat_messages INTEGER DEFAULT 0,
    metrics_data JSONB DEFAULT '{}'::jsonb,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP WITH TIME ZONE
);

CREATE INDEX idx_analytics_snapshots_session ON analytics.analytics_snapshots(session_id);
CREATE INDEX idx_analytics_snapshots_course ON analytics.analytics_snapshots(course_id);
CREATE INDEX idx_analytics_snapshots_time ON analytics.analytics_snapshots(snapshot_time);

-- ================================================
-- Learning Metrics Table
-- ================================================
CREATE TYPE analytics.metric_type AS ENUM (
    'ATTENDANCE', 'ENGAGEMENT', 'PERFORMANCE', 'PARTICIPATION',
    'COMPLETION', 'INTERACTION', 'RESPONSE_TIME', 'QUIZ_SCORE',
    'ASSIGNMENT_SCORE', 'POLL_PARTICIPATION'
);

CREATE TABLE IF NOT EXISTS analytics.learning_metrics (
    id BIGSERIAL PRIMARY KEY,
    student_id BIGINT NOT NULL,
    course_id BIGINT NOT NULL,
    metric_type analytics.metric_type NOT NULL,
    metric_value NUMERIC(10,2),
    period_start TIMESTAMP WITH TIME ZONE,
    period_end TIMESTAMP WITH TIME ZONE,
    sample_count INTEGER DEFAULT 0,
    breakdown JSONB DEFAULT '{}'::jsonb,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP WITH TIME ZONE
);

CREATE INDEX idx_learning_metrics_student_course ON analytics.learning_metrics(student_id, course_id);
CREATE INDEX idx_learning_metrics_type ON analytics.learning_metrics(metric_type);
CREATE INDEX idx_learning_metrics_period ON analytics.learning_metrics(period_start, period_end);

-- ================================================
-- Student Reports Table
-- ================================================
CREATE TYPE analytics.report_period AS ENUM ('DAILY', 'WEEKLY', 'MONTHLY', 'SEMESTER', 'CUSTOM');

CREATE TABLE IF NOT EXISTS analytics.student_reports (
    id BIGSERIAL PRIMARY KEY,
    student_id BIGINT NOT NULL,
    course_id BIGINT NOT NULL,
    period analytics.report_period NOT NULL,
    period_start TIMESTAMP WITH TIME ZONE NOT NULL,
    period_end TIMESTAMP WITH TIME ZONE NOT NULL,
    attendance_rate NUMERIC(5,2),
    engagement_score NUMERIC(5,2),
    performance_score NUMERIC(5,2),
    participation_count INTEGER DEFAULT 0,
    quiz_avg_score NUMERIC(5,2),
    assignment_avg_score NUMERIC(5,2),
    summary TEXT,
    detailed_metrics JSONB DEFAULT '{}'::jsonb,
    generated_at TIMESTAMP WITH TIME ZONE,
    report_url VARCHAR(500),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP WITH TIME ZONE
);

CREATE INDEX idx_student_reports_student_course ON analytics.student_reports(student_id, course_id);
CREATE INDEX idx_student_reports_period ON analytics.student_reports(period);
CREATE INDEX idx_student_reports_generated ON analytics.student_reports(generated_at);

-- ================================================
-- Course Reports Table
-- ================================================
CREATE TABLE IF NOT EXISTS analytics.course_reports (
    id BIGSERIAL PRIMARY KEY,
    course_id BIGINT NOT NULL,
    period analytics.report_period NOT NULL,
    period_start TIMESTAMP WITH TIME ZONE NOT NULL,
    period_end TIMESTAMP WITH TIME ZONE NOT NULL,
    total_students INTEGER DEFAULT 0,
    active_students INTEGER DEFAULT 0,
    avg_attendance_rate NUMERIC(5,2),
    avg_engagement_score NUMERIC(5,2),
    avg_performance_score NUMERIC(5,2),
    total_sessions INTEGER DEFAULT 0,
    total_assignments INTEGER DEFAULT 0,
    completion_rate NUMERIC(5,2),
    at_risk_students INTEGER DEFAULT 0,
    summary TEXT,
    detailed_stats JSONB DEFAULT '{}'::jsonb,
    generated_at TIMESTAMP WITH TIME ZONE,
    report_url VARCHAR(500),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP WITH TIME ZONE
);

CREATE INDEX idx_course_reports_course ON analytics.course_reports(course_id);
CREATE INDEX idx_course_reports_period ON analytics.course_reports(period);

-- ================================================
-- Risk Indicators Table
-- ================================================
CREATE TYPE analytics.risk_level AS ENUM ('LOW', 'MEDIUM', 'HIGH', 'CRITICAL');

CREATE TABLE IF NOT EXISTS analytics.risk_indicators (
    id BIGSERIAL PRIMARY KEY,
    student_id BIGINT NOT NULL,
    course_id BIGINT NOT NULL,
    risk_level analytics.risk_level NOT NULL DEFAULT 'LOW',
    risk_score NUMERIC(5,2) NOT NULL,
    attendance_risk NUMERIC(5,2),
    engagement_risk NUMERIC(5,2),
    performance_risk NUMERIC(5,2),
    calculated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    last_activity_at TIMESTAMP WITH TIME ZONE,
    days_inactive INTEGER DEFAULT 0,
    risk_factors JSONB DEFAULT '{}'::jsonb,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP WITH TIME ZONE,
    UNIQUE(student_id, course_id)
);

CREATE INDEX idx_risk_indicators_student_course ON analytics.risk_indicators(student_id, course_id);
CREATE INDEX idx_risk_indicators_level ON analytics.risk_indicators(risk_level);
CREATE INDEX idx_risk_indicators_score ON analytics.risk_indicators(risk_score);

-- ================================================
-- Risk Alerts Table
-- ================================================
CREATE TYPE analytics.alert_status AS ENUM ('PENDING', 'SENT', 'ACKNOWLEDGED', 'RESOLVED');

CREATE TABLE IF NOT EXISTS analytics.risk_alerts (
    id BIGSERIAL PRIMARY KEY,
    risk_indicator_id BIGINT NOT NULL REFERENCES analytics.risk_indicators(id),
    student_id BIGINT NOT NULL,
    course_id BIGINT NOT NULL,
    instructor_id BIGINT,
    status analytics.alert_status NOT NULL DEFAULT 'PENDING',
    alert_message VARCHAR(500) NOT NULL,
    recommendations TEXT,
    sent_at TIMESTAMP WITH TIME ZONE,
    acknowledged_at TIMESTAMP WITH TIME ZONE,
    acknowledged_by BIGINT,
    resolved_at TIMESTAMP WITH TIME ZONE,
    resolved_by BIGINT,
    resolution_notes TEXT,
    alert_data JSONB DEFAULT '{}'::jsonb,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP WITH TIME ZONE
);

CREATE INDEX idx_risk_alerts_student ON analytics.risk_alerts(student_id);
CREATE INDEX idx_risk_alerts_course ON analytics.risk_alerts(course_id);
CREATE INDEX idx_risk_alerts_status ON analytics.risk_alerts(status);
CREATE INDEX idx_risk_alerts_instructor ON analytics.risk_alerts(instructor_id);

-- ================================================
-- Interaction Logs Table
-- ================================================
CREATE TYPE analytics.interaction_type AS ENUM (
    'CHAT', 'REPLY', 'COLLABORATION', 'POLL_RESPONSE',
    'QUIZ_ANSWER', 'DISCUSSION', 'WHITEBOARD', 'BREAKOUT'
);

CREATE TABLE IF NOT EXISTS analytics.interaction_logs (
    id BIGSERIAL PRIMARY KEY,
    course_id BIGINT NOT NULL,
    session_id BIGINT,
    from_student_id BIGINT NOT NULL,
    to_student_id BIGINT NOT NULL,
    interaction_type analytics.interaction_type NOT NULL,
    interaction_time TIMESTAMP WITH TIME ZONE NOT NULL,
    weight INTEGER DEFAULT 1,
    context VARCHAR(500),
    metadata JSONB DEFAULT '{}'::jsonb,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP WITH TIME ZONE
);

CREATE INDEX idx_interaction_logs_course ON analytics.interaction_logs(course_id);
CREATE INDEX idx_interaction_logs_session ON analytics.interaction_logs(session_id);
CREATE INDEX idx_interaction_logs_from_student ON analytics.interaction_logs(from_student_id);
CREATE INDEX idx_interaction_logs_to_student ON analytics.interaction_logs(to_student_id);
CREATE INDEX idx_interaction_logs_time ON analytics.interaction_logs(interaction_time);

-- ================================================
-- Network Nodes Table
-- ================================================
CREATE TABLE IF NOT EXISTS analytics.network_nodes (
    id BIGSERIAL PRIMARY KEY,
    course_id BIGINT NOT NULL,
    student_id BIGINT NOT NULL,
    degree_centrality NUMERIC(10,8),
    betweenness_centrality NUMERIC(10,8),
    closeness_centrality NUMERIC(10,8),
    clustering_coefficient NUMERIC(10,8),
    total_connections INTEGER DEFAULT 0,
    cluster_id BIGINT,
    node_attributes JSONB DEFAULT '{}'::jsonb,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP WITH TIME ZONE,
    UNIQUE(course_id, student_id)
);

CREATE INDEX idx_network_nodes_course_student ON analytics.network_nodes(course_id, student_id);
CREATE INDEX idx_network_nodes_cluster ON analytics.network_nodes(cluster_id);

-- ================================================
-- Network Edges Table
-- ================================================
CREATE TABLE IF NOT EXISTS analytics.network_edges (
    id BIGSERIAL PRIMARY KEY,
    course_id BIGINT NOT NULL,
    from_student_id BIGINT NOT NULL,
    to_student_id BIGINT NOT NULL,
    interaction_count INTEGER DEFAULT 0,
    total_weight INTEGER DEFAULT 0,
    last_interaction_at TIMESTAMP WITH TIME ZONE,
    edge_attributes JSONB DEFAULT '{}'::jsonb,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP WITH TIME ZONE,
    UNIQUE(course_id, from_student_id, to_student_id)
);

CREATE INDEX idx_network_edges_course ON analytics.network_edges(course_id);
CREATE INDEX idx_network_edges_from_student ON analytics.network_edges(from_student_id);
CREATE INDEX idx_network_edges_to_student ON analytics.network_edges(to_student_id);

-- ================================================
-- Student Clusters Table
-- ================================================
CREATE TABLE IF NOT EXISTS analytics.student_clusters (
    id BIGSERIAL PRIMARY KEY,
    course_id BIGINT NOT NULL,
    cluster_name VARCHAR(200),
    cluster_number INTEGER,
    member_count INTEGER DEFAULT 0,
    avg_interaction_score NUMERIC(10,2),
    density NUMERIC(10,8),
    description TEXT,
    member_ids JSONB DEFAULT '[]'::jsonb,
    cluster_stats JSONB DEFAULT '{}'::jsonb,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP WITH TIME ZONE
);

CREATE INDEX idx_student_clusters_course ON analytics.student_clusters(course_id);
CREATE INDEX idx_student_clusters_number ON analytics.student_clusters(cluster_number);

-- ================================================
-- Comments
-- ================================================
COMMENT ON SCHEMA analytics IS 'E6 Learning Analytics Domain';
COMMENT ON TABLE analytics.analytics_snapshots IS 'Point-in-time analytics snapshots for real-time monitoring';
COMMENT ON TABLE analytics.learning_metrics IS 'Aggregated learning metrics for students';
COMMENT ON TABLE analytics.student_reports IS 'Generated student performance reports';
COMMENT ON TABLE analytics.course_reports IS 'Generated course-level reports';
COMMENT ON TABLE analytics.risk_indicators IS 'At-risk student indicators';
COMMENT ON TABLE analytics.risk_alerts IS 'Risk alerts and notifications';
COMMENT ON TABLE analytics.interaction_logs IS 'Student-student interaction logs';
COMMENT ON TABLE analytics.network_nodes IS 'Network graph nodes (students)';
COMMENT ON TABLE analytics.network_edges IS 'Network graph edges (connections)';
COMMENT ON TABLE analytics.student_clusters IS 'Clustered student groups';
