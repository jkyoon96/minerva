/**
 * Active Learning API client
 * - Polls/Voting
 * - Quizzes
 * - Breakout Rooms
 * - Whiteboard
 * - Discussion/Speaking Queue
 */

import apiClient, { parseApiError } from './client';
import { ApiResponse } from './types';
import {
  Poll,
  PollResponse,
  PollResult,
  PollTemplate,
  PollCreateRequest,
  PollResponseRequest,
  Quiz,
  QuizSubmission,
  QuizResult,
  Question,
  QuestionCreateRequest,
  QuizCreateRequest,
  QuizSubmitRequest,
  BreakoutSession,
  BreakoutRoom,
  BroadcastMessage,
  BreakoutSessionCreateRequest,
  BreakoutRoomAssignRequest,
  BroadcastMessageRequest,
  WhiteboardState,
  DrawingElement,
  WhiteboardElementRequest,
  SpeakingQueueEntry,
  DiscussionThread,
  ParticipationStat,
  SpeakingQueueJoinRequest,
  DiscussionThreadRequest,
} from '@/types/active';

/**
 * Poll APIs
 */
export const pollApi = {
  /**
   * Get all polls for a course
   */
  getPolls: async (courseId: number): Promise<Poll[]> => {
    try {
      const response = await apiClient.get<ApiResponse<Poll[]>>(`/v1/active/polls`, {
        params: { courseId },
      });
      return response.data.data;
    } catch (error) {
      throw parseApiError(error);
    }
  },

  /**
   * Get poll by ID
   */
  getPoll: async (pollId: number): Promise<Poll> => {
    try {
      const response = await apiClient.get<ApiResponse<Poll>>(`/v1/active/polls/${pollId}`);
      return response.data.data;
    } catch (error) {
      throw parseApiError(error);
    }
  },

  /**
   * Create a new poll
   */
  createPoll: async (data: PollCreateRequest): Promise<Poll> => {
    try {
      const response = await apiClient.post<ApiResponse<Poll>>(`/v1/active/polls`, data);
      return response.data.data;
    } catch (error) {
      throw parseApiError(error);
    }
  },

  /**
   * Update a poll
   */
  updatePoll: async (pollId: number, updates: Partial<PollCreateRequest>): Promise<Poll> => {
    try {
      const response = await apiClient.put<ApiResponse<Poll>>(`/v1/active/polls/${pollId}`, updates);
      return response.data.data;
    } catch (error) {
      throw parseApiError(error);
    }
  },

  /**
   * Delete a poll
   */
  deletePoll: async (pollId: number): Promise<void> => {
    try {
      await apiClient.delete(`/v1/active/polls/${pollId}`);
    } catch (error) {
      throw parseApiError(error);
    }
  },

  /**
   * Start a poll
   */
  startPoll: async (pollId: number): Promise<Poll> => {
    try {
      const response = await apiClient.post<ApiResponse<Poll>>(`/v1/active/polls/${pollId}/start`);
      return response.data.data;
    } catch (error) {
      throw parseApiError(error);
    }
  },

  /**
   * End a poll
   */
  endPoll: async (pollId: number): Promise<Poll> => {
    try {
      const response = await apiClient.post<ApiResponse<Poll>>(`/v1/active/polls/${pollId}/end`);
      return response.data.data;
    } catch (error) {
      throw parseApiError(error);
    }
  },

  /**
   * Submit poll response
   */
  respondToPoll: async (pollId: number, data: PollResponseRequest): Promise<PollResponse> => {
    try {
      const response = await apiClient.post<ApiResponse<PollResponse>>(
        `/v1/active/polls/${pollId}/responses`,
        data,
      );
      return response.data.data;
    } catch (error) {
      throw parseApiError(error);
    }
  },

  /**
   * Get poll results
   */
  getPollResults: async (pollId: number): Promise<PollResult> => {
    try {
      const response = await apiClient.get<ApiResponse<PollResult>>(`/v1/active/polls/${pollId}/results`);
      return response.data.data;
    } catch (error) {
      throw parseApiError(error);
    }
  },

  /**
   * Get poll templates
   */
  getTemplates: async (): Promise<PollTemplate[]> => {
    try {
      const response = await apiClient.get<ApiResponse<PollTemplate[]>>(`/v1/active/polls/templates`);
      return response.data.data;
    } catch (error) {
      throw parseApiError(error);
    }
  },
};

/**
 * Question Bank APIs
 */
