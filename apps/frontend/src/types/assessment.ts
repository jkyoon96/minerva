/**
 * Assessment & Feedback type definitions
 * - Auto Grading
 * - AI Grading
 * - Code Evaluation
 * - Feedback
 * - Participation
 * - Peer Review
 */

// ==================== Common Types ====================

export enum GradingStatus {
  PENDING = 'PENDING',
  GRADING = 'GRADING',
  GRADED = 'GRADED',
  REVIEWED = 'REVIEWED',
  PUBLISHED = 'PUBLISHED',
}

export enum SubmissionStatus {
  NOT_SUBMITTED = 'NOT_SUBMITTED',
  SUBMITTED = 'SUBMITTED',
  LATE = 'LATE',
  GRADED = 'GRADED',
}

// ==================== Auto Grading Types ====================

export enum QuestionType {
  MULTIPLE_CHOICE = 'MULTIPLE_CHOICE',
  TRUE_FALSE = 'TRUE_FALSE',
  FILL_BLANK = 'FILL_BLANK',
  MATCHING = 'MATCHING',
}

export interface AutoGradedQuestion {
  id: number;
  questionText: string;
  questionType: QuestionType;
  points: number;
  options?: string[];
  correctAnswer: string | string[];
  explanation?: string;
}

export interface AutoGradedSubmission {
  id: number;
  assignmentId: number;
  studentId: number;
  studentName: string;
  answers: {
    questionId: number;
    answer: string | string[];
  }[];
  score: number;
  totalPoints: number;
  percentage: number;
  submittedAt: string;
  gradedAt: string;
}

export interface AutoGradingResult {
  submission: AutoGradedSubmission;
  questionResults: {
    questionId: number;
    questionText: string;
    isCorrect: boolean;
    points: number;
    earnedPoints: number;
    correctAnswer: string | string[];
    studentAnswer: string | string[];
    explanation?: string;
  }[];
  statistics: {
    totalQuestions: number;
    correctAnswers: number;
    incorrectAnswers: number;
    averageScore: number;
  };
}

export interface AnswerStatistics {
  questionId: number;
  questionText: string;
  totalResponses: number;
  correctCount: number;
  incorrectCount: number;
  correctPercentage: number;
  options?: {
    optionText: string;
    count: number;
    percentage: number;
    isCorrect: boolean;
  }[];
}

// ==================== AI Grading Types ====================

export enum AIConfidenceLevel {
  HIGH = 'HIGH',
  MEDIUM = 'MEDIUM',
  LOW = 'LOW',
}

export interface AIGradingResult {
  id: number;
  submissionId: number;
  score: number;
  maxScore: number;
  confidence: AIConfidenceLevel;
  feedback: string;
  rubricScores: {
    criteriaId: number;
    criteriaName: string;
    score: number;
    maxScore: number;
    feedback: string;
  }[];
  suggestedImprovements: string[];
  gradedAt: string;
  reviewedBy?: number;
  reviewedByName?: string;
  reviewedAt?: string;
  finalScore?: number;
}

export interface AIGradingTask {
  id: number;
  assignmentId: number;
  assignmentTitle: string;
  submissionId: number;
  studentId: number;
  studentName: string;
  status: GradingStatus;
  aiScore?: number;
  aiConfidence?: AIConfidenceLevel;
  needsReview: boolean;
  createdAt: string;
}

export interface GradeModification {
  submissionId: number;
  originalScore: number;
  modifiedScore: number;
  reason: string;
  modifiedBy: number;
  modifiedAt: string;
}

// ==================== Code Evaluation Types ====================

export enum ProgrammingLanguage {
  PYTHON = 'PYTHON',
  JAVA = 'JAVA',
  JAVASCRIPT = 'JAVASCRIPT',
  CPP = 'CPP',
  C = 'C',
  GO = 'GO',
}

export enum TestCaseStatus {
  PASSED = 'PASSED',
  FAILED = 'FAILED',
  ERROR = 'ERROR',
  TIMEOUT = 'TIMEOUT',
}

export interface TestCase {
  id: number;
  name: string;
  input: string;
  expectedOutput: string;
  isHidden: boolean;
  points: number;
  timeoutSeconds: number;
}

export interface TestCaseResult {
  testCaseId: number;
  testCaseName: string;
  status: TestCaseStatus;
  actualOutput?: string;
  expectedOutput: string;
  executionTime: number;
  memoryUsed: number;
  errorMessage?: string;
  points: number;
  earnedPoints: number;
}

export interface CodeSubmission {
  id: number;
  assignmentId: number;
  studentId: number;
  studentName: string;
  code: string;
  language: ProgrammingLanguage;
  submittedAt: string;
  executionResults?: CodeExecutionResult;
}

export interface CodeExecutionResult {
  submissionId: number;
  status: 'SUCCESS' | 'COMPILATION_ERROR' | 'RUNTIME_ERROR' | 'TIMEOUT';
  testResults: TestCaseResult[];
  totalTests: number;
  passedTests: number;
  failedTests: number;
  score: number;
  totalPoints: number;
  compilationError?: string;
  executedAt: string;
}

export interface PlagiarismCheckResult {
  submissionId: number;
  similarityScore: number;
  matchedSubmissions: {
    submissionId: number;
    studentName: string;
    similarityPercentage: number;
    matchedLines: number;
  }[];
  checkedAt: string;
  flagged: boolean;
}

// ==================== Feedback Types ====================

export enum FeedbackType {
  AI_GENERATED = 'AI_GENERATED',
  INSTRUCTOR = 'INSTRUCTOR',
  PEER = 'PEER',
}

