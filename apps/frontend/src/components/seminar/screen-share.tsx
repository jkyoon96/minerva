'use client';

/**
 * Screen share component for displaying shared screen
 */

import React, { useEffect, useRef } from 'react';
import { Monitor, X } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { cn } from '@/lib/utils';

interface ScreenShareProps {
  stream: MediaStream | null;
  presenterName: string;
  onStop?: () => void;
  showPipVideo?: boolean;
  pipStream?: MediaStream | null;
  className?: string;
}

export const ScreenShare: React.FC<ScreenShareProps> = ({
  stream,
  presenterName,
  onStop,
  showPipVideo = false,
  pipStream,
  className,
}) => {
  const videoRef = useRef<HTMLVideoElement>(null);
  const pipVideoRef = useRef<HTMLVideoElement>(null);

  // Attach screen share stream
  useEffect(() => {
    if (videoRef.current && stream) {
      videoRef.current.srcObject = stream;
    }
  }, [stream]);

  // Attach PIP video stream
  useEffect(() => {
    if (pipVideoRef.current && pipStream) {
      pipVideoRef.current.srcObject = pipStream;
    }
  }, [pipStream]);

  if (!stream) {
    return null;
  }

  return (
    <div className={cn('relative h-full w-full bg-black', className)}>
      {/* Main screen share video */}
      <video
        ref={videoRef}
        autoPlay
        playsInline
        className="h-full w-full object-contain"
      />

      {/* Presenter info overlay */}
      <div className="absolute left-4 top-4 flex items-center gap-2 rounded-lg bg-black/70 px-3 py-2">
        <Monitor className="h-5 w-5 text-white" />
        <span className="text-sm font-medium text-white">
          {presenterName} is presenting
        </span>
      </div>

      {/* Stop sharing button (for presenter) */}
      {onStop && (
        <div className="absolute right-4 top-4">
          <Button
            variant="destructive"
            size="sm"
            onClick={onStop}
            className="gap-2"
          >
            <X className="h-4 w-4" />
            Stop Sharing
          </Button>
        </div>
      )}

      {/* Picture-in-Picture video */}
      {showPipVideo && pipStream && (
        <div className="absolute bottom-4 right-4 h-40 w-56 overflow-hidden rounded-lg border-2 border-white/50 shadow-lg">
          <video
            ref={pipVideoRef}
            autoPlay
            playsInline
            muted
            className="h-full w-full object-cover"
          />
        </div>
      )}
    </div>
  );
};
