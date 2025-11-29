/**
 * Analytics API client
 * - Real-time Analytics
 * - Learning Reports
 * - Risk Alerts
 * - Network Analysis
 */

import apiClient, { parseApiError } from './client';
import { ApiResponse } from './types';
import {
  AnalyticsSnapshot,
  EngagementTrend,
  ParticipantMetric,
  StudentReport,
  CourseReport,
  ReportExportResult,
  RiskStudent,
  RiskAlert,
  AlertSettings,
  Intervention,
  NetworkAnalysis,
  StudentInteractionDetail,
  StudentCluster,
  GetRealtimeAnalyticsRequest,
  GetTrendsRequest,
  GetSnapshotRequest,
  GetStudentReportRequest,
  GetCourseReportRequest,
  ExportReportRequest,
  GetRiskStudentsRequest,
  GetAlertsRequest,
  AcknowledgeAlertRequest,
  ResolveAlertRequest,
  CreateInterventionRequest,
  UpdateAlertSettingsRequest,
  GetNetworkAnalysisRequest,
  GetStudentInteractionsRequest,
} from '@/types/analytics';

/**
 * Real-time Analytics APIs
 */
export const realtimeAnalyticsApi = {
  /**
   * Get real-time analytics snapshot for a session
   */
  getSnapshot: async (params: GetRealtimeAnalyticsRequest): Promise<AnalyticsSnapshot> => {
    try {
      const response = await apiClient.get<ApiResponse<AnalyticsSnapshot>>(
        `/v1/analytics/realtime/${params.sessionId}`
      );
      return response.data.data;
    } catch (error) {
      throw parseApiError(error);
    }
  },

  /**
   * Get engagement trends for a course
   */
  getTrends: async (params: GetTrendsRequest): Promise<EngagementTrend[]> => {
    try {
      const response = await apiClient.get<ApiResponse<EngagementTrend[]>>(
        `/v1/analytics/trends/${params.courseId}`,
        {
          params: {
            from: params.from,
            to: params.to,
          },
        }
      );
      return response.data.data;
    } catch (error) {
      throw parseApiError(error);
    }
  },

  /**
   * Get participant metrics for a session
   */
  getParticipantMetrics: async (sessionId: number): Promise<ParticipantMetric[]> => {
    try {
      const response = await apiClient.get<ApiResponse<ParticipantMetric[]>>(
        `/v1/analytics/realtime/${sessionId}/participants`
      );
      return response.data.data;
    } catch (error) {
      throw parseApiError(error);
    }
  },

  /**
   * Get historical snapshots for a course
   */
  getSnapshots: async (params: GetSnapshotRequest): Promise<AnalyticsSnapshot[]> => {
    try {
      const response = await apiClient.get<ApiResponse<AnalyticsSnapshot[]>>(
        `/v1/analytics/snapshots/${params.courseId}`,
        {
          params: {
            timestamp: params.timestamp,
          },
        }
      );
      return response.data.data;
    } catch (error) {
      throw parseApiError(error);
    }
  },
};

/**
 * Reports APIs
 */
export const reportsApi = {
  /**
   * Get student individual report
   */
  getStudentReport: async (params: GetStudentReportRequest): Promise<StudentReport> => {
    try {
      const response = await apiClient.get<ApiResponse<StudentReport>>(
        `/v1/analytics/reports/student/${params.studentId}`,
        {
          params: {
            courseId: params.courseId,
            period: params.period,
            from: params.from,
            to: params.to,
          },
        }
      );
      return response.data.data;
    } catch (error) {
      throw parseApiError(error);
    }
  },

  /**
   * Get course comprehensive report
   */
  getCourseReport: async (params: GetCourseReportRequest): Promise<CourseReport> => {
    try {
      const response = await apiClient.get<ApiResponse<CourseReport>>(
        `/v1/analytics/reports/course/${params.courseId}`,
        {
          params: {
            period: params.period,
            from: params.from,
            to: params.to,
          },
        }
      );
      return response.data.data;
    } catch (error) {
      throw parseApiError(error);
    }
  },

  /**
   * Export report to PDF or Excel
   */
  exportReport: async (params: ExportReportRequest): Promise<ReportExportResult> => {
    try {
      const response = await apiClient.post<ApiResponse<ReportExportResult>>(
        `/v1/analytics/reports/export/${params.reportId}`,
        {
          format: params.format,
        }
      );
      return response.data.data;
    } catch (error) {
      throw parseApiError(error);
    }
  },

  /**
   * Get my learning report (student view)
   */
  getMyReport: async (courseId: number): Promise<StudentReport> => {
    try {
      const response = await apiClient.get<ApiResponse<StudentReport>>(
        `/v1/analytics/reports/me`,
        {
          params: { courseId },
        }
      );
      return response.data.data;
    } catch (error) {
      throw parseApiError(error);
    }
  },
};

/**
 * Risk & Alert APIs
 */
