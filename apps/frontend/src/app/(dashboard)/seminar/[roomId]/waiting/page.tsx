'use client';

/**
 * Seminar waiting room page - Wait for host to start the session
 */

import React, { useEffect } from 'react';
import { useRouter, useParams } from 'next/navigation';
import { Clock, Users, Video } from 'lucide-react';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { useSeminarStore } from '@/stores/seminarStore';
import { useWebSocket } from '@/hooks/useWebSocket';
import { seminarApi } from '@/lib/api';
import { useToast } from '@/components/ui/use-toast';
import { useAuth } from '@/hooks/useAuth';

export default function WaitingRoomPage() {
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
  } = useSeminarStore();

  // Connect to WebSocket
  const { isConnected } = useWebSocket({
    roomId,
    userId: user?.id || 0,
    onError: (error) => {
      console.error('WebSocket error:', error);
      toast({
        title: 'Connection Error',
        description: 'Failed to connect to the server',
        variant: 'destructive',
      });
    },
  });

  // Fetch room and join
  useEffect(() => {
    const joinRoom = async () => {
      try {
        // Fetch room data
        const roomData = await seminarApi.room.getRoom(roomId);
        setRoom(roomData);

        // Join room as participant
        const participant = await seminarApi.participant.joinRoom(roomId, {
          userName: user?.name || 'Guest',
          userEmail: user?.email,
        });
        setCurrentParticipant(participant);

        // Fetch participants
        const participantsList = await seminarApi.participant.getParticipants(roomId);
        setParticipants(participantsList);
      } catch (error) {
        console.error('Failed to join room:', error);
        toast({
          title: 'Error',
          description: 'Failed to join the waiting room',
          variant: 'destructive',
        });
      }
    };

    if (user) {
      joinRoom();
    }
  }, [roomId, user, setRoom, setCurrentParticipant, setParticipants, toast]);

  // Listen for room status changes
  useEffect(() => {
    if (room?.status === 'ACTIVE') {
      // Room started, navigate to live session
      router.push(`/seminar/${roomId}/live`);
    }
  }, [room?.status, roomId, router]);

  const handleLeave = () => {
    router.push('/dashboard');
  };

  return (
    <div className="flex min-h-screen items-center justify-center bg-gray-950 p-4">
      <Card className="w-full max-w-2xl bg-gray-900 border-gray-800">
        <CardHeader>
          <CardTitle className="text-2xl text-white text-center">
            Waiting for host to start...
          </CardTitle>
        </CardHeader>
        <CardContent className="space-y-6">
          {/* Status indicators */}
          <div className="grid gap-4 md:grid-cols-3">
            <div className="flex flex-col items-center gap-2 rounded-lg bg-gray-800 p-4">
              <Clock className="h-8 w-8 text-blue-500" />
              <div className="text-sm font-medium text-white">Status</div>
              <div className="text-xs text-gray-400">Waiting</div>
            </div>

            <div className="flex flex-col items-center gap-2 rounded-lg bg-gray-800 p-4">
              <Users className="h-8 w-8 text-green-500" />
              <div className="text-sm font-medium text-white">Participants</div>
              <div className="text-xs text-gray-400">
                {participants.length} waiting
              </div>
            </div>

            <div className="flex flex-col items-center gap-2 rounded-lg bg-gray-800 p-4">
              <Video className="h-8 w-8 text-purple-500" />
              <div className="text-sm font-medium text-white">Host</div>
              <div className="text-xs text-gray-400">{room?.hostName || 'Unknown'}</div>
            </div>
          </div>

          {/* Waiting message */}
          <div className="rounded-lg bg-blue-500/10 border border-blue-500/20 p-4">
            <p className="text-center text-sm text-blue-400">
              The host will let you in soon. Please wait...
            </p>
          </div>

          {/* Connection status */}
          <div className="flex items-center justify-center gap-2">
            <div
              className={`h-2 w-2 rounded-full ${
                isConnected ? 'bg-green-500' : 'bg-red-500'
              }`}
            />
            <span className="text-sm text-gray-400">
              {isConnected ? 'Connected' : 'Disconnected'}
            </span>
          </div>

          {/* Actions */}
          <div className="flex justify-center pt-4">
            <Button variant="outline" onClick={handleLeave}>
              Leave Waiting Room
            </Button>
          </div>
        </CardContent>
      </Card>
    </div>
  );
}
