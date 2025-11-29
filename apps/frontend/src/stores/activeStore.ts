/**
 * Active Learning state management store (Zustand)
 * - Polls
 * - Quizzes
 * - Breakout Rooms
 * - Whiteboard
 * - Discussion/Speaking Queue
 */

import { create } from 'zustand';
import {
  Poll,
  PollResponse,
  Quiz,
  QuizSubmission,
  Question,
  BreakoutSession,
  BreakoutRoom,
  WhiteboardState,
  DrawingElement,
  CursorPosition,
  SpeakingQueueEntry,
  DiscussionThread,
  ParticipationStat,
  PollUIState,
  QuizUIState,
  BreakoutUIState,
  WhiteboardUIState,
  DrawingTool,
  QuizAnswer,
} from '@/types/active';

interface ActiveState {
  // Poll state
  polls: Poll[];
  currentPoll: Poll | null;
  pollResponses: PollResponse[];
  pollUIState: PollUIState;

  // Quiz state
  quizzes: Quiz[];
  currentQuiz: Quiz | null;
  quizSubmissions: QuizSubmission[];
  questions: Question[];
  quizUIState: QuizUIState;

  // Breakout room state
  breakoutSession: BreakoutSession | null;
  currentBreakoutRoom: BreakoutRoom | null;
  breakoutUIState: BreakoutUIState;

  // Whiteboard state
  whiteboard: WhiteboardState | null;
  cursors: Map<number, CursorPosition>;
  whiteboardUIState: WhiteboardUIState;
  undoStack: DrawingElement[][];
  redoStack: DrawingElement[][];

  // Discussion state
  speakingQueue: SpeakingQueueEntry[];
  discussionThreads: DiscussionThread[];
  participationStats: ParticipationStat[];

  // WebSocket state
  wsConnected: boolean;
  error: string | null;

  // Actions - Polls
  setPolls: (polls: Poll[]) => void;
  addPoll: (poll: Poll) => void;
  updatePoll: (pollId: number, updates: Partial<Poll>) => void;
  setCurrentPoll: (poll: Poll | null) => void;
  setPollResponses: (responses: PollResponse[]) => void;
  addPollResponse: (response: PollResponse) => void;
  setPollUIState: (updates: Partial<PollUIState>) => void;

  // Actions - Quizzes
  setQuizzes: (quizzes: Quiz[]) => void;
  addQuiz: (quiz: Quiz) => void;
  updateQuiz: (quizId: number, updates: Partial<Quiz>) => void;
  setCurrentQuiz: (quiz: Quiz | null) => void;
  setQuestions: (questions: Question[]) => void;
  addQuestion: (question: Question) => void;
  setQuizSubmissions: (submissions: QuizSubmission[]) => void;
  addQuizSubmission: (submission: QuizSubmission) => void;
  setQuizUIState: (updates: Partial<QuizUIState>) => void;
  setSelectedAnswer: (questionId: number, answer: QuizAnswer) => void;
  nextQuestion: () => void;
  previousQuestion: () => void;
  decrementTimer: () => void;

  // Actions - Breakout Rooms
  setBreakoutSession: (session: BreakoutSession | null) => void;
  updateBreakoutSession: (updates: Partial<BreakoutSession>) => void;
  updateBreakoutRoom: (roomId: number, updates: Partial<BreakoutRoom>) => void;
  setCurrentBreakoutRoom: (room: BreakoutRoom | null) => void;
  setBreakoutUIState: (updates: Partial<BreakoutUIState>) => void;

  // Actions - Whiteboard
  setWhiteboard: (whiteboard: WhiteboardState | null) => void;
  addDrawingElement: (element: DrawingElement) => void;
  updateDrawingElement: (elementId: string, updates: Partial<DrawingElement>) => void;
  removeDrawingElement: (elementId: string) => void;
  clearWhiteboard: () => void;
  undo: () => void;
  redo: () => void;
  setCursors: (cursors: Map<number, CursorPosition>) => void;
  updateCursor: (userId: number, cursor: CursorPosition) => void;
  removeCursor: (userId: number) => void;
  setWhiteboardUIState: (updates: Partial<WhiteboardUIState>) => void;