export const riskAlertsApi = {
  /**
   * Get at-risk students for a course
   */
  getRiskStudents: async (params: GetRiskStudentsRequest): Promise<RiskStudent[]> => {
    try {
      const response = await apiClient.get<ApiResponse<RiskStudent[]>>(
        `/v1/analytics/risks/course/${params.courseId}`,
        {
          params: {
            riskLevel: params.riskLevel,
          },
        }
      );
      return response.data.data;
    } catch (error) {
      throw parseApiError(error);
    }
  },

  /**
   * Get alerts (optionally filtered)
   */
  getAlerts: async (params: GetAlertsRequest = {}): Promise<RiskAlert[]> => {
    try {
      const response = await apiClient.get<ApiResponse<RiskAlert[]>>(
        `/v1/analytics/alerts`,
        {
          params: {
            courseId: params.courseId,
            studentId: params.studentId,
            status: params.status,
            type: params.type,
          },
        }
      );
      return response.data.data;
    } catch (error) {
      throw parseApiError(error);
    }
  },

  /**
   * Acknowledge an alert
   */
  acknowledgeAlert: async (params: AcknowledgeAlertRequest): Promise<RiskAlert> => {
    try {
      const response = await apiClient.put<ApiResponse<RiskAlert>>(
        `/v1/analytics/alerts/${params.alertId}/acknowledge`,
        {
          note: params.note,
        }
      );
      return response.data.data;
    } catch (error) {
      throw parseApiError(error);
    }
  },

  /**
   * Resolve an alert
   */
  resolveAlert: async (params: ResolveAlertRequest): Promise<RiskAlert> => {
    try {
      const response = await apiClient.put<ApiResponse<RiskAlert>>(
        `/v1/analytics/alerts/${params.alertId}/resolve`,
        {
          resolution: params.resolution,
        }
      );
      return response.data.data;
    } catch (error) {
      throw parseApiError(error);
    }
  },

  /**
   * Get alert settings for a course
   */
  getAlertSettings: async (courseId: number): Promise<AlertSettings> => {
    try {
      const response = await apiClient.get<ApiResponse<AlertSettings>>(
        `/v1/analytics/alerts/settings/${courseId}`
      );
      return response.data.data;
    } catch (error) {
      throw parseApiError(error);
    }
  },

  /**
   * Update alert settings
   */
  updateAlertSettings: async (params: UpdateAlertSettingsRequest): Promise<AlertSettings> => {
    try {
      const response = await apiClient.put<ApiResponse<AlertSettings>>(
        `/v1/analytics/alerts/settings/${params.courseId}`,
        {
          thresholds: params.thresholds,
          notifications: params.notifications,
        }
      );
      return response.data.data;
    } catch (error) {
      throw parseApiError(error);
    }
  },

  /**
   * Create intervention record
   */
  createIntervention: async (params: CreateInterventionRequest): Promise<Intervention> => {
    try {
      const response = await apiClient.post<ApiResponse<Intervention>>(
        `/v1/analytics/intervention`,
        params
      );
      return response.data.data;
    } catch (error) {
      throw parseApiError(error);
    }
  },

  /**
   * Get interventions for a student
   */
  getInterventions: async (studentId: number): Promise<Intervention[]> => {
    try {
      const response = await apiClient.get<ApiResponse<Intervention[]>>(
        `/v1/analytics/intervention/student/${studentId}`
      );
      return response.data.data;
    } catch (error) {
      throw parseApiError(error);
    }
  },
};

/**
 * Network Analysis APIs
 */
export const networkAnalysisApi = {
  /**
   * Get network analysis for a course or session
   */
  getNetworkAnalysis: async (params: GetNetworkAnalysisRequest): Promise<NetworkAnalysis> => {
    try {
      const response = await apiClient.get<ApiResponse<NetworkAnalysis>>(
        `/v1/analytics/network/${params.courseId}`,
        {
          params: {
            sessionId: params.sessionId,
            from: params.from,
            to: params.to,
          },
        }
      );
      return response.data.data;
    } catch (error) {
      throw parseApiError(error);
    }
  },

  /**
   * Get student clusters for a course
   */
  getClusters: async (courseId: number): Promise<StudentCluster[]> => {
    try {
      const response = await apiClient.get<ApiResponse<StudentCluster[]>>(
        `/v1/analytics/network/${courseId}/clusters`
      );
      return response.data.data;
    } catch (error) {
      throw parseApiError(error);
    }
  },

  /**
   * Get student interaction details
   */
  getStudentInteractions: async (
    params: GetStudentInteractionsRequest
  ): Promise<StudentInteractionDetail> => {
    try {
      const response = await apiClient.get<ApiResponse<StudentInteractionDetail>>(
        `/v1/analytics/network/students/${params.studentId}/interactions`,
        {
          params: {
            courseId: params.courseId,
            from: params.from,
            to: params.to,
          },
        }
      );
      return response.data.data;
    } catch (error) {
      throw parseApiError(error);
    }
  },
};

// Export all APIs
export default {
  realtime: realtimeAnalyticsApi,
  reports: reportsApi,
  risks: riskAlertsApi,
  network: networkAnalysisApi,
};
