/**
 * Analytics type definitions
 * - Real-time Analytics
 * - Learning Reports
 * - Risk Alerts
 * - Network Analysis
 */

// ==================== Common Types ====================

export enum MetricType {
  PARTICIPATION = 'PARTICIPATION',
  ENGAGEMENT = 'ENGAGEMENT',
  ATTENDANCE = 'ATTENDANCE',
  PERFORMANCE = 'PERFORMANCE',
  INTERACTION = 'INTERACTION',
}

export enum ReportPeriod {
  DAILY = 'DAILY',
  WEEKLY = 'WEEKLY',
  MONTHLY = 'MONTHLY',
  SEMESTER = 'SEMESTER',
  CUSTOM = 'CUSTOM',
}

export enum RiskLevel {
  NONE = 'NONE',
  LOW = 'LOW',
  MEDIUM = 'MEDIUM',
  HIGH = 'HIGH',
  CRITICAL = 'CRITICAL',
}

export enum AlertStatus {
  ACTIVE = 'ACTIVE',
  ACKNOWLEDGED = 'ACKNOWLEDGED',
  RESOLVED = 'RESOLVED',
  DISMISSED = 'DISMISSED',
}

export enum AlertType {
  ATTENDANCE = 'ATTENDANCE',
  ENGAGEMENT = 'ENGAGEMENT',
  GRADE = 'GRADE',
  PARTICIPATION = 'PARTICIPATION',
  DEADLINE = 'DEADLINE',
}

export enum InteractionType {
  QUESTION = 'QUESTION',
  ANSWER = 'ANSWER',
  COMMENT = 'COMMENT',
  AGREEMENT = 'AGREEMENT',
  DISAGREEMENT = 'DISAGREEMENT',
  DISCUSSION = 'DISCUSSION',
}

// ==================== Real-time Analytics Types ====================

export interface AnalyticsSnapshot {
  sessionId: number;
  currentTime: string;
  duration: number; // seconds
  participants: {
    total: number;
    active: number;
    inactive: number;
    left: number;
  };
  engagement: {
    overall: number; // 0-100
    distribution: {
      high: number;
      medium: number;
      low: number;
    };
    trend: {
      time: string;
      score: number;
    }[];
  };
  participation: {
    chatMessages: number;
    questionsAsked: number;
    pollsParticipated: number;
    handsRaised: number;
  };
  alerts: {
    type: string;
    count: number;
    students: {
      id: number;
      name: string;
      engagementScore: number;
    }[];
  }[];
}

export interface ParticipantMetric {
  studentId: number;
  studentName: string;
  talkTime: number; // seconds
  messageCount: number;
  pollResponses: number;
  quizResponses: number;
  engagementScore: number;
  lastActivityTime: string;
  isActive: boolean;
  riskLevel: RiskLevel;
}

export interface EngagementTrend {
  timestamp: string;
  participationRate: number;
  averageEngagement: number;
  activeUsers: number;
}

export interface LearningMetric {
  metricType: MetricType;
  value: number;
  label: string;
  trend: 'UP' | 'DOWN' | 'STABLE';
  percentageChange: number;
  comparison?: {
    type: 'CLASS_AVERAGE' | 'PREVIOUS_PERIOD';
    value: number;
  };
}

// ==================== Reports Types ====================

export interface StudentReport {
  id: number;
  studentId: number;
  studentName: string;
  courseId: number;
  courseName: string;
  period: {
    from: string;
    to: string;
  };
  summary: {
    attendanceRate: number;
    participationScore: number;
    averageQuizScore: number;
    averageAssignmentScore: number;
    overallRank: number;
    totalStudents: number;
  };
  weeklyProgress: {
    week: number;
    participationScore: number;
    quizScore: number;
    classAverage: number;
  }[];
  strengths: {
    topic: string;
    score: number;
    percentage: number;
  }[];
  weaknesses: {
    topic: string;
    score: number;
    percentage: number;
    classAverage: number;
  }[];
  recommendations: {
    category: string;
    suggestion: string;
    resources: {
      title: string;
      url: string;
      type: 'VIDEO' | 'ARTICLE' | 'EXERCISE' | 'QUIZ';
    }[];
  }[];
  scoreDistribution: {
    range: string;
    count: number;
    yourScore?: number;
  }[];
  generatedAt: string;
}

