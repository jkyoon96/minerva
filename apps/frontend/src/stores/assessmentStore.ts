/**
 * Assessment & Feedback state management store (Zustand)
 * - Auto Grading
 * - AI Grading
 * - Code Evaluation
 * - Feedback
 * - Participation
 * - Peer Review
 */

import { create } from 'zustand';
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
  AssessmentUIState,
} from '@/types/assessment';

interface AssessmentState {
  // Auto grading state
  autoGradingResults: AutoGradingResult[];
  currentAutoGradingResult: AutoGradingResult | null;
  answerStatistics: AnswerStatistics[];

  // AI grading state
  aiGradingTasks: AIGradingTask[];
  aiGradingResults: AIGradingResult[];
  currentAIGrading: AIGradingResult | null;

  // Code evaluation state
  codeSubmissions: CodeSubmission[];
  currentCodeSubmission: CodeSubmission | null;
  executionResults: CodeExecutionResult[];
  plagiarismResults: PlagiarismCheckResult[];

  // Feedback state
  feedbacks: Feedback[];
  currentFeedback: Feedback | null;

  // Participation state
  participationDashboard: ParticipationDashboard | null;
  participationScores: ParticipationScore[];
  participationWeights: ParticipationWeightConfig | null;

  // Peer review state
  peerReviewAssignments: PeerReviewAssignment[];
  peerReviewResults: PeerReviewResult | null;
  peerReviewRubric: PeerReviewRubric | null;

  // UI state
  uiState: AssessmentUIState;

  // Loading & error states
  isLoading: boolean;
  error: string | null;

  // Actions - Auto Grading
  setAutoGradingResults: (results: AutoGradingResult[]) => void;
  setCurrentAutoGradingResult: (result: AutoGradingResult | null) => void;
  setAnswerStatistics: (statistics: AnswerStatistics[]) => void;

  // Actions - AI Grading
  setAIGradingTasks: (tasks: AIGradingTask[]) => void;
  updateAIGradingTask: (taskId: number, updates: Partial<AIGradingTask>) => void;
  setAIGradingResults: (results: AIGradingResult[]) => void;
  setCurrentAIGrading: (result: AIGradingResult | null) => void;
  updateAIGradingResult: (resultId: number, updates: Partial<AIGradingResult>) => void;

  // Actions - Code Evaluation
  setCodeSubmissions: (submissions: CodeSubmission[]) => void;
  setCurrentCodeSubmission: (submission: CodeSubmission | null) => void;
  setExecutionResults: (results: CodeExecutionResult[]) => void;
  addExecutionResult: (result: CodeExecutionResult) => void;
  setPlagiarismResults: (results: PlagiarismCheckResult[]) => void;

  // Actions - Feedback
  setFeedbacks: (feedbacks: Feedback[]) => void;
  addFeedback: (feedback: Feedback) => void;
  setCurrentFeedback: (feedback: Feedback | null) => void;

  // Actions - Participation
  setParticipationDashboard: (dashboard: ParticipationDashboard | null) => void;
  setParticipationScores: (scores: ParticipationScore[]) => void;
  setParticipationWeights: (weights: ParticipationWeightConfig | null) => void;
  updateParticipationWeight: (eventType: string, weight: number, basePoints: number) => void;

  // Actions - Peer Review
  setPeerReviewAssignments: (assignments: PeerReviewAssignment[]) => void;
  updatePeerReviewAssignment: (assignmentId: number, updates: Partial<PeerReviewAssignment>) => void;
  setPeerReviewResults: (results: PeerReviewResult | null) => void;
  setPeerReviewRubric: (rubric: PeerReviewRubric | null) => void;

  // Actions - UI State
  setUIState: (updates: Partial<AssessmentUIState>) => void;

  // Actions - General
  setLoading: (loading: boolean) => void;
  setError: (error: string | null) => void;
  reset: () => void;
}

const initialState = {
  // Auto grading initial state
  autoGradingResults: [],
  currentAutoGradingResult: null,
  answerStatistics: [],

  // AI grading initial state
  aiGradingTasks: [],
  aiGradingResults: [],
  currentAIGrading: null,

  // Code evaluation initial state
  codeSubmissions: [],
  currentCodeSubmission: null,
  executionResults: [],
  plagiarismResults: [],

  // Feedback initial state
  feedbacks: [],
  currentFeedback: null,

  // Participation initial state
  participationDashboard: null,
  participationScores: [],
  participationWeights: null,

  // Peer review initial state
  peerReviewAssignments: [],
  peerReviewResults: null,
  peerReviewRubric: null,

  // UI state
  uiState: {
    showGradeEditor: false,
    isGrading: false,
    viewMode: 'list' as const,
  },

  // Loading & error
  isLoading: false,
  error: null,
};

