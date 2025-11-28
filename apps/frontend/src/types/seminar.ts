/**
 * Seminar/Room related type definitions
 */

export enum RoomStatus {
  WAITING = 'WAITING',
  ACTIVE = 'ACTIVE',
  ENDED = 'ENDED',
}

export enum LayoutType {
  GALLERY = 'GALLERY',
  SPEAKER = 'SPEAKER',
  SIDEBAR = 'SIDEBAR',
}

export enum ParticipantRole {
  HOST = 'HOST',
  CO_HOST = 'CO_HOST',
  PARTICIPANT = 'PARTICIPANT',
}

export enum ParticipantStatus {
  WAITING = 'WAITING',
  JOINED = 'JOINED',
  LEFT = 'LEFT',
}

export enum ChatMessageType {
  PUBLIC = 'PUBLIC',
  PRIVATE = 'PRIVATE',
  SYSTEM = 'SYSTEM',
}

export enum ReactionType {
  THUMBS_UP = 'THUMBS_UP',
  CLAP = 'CLAP',
  HEART = 'HEART',
  LAUGH = 'LAUGH',
  SURPRISED = 'SURPRISED',
}

export interface Room {
  id: number;
  sessionId: number;
  hostId: number;
  hostName: string;
  status: RoomStatus;
  maxParticipants: number;
  currentParticipants: number;
  startedAt?: string;
  endedAt?: string;
  meetingUrl?: string;
  recordingUrl?: string;
  layout: LayoutType;
  settings?: Record<string, any>;
  createdAt: string;
  updatedAt: string;
}

export interface Participant {
  id: number;
  userId: number;
  userName: string;
  userEmail: string;
  role: ParticipantRole;
  status: ParticipantStatus;
  isHandRaised: boolean;
  isMuted: boolean;
  isVideoOn: boolean;
  isScreenSharing: boolean;
  joinedAt: string;
  leftAt?: string;
}

export interface ChatMessage {
  id: number;
  roomId: number;
  senderId: number;
  senderName: string;
  recipientId?: number;
  recipientName?: string;
  messageType: ChatMessageType;
  content: string;
  metadata?: Record<string, any>;
  sentAt: string;
}

export interface Reaction {
  id: number;
  roomId: number;
  participantId: number;
  participantName: string;
  reactionType: ReactionType;
  createdAt: string;
}

export interface MediaDevice {
  deviceId: string;
  label: string;
  kind: 'audioinput' | 'videoinput' | 'audiooutput';
}

export interface MediaSettings {
  audioDeviceId?: string;
  videoDeviceId?: string;
  audioEnabled: boolean;
  videoEnabled: boolean;
}

export interface ScreenShareSettings {
  enabled: boolean;
  displaySurface?: 'monitor' | 'window' | 'browser';
  cursor?: 'always' | 'motion' | 'never';
}

// WebSocket message types
export interface WebSocketMessage<T = any> {
  type: string;
  payload: T;
  timestamp: string;
}

export interface ParticipantJoinedPayload {
  participant: Participant;
}

export interface ParticipantLeftPayload {
  participantId: number;
}

export interface MediaStateChangedPayload {
  participantId: number;
  isMuted?: boolean;
  isVideoOn?: boolean;
  isScreenSharing?: boolean;
}

export interface HandRaisedPayload {
  participantId: number;
  isRaised: boolean;
}

export interface ChatMessagePayload {
  message: ChatMessage;
}

export interface ReactionPayload {
  reaction: Reaction;
}

export interface LayoutChangedPayload {
  layout: LayoutType;
}

// API Request/Response types
export interface RoomCreateRequest {
  sessionId: number;
  maxParticipants?: number;
  settings?: Record<string, any>;
}

export interface RoomJoinRequest {
  userName: string;
  userEmail?: string;
  role?: ParticipantRole;
}

export interface LayoutUpdateRequest {
  layout: LayoutType;
}

export interface ChatMessageRequest {
  recipientId?: number;
  content: string;
  messageType?: ChatMessageType;
}

export interface ReactionRequest {
  reactionType: ReactionType;
}

export interface ScreenShareRequest {
  enabled: boolean;
}

// UI State types
export interface VideoTileState {
  participantId: number;
  stream: MediaStream | null;
  isMuted: boolean;
  isVideoOn: boolean;
  isScreenSharing: boolean;
  isSpeaking: boolean;
  displayName: string;
}

export interface SeminarUIState {
  isSidebarOpen: boolean;
  activeTab: 'chat' | 'participants' | 'activities';
  isDeviceSettingsOpen: boolean;
  isLayoutSelectorOpen: boolean;
  chatSearchQuery: string;
  selectedChatRecipient?: Participant;
}