export const questionApi = {
  /**
   * Get all questions for a course
   */
  getQuestions: async (courseId: number, filters?: { tags?: string[]; difficulty?: string }): Promise<Question[]> => {
    try {
      const response = await apiClient.get<ApiResponse<Question[]>>(`/v1/active/questions`, {
        params: { courseId, ...filters },
      });
      return response.data.data;
    } catch (error) {
      throw parseApiError(error);
    }
  },

  /**
   * Get question by ID
   */
  getQuestion: async (questionId: number): Promise<Question> => {
    try {
      const response = await apiClient.get<ApiResponse<Question>>(`/v1/active/questions/${questionId}`);
      return response.data.data;
    } catch (error) {
      throw parseApiError(error);
    }
  },

  /**
   * Create a new question
   */
  createQuestion: async (data: QuestionCreateRequest): Promise<Question> => {
    try {
      const response = await apiClient.post<ApiResponse<Question>>(`/v1/active/questions`, data);
      return response.data.data;
    } catch (error) {
      throw parseApiError(error);
    }
  },

  /**
   * Update a question
   */
  updateQuestion: async (questionId: number, updates: Partial<QuestionCreateRequest>): Promise<Question> => {
    try {
      const response = await apiClient.put<ApiResponse<Question>>(
        `/v1/active/questions/${questionId}`,
        updates,
      );
      return response.data.data;
    } catch (error) {
      throw parseApiError(error);
    }
  },

  /**
   * Delete a question
   */
  deleteQuestion: async (questionId: number): Promise<void> => {
    try {
      await apiClient.delete(`/v1/active/questions/${questionId}`);
    } catch (error) {
      throw parseApiError(error);
    }
  },

  /**
   * Bulk import questions
   */
  importQuestions: async (courseId: number, questions: QuestionCreateRequest[]): Promise<Question[]> => {
    try {
      const response = await apiClient.post<ApiResponse<Question[]>>(`/v1/active/questions/import`, {
        courseId,
        questions,
      });
      return response.data.data;
    } catch (error) {
      throw parseApiError(error);
    }
  },
};

/**
 * Quiz APIs
 */
export const quizApi = {
  /**
   * Get all quizzes for a course
   */
  getQuizzes: async (courseId: number): Promise<Quiz[]> => {
    try {
      const response = await apiClient.get<ApiResponse<Quiz[]>>(`/v1/active/quizzes`, {
        params: { courseId },
      });
      return response.data.data;
    } catch (error) {
      throw parseApiError(error);
    }
  },

  /**
   * Get quiz by ID
   */
  getQuiz: async (quizId: number): Promise<Quiz> => {
    try {
      const response = await apiClient.get<ApiResponse<Quiz>>(`/v1/active/quizzes/${quizId}`);
      return response.data.data;
    } catch (error) {
      throw parseApiError(error);
    }
  },

  /**
   * Create a new quiz
   */
  createQuiz: async (data: QuizCreateRequest): Promise<Quiz> => {
    try {
      const response = await apiClient.post<ApiResponse<Quiz>>(`/v1/active/quizzes`, data);
      return response.data.data;
    } catch (error) {
      throw parseApiError(error);
    }
  },

  /**
   * Update a quiz
   */
  updateQuiz: async (quizId: number, updates: Partial<QuizCreateRequest>): Promise<Quiz> => {
    try {
      const response = await apiClient.put<ApiResponse<Quiz>>(`/v1/active/quizzes/${quizId}`, updates);
      return response.data.data;
    } catch (error) {
      throw parseApiError(error);
    }
  },

  /**
   * Delete a quiz
   */
  deleteQuiz: async (quizId: number): Promise<void> => {
    try {
      await apiClient.delete(`/v1/active/quizzes/${quizId}`);
    } catch (error) {
      throw parseApiError(error);
    }
  },

  /**
   * Start a quiz
   */
  startQuiz: async (quizId: number): Promise<Quiz> => {
    try {
      const response = await apiClient.post<ApiResponse<Quiz>>(`/v1/active/quizzes/${quizId}/start`);
      return response.data.data;
    } catch (error) {
      throw parseApiError(error);
    }
  },

  /**
   * Submit quiz answers
   */
  submitQuiz: async (quizId: number, data: QuizSubmitRequest): Promise<QuizSubmission> => {
    try {
      const response = await apiClient.post<ApiResponse<QuizSubmission>>(
        `/v1/active/quizzes/${quizId}/submit`,
        data,
      );
      return response.data.data;
    } catch (error) {
      throw parseApiError(error);
    }
  },

  /**
   * Get quiz submissions
   */
  getSubmissions: async (quizId: number): Promise<QuizSubmission[]> => {
    try {
      const response = await apiClient.get<ApiResponse<QuizSubmission[]>>(
        `/v1/active/quizzes/${quizId}/submissions`,
      );
      return response.data.data;
    } catch (error) {
      throw parseApiError(error);
    }
  },

  /**
   * Get quiz results
   */
  getResults: async (submissionId: number): Promise<QuizResult> => {
    try {
      const response = await apiClient.get<ApiResponse<QuizResult>>(
        `/v1/active/quizzes/submissions/${submissionId}/results`,
      );
      return response.data.data;
    } catch (error) {
      throw parseApiError(error);
    }
  },
};