export interface Feedback {
  id: number;
  submissionId: number;
  studentId: number;
  type: FeedbackType;
  content: string;
  strengths: string[];
  improvements: string[];
  resources: LearningResource[];
  createdBy?: number;
  createdByName?: string;
  createdAt: string;
}

export interface LearningResource {
  id: number;
  title: string;
  description: string;
  url: string;
  type: 'VIDEO' | 'ARTICLE' | 'TUTORIAL' | 'EXERCISE' | 'DOCUMENTATION';
  relevanceScore: number;
}

export interface ImprovementSuggestion {
  id: number;
  category: string;
  priority: 'HIGH' | 'MEDIUM' | 'LOW';
  suggestion: string;
  examples?: string[];
  relatedConcepts: string[];
}

// ==================== Participation Types ====================

export enum ParticipationEventType {
  POLL_RESPONSE = 'POLL_RESPONSE',
  QUIZ_SUBMISSION = 'QUIZ_SUBMISSION',
  DISCUSSION_POST = 'DISCUSSION_POST',
  QUESTION_ASKED = 'QUESTION_ASKED',
  SPEAKING_TURN = 'SPEAKING_TURN',
  BREAKOUT_PARTICIPATION = 'BREAKOUT_PARTICIPATION',
  ASSIGNMENT_SUBMISSION = 'ASSIGNMENT_SUBMISSION',
}

export interface ParticipationEvent {
  id: number;
  sessionId: number;
  studentId: number;
  eventType: ParticipationEventType;
  eventId: number;
  points: number;
  timestamp: string;
}

export interface ParticipationScore {
  studentId: number;
  studentName: string;
  totalScore: number;
  eventBreakdown: {
    eventType: ParticipationEventType;
    count: number;
    points: number;
    percentage: number;
  }[];
  sessionsAttended: number;
  totalSessions: number;
  attendanceRate: number;
  lastActivity: string;
}

export interface ParticipationWeightConfig {
  id: number;
  courseId: number;
  weights: {
    eventType: ParticipationEventType;
    weight: number;
    basePoints: number;
  }[];
  totalWeight: number;
  updatedAt: string;
}

export interface ParticipationDashboard {
  student: {
    id: number;
    name: string;
  };
  currentScore: number;
  maxScore: number;
  percentage: number;
  rank: number;
  totalStudents: number;
  recentEvents: ParticipationEvent[];
  eventSummary: {
    eventType: ParticipationEventType;
    count: number;
    points: number;
  }[];
  trend: {
    date: string;
    score: number;
  }[];
}

// ==================== Peer Review Types ====================

export enum PeerReviewStatus {
  PENDING = 'PENDING',
  IN_PROGRESS = 'IN_PROGRESS',
  COMPLETED = 'COMPLETED',
  OVERDUE = 'OVERDUE',
}

export interface PeerReviewRubric {
  id: number;
  assignmentId: number;
  criteria: {
    id: number;
    name: string;
    description: string;
    maxScore: number;
    scoreDescriptions: {
      score: number;
      description: string;
    }[];
  }[];
}

export interface PeerReviewAssignment {
  id: number;
  assignmentId: number;
  reviewerId: number;
  reviewerName: string;
  submissionId: number;
  authorId: number;
  authorName: string;
  status: PeerReviewStatus;
  isAnonymous: boolean;
  dueDate: string;
  completedAt?: string;
}

export interface PeerReviewSubmission {
  id: number;
  assignmentId: number;
  reviewerId: number;
  submissionId: number;
  rubricScores: {
    criteriaId: number;
    score: number;
    comment?: string;
  }[];
  overallComment: string;
  totalScore: number;
  submittedAt: string;
}

export interface PeerReviewResult {
  submissionId: number;
  studentId: number;
  studentName: string;
  reviewsReceived: {
    reviewId: number;
    reviewerId: number;
    reviewerName?: string;
    isAnonymous: boolean;
    rubricScores: {
      criteriaId: number;
      criteriaName: string;
      score: number;
      maxScore: number;
      comment?: string;
    }[];
    overallComment: string;
    totalScore: number;
    submittedAt: string;
  }[];
  reviewsGiven: {
    reviewId: number;
    submissionId: number;
    authorId: number;
    authorName?: string;
    status: PeerReviewStatus;
    submittedAt?: string;
  }[];
  aggregatedScore: {
    averageScore: number;
    medianScore: number;
    criteriaAverages: {
      criteriaId: number;
      criteriaName: string;
      average: number;
      maxScore: number;
    }[];
  };
  outliers: number[];
}

// ==================== API Request/Response Types ====================

export interface GradeAssignmentRequest {
  assignmentId: number;
  submissionId: number;
  score: number;
  feedback?: string;
}

export interface ModifyGradeRequest {
  submissionId: number;
  newScore: number;
  reason: string;
}

export interface ExecuteCodeRequest {
  submissionId: number;
  code: string;
  language: ProgrammingLanguage;
}

export interface SubmitPeerReviewRequest {
  assignmentId: number;
  submissionId: number;
  rubricScores: {
    criteriaId: number;
    score: number;
    comment?: string;
  }[];
  overallComment: string;
}

export interface UpdateParticipationWeightsRequest {
  courseId: number;
  weights: {
    eventType: ParticipationEventType;
    weight: number;
    basePoints: number;
  }[];
}

// ==================== UI State Types ====================

export interface AssessmentUIState {
  selectedAssignment?: number;
  selectedSubmission?: number;
  showGradeEditor: boolean;
  isGrading: boolean;
  viewMode: 'list' | 'detail';
}