  // Actions - Discussion
  setSpeakingQueue: (queue: SpeakingQueueEntry[]) => void;
  addToSpeakingQueue: (entry: SpeakingQueueEntry) => void;
  updateSpeakingQueueEntry: (entryId: number, updates: Partial<SpeakingQueueEntry>) => void;
  removeFromSpeakingQueue: (entryId: number) => void;
  setDiscussionThreads: (threads: DiscussionThread[]) => void;
  addDiscussionThread: (thread: DiscussionThread) => void;
  updateDiscussionThread: (threadId: number, updates: Partial<DiscussionThread>) => void;
  setParticipationStats: (stats: ParticipationStat[]) => void;

  // Actions - Connection
  setWSConnected: (connected: boolean) => void;
  setError: (error: string | null) => void;

  // Actions - Reset
  reset: () => void;
}

const initialState = {
  // Poll initial state
  polls: [],
  currentPoll: null,
  pollResponses: [],
  pollUIState: {
    isCreating: false,
    isVoting: false,
    showResults: false,
  },

  // Quiz initial state
  quizzes: [],
  currentQuiz: null,
  quizSubmissions: [],
  questions: [],
  quizUIState: {
    currentQuestionIndex: 0,
    isSubmitting: false,
    showReview: false,
    selectedAnswers: new Map(),
  },

  // Breakout room initial state
  breakoutSession: null,
  currentBreakoutRoom: null,
  breakoutUIState: {
    showBroadcastModal: false,
    isMonitoring: false,
  },

  // Whiteboard initial state
  whiteboard: null,
  cursors: new Map(),
  whiteboardUIState: {
    currentTool: DrawingTool.PEN,
    currentStyle: {
      color: '#000000',
      width: 2,
      opacity: 1,
    },
    isDrawing: false,
    showToolbar: true,
  },
  undoStack: [],
  redoStack: [],

  // Discussion initial state
  speakingQueue: [],
  discussionThreads: [],
  participationStats: [],

  // Connection state
  wsConnected: false,
  error: null,
};

