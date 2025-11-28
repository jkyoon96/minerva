/**
 * Seminar state management store (Zustand)
 * - Room state
 * - Participants
 * - Chat messages
 * - Media settings
 * - UI state
 */

import { create } from 'zustand';
import {
  Room,
  Participant,
  ChatMessage,
  Reaction,
  LayoutType,
  MediaSettings,
  SeminarUIState,
  ParticipantRole,
} from '@/types/seminar';

interface SeminarState {
  // Room state
  room: Room | null;
  participants: Participant[];
  currentParticipant: Participant | null;
  messages: ChatMessage[];
  reactions: Reaction[];

  // Media state
  mediaSettings: MediaSettings;
  localStream: MediaStream | null;
  screenShareStream: MediaStream | null;
  participantStreams: Map<number, MediaStream>;

  // UI state
  uiState: SeminarUIState;
  isConnecting: boolean;
  isConnected: boolean;
  error: string | null;

  // WebSocket state
  wsConnected: boolean;

  // Actions - Room
  setRoom: (room: Room | null) => void;
  updateRoom: (updates: Partial<Room>) => void;
  setLayout: (layout: LayoutType) => void;

  // Actions - Participants
  setParticipants: (participants: Participant[]) => void;
  addParticipant: (participant: Participant) => void;
  removeParticipant: (participantId: number) => void;
  updateParticipant: (participantId: number, updates: Partial<Participant>) => void;
  setCurrentParticipant: (participant: Participant | null) => void;
  toggleHandRaise: (participantId: number) => void;
  updateMediaState: (
    participantId: number,
    updates: { isMuted?: boolean; isVideoOn?: boolean; isScreenSharing?: boolean },
  ) => void;

  // Actions - Chat
  setMessages: (messages: ChatMessage[]) => void;
  addMessage: (message: ChatMessage) => void;
  removeMessage: (messageId: number) => void;
  clearMessages: () => void;

  // Actions - Reactions
  addReaction: (reaction: Reaction) => void;
  clearReactions: () => void;

  // Actions - Media
  setMediaSettings: (settings: Partial<MediaSettings>) => void;
  setLocalStream: (stream: MediaStream | null) => void;
  setScreenShareStream: (stream: MediaStream | null) => void;
  setParticipantStream: (participantId: number, stream: MediaStream | null) => void;

  // Actions - UI
  setUIState: (updates: Partial<SeminarUIState>) => void;
  toggleSidebar: () => void;
  setActiveTab: (tab: 'chat' | 'participants' | 'activities') => void;

  // Actions - Connection
  setConnecting: (isConnecting: boolean) => void;
  setConnected: (isConnected: boolean) => void;
  setError: (error: string | null) => void;
  setWSConnected: (connected: boolean) => void;

  // Actions - Reset
  reset: () => void;
  cleanup: () => void;
}

const initialState = {
  room: null,
  participants: [],
  currentParticipant: null,
  messages: [],
  reactions: [],
  mediaSettings: {
    audioEnabled: true,
    videoEnabled: true,
  },
  localStream: null,
  screenShareStream: null,
  participantStreams: new Map(),
  uiState: {
    isSidebarOpen: true,
    activeTab: 'chat' as const,
    isDeviceSettingsOpen: false,
    isLayoutSelectorOpen: false,
    chatSearchQuery: '',
  },
  isConnecting: false,
  isConnected: false,
  error: null,
  wsConnected: false,
};

