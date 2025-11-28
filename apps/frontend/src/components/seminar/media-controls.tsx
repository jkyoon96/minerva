'use client';

/**
 * Media controls component for mute/unmute, video on/off, screen share, etc.
 */

import React from 'react';
import {
  Mic,
  MicOff,
  Video,
  VideoOff,
  Monitor,
  MonitorOff,
  PhoneOff,
  Settings,
  MoreVertical,
} from 'lucide-react';
import { Button } from '@/components/ui/button';
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu';
import { cn } from '@/lib/utils';

interface MediaControlsProps {
  isMuted: boolean;
  isVideoOn: boolean;
  isScreenSharing: boolean;
  onToggleMute: () => void;
  onToggleVideo: () => void;
  onToggleScreenShare: () => void;
  onLeave: () => void;
  onOpenSettings?: () => void;
  className?: string;
}

export const MediaControls: React.FC<MediaControlsProps> = ({
  isMuted,
  isVideoOn,
  isScreenSharing,
  onToggleMute,
  onToggleVideo,
  onToggleScreenShare,
  onLeave,
  onOpenSettings,
  className,
}) => {
  return (
    <div
      className={cn(
        'flex items-center justify-center gap-3 rounded-lg bg-gray-900 p-4',
        className,
      )}
    >
      {/* Microphone toggle */}
      <Button
        variant={isMuted ? 'destructive' : 'secondary'}
        size="lg"
        onClick={onToggleMute}
        className="h-12 w-12 rounded-full"
        title={isMuted ? 'Unmute' : 'Mute'}
      >
        {isMuted ? <MicOff className="h-5 w-5" /> : <Mic className="h-5 w-5" />}
      </Button>

      {/* Video toggle */}
      <Button
        variant={!isVideoOn ? 'destructive' : 'secondary'}
        size="lg"
        onClick={onToggleVideo}
        className="h-12 w-12 rounded-full"
        title={isVideoOn ? 'Stop video' : 'Start video'}
      >
        {isVideoOn ? <Video className="h-5 w-5" /> : <VideoOff className="h-5 w-5" />}
      </Button>

      {/* Screen share toggle */}
      <Button
        variant={isScreenSharing ? 'default' : 'secondary'}
        size="lg"
        onClick={onToggleScreenShare}
        className="h-12 w-12 rounded-full"
        title={isScreenSharing ? 'Stop sharing' : 'Share screen'}
      >
        {isScreenSharing ? (
          <MonitorOff className="h-5 w-5" />
        ) : (
          <Monitor className="h-5 w-5" />
        )}
      </Button>

      {/* Settings */}
      {onOpenSettings && (
        <Button
          variant="secondary"
          size="lg"
          onClick={onOpenSettings}
          className="h-12 w-12 rounded-full"
          title="Settings"
        >
          <Settings className="h-5 w-5" />
        </Button>
      )}

      {/* More options */}
      <DropdownMenu>
        <DropdownMenuTrigger asChild>
          <Button
            variant="secondary"
            size="lg"
            className="h-12 w-12 rounded-full"
            title="More options"
          >
            <MoreVertical className="h-5 w-5" />
          </Button>
        </DropdownMenuTrigger>
        <DropdownMenuContent align="end" className="w-48">
          <DropdownMenuItem onClick={onOpenSettings}>
            <Settings className="mr-2 h-4 w-4" />
            Settings
          </DropdownMenuItem>
        </DropdownMenuContent>
      </DropdownMenu>

      {/* Leave button */}
      <Button
        variant="destructive"
        size="lg"
        onClick={onLeave}
        className="h-12 px-6"
        title="Leave meeting"
      >
        <PhoneOff className="mr-2 h-5 w-5" />
        Leave
      </Button>
    </div>
  );
};