export const useActiveStore = create<ActiveState>()((set, get) => ({
  ...initialState,

  // Poll actions
  setPolls: (polls) => set({ polls }),

  addPoll: (poll) =>
    set((state) => ({
      polls: [...state.polls, poll],
    })),

  updatePoll: (pollId, updates) =>
    set((state) => ({
      polls: state.polls.map((p) => (p.id === pollId ? { ...p, ...updates } : p)),
      currentPoll: state.currentPoll?.id === pollId ? { ...state.currentPoll, ...updates } : state.currentPoll,
    })),

  setCurrentPoll: (poll) => set({ currentPoll: poll }),

  setPollResponses: (responses) => set({ pollResponses: responses }),

  addPollResponse: (response) =>
    set((state) => ({
      pollResponses: [...state.pollResponses, response],
    })),

  setPollUIState: (updates) =>
    set((state) => ({
      pollUIState: { ...state.pollUIState, ...updates },
    })),

  // Quiz actions
  setQuizzes: (quizzes) => set({ quizzes }),

  addQuiz: (quiz) =>
    set((state) => ({
      quizzes: [...state.quizzes, quiz],
    })),

  updateQuiz: (quizId, updates) =>
    set((state) => ({
      quizzes: state.quizzes.map((q) => (q.id === quizId ? { ...q, ...updates } : q)),
      currentQuiz: state.currentQuiz?.id === quizId ? { ...state.currentQuiz, ...updates } : state.currentQuiz,
    })),

  setCurrentQuiz: (quiz) =>
    set({
      currentQuiz: quiz,
      quizUIState: {
        currentQuestionIndex: 0,
        timeRemaining: quiz?.timeLimitMinutes ? quiz.timeLimitMinutes * 60 : undefined,
        isSubmitting: false,
        showReview: false,
        selectedAnswers: new Map(),
      },
    }),

  setQuestions: (questions) => set({ questions }),

  addQuestion: (question) =>
    set((state) => ({
      questions: [...state.questions, question],
    })),

  setQuizSubmissions: (submissions) => set({ quizSubmissions: submissions }),

  addQuizSubmission: (submission) =>
    set((state) => ({
      quizSubmissions: [...state.quizSubmissions, submission],
    })),

  setQuizUIState: (updates) =>
    set((state) => ({
      quizUIState: { ...state.quizUIState, ...updates },
    })),

  setSelectedAnswer: (questionId, answer) =>
    set((state) => {
      const newAnswers = new Map(state.quizUIState.selectedAnswers);
      newAnswers.set(questionId, answer);
      return {
        quizUIState: {
          ...state.quizUIState,
          selectedAnswers: newAnswers,
        },
      };
    }),

  nextQuestion: () =>
    set((state) => {
      const maxIndex = (state.currentQuiz?.questions.length || 1) - 1;
      const nextIndex = Math.min(state.quizUIState.currentQuestionIndex + 1, maxIndex);
      return {
        quizUIState: {
          ...state.quizUIState,
          currentQuestionIndex: nextIndex,
        },
      };
    }),

  previousQuestion: () =>
    set((state) => ({
      quizUIState: {
        ...state.quizUIState,
        currentQuestionIndex: Math.max(state.quizUIState.currentQuestionIndex - 1, 0),
      },
    })),

  decrementTimer: () =>
    set((state) => {
      const timeRemaining = state.quizUIState.timeRemaining;
      if (timeRemaining === undefined || timeRemaining <= 0) return state;

      return {
        quizUIState: {
          ...state.quizUIState,
          timeRemaining: timeRemaining - 1,
        },
      };
    }),

  // Breakout room actions
  setBreakoutSession: (session) => set({ breakoutSession: session }),

  updateBreakoutSession: (updates) =>
    set((state) => ({
      breakoutSession: state.breakoutSession ? { ...state.breakoutSession, ...updates } : null,
    })),

  updateBreakoutRoom: (roomId, updates) =>
    set((state) => ({
      breakoutSession: state.breakoutSession
        ? {
            ...state.breakoutSession,
            rooms: state.breakoutSession.rooms.map((r) => (r.id === roomId ? { ...r, ...updates } : r)),
          }
        : null,
      currentBreakoutRoom:
        state.currentBreakoutRoom?.id === roomId ? { ...state.currentBreakoutRoom, ...updates } : state.currentBreakoutRoom,
    })),

  setCurrentBreakoutRoom: (room) => set({ currentBreakoutRoom: room }),

  setBreakoutUIState: (updates) =>
    set((state) => ({
      breakoutUIState: { ...state.breakoutUIState, ...updates },
    })),

  // Whiteboard actions
  setWhiteboard: (whiteboard) =>
    set({
      whiteboard,
      undoStack: [],
      redoStack: [],
    }),

  addDrawingElement: (element) =>
    set((state) => {
      if (!state.whiteboard) return state;

      const newElements = [...state.whiteboard.elements, element];
      const newUndoStack = [...state.undoStack, state.whiteboard.elements];

      return {
        whiteboard: {
          ...state.whiteboard,
          elements: newElements,
        },
        undoStack: newUndoStack,
        redoStack: [], // Clear redo stack when new action is performed
      };
    }),

  updateDrawingElement: (elementId, updates) =>
    set((state) => {
      if (!state.whiteboard) return state;

      return {
        whiteboard: {
          ...state.whiteboard,
          elements: state.whiteboard.elements.map((el) => (el.id === elementId ? { ...el, ...updates } : el)),
        },
      };
    }),

  removeDrawingElement: (elementId) =>
    set((state) => {
      if (!state.whiteboard) return state;

      const newElements = state.whiteboard.elements.filter((el) => el.id !== elementId);
      const newUndoStack = [...state.undoStack, state.whiteboard.elements];

      return {
        whiteboard: {
          ...state.whiteboard,
          elements: newElements,
        },
        undoStack: newUndoStack,
        redoStack: [],
      };
    }),

  clearWhiteboard: () =>
    set((state) => {
      if (!state.whiteboard) return state;

      const newUndoStack = [...state.undoStack, state.whiteboard.elements];

      return {
        whiteboard: {
          ...state.whiteboard,
          elements: [],
        },
        undoStack: newUndoStack,
        redoStack: [],
      };
    }),

  undo: () =>
    set((state) => {
      if (!state.whiteboard || state.undoStack.length === 0) return state;

      const previousElements = state.undoStack[state.undoStack.length - 1];
      const newUndoStack = state.undoStack.slice(0, -1);
      const newRedoStack = [...state.redoStack, state.whiteboard.elements];

      return {
        whiteboard: {
          ...state.whiteboard,
          elements: previousElements,
        },
        undoStack: newUndoStack,
        redoStack: newRedoStack,
      };
    }),

  redo: () =>
    set((state) => {
      if (!state.whiteboard || state.redoStack.length === 0) return state;

      const nextElements = state.redoStack[state.redoStack.length - 1];
      const newRedoStack = state.redoStack.slice(0, -1);
      const newUndoStack = [...state.undoStack, state.whiteboard.elements];

      return {
        whiteboard: {
          ...state.whiteboard,
          elements: nextElements,
        },
        undoStack: newUndoStack,
        redoStack: newRedoStack,
      };
    }),

  setCursors: (cursors) => set({ cursors }),

  updateCursor: (userId, cursor) =>
    set((state) => {
      const newCursors = new Map(state.cursors);
      newCursors.set(userId, cursor);
      return { cursors: newCursors };
    }),

  removeCursor: (userId) =>
    set((state) => {
      const newCursors = new Map(state.cursors);
      newCursors.delete(userId);
      return { cursors: newCursors };
    }),

  setWhiteboardUIState: (updates) =>
    set((state) => ({
      whiteboardUIState: { ...state.whiteboardUIState, ...updates },
    })),

  // Discussion actions
  setSpeakingQueue: (queue) => set({ speakingQueue: queue }),

  addToSpeakingQueue: (entry) =>
    set((state) => ({
      speakingQueue: [...state.speakingQueue, entry],
    })),

  updateSpeakingQueueEntry: (entryId, updates) =>
    set((state) => ({
      speakingQueue: state.speakingQueue.map((e) => (e.id === entryId ? { ...e, ...updates } : e)),
    })),

  removeFromSpeakingQueue: (entryId) =>
    set((state) => ({
      speakingQueue: state.speakingQueue.filter((e) => e.id !== entryId),
    })),

  setDiscussionThreads: (threads) => set({ discussionThreads: threads }),

  addDiscussionThread: (thread) =>
    set((state) => ({
      discussionThreads: [...state.discussionThreads, thread],
    })),

  updateDiscussionThread: (threadId, updates) =>
    set((state) => ({
      discussionThreads: state.discussionThreads.map((t) => (t.id === threadId ? { ...t, ...updates } : t)),
    })),

  setParticipationStats: (stats) => set({ participationStats: stats }),

  // Connection actions
  setWSConnected: (connected) => set({ wsConnected: connected }),

  setError: (error) => set({ error }),

  // Reset
  reset: () =>
    set({
      ...initialState,
      quizUIState: {
        ...initialState.quizUIState,
        selectedAnswers: new Map(),
      },
      cursors: new Map(),
    }),
}));

