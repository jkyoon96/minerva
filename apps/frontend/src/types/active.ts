/**
 * Active Learning type definitions
 * - Polls/Voting
 * - Quizzes
 * - Breakout Rooms
 * - Whiteboard
 * - Discussion/Speaking Queue
 */

// ==================== Poll/Vote Types ====================

export enum PollType {
  MULTIPLE_CHOICE = 'MULTIPLE_CHOICE',
  RATING = 'RATING',
  WORD_CLOUD = 'WORD_CLOUD',
  OPEN_ENDED = 'OPEN_ENDED',
  YES_NO = 'YES_NO',
}

export enum PollStatus {
  DRAFT = 'DRAFT',
  ACTIVE = 'ACTIVE',
  ENDED = 'ENDED',
  ARCHIVED = 'ARCHIVED',
}

export interface PollOption {
  id: number;
  pollId: number;
  optionText: string;
  position: number;
  votesCount: number;
  createdAt: string;
}

export interface Poll {
  id: number;
  courseId: number;
  sessionId?: number;
  createdById: number;
  createdByName: string;
  question: string;
  description?: string;
  pollType: PollType;
  status: PollStatus;
  allowMultiple: boolean;
  allowAnonymous: boolean;
  showResults: boolean;
  options: PollOption[];
  totalResponses: number;
  startedAt?: string;
  endedAt?: string;
  createdAt: string;
  updatedAt: string;
}

export interface PollResponse {
  id: number;
  pollId: number;
  userId: number;
  userName: string;
  selectedOptionIds?: number[];
  textResponse?: string;
  ratingValue?: number;
  isAnonymous: boolean;
  createdAt: string;
}

export interface PollResult {
  poll: Poll;
  responses: PollResponse[];
  optionStats: {
    optionId: number;
    optionText: string;
    count: number;
    percentage: number;
  }[];
  wordCloudData?: { text: string; value: number }[];
  ratingAverage?: number;
}

export interface PollTemplate {
  id: number;
  name: string;
  description?: string;
  pollType: PollType;
  question: string;
  options: string[];
  tags: string[];
}

// ==================== Quiz Types ====================

export enum QuestionType {
  MULTIPLE_CHOICE = 'MULTIPLE_CHOICE',
  TRUE_FALSE = 'TRUE_FALSE',
  SHORT_ANSWER = 'SHORT_ANSWER',
  ESSAY = 'ESSAY',
  MATCHING = 'MATCHING',
  FILL_BLANK = 'FILL_BLANK',
}

export enum QuestionDifficulty {
  EASY = 'EASY',
  MEDIUM = 'MEDIUM',
  HARD = 'HARD',
}

export interface QuestionOption {
  id: number;
  questionId: number;
  optionText: string;
  isCorrect: boolean;
  position: number;
  explanation?: string;
}

export interface Question {
  id: number;
  courseId: number;
  createdById: number;
  createdByName: string;
  questionType: QuestionType;
  questionText: string;
  description?: string;
  points: number;
  difficulty: QuestionDifficulty;
  options: QuestionOption[];
  correctAnswer?: string;
  explanation?: string;
  tags: string[];
  timeLimitSeconds?: number;
  createdAt: string;
  updatedAt: string;
}

export enum QuizStatus {
  DRAFT = 'DRAFT',
  PUBLISHED = 'PUBLISHED',
  ACTIVE = 'ACTIVE',
  ENDED = 'ENDED',
  GRADED = 'GRADED',
}

export interface Quiz {
  id: number;
  courseId: number;
  sessionId?: number;
  createdById: number;
  createdByName: string;
  title: string;
  description?: string;
  status: QuizStatus;
  totalPoints: number;
  passingScore: number;
  timeLimitMinutes?: number;
  allowRetake: boolean;
  shuffleQuestions: boolean;
  showCorrectAnswers: boolean;
  showScoreImmediately: boolean;
  questions: Question[];
  startedAt?: string;
  endedAt?: string;
  createdAt: string;
  updatedAt: string;
}