export interface CourseReport {
  id: number;
  courseId: number;
  courseName: string;
  period: {
    from: string;
    to: string;
  };
  overview: {
    totalStudents: number;
    activeStudents: number;
    completionRate: number;
    sessionsCompleted: number;
    totalSessions: number;
    totalParticipationTime: number; // hours
    averageParticipationRate: number;
  };
  weeklyTrend: {
    week: number;
    participationRate: number;
    averageScore: number;
    notes?: string;
  }[];
  questionAnalysis: {
    rank: number;
    topic: string;
    correctRate: number;
    relatedSession: string;
  }[];
  gradeDistribution: {
    grade: string;
    range: string;
    count: number;
    percentage: number;
  }[];
  studentClassification: {
    category: 'EXCELLENT' | 'GOOD' | 'AVERAGE' | 'AT_RISK';
    percentage: number;
    count: number;
    students: {
      id: number;
      name: string;
      score: number;
    }[];
  }[];
  correlation: {
    participationVsGrade: number;
    attendanceVsGrade: number;
    interpretation: string;
  };
  generatedAt: string;
}

export interface ReportExportRequest {
  reportId: number;
  format: 'PDF' | 'EXCEL' | 'CSV';
  options?: {
    includeCharts?: boolean;
    includeRawData?: boolean;
  };
}

export interface ReportExportResult {
  reportId: number;
  downloadUrl: string;
  expiresAt: string;
  fileSize: number;
  format: string;
}

// ==================== Risk & Alert Types ====================

export interface RiskIndicator {
  indicatorType: 'ATTENDANCE' | 'ENGAGEMENT' | 'GRADE' | 'PARTICIPATION';
  score: number; // 0-100, higher = more risk
  threshold: number;
  isTriggered: boolean;
  details: string[];
}

export interface RiskStudent {
  studentId: number;
  studentName: string;
  email: string;
  riskLevel: RiskLevel;
  riskScore: number; // 0-100
  indicators: RiskIndicator[];
  lastActivity: string;
  alerts: RiskAlert[];
  interventions: Intervention[];
}

export interface RiskAlert {
  id: number;
  courseId: number;
  studentId: number;
  studentName: string;
  type: AlertType;
  severity: RiskLevel;
  message: string;
  details: {
    [key: string]: any;
  };
  triggeredAt: string;
  status: AlertStatus;
  acknowledgedBy?: number;
  acknowledgedByName?: string;
  acknowledgedAt?: string;
  resolvedBy?: number;
  resolvedByName?: string;
  resolvedAt?: string;
  note?: string;
}

export interface Intervention {
  id: number;
  studentId: number;
  type: 'EMAIL' | 'MEETING' | 'NOTE' | 'ALERT_ESCALATION' | 'RESOLVED';
  description: string;
  createdBy: number;
  createdByName: string;
  createdAt: string;
  status: 'PENDING' | 'COMPLETED';
}

export interface AlertSettings {
  courseId: number;
  thresholds: {
    consecutiveAbsences: number;
    participationDropPercentage: number;
    gradeDropPercentage: number;
    missedAssignments: number;
  };
  notifications: {
    emailEnabled: boolean;
    dashboardEnabled: boolean;
    departmentSharingEnabled: boolean;
  };
  updatedAt: string;
}

// ==================== Network Analysis Types ====================

export interface NetworkNode {
  id: number;
  studentId: number;
  studentName: string;
  centrality: number; // 0-1
  connections: number;
  interactionCount: number;
  role: 'HUB' | 'BRIDGE' | 'PERIPHERAL' | 'ISOLATED';
  clusterId?: number;
}

export interface NetworkEdge {
  id: number;
  sourceId: number;
  targetId: number;
  weight: number; // interaction frequency
  interactionTypes: {
    type: InteractionType;
    count: number;
  }[];
}