export const useSeminarStore = create<SeminarState>()((set, get) => ({
  ...initialState,

  // Room actions
  setRoom: (room) => {
    set({ room });
  },

  updateRoom: (updates) => {
    set((state) => ({
      room: state.room ? { ...state.room, ...updates } : null,
    }));
  },

  setLayout: (layout) => {
    set((state) => ({
      room: state.room ? { ...state.room, layout } : null,
    }));
  },

  // Participant actions
  setParticipants: (participants) => {
    set({ participants });
  },

  addParticipant: (participant) => {
    set((state) => ({
      participants: [...state.participants, participant],
    }));
  },

  removeParticipant: (participantId) => {
    set((state) => ({
      participants: state.participants.filter((p) => p.id !== participantId),
    }));
  },

  updateParticipant: (participantId, updates) => {
    set((state) => ({
      participants: state.participants.map((p) =>
        p.id === participantId ? { ...p, ...updates } : p,
      ),
      currentParticipant:
        state.currentParticipant?.id === participantId
          ? { ...state.currentParticipant, ...updates }
          : state.currentParticipant,
    }));
  },

  setCurrentParticipant: (participant) => {
    set({ currentParticipant: participant });
  },

  toggleHandRaise: (participantId) => {
    set((state) => ({
      participants: state.participants.map((p) =>
        p.id === participantId ? { ...p, isHandRaised: !p.isHandRaised } : p,
      ),
    }));
  },

  updateMediaState: (participantId, updates) => {
    set((state) => ({
      participants: state.participants.map((p) =>
        p.id === participantId ? { ...p, ...updates } : p,
      ),
      currentParticipant:
        state.currentParticipant?.id === participantId
          ? { ...state.currentParticipant, ...updates }
          : state.currentParticipant,
    }));
  },

  // Chat actions
  setMessages: (messages) => {
    set({ messages });
  },

  addMessage: (message) => {
    set((state) => ({
      messages: [...state.messages, message],
    }));
  },

  removeMessage: (messageId) => {
    set((state) => ({
      messages: state.messages.filter((m) => m.id !== messageId),
    }));
  },

  clearMessages: () => {
    set({ messages: [] });
  },

  // Reaction actions
  addReaction: (reaction) => {
    set((state) => ({
      reactions: [...state.reactions, reaction],
    }));

    // Auto-remove reaction after 3 seconds
    setTimeout(() => {
      set((state) => ({
        reactions: state.reactions.filter((r) => r.id !== reaction.id),
      }));
    }, 3000);
  },

  clearReactions: () => {
    set({ reactions: [] });
  },

  // Media actions
  setMediaSettings: (settings) => {
    set((state) => ({
      mediaSettings: { ...state.mediaSettings, ...settings },
    }));
  },

  setLocalStream: (stream) => {
    // Stop existing stream if any
    const existingStream = get().localStream;
    if (existingStream) {
      existingStream.getTracks().forEach((track) => track.stop());
    }
    set({ localStream: stream });
  },

  setScreenShareStream: (stream) => {
    // Stop existing screen share if any
    const existingStream = get().screenShareStream;
    if (existingStream) {
      existingStream.getTracks().forEach((track) => track.stop());
    }
    set({ screenShareStream: stream });
  },

  setParticipantStream: (participantId, stream) => {
    set((state) => {
      const newStreams = new Map(state.participantStreams);
      if (stream) {
        newStreams.set(participantId, stream);
      } else {
        // Stop existing stream if any
        const existingStream = newStreams.get(participantId);
        if (existingStream) {
          existingStream.getTracks().forEach((track) => track.stop());
        }
        newStreams.delete(participantId);
      }
      return { participantStreams: newStreams };
    });
  },

  // UI actions
  setUIState: (updates) => {
    set((state) => ({
      uiState: { ...state.uiState, ...updates },
    }));
  },

  toggleSidebar: () => {
    set((state) => ({
      uiState: { ...state.uiState, isSidebarOpen: !state.uiState.isSidebarOpen },
    }));
  },

  setActiveTab: (tab) => {
    set((state) => ({
      uiState: { ...state.uiState, activeTab: tab },
    }));
  },

  // Connection actions
  setConnecting: (isConnecting) => {
    set({ isConnecting });
  },

  setConnected: (isConnected) => {
    set({ isConnected });
  },

  setError: (error) => {
    set({ error });
  },

  setWSConnected: (connected) => {
    set({ wsConnected: connected });
  },

  // Reset actions
  reset: () => {
    set(initialState);
  },

  cleanup: () => {
    // Stop all media streams
    const state = get();

    if (state.localStream) {
      state.localStream.getTracks().forEach((track) => track.stop());
    }

    if (state.screenShareStream) {
      state.screenShareStream.getTracks().forEach((track) => track.stop());
    }

    state.participantStreams.forEach((stream) => {
      stream.getTracks().forEach((track) => track.stop());
    });

    set({
      ...initialState,
      participantStreams: new Map(),
    });
  },
}));

// Selectors (for performance optimization)
export const seminarSelectors = {
  // Get host participant
  getHost: (state: SeminarState) =>
    state.participants.find((p) => p.role === ParticipantRole.HOST),

  // Get raised hands count
  getRaisedHandsCount: (state: SeminarState) =>
    state.participants.filter((p) => p.isHandRaised).length,

  // Get participants with raised hands
  getParticipantsWithRaisedHands: (state: SeminarState) =>
    state.participants.filter((p) => p.isHandRaised),

  // Check if current user is host
  isCurrentUserHost: (state: SeminarState) =>
    state.currentParticipant?.role === ParticipantRole.HOST ||
    state.currentParticipant?.role === ParticipantRole.CO_HOST,

  // Get unread messages count (could be enhanced with read status)
  getUnreadMessagesCount: (state: SeminarState) => state.messages.length,
};