export const useAssessmentStore = create<AssessmentState>()((set, get) => ({
  ...initialState,

  // Auto grading actions
  setAutoGradingResults: (results) => set({ autoGradingResults: results }),

  setCurrentAutoGradingResult: (result) => set({ currentAutoGradingResult: result }),

  setAnswerStatistics: (statistics) => set({ answerStatistics: statistics }),

  // AI grading actions
  setAIGradingTasks: (tasks) => set({ aiGradingTasks: tasks }),

  updateAIGradingTask: (taskId, updates) =>
    set((state) => ({
      aiGradingTasks: state.aiGradingTasks.map((task) =>
        task.id === taskId ? { ...task, ...updates } : task
      ),
    })),

  setAIGradingResults: (results) => set({ aiGradingResults: results }),

  setCurrentAIGrading: (result) => set({ currentAIGrading: result }),

  updateAIGradingResult: (resultId, updates) =>
    set((state) => ({
      aiGradingResults: state.aiGradingResults.map((result) =>
        result.id === resultId ? { ...result, ...updates } : result
      ),
      currentAIGrading:
        state.currentAIGrading?.id === resultId
          ? { ...state.currentAIGrading, ...updates }
          : state.currentAIGrading,
    })),

  // Code evaluation actions
  setCodeSubmissions: (submissions) => set({ codeSubmissions: submissions }),

  setCurrentCodeSubmission: (submission) => set({ currentCodeSubmission: submission }),

  setExecutionResults: (results) => set({ executionResults: results }),

  addExecutionResult: (result) =>
    set((state) => ({
      executionResults: [...state.executionResults, result],
    })),

  setPlagiarismResults: (results) => set({ plagiarismResults: results }),

  // Feedback actions
  setFeedbacks: (feedbacks) => set({ feedbacks }),

  addFeedback: (feedback) =>
    set((state) => ({
      feedbacks: [...state.feedbacks, feedback],
    })),

  setCurrentFeedback: (feedback) => set({ currentFeedback: feedback }),

  // Participation actions
  setParticipationDashboard: (dashboard) => set({ participationDashboard: dashboard }),

  setParticipationScores: (scores) => set({ participationScores: scores }),

  setParticipationWeights: (weights) => set({ participationWeights: weights }),

  updateParticipationWeight: (eventType, weight, basePoints) =>
    set((state) => {
      if (!state.participationWeights) return state;

      return {
        participationWeights: {
          ...state.participationWeights,
          weights: state.participationWeights.weights.map((w) =>
            w.eventType === eventType ? { ...w, weight, basePoints } : w
          ),
        },
      };
    }),

  // Peer review actions
  setPeerReviewAssignments: (assignments) => set({ peerReviewAssignments: assignments }),

  updatePeerReviewAssignment: (assignmentId, updates) =>
    set((state) => ({
      peerReviewAssignments: state.peerReviewAssignments.map((assignment) =>
        assignment.id === assignmentId ? { ...assignment, ...updates } : assignment
      ),
    })),

  setPeerReviewResults: (results) => set({ peerReviewResults: results }),

  setPeerReviewRubric: (rubric) => set({ peerReviewRubric: rubric }),

  // UI state actions
  setUIState: (updates) =>
    set((state) => ({
      uiState: { ...state.uiState, ...updates },
    })),

  // General actions
  setLoading: (loading) => set({ isLoading: loading }),

  setError: (error) => set({ error }),

  reset: () => set(initialState),
}));

// Selectors
export const assessmentSelectors = {
  // Get pending AI grading tasks
  getPendingAITasks: (state: AssessmentState) =>
    state.aiGradingTasks.filter((task) => task.needsReview),

  // Get submissions with plagiarism issues
  getFlaggedSubmissions: (state: AssessmentState) =>
    state.plagiarismResults.filter((result) => result.flagged),

  // Get pending peer review assignments
  getPendingPeerReviews: (state: AssessmentState) =>
    state.peerReviewAssignments.filter((assignment) => assignment.status === 'PENDING'),

  // Get completed peer reviews
  getCompletedPeerReviews: (state: AssessmentState) =>
    state.peerReviewAssignments.filter((assignment) => assignment.status === 'COMPLETED'),

  // Calculate participation percentage
  getParticipationPercentage: (state: AssessmentState) => {
    if (!state.participationDashboard) return 0;
    return state.participationDashboard.percentage;
  },

  // Get recent feedbacks
  getRecentFeedbacks: (state: AssessmentState, limit: number = 5) =>
    state.feedbacks.slice(0, limit),
};
