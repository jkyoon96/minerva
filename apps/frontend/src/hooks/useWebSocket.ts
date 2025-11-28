/**
 * WebSocket hook for real-time communication
 */

import { useEffect, useRef, useCallback } from 'react';
import { io, Socket } from 'socket.io-client';
import { useSeminarStore } from '@/stores/seminarStore';
import {
  WebSocketMessage,
  ParticipantJoinedPayload,
  ParticipantLeftPayload,
  MediaStateChangedPayload,
  HandRaisedPayload,
  ChatMessagePayload,
  ReactionPayload,
  LayoutChangedPayload,
} from '@/types/seminar';

const WS_URL = process.env.NEXT_PUBLIC_WS_URL || 'http://localhost:8000';

interface UseWebSocketOptions {
  roomId: number;
  userId: number;
  onError?: (error: Error) => void;
}

export const useWebSocket = ({ roomId, userId, onError }: UseWebSocketOptions) => {
  const socketRef = useRef<Socket | null>(null);
  const {
    setWSConnected,
    addParticipant,
    removeParticipant,
    updateParticipant,
    addMessage,
    addReaction,
    setLayout,
  } = useSeminarStore();

  // Connect to WebSocket
  const connect = useCallback(() => {
    if (socketRef.current?.connected) {
      return;
    }

    const socket = io(WS_URL, {
      auth: {
        userId,
        roomId,
      },
      transports: ['websocket'],
      reconnection: true,
      reconnectionDelay: 1000,
      reconnectionDelayMax: 5000,
      reconnectionAttempts: 5,
    });

    // Connection events
    socket.on('connect', () => {
      console.log('WebSocket connected');
      setWSConnected(true);
    });

    socket.on('disconnect', (reason) => {
      console.log('WebSocket disconnected:', reason);
      setWSConnected(false);
    });

    socket.on('connect_error', (error) => {
      console.error('WebSocket connection error:', error);
      onError?.(error);
    });

    // Room events
    socket.on('participant:joined', (data: ParticipantJoinedPayload) => {
      addParticipant(data.participant);
    });

    socket.on('participant:left', (data: ParticipantLeftPayload) => {
      removeParticipant(data.participantId);
    });

    socket.on('media:changed', (data: MediaStateChangedPayload) => {
      updateParticipant(data.participantId, {
        isMuted: data.isMuted,
        isVideoOn: data.isVideoOn,
        isScreenSharing: data.isScreenSharing,
      });
    });

    socket.on('hand:raised', (data: HandRaisedPayload) => {
      updateParticipant(data.participantId, {
        isHandRaised: data.isRaised,
      });
    });

    // Chat events
    socket.on('chat:message', (data: ChatMessagePayload) => {
      addMessage(data.message);
    });

    // Reaction events
    socket.on('reaction:sent', (data: ReactionPayload) => {
      addReaction(data.reaction);
    });

    // Layout events
    socket.on('layout:changed', (data: LayoutChangedPayload) => {
      setLayout(data.layout);
    });

    socketRef.current = socket;
  }, [
    roomId,
    userId,
    setWSConnected,
    addParticipant,
    removeParticipant,
    updateParticipant,
    addMessage,
    addReaction,
    setLayout,
    onError,
  ]);

  // Disconnect from WebSocket
  const disconnect = useCallback(() => {
    if (socketRef.current) {
      socketRef.current.disconnect();
      socketRef.current = null;
      setWSConnected(false);
    }
  }, [setWSConnected]);

  // Send message
  const sendMessage = useCallback(
    <T = any>(type: string, payload: T) => {
      if (socketRef.current?.connected) {
        const message: WebSocketMessage<T> = {
          type,
          payload,
          timestamp: new Date().toISOString(),
        };
        socketRef.current.emit(type, message);
      } else {
        console.warn('WebSocket not connected, message not sent:', type);
      }
    },
    [],
  );

  // Auto-connect on mount, disconnect on unmount
  useEffect(() => {
    connect();

    return () => {
      disconnect();
    };
  }, [connect, disconnect]);

  return {
    socket: socketRef.current,
    isConnected: socketRef.current?.connected ?? false,
    connect,
    disconnect,
    sendMessage,
  };
};