export interface QuizAnswer {
  questionId: number;
  selectedOptionIds?: number[];
  textAnswer?: string;
}

export interface QuizSubmission {
  id: number;
  quizId: number;
  userId: number;
  userName: string;
  answers: QuizAnswer[];
  score: number;
  totalPoints: number;
  percentage: number;
  timeSpentSeconds: number;
  submittedAt: string;
  gradedAt?: string;
}

export interface QuizResult {
  submission: QuizSubmission;
  questionResults: {
    questionId: number;
    questionText: string;
    questionType: QuestionType;
    points: number;
    earnedPoints: number;
    isCorrect: boolean;
    userAnswer: QuizAnswer;
    correctAnswer?: string;
    explanation?: string;
  }[];
}

// ==================== Breakout Room Types ====================

export enum BreakoutRoomStatus {
  SETUP = 'SETUP',
  ACTIVE = 'ACTIVE',
  ENDED = 'ENDED',
}

export enum AssignmentMethod {
  MANUAL = 'MANUAL',
  RANDOM = 'RANDOM',
  BALANCED = 'BALANCED',
}

export interface BreakoutRoom {
  id: number;
  sessionId: number;
  roomNumber: number;
  name: string;
  status: BreakoutRoomStatus;
  capacity: number;
  currentParticipants: number;
  participants: number[];
  topic?: string;
  meetingUrl?: string;
  createdAt: string;
  updatedAt: string;
}

export interface BreakoutSession {
  id: number;
  sessionId: number;
  hostId: number;
  hostName: string;
  status: BreakoutRoomStatus;
  totalRooms: number;
  assignmentMethod: AssignmentMethod;
  durationMinutes: number;
  rooms: BreakoutRoom[];
  unassignedParticipants: number[];
  startedAt?: string;
  endedAt?: string;
  createdAt: string;
  updatedAt: string;
}

export interface BroadcastMessage {
  id: number;
  sessionId: number;
  senderId: number;
  senderName: string;
  message: string;
  targetRooms?: number[];
  sentAt: string;
}

// ==================== Whiteboard Types ====================

export enum DrawingTool {
  PEN = 'PEN',
  HIGHLIGHTER = 'HIGHLIGHTER',
  ERASER = 'ERASER',
  TEXT = 'TEXT',
  RECTANGLE = 'RECTANGLE',
  CIRCLE = 'CIRCLE',
  LINE = 'LINE',
  ARROW = 'ARROW',
  SELECT = 'SELECT',
}

export interface Point {
  x: number;
  y: number;
}

export interface DrawingStyle {
  color: string;
  width: number;
  opacity: number;
}

export interface DrawingElement {
  id: string;
  tool: DrawingTool;
  points: Point[];
  style: DrawingStyle;
  text?: string;
  userId: number;
  userName: string;
  timestamp: number;
}

export interface WhiteboardState {
  id: number;
  sessionId: number;
  elements: DrawingElement[];
  backgroundUrl?: string;
  width: number;
  height: number;
  updatedAt: string;
}

export interface CursorPosition {
  userId: number;
  userName: string;
  x: number;
  y: number;
  color: string;
  timestamp: number;
}

// ==================== Discussion/Speaking Queue Types ====================

export enum SpeakingStatus {
  WAITING = 'WAITING',
  SPEAKING = 'SPEAKING',
  COMPLETED = 'COMPLETED',
  CANCELLED = 'CANCELLED',
}

export interface SpeakingQueueEntry {
  id: number;
  sessionId: number;
  userId: number;
  userName: string;
  status: SpeakingStatus;
  position: number;
  topic?: string;
  requestedAt: string;
  startedAt?: string;
  endedAt?: string;
  durationSeconds?: number;
}

export interface DiscussionThread {
  id: number;
  sessionId: number;
  parentId?: number;
  authorId: number;
  authorName: string;
  content: string;
  attachments?: string[];
  likes: number;
  replies: DiscussionThread[];
  createdAt: string;
  updatedAt: string;
}