export interface StudentCluster {
  id: number;
  name: string;
  color: string;
  size: number;
  members: number[];
  leaderId: number;
  leaderName: string;
  internalConnectivity: 'HIGH' | 'MEDIUM' | 'LOW';
  characteristics: string[];
}

export interface NetworkAnalysis {
  courseId: number;
  sessionId?: number;
  period?: {
    from: string;
    to: string;
  };
  statistics: {
    totalNodes: number;
    totalEdges: number;
    networkDensity: number; // 0-1
    averageCentrality: number;
    isolatedStudents: number;
  };
  nodes: NetworkNode[];
  edges: NetworkEdge[];
  clusters: StudentCluster[];
  topInfluencers: {
    studentId: number;
    studentName: string;
    centrality: number;
    rank: number;
  }[];
  isolatedStudents: {
    studentId: number;
    studentName: string;
    connections: number;
  }[];
  insights: {
    category: string;
    description: string;
    recommendation: string;
  }[];
  weeklyEvolution?: {
    week: number;
    density: number;
    newConnections: number;
    notes?: string;
  }[];
}

export interface StudentInteractionDetail {
  studentId: number;
  studentName: string;
  role: NetworkNode['role'];
  centrality: number;
  connections: number;
  statistics: {
    totalInteractions: number;
    initiatedInteractions: number;
    respondedInteractions: number;
  };
  topInteractions: {
    partnerId: number;
    partnerName: string;
    count: number;
  }[];
  interactionTypeBreakdown: {
    type: InteractionType;
    count: number;
    percentage: number;
  }[];
  weeklyActivity: {
    week: number;
    interactionCount: number;
  }[];
  strengths: string[];
  growthOpportunities: string[];
  recommendedRole: string;
}

// ==================== API Request/Response Types ====================

export interface GetRealtimeAnalyticsRequest {
  sessionId: number;
}

export interface GetTrendsRequest {
  courseId: number;
  from?: string;
  to?: string;
}

export interface GetSnapshotRequest {
  courseId: number;
  timestamp?: string;
}

export interface GetStudentReportRequest {
  studentId: number;
  courseId: number;
  period?: ReportPeriod;
  from?: string;
  to?: string;
}

export interface GetCourseReportRequest {
  courseId: number;
  period?: ReportPeriod;
  from?: string;
  to?: string;
}

export interface ExportReportRequest {
  reportId: number;
  format: 'PDF' | 'EXCEL';
}

export interface GetRiskStudentsRequest {
  courseId: number;
  riskLevel?: RiskLevel;
}

export interface GetAlertsRequest {
  courseId?: number;
  studentId?: number;
  status?: AlertStatus;
  type?: AlertType;
}

export interface AcknowledgeAlertRequest {
  alertId: number;
  note?: string;
}

export interface ResolveAlertRequest {
  alertId: number;
  resolution: string;
}

export interface CreateInterventionRequest {
  studentId: number;
  type: Intervention['type'];
  description: string;
}

export interface UpdateAlertSettingsRequest {
  courseId: number;
  thresholds?: Partial<AlertSettings['thresholds']>;
  notifications?: Partial<AlertSettings['notifications']>;
}

export interface GetNetworkAnalysisRequest {
  courseId: number;
  sessionId?: number;
  from?: string;
  to?: string;
}

export interface GetStudentInteractionsRequest {
  studentId: number;
  courseId: number;
  from?: string;
  to?: string;
}

// ==================== UI State Types ====================

export interface AnalyticsUIState {
  selectedCourse?: number;
  selectedSession?: number;
  selectedStudent?: number;
  viewMode: 'realtime' | 'reports' | 'risks' | 'network';
  dateRange: {
    from: string;
    to: string;
  };
  filters: {
    riskLevel?: RiskLevel;
    alertStatus?: AlertStatus;
    metricType?: MetricType;
  };
  showInterventionModal: boolean;
  showExportModal: boolean;
}

// ==================== Chart Data Types ====================

export interface ChartDataPoint {
  label: string;
  value: number;
  color?: string;
  metadata?: any;
}

export interface TimeSeriesData {
  timestamp: string;
  value: number;
  label?: string;
}

export interface HeatmapCell {
  x: string | number;
  y: string | number;
  value: number;
  color: string;
}
