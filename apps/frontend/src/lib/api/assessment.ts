/**
 * Assessment & Feedback API client
 * - Auto Grading
 * - AI Grading
 * - Code Evaluation
 * - Feedback
 * - Participation
 * - Peer Review
 */

import apiClient, { parseApiError } from './client';
import { ApiResponse } from './types';
import {
  AutoGradingResult,
  AnswerStatistics,
  AIGradingResult,
  AIGradingTask,
  CodeSubmission,
  CodeExecutionResult,
  PlagiarismCheckResult,
  Feedback,
  ParticipationDashboard,
  ParticipationScore,
  ParticipationWeightConfig,
  PeerReviewAssignment,
  PeerReviewResult,
  PeerReviewRubric,
  PeerReviewSubmission,
  ExecuteCodeRequest,
  ModifyGradeRequest,
  SubmitPeerReviewRequest,
  UpdateParticipationWeightsRequest,
} from '@/types/assessment';

/**
 * Auto Grading APIs
 */
export const autoGradingApi = {
  /**
   * Get auto grading result for a submission
   */
  getResult: async (submissionId: number): Promise<AutoGradingResult> => {
    try {
      const response = await apiClient.get<ApiResponse<AutoGradingResult>>(
        `/v1/assessment/auto-grading/submissions/${submissionId}/results`
      );
      return response.data.data;
    } catch (error) {
      throw parseApiError(error);
    }
  },

  /**
   * Get all auto grading results for an assignment
   */
  getAssignmentResults: async (assignmentId: number): Promise<AutoGradingResult[]> => {
    try {
      const response = await apiClient.get<ApiResponse<AutoGradingResult[]>>(
        `/v1/assessment/auto-grading/assignments/${assignmentId}/results`
      );
      return response.data.data;
    } catch (error) {
      throw parseApiError(error);
    }
  },

  /**
   * Get answer statistics for an assignment
   */
  getAnswerStatistics: async (assignmentId: number): Promise<AnswerStatistics[]> => {
    try {
      const response = await apiClient.get<ApiResponse<AnswerStatistics[]>>(
        `/v1/assessment/auto-grading/assignments/${assignmentId}/statistics`
      );
      return response.data.data;
    } catch (error) {
      throw parseApiError(error);
    }
  },

  /**
   * Regrade a submission
   */
  regradeSubmission: async (submissionId: number): Promise<AutoGradingResult> => {
    try {
      const response = await apiClient.post<ApiResponse<AutoGradingResult>>(
        `/v1/assessment/auto-grading/submissions/${submissionId}/regrade`
      );
      return response.data.data;
    } catch (error) {
      throw parseApiError(error);
    }
  },
};

/**
 * AI Grading APIs
 */
export const aiGradingApi = {
  /**
   * Get AI grading tasks for an assignment
   */
  getTasks: async (assignmentId: number): Promise<AIGradingTask[]> => {
    try {
      const response = await apiClient.get<ApiResponse<AIGradingTask[]>>(
        `/v1/assessment/ai-grading/assignments/${assignmentId}/tasks`
      );
      return response.data.data;
    } catch (error) {
      throw parseApiError(error);
    }
  },

  /**
   * Get AI grading result for a submission
   */
  getResult: async (submissionId: number): Promise<AIGradingResult> => {
    try {
      const response = await apiClient.get<ApiResponse<AIGradingResult>>(
        `/v1/assessment/ai-grading/submissions/${submissionId}/results`
      );
      return response.data.data;
    } catch (error) {
      throw parseApiError(error);
    }
  },

  /**
   * Trigger AI grading for a submission
   */
  gradeSubmission: async (submissionId: number): Promise<AIGradingResult> => {
    try {
      const response = await apiClient.post<ApiResponse<AIGradingResult>>(
        `/v1/assessment/ai-grading/submissions/${submissionId}/grade`
      );
      return response.data.data;
    } catch (error) {
      throw parseApiError(error);
    }
  },

  /**
   * Modify AI-generated grade
   */
  modifyGrade: async (data: ModifyGradeRequest): Promise<AIGradingResult> => {
    try {
      const response = await apiClient.post<ApiResponse<AIGradingResult>>(
        `/v1/assessment/ai-grading/submissions/${data.submissionId}/modify`,
        data
      );
      return response.data.data;
    } catch (error) {
      throw parseApiError(error);
    }
  },

  /**
   * Get pending reviews (high confidence needed)
   */
  getPendingReviews: async (assignmentId: number): Promise<AIGradingTask[]> => {
    try {
      const response = await apiClient.get<ApiResponse<AIGradingTask[]>>(
        `/v1/assessment/ai-grading/assignments/${assignmentId}/pending-reviews`
      );
      return response.data.data;
    } catch (error) {
      throw parseApiError(error);
    }
  },

  /**
   * Approve AI grading result
   */
  approveResult: async (submissionId: number): Promise<AIGradingResult> => {
    try {
      const response = await apiClient.post<ApiResponse<AIGradingResult>>(
        `/v1/assessment/ai-grading/submissions/${submissionId}/approve`
      );
      return response.data.data;
    } catch (error) {
      throw parseApiError(error);
    }
  },
};

