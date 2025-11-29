/**
 * Analytics state management store (Zustand)
 * - Real-time Analytics
 * - Learning Reports
 * - Risk Alerts
 * - Network Analysis
 */

import { create } from 'zustand';
import {
  AnalyticsSnapshot,
  EngagementTrend,
  ParticipantMetric,
  StudentReport,
  CourseReport,
  RiskStudent,
  RiskAlert,
  AlertSettings,
  Intervention,
  NetworkAnalysis,
  StudentInteractionDetail,
  StudentCluster,
  AnalyticsUIState,
  RiskLevel,
  AlertStatus,
} from '@/types/analytics';

interface AnalyticsState {
  // Real-time analytics state
  currentSnapshot: AnalyticsSnapshot | null;
  engagementTrends: EngagementTrend[];
  participantMetrics: ParticipantMetric[];
  historicalSnapshots: AnalyticsSnapshot[];

  // Reports state
  studentReports: Map<number, StudentReport>;
  courseReports: Map<number, CourseReport>;
  currentStudentReport: StudentReport | null;
  currentCourseReport: CourseReport | null;

  // Risk & alerts state
  riskStudents: RiskStudent[];
  alerts: RiskAlert[];
  alertSettings: AlertSettings | null;
  interventions: Map<number, Intervention[]>; // studentId -> interventions

  // Network analysis state
  networkAnalysis: NetworkAnalysis | null;
  studentClusters: StudentCluster[];
  studentInteractionDetail: StudentInteractionDetail | null;

  // UI state
  uiState: AnalyticsUIState;

  // Loading & error states
  isLoading: boolean;
  error: string | null;

  // Actions - Real-time Analytics
  setCurrentSnapshot: (snapshot: AnalyticsSnapshot | null) => void;
  setEngagementTrends: (trends: EngagementTrend[]) => void;
  setParticipantMetrics: (metrics: ParticipantMetric[]) => void;
  setHistoricalSnapshots: (snapshots: AnalyticsSnapshot[]) => void;
  updateParticipantMetric: (studentId: number, updates: Partial<ParticipantMetric>) => void;

  // Actions - Reports
  setStudentReport: (studentId: number, report: StudentReport) => void;
  setCourseReport: (courseId: number, report: CourseReport) => void;
  setCurrentStudentReport: (report: StudentReport | null) => void;
  setCurrentCourseReport: (report: CourseReport | null) => void;

  // Actions - Risk & Alerts
  setRiskStudents: (students: RiskStudent[]) => void;
  updateRiskStudent: (studentId: number, updates: Partial<RiskStudent>) => void;
  setAlerts: (alerts: RiskAlert[]) => void;
  updateAlert: (alertId: number, updates: Partial<RiskAlert>) => void;
  addAlert: (alert: RiskAlert) => void;
  removeAlert: (alertId: number) => void;
  setAlertSettings: (settings: AlertSettings | null) => void;
  setInterventions: (studentId: number, interventions: Intervention[]) => void;
  addIntervention: (studentId: number, intervention: Intervention) => void;

  // Actions - Network Analysis
  setNetworkAnalysis: (analysis: NetworkAnalysis | null) => void;
  setStudentClusters: (clusters: StudentCluster[]) => void;
  setStudentInteractionDetail: (detail: StudentInteractionDetail | null) => void;

  // Actions - UI State
  setUIState: (updates: Partial<AnalyticsUIState>) => void;
  setViewMode: (mode: AnalyticsUIState['viewMode']) => void;
  setDateRange: (from: string, to: string) => void;
  setSelectedCourse: (courseId: number | undefined) => void;
  setSelectedSession: (sessionId: number | undefined) => void;
  setSelectedStudent: (studentId: number | undefined) => void;

  // Actions - General
  setLoading: (loading: boolean) => void;
  setError: (error: string | null) => void;
  reset: () => void;
}

const initialState = {
  // Real-time analytics
  currentSnapshot: null,
  engagementTrends: [],
  participantMetrics: [],
  historicalSnapshots: [],

  // Reports
  studentReports: new Map<number, StudentReport>(),
  courseReports: new Map<number, CourseReport>(),
  currentStudentReport: null,
  currentCourseReport: null,

  // Risk & alerts
  riskStudents: [],
  alerts: [],
  alertSettings: null,
  interventions: new Map<number, Intervention[]>(),

  // Network analysis
  networkAnalysis: null,
  studentClusters: [],
  studentInteractionDetail: null,

  // UI state
  uiState: {
    viewMode: 'realtime' as const,
    dateRange: {
      from: new Date(Date.now() - 30 * 24 * 60 * 60 * 1000).toISOString().split('T')[0],
      to: new Date().toISOString().split('T')[0],
    },
    filters: {},
    showInterventionModal: false,
    showExportModal: false,
  },

  // Loading & error
  isLoading: false,
  error: null,
};