/**
 * Breakout Room APIs
 */
export const breakoutApi = {
  /**
   * Create breakout session
   */
  createSession: async (data: BreakoutSessionCreateRequest): Promise<BreakoutSession> => {
    try {
      const response = await apiClient.post<ApiResponse<BreakoutSession>>(
        `/v1/active/breakout/sessions`,
        data,
      );
      return response.data.data;
    } catch (error) {
      throw parseApiError(error);
    }
  },

  /**
   * Get breakout session
   */
  getSession: async (sessionId: number): Promise<BreakoutSession> => {
    try {
      const response = await apiClient.get<ApiResponse<BreakoutSession>>(
        `/v1/active/breakout/sessions/${sessionId}`,
      );
      return response.data.data;
    } catch (error) {
      throw parseApiError(error);
    }
  },

  /**
   * Start breakout session
   */
  startSession: async (sessionId: number): Promise<BreakoutSession> => {
    try {
      const response = await apiClient.post<ApiResponse<BreakoutSession>>(
        `/v1/active/breakout/sessions/${sessionId}/start`,
      );
      return response.data.data;
    } catch (error) {
      throw parseApiError(error);
    }
  },

  /**
   * End breakout session
   */
  endSession: async (sessionId: number): Promise<BreakoutSession> => {
    try {
      const response = await apiClient.post<ApiResponse<BreakoutSession>>(
        `/v1/active/breakout/sessions/${sessionId}/end`,
      );
      return response.data.data;
    } catch (error) {
      throw parseApiError(error);
    }
  },

  /**
   * Assign participants to room
   */
  assignParticipants: async (sessionId: number, data: BreakoutRoomAssignRequest): Promise<BreakoutRoom> => {
    try {
      const response = await apiClient.post<ApiResponse<BreakoutRoom>>(
        `/v1/active/breakout/sessions/${sessionId}/assign`,
        data,
      );
      return response.data.data;
    } catch (error) {
      throw parseApiError(error);
    }
  },

  /**
   * Broadcast message to rooms
   */
  broadcastMessage: async (sessionId: number, data: BroadcastMessageRequest): Promise<BroadcastMessage> => {
    try {
      const response = await apiClient.post<ApiResponse<BroadcastMessage>>(
        `/v1/active/breakout/sessions/${sessionId}/broadcast`,
        data,
      );
      return response.data.data;
    } catch (error) {
      throw parseApiError(error);
    }
  },

  /**
   * Get breakout room
   */
  getRoom: async (roomId: number): Promise<BreakoutRoom> => {
    try {
      const response = await apiClient.get<ApiResponse<BreakoutRoom>>(`/v1/active/breakout/rooms/${roomId}`);
      return response.data.data;
    } catch (error) {
      throw parseApiError(error);
    }
  },

  /**
   * Join breakout room
   */
  joinRoom: async (roomId: number): Promise<BreakoutRoom> => {
    try {
      const response = await apiClient.post<ApiResponse<BreakoutRoom>>(
        `/v1/active/breakout/rooms/${roomId}/join`,
      );
      return response.data.data;
    } catch (error) {
      throw parseApiError(error);
    }
  },

  /**
   * Leave breakout room
   */
  leaveRoom: async (roomId: number): Promise<void> => {
    try {
      await apiClient.post(`/v1/active/breakout/rooms/${roomId}/leave`);
    } catch (error) {
      throw parseApiError(error);
    }
  },
};

/**
 * Whiteboard APIs
 */
export const whiteboardApi = {
  /**
   * Get whiteboard state
   */
  getWhiteboard: async (sessionId: number): Promise<WhiteboardState> => {
    try {
      const response = await apiClient.get<ApiResponse<WhiteboardState>>(
        `/v1/active/whiteboard/${sessionId}`,
      );
      return response.data.data;
    } catch (error) {
      throw parseApiError(error);
    }
  },

  /**
   * Add drawing element
   */
  addElement: async (sessionId: number, data: WhiteboardElementRequest): Promise<DrawingElement> => {
    try {
      const response = await apiClient.post<ApiResponse<DrawingElement>>(
        `/v1/active/whiteboard/${sessionId}/elements`,
        data,
      );
      return response.data.data;
    } catch (error) {
      throw parseApiError(error);
    }
  },

  /**
   * Update drawing element
   */
  updateElement: async (
    sessionId: number,
    elementId: string,
    updates: Partial<WhiteboardElementRequest>,
  ): Promise<DrawingElement> => {
    try {
      const response = await apiClient.put<ApiResponse<DrawingElement>>(
        `/v1/active/whiteboard/${sessionId}/elements/${elementId}`,
        updates,
      );
      return response.data.data;
    } catch (error) {
      throw parseApiError(error);
    }
  },

  /**
   * Delete drawing element
   */
  deleteElement: async (sessionId: number, elementId: string): Promise<void> => {
    try {
      await apiClient.delete(`/v1/active/whiteboard/${sessionId}/elements/${elementId}`);
    } catch (error) {
      throw parseApiError(error);
    }
  },

  /**
   * Clear whiteboard
   */
  clearWhiteboard: async (sessionId: number): Promise<void> => {
    try {
      await apiClient.post(`/v1/active/whiteboard/${sessionId}/clear`);
    } catch (error) {
      throw parseApiError(error);
    }
  },

  /**
   * Export whiteboard as image
   */
  exportWhiteboard: async (sessionId: number, format: 'png' | 'svg' = 'png'): Promise<Blob> => {
    try {
      const response = await apiClient.get(`/v1/active/whiteboard/${sessionId}/export`, {
        params: { format },
        responseType: 'blob',
      });
      return response.data;
    } catch (error) {
      throw parseApiError(error);
    }
  },
};