/**
 * Code Evaluation APIs
 */
export const codeEvaluationApi = {
  /**
   * Get code submission
   */
  getSubmission: async (submissionId: number): Promise<CodeSubmission> => {
    try {
      const response = await apiClient.get<ApiResponse<CodeSubmission>>(
        `/v1/assessment/code/submissions/${submissionId}`
      );
      return response.data.data;
    } catch (error) {
      throw parseApiError(error);
    }
  },

  /**
   * Execute code and run test cases
   */
  executeCode: async (data: ExecuteCodeRequest): Promise<CodeExecutionResult> => {
    try {
      const response = await apiClient.post<ApiResponse<CodeExecutionResult>>(
        `/v1/assessment/code/submissions/${data.submissionId}/execute`,
        data
      );
      return response.data.data;
    } catch (error) {
      throw parseApiError(error);
    }
  },

  /**
   * Get execution results
   */
  getExecutionResults: async (submissionId: number): Promise<CodeExecutionResult> => {
    try {
      const response = await apiClient.get<ApiResponse<CodeExecutionResult>>(
        `/v1/assessment/code/submissions/${submissionId}/results`
      );
      return response.data.data;
    } catch (error) {
      throw parseApiError(error);
    }
  },

  /**
   * Check for plagiarism
   */
  checkPlagiarism: async (submissionId: number): Promise<PlagiarismCheckResult> => {
    try {
      const response = await apiClient.post<ApiResponse<PlagiarismCheckResult>>(
        `/v1/assessment/code/submissions/${submissionId}/plagiarism-check`
      );
      return response.data.data;
    } catch (error) {
      throw parseApiError(error);
    }
  },

  /**
   * Get plagiarism results for assignment
   */
  getAssignmentPlagiarismResults: async (assignmentId: number): Promise<PlagiarismCheckResult[]> => {
    try {
      const response = await apiClient.get<ApiResponse<PlagiarismCheckResult[]>>(
        `/v1/assessment/code/assignments/${assignmentId}/plagiarism`
      );
      return response.data.data;
    } catch (error) {
      throw parseApiError(error);
    }
  },
};

/**
 * Feedback APIs
 */
export const feedbackApi = {
  /**
   * Get feedback for a submission
   */
  getSubmissionFeedback: async (submissionId: number): Promise<Feedback[]> => {
    try {
      const response = await apiClient.get<ApiResponse<Feedback[]>>(
        `/v1/assessment/feedback/submissions/${submissionId}`
      );
      return response.data.data;
    } catch (error) {
      throw parseApiError(error);
    }
  },

  /**
   * Get all feedback for a student
   */
  getStudentFeedback: async (studentId: number, courseId: number): Promise<Feedback[]> => {
    try {
      const response = await apiClient.get<ApiResponse<Feedback[]>>(
        `/v1/assessment/feedback/students/${studentId}`,
        { params: { courseId } }
      );
      return response.data.data;
    } catch (error) {
      throw parseApiError(error);
    }
  },

  /**
   * Generate AI feedback for submission
   */
  generateAIFeedback: async (submissionId: number): Promise<Feedback> => {
    try {
      const response = await apiClient.post<ApiResponse<Feedback>>(
        `/v1/assessment/feedback/submissions/${submissionId}/generate`
      );
      return response.data.data;
    } catch (error) {
      throw parseApiError(error);
    }
  },

  /**
   * Create custom feedback
   */
  createFeedback: async (submissionId: number, content: string): Promise<Feedback> => {
    try {
      const response = await apiClient.post<ApiResponse<Feedback>>(
        `/v1/assessment/feedback/submissions/${submissionId}`,
        { content }
      );
      return response.data.data;
    } catch (error) {
      throw parseApiError(error);
    }
  },
};

/**
 * Participation APIs
 */
