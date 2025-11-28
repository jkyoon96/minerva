'use client';

/**
 * Live seminar session page - Main video conferencing interface
 */

import React, { useEffect, useState, useCallback } from 'react';
import { useRouter, useParams } from 'next/navigation';
import { MessageSquare, Users, Activity, PanelRightClose, PanelRightOpen } from 'lucide-react';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { Button } from '@/components/ui/button';
import {
  VideoGrid,
  ScreenShare,
  MediaControls,
  ChatPanel,
  ParticipantList,
  HandRaise,
  ReactionButtons,
  LayoutSelector,
} from '@/components/seminar';
import { useSeminarStore, seminarSelectors } from '@/stores/seminarStore';
import { useWebSocket } from '@/hooks/useWebSocket';
import { useMediaDevices } from '@/hooks/useMediaDevices';
import { seminarApi } from '@/lib/api';
import { useToast } from '@/components/ui/use-toast';
import { useAuth } from '@/hooks/useAuth';
import { LayoutType, ReactionType, ChatMessageType } from '@/types/seminar';
import { cn } from '@/lib/utils';

export default function LiveSeminarPage() {
  const router = useRouter();
  const params = useParams();
  const { toast } = useToast();
  const { user } = useAuth();
  const roomId = Number(params.roomId);

  const {
    room,
    setRoom,
    participants,
    setParticipants,
    currentParticipant,
    setCurrentParticipant,
    messages,
    addMessage,
    localStream,
    setLocalStream,
    screenShareStream,
    setScreenShareStream,
    participantStreams,
    mediaSettings,
    setMediaSettings,
    updateParticipant,
    uiState,
    setUIState,
    toggleSidebar,
    setActiveTab,
    cleanup,
  } = useSeminarStore();

  const { getUserMedia, getDisplayMedia, toggleAudio, toggleVideo } = useMediaDevices();
  const [isScreenSharing, setIsScreenSharing] = useState(false);

  // Connect to WebSocket
  const { sendMessage } = useWebSocket({
    roomId,
    userId: user?.id || 0,
    onError: (error) => {
      console.error('WebSocket error:', error);
      toast({
        title: 'Connection Error',
        description: 'Failed to maintain connection to the server',
        variant: 'destructive',
      });
    },
  });

  // Initialize room and join
  useEffect(() => {
    const initializeRoom = async () => {
      try {
        // Fetch room data
        const roomData = await seminarApi.room.getRoom(roomId);
        setRoom(roomData);

        // If not already joined, join the room
        if (!currentParticipant) {
          const participant = await seminarApi.participant.joinRoom(roomId, {
            userName: user?.name || 'Guest',
            userEmail: user?.email,
          });
          setCurrentParticipant(participant);
        }

        // Fetch participants
        const participantsList = await seminarApi.participant.getParticipants(roomId);
        setParticipants(participantsList);

        // Fetch chat history
        const chatHistory = await seminarApi.chat.getChatHistory(roomId, 50);
        chatHistory.forEach((msg) => addMessage(msg));
      } catch (error) {
        console.error('Failed to initialize room:', error);
        toast({
          title: 'Error',
          description: 'Failed to join the seminar',
          variant: 'destructive',
        });
        router.push('/dashboard');
      }
    };

    if (user) {
      initializeRoom();
    }
  }, [roomId, user]);

  // Initialize local media stream
  useEffect(() => {
    const initMedia = async () => {
      if (!localStream) {
        const stream = await getUserMedia(mediaSettings);
        if (stream) {
          setLocalStream(stream);
        }
      }
    };

    initMedia();

    // Cleanup on unmount
    return () => {
      cleanup();
    };
  }, []);

  // Handle mute toggle
  const handleToggleMute = useCallback(async () => {
    if (localStream && currentParticipant) {
      const newMuted = !currentParticipant.isMuted;
      toggleAudio(localStream, !newMuted);

      // Update server
      try {
        await seminarApi.participant.updateMediaState(roomId, currentParticipant.id, {
          isMuted: newMuted,
        });
        updateParticipant(currentParticipant.id, { isMuted: newMuted });
      } catch (error) {
        console.error('Failed to update mute state:', error);
      }
    }
  }, [localStream, currentParticipant, roomId, toggleAudio, updateParticipant]);

  // Handle video toggle
  const handleToggleVideo = useCallback(async () => {
    if (localStream && currentParticipant) {
      const newVideoOn = !currentParticipant.isVideoOn;
      toggleVideo(localStream, newVideoOn);

      // Update server
      try {
        await seminarApi.participant.updateMediaState(roomId, currentParticipant.id, {
          isVideoOn: newVideoOn,
        });
        updateParticipant(currentParticipant.id, { isVideoOn: newVideoOn });
      } catch (error) {
        console.error('Failed to update video state:', error);
      }
    }
  }, [localStream, currentParticipant, roomId, toggleVideo, updateParticipant]);

  // Handle screen share toggle
  const handleToggleScreenShare = useCallback(async () => {
    if (!currentParticipant) return;

    if (isScreenSharing) {
      // Stop screen sharing
      if (screenShareStream) {
        screenShareStream.getTracks().forEach((track) => track.stop());
        setScreenShareStream(null);
      }

      try {
        await seminarApi.screenShare.toggleScreenShare(roomId, currentParticipant.id, {
          enabled: false,
        });
        updateParticipant(currentParticipant.id, { isScreenSharing: false });
        setIsScreenSharing(false);
      } catch (error) {
        console.error('Failed to stop screen share:', error);
      }
    } else {
      // Start screen sharing
      const stream = await getDisplayMedia();
      if (stream) {
        setScreenShareStream(stream);

        // Listen for user stopping share via browser UI
        stream.getVideoTracks()[0].addEventListener('ended', () => {
          handleToggleScreenShare();
        });

        try {
          await seminarApi.screenShare.toggleScreenShare(roomId, currentParticipant.id, {
            enabled: true,
          });
          updateParticipant(currentParticipant.id, { isScreenSharing: true });
          setIsScreenSharing(true);
        } catch (error) {
          console.error('Failed to start screen share:', error);
          stream.getTracks().forEach((track) => track.stop());
          setScreenShareStream(null);
        }
      }
    }
  }, [
    isScreenSharing,
    screenShareStream,
    currentParticipant,
    roomId,
    getDisplayMedia,
    setScreenShareStream,
    updateParticipant,
  ]);

  // Handle leaving the room
  const handleLeave = useCallback(async () => {
    if (currentParticipant) {
      try {
        await seminarApi.participant.leaveRoom(roomId, currentParticipant.id);
      } catch (error) {
        console.error('Failed to leave room:', error);
      }
    }

    cleanup();
    router.push('/dashboard');
  }, [currentParticipant, roomId, cleanup, router]);

  // Handle sending chat message
  const handleSendMessage = useCallback(
    async (content: string, recipientId?: number) => {
      try {
        await seminarApi.chat.sendMessage(roomId, {
          content,
          recipientId,
          messageType: recipientId ? ChatMessageType.PRIVATE : ChatMessageType.PUBLIC,
        });
      } catch (error) {
        console.error('Failed to send message:', error);
        toast({
          title: 'Error',
          description: 'Failed to send message',
          variant: 'destructive',
        });
      }
    },
    [roomId, toast],
  );

  // Handle hand raise
  const handleToggleHandRaise = useCallback(async () => {
    if (!currentParticipant) return;

    const newIsRaised = !currentParticipant.isHandRaised;

    try {
      await seminarApi.participant.toggleHandRaise(
        roomId,
        currentParticipant.id,
        newIsRaised,
      );
      updateParticipant(currentParticipant.id, { isHandRaised: newIsRaised });
    } catch (error) {
      console.error('Failed to toggle hand raise:', error);
    }
  }, [currentParticipant, roomId, updateParticipant]);

  // Handle reaction
  const handleReact = useCallback(
    async (type: ReactionType) => {
      try {
        await seminarApi.reaction.sendReaction(roomId, {
          reactionType: type,
        });
      } catch (error) {
        console.error('Failed to send reaction:', error);
      }
    },
    [roomId],
  );

  // Handle layout change
  const handleLayoutChange = useCallback(
    async (layout: LayoutType) => {
      if (!seminarSelectors.isCurrentUserHost(useSeminarStore.getState())) {
        toast({
          title: 'Permission Denied',
          description: 'Only the host can change the layout',
          variant: 'destructive',
        });
        return;
      }

      try {
        await seminarApi.room.updateLayout(roomId, { layout });
      } catch (error) {
        console.error('Failed to update layout:', error);
        toast({
          title: 'Error',
          description: 'Failed to change layout',
          variant: 'destructive',
        });
      }
    },
    [roomId, toast],
  );

  // Find presenter for screen share
  const presenter = participants.find((p) => p.isScreenSharing);
  const isHost = seminarSelectors.isCurrentUserHost(useSeminarStore.getState());

  if (!room || !currentParticipant) {
    return (
      <div className="flex min-h-screen items-center justify-center bg-gray-950">
        <div className="text-white">Loading...</div>
      </div>
    );
  }

  return (
    <div className="flex h-screen flex-col bg-gray-950">
      {/* Header */}
      <div className="flex items-center justify-between border-b border-gray-800 bg-gray-900 px-4 py-3">
        <div>
          <h1 className="text-lg font-semibold text-white">
            {room.hostName ? `${room.hostName}'s Seminar` : 'Live Seminar'}
          </h1>
          <p className="text-sm text-gray-400">
            {participants.length} participant{participants.length !== 1 ? 's' : ''}
          </p>
        </div>

        <div className="flex items-center gap-2">
          <LayoutSelector
            currentLayout={room.layout}
            onLayoutChange={handleLayoutChange}
            disabled={!isHost}
          />

          <Button
            variant="ghost"
            size="icon"
            onClick={toggleSidebar}
            title={uiState.isSidebarOpen ? 'Hide sidebar' : 'Show sidebar'}
          >
            {uiState.isSidebarOpen ? (
              <PanelRightClose className="h-5 w-5" />
            ) : (
              <PanelRightOpen className="h-5 w-5" />
            )}
          </Button>
        </div>
      </div>

      {/* Main content */}
      <div className="flex flex-1 overflow-hidden">
        {/* Video area */}
        <div className="flex flex-1 flex-col">
          {/* Screen share or video grid */}
          <div className="flex-1 overflow-hidden">
            {presenter && presenter.isScreenSharing ? (
              <ScreenShare
                stream={screenShareStream}
                presenterName={presenter.userName}
                onStop={
                  presenter.id === currentParticipant.id
                    ? handleToggleScreenShare
                    : undefined
                }
                showPipVideo={true}
                pipStream={localStream}
              />
            ) : (
              <VideoGrid
                participants={participants}
                streams={participantStreams}
                currentUserId={currentParticipant.userId}
              />
            )}
          </div>

          {/* Bottom controls */}
          <div className="border-t border-gray-800 bg-gray-900 p-4">
            <div className="flex items-center justify-between">
              {/* Left: Reactions */}
              <ReactionButtons onReact={handleReact} />

              {/* Center: Media controls */}
              <MediaControls
                isMuted={currentParticipant.isMuted}
                isVideoOn={currentParticipant.isVideoOn}
                isScreenSharing={currentParticipant.isScreenSharing}
                onToggleMute={handleToggleMute}
                onToggleVideo={handleToggleVideo}
                onToggleScreenShare={handleToggleScreenShare}
                onLeave={handleLeave}
              />

              {/* Right: Hand raise */}
              <HandRaise
                isRaised={currentParticipant.isHandRaised}
                onToggle={handleToggleHandRaise}
              />
            </div>
          </div>
        </div>

        {/* Sidebar */}
        {uiState.isSidebarOpen && (
          <div className="w-80 border-l border-gray-800 bg-gray-900">
            <Tabs
              value={uiState.activeTab}
              onValueChange={(value) =>
                setActiveTab(value as 'chat' | 'participants' | 'activities')
              }
              className="flex h-full flex-col"
            >
              <TabsList className="grid w-full grid-cols-3 bg-gray-800">
                <TabsTrigger value="chat" className="gap-2">
                  <MessageSquare className="h-4 w-4" />
                  Chat
                </TabsTrigger>
                <TabsTrigger value="participants" className="gap-2">
                  <Users className="h-4 w-4" />
                  People
                </TabsTrigger>
                <TabsTrigger value="activities" className="gap-2">
                  <Activity className="h-4 w-4" />
                  Activity
                </TabsTrigger>
              </TabsList>

              <TabsContent value="chat" className="flex-1 m-0">
                <ChatPanel
                  messages={messages}
                  participants={participants}
                  currentUserId={currentParticipant.userId}
                  onSendMessage={handleSendMessage}
                />
              </TabsContent>

              <TabsContent value="participants" className="flex-1 m-0">
                <ParticipantList
                  participants={participants}
                  currentUserId={currentParticipant.userId}
                  isHost={isHost}
                />
              </TabsContent>

              <TabsContent value="activities" className="flex-1 m-0">
                <div className="flex h-full items-center justify-center text-gray-500">
                  Activities panel (Coming soon)
                </div>
              </TabsContent>
            </Tabs>
          </div>
        )}
      </div>
    </div>
  );
}