/**
 * Discussion/Speaking Queue APIs
 */
export const discussionApi = {
  /**
   * Get speaking queue
   */
  getSpeakingQueue: async (sessionId: number): Promise<SpeakingQueueEntry[]> => {
    try {
      const response = await apiClient.get<ApiResponse<SpeakingQueueEntry[]>>(
        `/v1/active/discussion/${sessionId}/queue`,
      );
      return response.data.data;
    } catch (error) {
      throw parseApiError(error);
    }
  },

  /**
   * Join speaking queue
   */
  joinQueue: async (sessionId: number, data: SpeakingQueueJoinRequest): Promise<SpeakingQueueEntry> => {
    try {
      const response = await apiClient.post<ApiResponse<SpeakingQueueEntry>>(
        `/v1/active/discussion/${sessionId}/queue`,
        data,
      );
      return response.data.data;
    } catch (error) {
      throw parseApiError(error);
    }
  },

  /**
   * Leave speaking queue
   */
  leaveQueue: async (sessionId: number, entryId: number): Promise<void> => {
    try {
      await apiClient.delete(`/v1/active/discussion/${sessionId}/queue/${entryId}`);
    } catch (error) {
      throw parseApiError(error);
    }
  },

  /**
   * Start speaking (host only)
   */
  startSpeaking: async (sessionId: number, entryId: number): Promise<SpeakingQueueEntry> => {
    try {
      const response = await apiClient.post<ApiResponse<SpeakingQueueEntry>>(
        `/v1/active/discussion/${sessionId}/queue/${entryId}/start`,
      );
      return response.data.data;
    } catch (error) {
      throw parseApiError(error);
    }
  },

  /**
   * End speaking (host only)
   */
  endSpeaking: async (sessionId: number, entryId: number): Promise<SpeakingQueueEntry> => {
    try {
      const response = await apiClient.post<ApiResponse<SpeakingQueueEntry>>(
        `/v1/active/discussion/${sessionId}/queue/${entryId}/end`,
      );
      return response.data.data;
    } catch (error) {
      throw parseApiError(error);
    }
  },

  /**
   * Get discussion threads
   */
  getThreads: async (sessionId: number): Promise<DiscussionThread[]> => {
    try {
      const response = await apiClient.get<ApiResponse<DiscussionThread[]>>(
        `/v1/active/discussion/${sessionId}/threads`,
      );
      return response.data.data;
    } catch (error) {
      throw parseApiError(error);
    }
  },

  /**
   * Create discussion thread
   */
  createThread: async (sessionId: number, data: DiscussionThreadRequest): Promise<DiscussionThread> => {
    try {
      const response = await apiClient.post<ApiResponse<DiscussionThread>>(
        `/v1/active/discussion/${sessionId}/threads`,
        data,
      );
      return response.data.data;
    } catch (error) {
      throw parseApiError(error);
    }
  },

  /**
   * Like a thread
   */
  likeThread: async (sessionId: number, threadId: number): Promise<DiscussionThread> => {
    try {
      const response = await apiClient.post<ApiResponse<DiscussionThread>>(
        `/v1/active/discussion/${sessionId}/threads/${threadId}/like`,
      );
      return response.data.data;
    } catch (error) {
      throw parseApiError(error);
    }
  },

  /**
   * Get participation stats
   */
  getParticipationStats: async (sessionId: number): Promise<ParticipationStat[]> => {
    try {
      const response = await apiClient.get<ApiResponse<ParticipationStat[]>>(
        `/v1/active/discussion/${sessionId}/stats`,
      );
      return response.data.data;
    } catch (error) {
      throw parseApiError(error);
    }
  },
};

// Export all APIs
export default {
  poll: pollApi,
  question: questionApi,
  quiz: quizApi,
  breakout: breakoutApi,
  whiteboard: whiteboardApi,
  discussion: discussionApi,
};