export const participationApi = {
  /**
   * Get participation dashboard for a student
   */
  getDashboard: async (studentId: number, courseId: number): Promise<ParticipationDashboard> => {
    try {
      const response = await apiClient.get<ApiResponse<ParticipationDashboard>>(
        `/v1/assessment/participation/students/${studentId}/dashboard`,
        { params: { courseId } }
      );
      return response.data.data;
    } catch (error) {
      throw parseApiError(error);
    }
  },

  /**
   * Get all participation scores for a course
   */
  getCourseScores: async (courseId: number): Promise<ParticipationScore[]> => {
    try {
      const response = await apiClient.get<ApiResponse<ParticipationScore[]>>(
        `/v1/assessment/participation/courses/${courseId}/scores`
      );
      return response.data.data;
    } catch (error) {
      throw parseApiError(error);
    }
  },

  /**
   * Get participation weight configuration
   */
  getWeightConfig: async (courseId: number): Promise<ParticipationWeightConfig> => {
    try {
      const response = await apiClient.get<ApiResponse<ParticipationWeightConfig>>(
        `/v1/assessment/participation/courses/${courseId}/weights`
      );
      return response.data.data;
    } catch (error) {
      throw parseApiError(error);
    }
  },

  /**
   * Update participation weights (professor only)
   */
  updateWeights: async (data: UpdateParticipationWeightsRequest): Promise<ParticipationWeightConfig> => {
    try {
      const response = await apiClient.put<ApiResponse<ParticipationWeightConfig>>(
        `/v1/assessment/participation/courses/${data.courseId}/weights`,
        data
      );
      return response.data.data;
    } catch (error) {
      throw parseApiError(error);
    }
  },
};

/**
 * Peer Review APIs
 */
export const peerReviewApi = {
  /**
   * Get peer review assignments for a student
   */
  getAssignments: async (studentId: number, assignmentId: number): Promise<PeerReviewAssignment[]> => {
    try {
      const response = await apiClient.get<ApiResponse<PeerReviewAssignment[]>>(
        `/v1/assessment/peer-review/students/${studentId}/assignments`,
        { params: { assignmentId } }
      );
      return response.data.data;
    } catch (error) {
      throw parseApiError(error);
    }
  },

  /**
   * Get peer review rubric for an assignment
   */
  getRubric: async (assignmentId: number): Promise<PeerReviewRubric> => {
    try {
      const response = await apiClient.get<ApiResponse<PeerReviewRubric>>(
        `/v1/assessment/peer-review/assignments/${assignmentId}/rubric`
      );
      return response.data.data;
    } catch (error) {
      throw parseApiError(error);
    }
  },

  /**
   * Submit peer review
   */
  submitReview: async (data: SubmitPeerReviewRequest): Promise<PeerReviewSubmission> => {
    try {
      const response = await apiClient.post<ApiResponse<PeerReviewSubmission>>(
        `/v1/assessment/peer-review/assignments/${data.assignmentId}/submissions/${data.submissionId}/reviews`,
        data
      );
      return response.data.data;
    } catch (error) {
      throw parseApiError(error);
    }
  },

  /**
   * Get peer review results for a submission
   */
  getResults: async (submissionId: number): Promise<PeerReviewResult> => {
    try {
      const response = await apiClient.get<ApiResponse<PeerReviewResult>>(
        `/v1/assessment/peer-review/submissions/${submissionId}/results`
      );
      return response.data.data;
    } catch (error) {
      throw parseApiError(error);
    }
  },

  /**
   * Get all peer reviews given by a student
   */
  getReviewsGiven: async (studentId: number, assignmentId: number): Promise<PeerReviewAssignment[]> => {
    try {
      const response = await apiClient.get<ApiResponse<PeerReviewAssignment[]>>(
        `/v1/assessment/peer-review/students/${studentId}/reviews-given`,
        { params: { assignmentId } }
      );
      return response.data.data;
    } catch (error) {
      throw parseApiError(error);
    }
  },

  /**
   * Get all peer reviews received by a student
   */
  getReviewsReceived: async (studentId: number, assignmentId: number): Promise<PeerReviewResult> => {
    try {
      const response = await apiClient.get<ApiResponse<PeerReviewResult>>(
        `/v1/assessment/peer-review/students/${studentId}/reviews-received`,
        { params: { assignmentId } }
      );
      return response.data.data;
    } catch (error) {
      throw parseApiError(error);
    }
  },
};

// Export all APIs
export default {
  autoGrading: autoGradingApi,
  aiGrading: aiGradingApi,
  codeEvaluation: codeEvaluationApi,
  feedback: feedbackApi,
  participation: participationApi,
  peerReview: peerReviewApi,
};
