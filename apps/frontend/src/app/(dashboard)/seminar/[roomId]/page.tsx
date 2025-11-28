'use client';

/**
 * Seminar entry page - Device selection and preview
 */

import React, { useEffect, useState } from 'react';
import { useRouter, useParams } from 'next/navigation';
import { WaitingRoom } from '@/components/seminar';
import { useMediaDevices } from '@/hooks/useMediaDevices';
import { useSeminarStore } from '@/stores/seminarStore';
import { seminarApi } from '@/lib/api';
import { MediaSettings } from '@/types/seminar';
import { useToast } from '@/components/ui/use-toast';

export default function SeminarEntryPage() {
  const router = useRouter();
  const params = useParams();
  const { toast } = useToast();
  const roomId = Number(params.roomId);

  const { getUserMedia } = useMediaDevices();
  const {
    room,
    setRoom,
    mediaSettings,
    setMediaSettings,
    localStream,
    setLocalStream,
    setError,
  } = useSeminarStore();

  const [isJoining, setIsJoining] = useState(false);

  // Fetch room info
  useEffect(() => {
    const fetchRoom = async () => {
      try {
        const roomData = await seminarApi.room.getRoom(roomId);
        setRoom(roomData);
      } catch (error) {
        console.error('Failed to fetch room:', error);
        setError('Failed to load room information');
        toast({
          title: 'Error',
          description: 'Failed to load room information',
          variant: 'destructive',
        });
      }
    };

    fetchRoom();
  }, [roomId, setRoom, setError, toast]);

  // Initialize media stream
  useEffect(() => {
    const initMedia = async () => {
      const stream = await getUserMedia(mediaSettings);
      if (stream) {
        setLocalStream(stream);
      }
    };

    initMedia();

    // Cleanup on unmount
    return () => {
      if (localStream) {
        localStream.getTracks().forEach((track) => track.stop());
      }
    };
  }, []);

  // Update media stream when settings change
  useEffect(() => {
    const updateMedia = async () => {
      // Stop existing stream
      if (localStream) {
        localStream.getTracks().forEach((track) => track.stop());
      }

      // Get new stream with updated settings
      const stream = await getUserMedia(mediaSettings);
      if (stream) {
        setLocalStream(stream);
      }
    };

    updateMedia();
  }, [mediaSettings.audioDeviceId, mediaSettings.videoDeviceId]);

  const handleSettingsChange = (updates: Partial<MediaSettings>) => {
    setMediaSettings(updates);
  };

  const handleJoin = async () => {
    if (!room) {
      toast({
        title: 'Error',
        description: 'Room information not loaded',
        variant: 'destructive',
      });
      return;
    }

    setIsJoining(true);

    try {
      // Navigate to waiting room or live session based on room status
      if (room.status === 'WAITING') {
        router.push(`/seminar/${roomId}/waiting`);
      } else {
        router.push(`/seminar/${roomId}/live`);
      }
    } catch (error) {
      console.error('Failed to join room:', error);
      toast({
        title: 'Error',
        description: 'Failed to join the seminar',
        variant: 'destructive',
      });
      setIsJoining(false);
    }
  };

  if (!room) {
    return (
      <div className="flex min-h-screen items-center justify-center bg-gray-950">
        <div className="text-white">Loading...</div>
      </div>
    );
  }

  return (
    <WaitingRoom
      stream={localStream}
      settings={mediaSettings}
      roomName={room.hostName ? `${room.hostName}'s Seminar` : 'Seminar'}
      onSettingsChange={handleSettingsChange}
      onJoin={handleJoin}
      isJoining={isJoining}
    />
  );
}
