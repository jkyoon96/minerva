# Seminar Components API Reference

## Video Components

### VideoTile

Individual participant video display component.

```typescript
<VideoTile
  participant={participant}      // Participant object
  stream={mediaStream}           // MediaStream or null
  isSpeaking={false}             // Optional: speaking indicator
  isLocal={false}                // Optional: is current user
  className=""                   // Optional: custom classes
/>
```

**Props**:
- `participant: Participant` - Required
- `stream: MediaStream | null` - Required
- `isSpeaking?: boolean` - Optional (default: false)
- `isLocal?: boolean` - Optional (default: false)
- `className?: string` - Optional

**Features**:
- Video or avatar display
- Muted/video-off indicators
- Hand raised indicator
- Speaking detection (blue ring)
- Screen sharing badge

---

### VideoGrid

Grid layout for multiple participant videos.

```typescript
<VideoGrid
  participants={participants}
  streams={participantStreamsMap}
  currentUserId={userId}
  className=""
/>
```

**Props**:
- `participants: Participant[]` - Required
- `streams: Map<number, MediaStream>` - Required
- `currentUserId?: number` - Optional
- `className?: string` - Optional

**Features**:
- Responsive grid (2-5 columns)
- Auto-adjusts for participant count
- Smooth transitions

---

### MediaControls

Bottom control bar with media buttons.

```typescript
<MediaControls
  isMuted={isMuted}
  isVideoOn={isVideoOn}
  isScreenSharing={isScreenSharing}
  onToggleMute={() => {}}
  onToggleVideo={() => {}}
  onToggleScreenShare={() => {}}
  onLeave={() => {}}
  onOpenSettings={() => {}}  // Optional
  className=""
/>
```

**Props**:
- `isMuted: boolean` - Required
- `isVideoOn: boolean` - Required
- `isScreenSharing: boolean` - Required
- `onToggleMute: () => void` - Required
- `onToggleVideo: () => void` - Required
- `onToggleScreenShare: () => void` - Required
- `onLeave: () => void` - Required
- `onOpenSettings?: () => void` - Optional
- `className?: string` - Optional

---

## Screen Share

### ScreenShare

Full-screen screen share display.

```typescript
<ScreenShare
  stream={screenShareStream}
  presenterName="John Doe"
  onStop={() => {}}          // Only for presenter
  showPipVideo={true}
  pipStream={localStream}
  className=""
/>
```

**Props**:
- `stream: MediaStream | null` - Required
- `presenterName: string` - Required
- `onStop?: () => void` - Optional (presenter only)
- `showPipVideo?: boolean` - Optional (default: false)
- `pipStream?: MediaStream | null` - Optional
- `className?: string` - Optional

---

## Chat Components

### ChatPanel

Full chat interface with messages and input.

```typescript
<ChatPanel
  messages={messages}
  participants={participants}
  currentUserId={userId}
  onSendMessage={(content, recipientId) => {}}
  onDeleteMessage={(messageId) => {}}  // Optional
  className=""
/>
```

**Props**:
- `messages: ChatMessage[]` - Required
- `participants: Participant[]` - Required
- `currentUserId: number` - Required
- `onSendMessage: (content: string, recipientId?: number) => void` - Required
- `onDeleteMessage?: (messageId: number) => void` - Optional
- `className?: string` - Optional

**Features**:
- Auto-scroll to bottom
- Search messages
- Public/private messaging
- Emoji picker (placeholder)
- Delete own messages

---

### ChatMessage

Individual message display.

```typescript
<ChatMessage
  message={message}
  isOwn={message.senderId === currentUserId}
  onDelete={(msgId) => {}}  // Optional
  className=""
/>
```

**Props**:
- `message: ChatMessage` - Required
- `isOwn: boolean` - Required
- `onDelete?: (messageId: number) => void` - Optional
- `className?: string` - Optional

**Supports**:
- Public messages
- Private (DM) messages
- System messages

---

## Interaction Components

### HandRaise

Hand raise/lower button.

```typescript
<HandRaise
  isRaised={isRaised}
  onToggle={() => {}}
  disabled={false}
  className=""
/>
```

**Props**:
- `isRaised: boolean` - Required
- `onToggle: () => void` - Required
- `disabled?: boolean` - Optional (default: false)
- `className?: string` - Optional

---

### ReactionButtons

Emoji reaction buttons.

```typescript
<ReactionButtons
  onReact={(type) => {}}
  className=""
/>
```

**Props**:
- `onReact: (type: ReactionType) => void` - Required
- `className?: string` - Optional

**Reactions**:
- THUMBS_UP (👍)
- CLAP (👏)
- HEART (❤️)
- LAUGH (😂)
- SURPRISED (😮)

---

### ParticipantList

Participant management panel.

```typescript
<ParticipantList
  participants={participants}
  currentUserId={userId}
  isHost={isHost}
  onAdmit={(participantId) => {}}       // Optional (host)
  onRemove={(participantId) => {}}      // Optional (host)
  onSendMessage={(participantId) => {}} // Optional
  className=""
/>
```

