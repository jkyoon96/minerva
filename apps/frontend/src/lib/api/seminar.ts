/**
 * Seminar/Room API client
 */

import apiClient, { parseApiError } from './client';
import { ApiResponse } from './types';
import {
  Room,
  Participant,
  ChatMessage,
  Reaction,
  RoomCreateRequest,
  RoomJoinRequest,
  LayoutUpdateRequest,
  ChatMessageRequest,
  ReactionRequest,
  ScreenShareRequest,
  LayoutType,
} from '@/types/seminar';

/**
 * Room Management APIs
 */
export const roomApi = {
  /**
   * Create a new seminar room
   */
  createRoom: async (data: RoomCreateRequest): Promise<Room> => {
    try {
      const response = await apiClient.post<ApiResponse<Room>>('/v1/rooms', data);
      return response.data.data;
    } catch (error) {
      throw parseApiError(error);
    }
  },

  /**
   * Get room by ID
   */
  getRoom: async (roomId: number): Promise<Room> => {
    try {
      const response = await apiClient.get<ApiResponse<Room>>(`/v1/rooms/${roomId}`);
      return response.data.data;
    } catch (error) {
      throw parseApiError(error);
    }
  },

  /**
   * Get room by session ID
   */
  getRoomBySessionId: async (sessionId: number): Promise<Room> => {
    try {
      const response = await apiClient.get<ApiResponse<Room>>(
        `/v1/rooms/session/${sessionId}`,
      );
      return response.data.data;
    } catch (error) {
      throw parseApiError(error);
    }
  },

  /**
   * Start a room (host only)
   */
  startRoom: async (roomId: number): Promise<Room> => {
    try {
      const response = await apiClient.post<ApiResponse<Room>>(
        `/v1/rooms/${roomId}/start`,
      );
      return response.data.data;
    } catch (error) {
      throw parseApiError(error);
    }
  },

  /**
   * End a room (host only)
   */
  endRoom: async (roomId: number): Promise<Room> => {
    try {
      const response = await apiClient.post<ApiResponse<Room>>(`/v1/rooms/${roomId}/end`);
      return response.data.data;
    } catch (error) {
      throw parseApiError(error);
    }
  },

  /**
   * Update room layout (host only)
   */
  updateLayout: async (roomId: number, data: LayoutUpdateRequest): Promise<Room> => {
    try {
      const response = await apiClient.put<ApiResponse<Room>>(
        `/v1/rooms/${roomId}/layout`,
        data,
      );
      return response.data.data;
    } catch (error) {
      throw parseApiError(error);
    }
  },

  /**
   * Get current layout
   */
  getLayout: async (roomId: number): Promise<LayoutType> => {
    try {
      const response = await apiClient.get<ApiResponse<LayoutType>>(
        `/v1/rooms/${roomId}/layout`,
      );
      return response.data.data;
    } catch (error) {
      throw parseApiError(error);
    }
  },

  /**
   * Get active rooms
   */
  getActiveRooms: async (): Promise<Room[]> => {
    try {
      const response = await apiClient.get<ApiResponse<Room[]>>('/v1/rooms/active');
      return response.data.data;
    } catch (error) {
      throw parseApiError(error);
    }
  },

  /**
   * Get rooms by host
   */
  getRoomsByHost: async (hostId: number): Promise<Room[]> => {
    try {
      const response = await apiClient.get<ApiResponse<Room[]>>(
        `/v1/rooms/host/${hostId}`,
      );
      return response.data.data;
    } catch (error) {
      throw parseApiError(error);
    }
  },
};

/**
 * Participant Management APIs
 */
