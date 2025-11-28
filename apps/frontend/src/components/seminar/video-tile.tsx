'use client';

/**
 * Video tile component for displaying individual participant video
 */

import React, { useEffect, useRef, useState } from 'react';
import { Mic, MicOff, Video, VideoOff, Hand, UserCircle } from 'lucide-react';
import { cn } from '@/lib/utils';
import { Participant } from '@/types/seminar';

interface VideoTileProps {
  participant: Participant;
  stream: MediaStream | null;
  isSpeaking?: boolean;
  isLocal?: boolean;
  className?: string;
}

export const VideoTile: React.FC<VideoTileProps> = ({
  participant,
  stream,
  isSpeaking = false,
  isLocal = false,
  className,
}) => {
  const videoRef = useRef<HTMLVideoElement>(null);
  const [hasVideo, setHasVideo] = useState(false);

  // Attach stream to video element
  useEffect(() => {
    if (videoRef.current && stream) {
      videoRef.current.srcObject = stream;

      // Check if stream has video tracks
      const videoTracks = stream.getVideoTracks();
      setHasVideo(videoTracks.length > 0 && videoTracks[0].enabled);

      // Listen for track enable/disable
      const handleTrackEnded = () => {
        setHasVideo(false);
      };

      videoTracks.forEach((track) => {
        track.addEventListener('ended', handleTrackEnded);
      });

      return () => {
        videoTracks.forEach((track) => {
          track.removeEventListener('ended', handleTrackEnded);
        });
      };
    }
  }, [stream]);

  // Update hasVideo when participant.isVideoOn changes
  useEffect(() => {
    setHasVideo(participant.isVideoOn && stream !== null);
  }, [participant.isVideoOn, stream]);

  return (
    <div
      className={cn(
        'relative aspect-video overflow-hidden rounded-lg bg-gray-900',
        isSpeaking && 'ring-4 ring-blue-500',
        className,
      )}
    >
      {/* Video element */}
      {hasVideo && participant.isVideoOn ? (
        <video
          ref={videoRef}
          autoPlay
          playsInline
          muted={isLocal}
          className="h-full w-full object-cover"
        />
      ) : (
        /* Avatar placeholder when video is off */
        <div className="flex h-full w-full items-center justify-center bg-gray-800">
          <UserCircle className="h-20 w-20 text-gray-600" />
        </div>
      )}

      {/* Participant name overlay */}
      <div className="absolute bottom-0 left-0 right-0 bg-gradient-to-t from-black/70 to-transparent p-3">
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-2">
            <span className="text-sm font-medium text-white">
              {participant.userName}
              {isLocal && ' (You)'}
            </span>
          </div>

          {/* Status indicators */}
          <div className="flex items-center gap-1">
            {/* Hand raised indicator */}
            {participant.isHandRaised && (
              <div className="rounded-full bg-yellow-500 p-1">
                <Hand className="h-4 w-4 text-white" />
              </div>
            )}

            {/* Microphone status */}
            {participant.isMuted ? (
              <div className="rounded-full bg-red-500 p-1">
                <MicOff className="h-4 w-4 text-white" />
              </div>
            ) : (
              <div className="rounded-full bg-green-500 p-1">
                <Mic className="h-4 w-4 text-white" />
              </div>
            )}

            {/* Video status */}
            {!participant.isVideoOn && (
              <div className="rounded-full bg-gray-700 p-1">
                <VideoOff className="h-4 w-4 text-white" />
              </div>
            )}
          </div>
        </div>
      </div>

      {/* Screen sharing indicator */}
      {participant.isScreenSharing && (
        <div className="absolute right-2 top-2 rounded bg-blue-600 px-2 py-1 text-xs font-semibold text-white">
          Presenting
        </div>
      )}
    </div>
  );
};