// Selectors
export const activeSelectors = {
  // Get active polls
  getActivePolls: (state: ActiveState) => state.polls.filter((p) => p.status === 'ACTIVE'),

  // Get user's poll response
  getUserPollResponse: (state: ActiveState, pollId: number, userId: number) =>
    state.pollResponses.find((r) => r.pollId === pollId && r.userId === userId),

  // Get active quizzes
  getActiveQuizzes: (state: ActiveState) => state.quizzes.filter((q) => q.status === 'ACTIVE'),

  // Get current quiz question
  getCurrentQuestion: (state: ActiveState) =>
    state.currentQuiz?.questions[state.quizUIState.currentQuestionIndex],

  // Get quiz progress
  getQuizProgress: (state: ActiveState) => {
    if (!state.currentQuiz) return 0;
    return (state.quizUIState.selectedAnswers.size / state.currentQuiz.questions.length) * 100;
  },

  // Get unassigned participants in breakout session
  getUnassignedParticipants: (state: ActiveState) => state.breakoutSession?.unassignedParticipants || [],

  // Get speaking queue position
  getSpeakingQueuePosition: (state: ActiveState, userId: number) => {
    const entry = state.speakingQueue.find((e) => e.userId === userId && e.status === 'WAITING');
    return entry?.position;
  },

  // Check if user can undo/redo
  canUndo: (state: ActiveState) => state.undoStack.length > 0,
  canRedo: (state: ActiveState) => state.redoStack.length > 0,
};