**Props**:
- `participants: Participant[]` - Required
- `currentUserId: number` - Required
- `isHost?: boolean` - Optional (default: false)
- `onAdmit?: (participantId: number) => void` - Optional
- `onRemove?: (participantId: number) => void` - Optional
- `onSendMessage?: (participantId: number) => void` - Optional
- `className?: string` - Optional

**Features**:
- Search participants
- Waiting room section (host)
- Admit/remove actions (host)
- Status indicators
- Send DM action

---

## Layout Components

### LayoutSelector

Layout switcher dropdown.

```typescript
<LayoutSelector
  currentLayout={LayoutType.GALLERY}
  onLayoutChange={(layout) => {}}
  disabled={false}
  className=""
/>
```

**Props**:
- `currentLayout: LayoutType` - Required
- `onLayoutChange: (layout: LayoutType) => void` - Required
- `disabled?: boolean` - Optional (default: false)
- `className?: string` - Optional

**Layouts**:
- GALLERY - All participants equal
- SPEAKER - Active speaker focus
- SIDEBAR - Speaker with sidebar

---

## Setup Components

### DeviceSelector

Camera/mic/speaker selection.

```typescript
<DeviceSelector
  settings={mediaSettings}
  onSettingsChange={(updates) => {}}
  className=""
/>
```

**Props**:
- `settings: MediaSettings` - Required
- `onSettingsChange: (settings: Partial<MediaSettings>) => void` - Required
- `className?: string` - Optional

**Features**:
- Camera dropdown
- Microphone dropdown
- Speaker dropdown (if available)
- Refresh devices button
- Permission request

---

### WaitingRoom

Pre-session setup interface.

```typescript
<WaitingRoom
  stream={localStream}
  settings={mediaSettings}
  roomName="Seminar Room"
  onSettingsChange={(updates) => {}}
  onJoin={() => {}}
  isJoining={false}
  className=""
/>
```

**Props**:
- `stream: MediaStream | null` - Required
- `settings: MediaSettings` - Required
- `roomName?: string` - Optional (default: "Seminar Room")
- `onSettingsChange: (settings: Partial<MediaSettings>) => void` - Required
- `onJoin: () => void` - Required
- `isJoining?: boolean` - Optional (default: false)
- `className?: string` - Optional

**Features**:
- Video preview
- Device selector
- Audio/video toggles
- Join button
- Settings panel

---

## TypeScript Types

### Core Types

```typescript
// Room
interface Room {
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

// Participant
interface Participant {
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

// Chat Message
interface ChatMessage {
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

// Media Settings
interface MediaSettings {
  audioDeviceId?: string;
  videoDeviceId?: string;
  audioEnabled: boolean;
  videoEnabled: boolean;
}
```

### Enums

```typescript
enum RoomStatus {
  WAITING = 'WAITING',
  ACTIVE = 'ACTIVE',
  ENDED = 'ENDED',
}

enum LayoutType {
  GALLERY = 'GALLERY',
  SPEAKER = 'SPEAKER',
  SIDEBAR = 'SIDEBAR',
}

enum ParticipantRole {
  HOST = 'HOST',
  CO_HOST = 'CO_HOST',
  PARTICIPANT = 'PARTICIPANT',
}

enum ChatMessageType {
  PUBLIC = 'PUBLIC',
  PRIVATE = 'PRIVATE',
  SYSTEM = 'SYSTEM',
}

enum ReactionType {
  THUMBS_UP = 'THUMBS_UP',
  CLAP = 'CLAP',
  HEART = 'HEART',
  LAUGH = 'LAUGH',
  SURPRISED = 'SURPRISED',
}
```

---

## Usage Examples

### Complete Live Session

```typescript
'use client';

import { useEffect } from 'react';
import { useParams } from 'next/navigation';
import {
  VideoGrid,
  MediaControls,
  ChatPanel,
  ParticipantList,
  HandRaise,
} from '@/components/seminar';
import { useSeminarStore } from '@/stores/seminarStore';
import { useWebSocket } from '@/hooks/useWebSocket';

export default function LiveSession() {
  const params = useParams();
  const roomId = Number(params.roomId);

  const {
    participants,
    currentParticipant,
    messages,
    localStream,
    participantStreams,
  } = useSeminarStore();

  const { isConnected } = useWebSocket({
    roomId,
    userId: currentParticipant?.userId || 0,
  });

  return (
    <div className="flex h-screen">
      <div className="flex-1">
        <VideoGrid
          participants={participants}
          streams={participantStreams}
          currentUserId={currentParticipant?.userId}
        />
        <MediaControls
          isMuted={currentParticipant?.isMuted || false}
          isVideoOn={currentParticipant?.isVideoOn || false}
          isScreenSharing={currentParticipant?.isScreenSharing || false}
          onToggleMute={() => {}}
          onToggleVideo={() => {}}
          onToggleScreenShare={() => {}}
          onLeave={() => {}}
        />
      </div>
      <div className="w-80">
        <ChatPanel
          messages={messages}
          participants={participants}
          currentUserId={currentParticipant?.userId || 0}
          onSendMessage={(content) => {}}
        />
      </div>
    </div>
  );
}
```

---

For full documentation, see `SEMINAR_README.md`
