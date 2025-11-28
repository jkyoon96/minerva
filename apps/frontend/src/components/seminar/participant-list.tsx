'use client';

/**
 * Participant list component
 */

import React, { useState } from 'react';
import {
  Search,
  Mic,
  MicOff,
  Video,
  VideoOff,
  Hand,
  Crown,
  MoreVertical,
  UserPlus,
  UserMinus,
} from 'lucide-react';
import { Input } from '@/components/ui/input';
import { Button } from '@/components/ui/button';
import { ScrollArea } from '@/components/ui/scroll-area';
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu';
import { Participant, ParticipantRole } from '@/types/seminar';
import { cn } from '@/lib/utils';

interface ParticipantListProps {
  participants: Participant[];
  currentUserId: number;
  isHost?: boolean;
  onAdmit?: (participantId: number) => void;
  onRemove?: (participantId: number) => void;
  onSendMessage?: (participantId: number) => void;
  className?: string;
}

export const ParticipantList: React.FC<ParticipantListProps> = ({
  participants,
  currentUserId,
  isHost = false,
  onAdmit,
  onRemove,
  onSendMessage,
  className,
}) => {
  const [searchQuery, setSearchQuery] = useState('');

  // Filter participants by search query
  const filteredParticipants = participants.filter((p) =>
    p.userName.toLowerCase().includes(searchQuery.toLowerCase()),
  );

  // Separate participants by status
  const joinedParticipants = filteredParticipants.filter(
    (p) => p.status === 'JOINED',
  );
  const waitingParticipants = filteredParticipants.filter(
    (p) => p.status === 'WAITING',
  );

  const getRoleIcon = (role: ParticipantRole) => {
    if (role === ParticipantRole.HOST || role === ParticipantRole.CO_HOST) {
      return <Crown className="h-4 w-4 text-yellow-500" />;
    }
    return null;
  };

  return (
    <div className={cn('flex h-full flex-col bg-gray-900', className)}>
      {/* Header */}
      <div className="border-b border-gray-800 p-4">
        <div className="flex items-center justify-between">
          <h3 className="text-lg font-semibold text-white">
            Participants ({joinedParticipants.length})
          </h3>
        </div>

        {/* Search */}
        <div className="mt-3 relative">
          <Search className="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-gray-500" />
          <Input
            type="text"
            placeholder="Search participants..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            className="pl-9 bg-gray-800 border-gray-700 text-white"
          />
        </div>
      </div>

      {/* Waiting room (host only) */}
      {isHost && waitingParticipants.length > 0 && (
        <div className="border-b border-gray-800 p-4">
          <h4 className="mb-2 text-sm font-semibold text-gray-400">
            Waiting Room ({waitingParticipants.length})
          </h4>
          <div className="space-y-2">
            {waitingParticipants.map((participant) => (
              <div
                key={participant.id}
                className="flex items-center justify-between rounded-lg bg-gray-800 p-2"
              >
                <span className="text-sm text-white">{participant.userName}</span>
                {onAdmit && (
                  <Button
                    size="sm"
                    onClick={() => onAdmit(participant.id)}
                    className="gap-1"
                  >
                    <UserPlus className="h-4 w-4" />
                    Admit
                  </Button>
                )}
              </div>
            ))}
          </div>
        </div>
      )}

      {/* Participant list */}
      <ScrollArea className="flex-1">
        <div className="space-y-1 p-2">
          {joinedParticipants.map((participant) => {
            const isCurrentUser = participant.userId === currentUserId;

            return (
              <div
                key={participant.id}
                className={cn(
                  'group flex items-center gap-3 rounded-lg p-3 hover:bg-gray-800',
                  isCurrentUser && 'bg-gray-800/50',
                )}
              >
                {/* Avatar */}
                <div className="flex h-9 w-9 shrink-0 items-center justify-center rounded-full bg-blue-600 text-sm font-semibold text-white">
                  {participant.userName.charAt(0).toUpperCase()}
                </div>

                {/* Info */}
                <div className="min-w-0 flex-1">
                  <div className="flex items-center gap-2">
                    <span className="truncate text-sm font-medium text-white">
                      {participant.userName}
                      {isCurrentUser && ' (You)'}
                    </span>
                    {getRoleIcon(participant.role)}
                  </div>

                  {/* Status indicators */}
                  <div className="mt-1 flex items-center gap-2">
                    {participant.isHandRaised && (
                      <Hand className="h-4 w-4 text-yellow-500" />
                    )}
                    {participant.isMuted ? (
                      <MicOff className="h-4 w-4 text-red-500" />
                    ) : (
                      <Mic className="h-4 w-4 text-green-500" />
                    )}
                    {participant.isVideoOn ? (
                      <Video className="h-4 w-4 text-green-500" />
                    ) : (
                      <VideoOff className="h-4 w-4 text-gray-500" />
                    )}
                  </div>
                </div>

                {/* Actions (for host or own controls) */}
                {!isCurrentUser && (isHost || onSendMessage) && (
                  <div className="shrink-0 opacity-0 transition-opacity group-hover:opacity-100">
                    <DropdownMenu>
                      <DropdownMenuTrigger asChild>
                        <Button variant="ghost" size="sm" className="h-8 w-8 p-0">
                          <MoreVertical className="h-4 w-4" />
                        </Button>
                      </DropdownMenuTrigger>
                      <DropdownMenuContent align="end">
                        {onSendMessage && (
                          <DropdownMenuItem
                            onClick={() => onSendMessage(participant.userId)}
                          >
                            Send Message
                          </DropdownMenuItem>
                        )}
                        {isHost && onRemove && (
                          <DropdownMenuItem
                            onClick={() => onRemove(participant.id)}
                            className="text-red-500"
                          >
                            <UserMinus className="mr-2 h-4 w-4" />
                            Remove
                          </DropdownMenuItem>
                        )}
                      </DropdownMenuContent>
                    </DropdownMenu>
                  </div>
                )}
              </div>
            );
          })}
        </div>

        {joinedParticipants.length === 0 && (
          <div className="flex h-full items-center justify-center text-gray-500">
            No participants found
          </div>
        )}
      </ScrollArea>
    </div>
  );
};