export const participantApi = {
  /**
   * Join a room
   */
  joinRoom: async (roomId: number, data: RoomJoinRequest): Promise<Participant> => {
    try {
      const response = await apiClient.post<ApiResponse<Participant>>(
        `/v1/rooms/${roomId}/participants`,
        data,
      );
      return response.data.data;
    } catch (error) {
      throw parseApiError(error);
    }
  },

  /**
   * Leave a room
   */
  leaveRoom: async (roomId: number, participantId: number): Promise<void> => {
    try {
      await apiClient.delete(`/v1/rooms/${roomId}/participants/${participantId}`);
    } catch (error) {
      throw parseApiError(error);
    }
  },

  /**
   * Get all participants in a room
   */
  getParticipants: async (roomId: number): Promise<Participant[]> => {
    try {
      const response = await apiClient.get<ApiResponse<Participant[]>>(
        `/v1/rooms/${roomId}/participants`,
      );
      return response.data.data;
    } catch (error) {
      throw parseApiError(error);
    }
  },

  /**
   * Toggle hand raise
   */
  toggleHandRaise: async (
    roomId: number,
    participantId: number,
    isRaised: boolean,
  ): Promise<Participant> => {
    try {
      const response = await apiClient.put<ApiResponse<Participant>>(
        `/v1/rooms/${roomId}/participants/${participantId}/hand`,
        { isRaised },
      );
      return response.data.data;
    } catch (error) {
      throw parseApiError(error);
    }
  },

  /**
   * Update media state (mute/video)
   */
  updateMediaState: async (
    roomId: number,
    participantId: number,
    updates: { isMuted?: boolean; isVideoOn?: boolean; isScreenSharing?: boolean },
  ): Promise<Participant> => {
    try {
      const response = await apiClient.put<ApiResponse<Participant>>(
        `/v1/rooms/${roomId}/participants/${participantId}/media`,
        updates,
      );
      return response.data.data;
    } catch (error) {
      throw parseApiError(error);
    }
  },

  /**
   * Admit participant from waiting room (host only)
   */
  admitParticipant: async (
    roomId: number,
    participantId: number,
  ): Promise<Participant> => {
    try {
      const response = await apiClient.post<ApiResponse<Participant>>(
        `/v1/rooms/${roomId}/participants/${participantId}/admit`,
      );
      return response.data.data;
    } catch (error) {
      throw parseApiError(error);
    }
  },

  /**
   * Remove participant (host only)
   */
  removeParticipant: async (roomId: number, participantId: number): Promise<void> => {
    try {
      await apiClient.post(`/v1/rooms/${roomId}/participants/${participantId}/remove`);
    } catch (error) {
      throw parseApiError(error);
    }
  },
};

/**
 * Chat APIs
 */
export const chatApi = {
  /**
   * Send a chat message
   */
  sendMessage: async (
    roomId: number,
    data: ChatMessageRequest,
  ): Promise<ChatMessage> => {
    try {
      const response = await apiClient.post<ApiResponse<ChatMessage>>(
        `/v1/rooms/${roomId}/chat`,
        data,
      );
      return response.data.data;
    } catch (error) {
      throw parseApiError(error);
    }
  },

  /**
   * Get chat history
   */
  getChatHistory: async (roomId: number, limit?: number): Promise<ChatMessage[]> => {
    try {
      const response = await apiClient.get<ApiResponse<ChatMessage[]>>(
        `/v1/rooms/${roomId}/chat`,
        {
          params: { limit },
        },
      );
      return response.data.data;
    } catch (error) {
      throw parseApiError(error);
    }
  },

  /**
   * Delete a chat message
   */
  deleteMessage: async (roomId: number, messageId: number): Promise<void> => {
    try {
      await apiClient.delete(`/v1/rooms/${roomId}/chat/${messageId}`);
    } catch (error) {
      throw parseApiError(error);
    }
  },
};

/**
 * Reaction APIs
 */
export const reactionApi = {
  /**
   * Send a reaction
   */
  sendReaction: async (
    roomId: number,
    data: ReactionRequest,
  ): Promise<Reaction> => {
    try {
      const response = await apiClient.post<ApiResponse<Reaction>>(
        `/v1/rooms/${roomId}/reactions`,
        data,
      );
      return response.data.data;
    } catch (error) {
      throw parseApiError(error);
    }
  },
};

/**
 * Screen Share APIs
 */
export const screenShareApi = {
  /**
   * Start/stop screen sharing
   */
  toggleScreenShare: async (
    roomId: number,
    participantId: number,
    data: ScreenShareRequest,
  ): Promise<Participant> => {
    try {
      const response = await apiClient.put<ApiResponse<Participant>>(
        `/v1/rooms/${roomId}/participants/${participantId}/screen-share`,
        data,
      );
      return response.data.data;
    } catch (error) {
      throw parseApiError(error);
    }
  },
};

// Export all APIs
export default {
  room: roomApi,
  participant: participantApi,
  chat: chatApi,
  reaction: reactionApi,
  screenShare: screenShareApi,
};