export interface ParticipationStat {
  userId: number;
  userName: string;
  speakingCount: number;
  totalSpeakingTime: number;
  questionsAsked: number;
  repliesGiven: number;
  pollsAnswered: number;
  quizzesTaken: number;
  participationScore: number;
}

// ==================== WebSocket Message Types ====================

export interface ActiveWebSocketMessage<T = any> {
  type: string;
  payload: T;
  timestamp: string;
}

export interface PollUpdatedPayload {
  poll: Poll;
}

export interface PollResponsePayload {
  response: PollResponse;
}

export interface QuizStartedPayload {
  quiz: Quiz;
}

export interface QuizSubmittedPayload {
  submission: QuizSubmission;
}

export interface BreakoutRoomUpdatedPayload {
  room: BreakoutRoom;
}

export interface BroadcastMessagePayload {
  message: BroadcastMessage;
}

export interface WhiteboardUpdatedPayload {
  element: DrawingElement;
}

export interface CursorMovedPayload {
  cursor: CursorPosition;
}

export interface SpeakingQueueUpdatedPayload {
  entry: SpeakingQueueEntry;
}

// ==================== API Request/Response Types ====================

export interface PollCreateRequest {
  courseId: number;
  sessionId?: number;
  question: string;
  description?: string;
  pollType: PollType;
  options?: string[];
  allowMultiple?: boolean;
  allowAnonymous?: boolean;
  showResults?: boolean;
}

export interface PollResponseRequest {
  selectedOptionIds?: number[];
  textResponse?: string;
  ratingValue?: number;
  isAnonymous?: boolean;
}

export interface QuestionCreateRequest {
  courseId: number;
  questionType: QuestionType;
  questionText: string;
  description?: string;
  points: number;
  difficulty: QuestionDifficulty;
  options?: { optionText: string; isCorrect: boolean; explanation?: string }[];
  correctAnswer?: string;
  explanation?: string;
  tags?: string[];
  timeLimitSeconds?: number;
}

export interface QuizCreateRequest {
  courseId: number;
  sessionId?: number;
  title: string;
  description?: string;
  questionIds: number[];
  totalPoints?: number;
  passingScore?: number;
  timeLimitMinutes?: number;
  allowRetake?: boolean;
  shuffleQuestions?: boolean;
  showCorrectAnswers?: boolean;
  showScoreImmediately?: boolean;
}

export interface QuizSubmitRequest {
  answers: QuizAnswer[];
  timeSpentSeconds: number;
}

export interface BreakoutSessionCreateRequest {
  sessionId: number;
  totalRooms: number;
  assignmentMethod: AssignmentMethod;
  durationMinutes: number;
  roomNames?: string[];
  roomCapacities?: number[];
}

export interface BreakoutRoomAssignRequest {
  roomId: number;
  participantIds: number[];
}

export interface BroadcastMessageRequest {
  message: string;
  targetRooms?: number[];
}

export interface WhiteboardElementRequest {
  tool: DrawingTool;
  points: Point[];
  style: DrawingStyle;
  text?: string;
}

export interface SpeakingQueueJoinRequest {
  topic?: string;
}

export interface DiscussionThreadRequest {
  parentId?: number;
  content: string;
  attachments?: string[];
}

// ==================== UI State Types ====================

export interface PollUIState {
  selectedTemplate?: PollTemplate;
  isCreating: boolean;
  isVoting: boolean;
  showResults: boolean;
}

export interface QuizUIState {
  currentQuestionIndex: number;
  timeRemaining?: number;
  isSubmitting: boolean;
  showReview: boolean;
  selectedAnswers: Map<number, QuizAnswer>;
}

export interface BreakoutUIState {
  selectedRoom?: number;
  draggedParticipant?: number;
  showBroadcastModal: boolean;
  isMonitoring: boolean;
}

export interface WhiteboardUIState {
  currentTool: DrawingTool;
  currentStyle: DrawingStyle;
  isDrawing: boolean;
  selectedElementId?: string;
  showToolbar: boolean;
}