export const useAnalyticsStore = create<AnalyticsState>()((set, get) => ({
  ...initialState,

  // Real-time analytics actions
  setCurrentSnapshot: (snapshot) => set({ currentSnapshot: snapshot }),

  setEngagementTrends: (trends) => set({ engagementTrends: trends }),

  setParticipantMetrics: (metrics) => set({ participantMetrics: metrics }),

  setHistoricalSnapshots: (snapshots) => set({ historicalSnapshots: snapshots }),

  updateParticipantMetric: (studentId, updates) =>
    set((state) => ({
      participantMetrics: state.participantMetrics.map((metric) =>
        metric.studentId === studentId ? { ...metric, ...updates } : metric
      ),
    })),

  // Reports actions
  setStudentReport: (studentId, report) =>
    set((state) => {
      const newMap = new Map(state.studentReports);
      newMap.set(studentId, report);
      return { studentReports: newMap };
    }),

  setCourseReport: (courseId, report) =>
    set((state) => {
      const newMap = new Map(state.courseReports);
      newMap.set(courseId, report);
      return { courseReports: newMap };
    }),

  setCurrentStudentReport: (report) => set({ currentStudentReport: report }),

  setCurrentCourseReport: (report) => set({ currentCourseReport: report }),

  // Risk & alerts actions
  setRiskStudents: (students) => set({ riskStudents: students }),

  updateRiskStudent: (studentId, updates) =>
    set((state) => ({
      riskStudents: state.riskStudents.map((student) =>
        student.studentId === studentId ? { ...student, ...updates } : student
      ),
    })),

  setAlerts: (alerts) => set({ alerts }),

  updateAlert: (alertId, updates) =>
    set((state) => ({
      alerts: state.alerts.map((alert) =>
        alert.id === alertId ? { ...alert, ...updates } : alert
      ),
    })),

  addAlert: (alert) =>
    set((state) => ({
      alerts: [alert, ...state.alerts],
    })),

  removeAlert: (alertId) =>
    set((state) => ({
      alerts: state.alerts.filter((alert) => alert.id !== alertId),
    })),

  setAlertSettings: (settings) => set({ alertSettings: settings }),

  setInterventions: (studentId, interventions) =>
    set((state) => {
      const newMap = new Map(state.interventions);
      newMap.set(studentId, interventions);
      return { interventions: newMap };
    }),

  addIntervention: (studentId, intervention) =>
    set((state) => {
      const newMap = new Map(state.interventions);
      const existing = newMap.get(studentId) || [];
      newMap.set(studentId, [intervention, ...existing]);
      return { interventions: newMap };
    }),

  // Network analysis actions
  setNetworkAnalysis: (analysis) => set({ networkAnalysis: analysis }),

  setStudentClusters: (clusters) => set({ studentClusters: clusters }),

  setStudentInteractionDetail: (detail) => set({ studentInteractionDetail: detail }),

  // UI state actions
  setUIState: (updates) =>
    set((state) => ({
      uiState: { ...state.uiState, ...updates },
    })),

  setViewMode: (mode) =>
    set((state) => ({
      uiState: { ...state.uiState, viewMode: mode },
    })),

  setDateRange: (from, to) =>
    set((state) => ({
      uiState: { ...state.uiState, dateRange: { from, to } },
    })),

  setSelectedCourse: (courseId) =>
    set((state) => ({
      uiState: { ...state.uiState, selectedCourse: courseId },
    })),

  setSelectedSession: (sessionId) =>
    set((state) => ({
      uiState: { ...state.uiState, selectedSession: sessionId },
    })),

  setSelectedStudent: (studentId) =>
    set((state) => ({
      uiState: { ...state.uiState, selectedStudent: studentId },
    })),

  // General actions
  setLoading: (loading) => set({ isLoading: loading }),

  setError: (error) => set({ error }),

  reset: () => set(initialState),
}));

// Selectors
export const analyticsSelectors = {
  // Get active alerts
  getActiveAlerts: (state: AnalyticsState) =>
    state.alerts.filter((alert) => alert.status === AlertStatus.ACTIVE),

  // Get high-risk students
  getHighRiskStudents: (state: AnalyticsState) =>
    state.riskStudents.filter(
      (student) => student.riskLevel === RiskLevel.HIGH || student.riskLevel === RiskLevel.CRITICAL
    ),

  // Get isolated students from network
  getIsolatedStudents: (state: AnalyticsState) =>
    state.networkAnalysis?.isolatedStudents || [],

  // Get top influencers
  getTopInfluencers: (state: AnalyticsState) => state.networkAnalysis?.topInfluencers || [],

  // Get participant metrics by risk level
  getParticipantsByRisk: (state: AnalyticsState, riskLevel: RiskLevel) =>
    state.participantMetrics.filter((metric) => metric.riskLevel === riskLevel),

  // Get recent interventions
  getRecentInterventions: (state: AnalyticsState, limit: number = 10) => {
    const allInterventions: Intervention[] = [];
    state.interventions.forEach((interventions) => {
      allInterventions.push(...interventions);
    });
    return allInterventions
      .sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime())
      .slice(0, limit);
  },

  // Calculate alert statistics
  getAlertStatistics: (state: AnalyticsState) => {
    const total = state.alerts.length;
    const active = state.alerts.filter((a) => a.status === AlertStatus.ACTIVE).length;
    const resolved = state.alerts.filter((a) => a.status === AlertStatus.RESOLVED).length;
    const acknowledged = state.alerts.filter((a) => a.status === AlertStatus.ACKNOWLEDGED).length;

    return { total, active, resolved, acknowledged };
  },

  // Get engagement summary
  getEngagementSummary: (state: AnalyticsState) => {
    if (!state.currentSnapshot) return null;

    const { engagement, participation } = state.currentSnapshot;
    return {
      overall: engagement.overall,
      distribution: engagement.distribution,
      participation,
      totalParticipants: state.currentSnapshot.participants.total,
      activeParticipants: state.currentSnapshot.participants.active,
    };
  },

  // Get risk distribution
  getRiskDistribution: (state: AnalyticsState) => {
    const distribution = {
      [RiskLevel.NONE]: 0,
      [RiskLevel.LOW]: 0,
      [RiskLevel.MEDIUM]: 0,
      [RiskLevel.HIGH]: 0,
      [RiskLevel.CRITICAL]: 0,
    };

    state.riskStudents.forEach((student) => {
      distribution[student.riskLevel]++;
    });

    return distribution;
  },
};
