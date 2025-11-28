'use client';

/**
 * Waiting room component for pre-session setup
 */

import React, { useRef, useEffect } from 'react';
import { Video, VideoOff, Mic, MicOff, Settings, LogIn } from 'lucide-react';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Switch } from '@/components/ui/switch';
import { Label } from '@/components/ui/label';
import { DeviceSelector } from './device-selector';
import { MediaSettings } from '@/types/seminar';
import { cn } from '@/lib/utils';

interface WaitingRoomProps {
  stream: MediaStream | null;
  settings: MediaSettings;
  roomName?: string;
  onSettingsChange: (settings: Partial<MediaSettings>) => void;
  onJoin: () => void;
  isJoining?: boolean;
  className?: string;
}

export const WaitingRoom: React.FC<WaitingRoomProps> = ({
  stream,
  settings,
  roomName = 'Seminar Room',
  onSettingsChange,
  onJoin,
  isJoining = false,
  className,
}) => {
  const videoRef = useRef<HTMLVideoElement>(null);
  const [showSettings, setShowSettings] = React.useState(false);

  // Attach stream to video preview
  useEffect(() => {
    if (videoRef.current && stream) {
      videoRef.current.srcObject = stream;
    }
  }, [stream]);

  return (
    <div
      className={cn(
        'flex min-h-screen items-center justify-center bg-gray-950 p-4',
        className,
      )}
    >
      <Card className="w-full max-w-4xl bg-gray-900 border-gray-800">
        <CardHeader>
          <CardTitle className="text-2xl text-white">
            Ready to join {roomName}?
          </CardTitle>
        </CardHeader>
        <CardContent className="space-y-6">
          <div className="grid gap-6 md:grid-cols-2">
            {/* Video preview */}
            <div className="space-y-4">
              <div className="relative aspect-video overflow-hidden rounded-lg bg-gray-950">
                {settings.videoEnabled && stream ? (
                  <video
                    ref={videoRef}
                    autoPlay
                    playsInline
                    muted
                    className="h-full w-full object-cover"
                  />
                ) : (
                  <div className="flex h-full items-center justify-center">
                    <VideoOff className="h-16 w-16 text-gray-600" />
                  </div>
                )}

                {/* Preview controls overlay */}
                <div className="absolute bottom-4 left-1/2 flex -translate-x-1/2 gap-2">
                  <Button
                    variant={settings.audioEnabled ? 'secondary' : 'destructive'}
                    size="icon"
                    onClick={() =>
                      onSettingsChange({ audioEnabled: !settings.audioEnabled })
                    }
                    title={settings.audioEnabled ? 'Mute' : 'Unmute'}
                  >
                    {settings.audioEnabled ? (
                      <Mic className="h-5 w-5" />
                    ) : (
                      <MicOff className="h-5 w-5" />
                    )}
                  </Button>

                  <Button
                    variant={settings.videoEnabled ? 'secondary' : 'destructive'}
                    size="icon"
                    onClick={() =>
                      onSettingsChange({ videoEnabled: !settings.videoEnabled })
                    }
                    title={settings.videoEnabled ? 'Stop video' : 'Start video'}
                  >
                    {settings.videoEnabled ? (
                      <Video className="h-5 w-5" />
                    ) : (
                      <VideoOff className="h-5 w-5" />
                    )}
                  </Button>
                </div>
              </div>

              {/* Quick toggles */}
              <div className="space-y-3">
                <div className="flex items-center justify-between rounded-lg bg-gray-800 p-3">
                  <Label htmlFor="audio-toggle" className="text-white">
                    Microphone
                  </Label>
                  <Switch
                    id="audio-toggle"
                    checked={settings.audioEnabled}
                    onCheckedChange={(checked) =>
                      onSettingsChange({ audioEnabled: checked })
                    }
                  />
                </div>

                <div className="flex items-center justify-between rounded-lg bg-gray-800 p-3">
                  <Label htmlFor="video-toggle" className="text-white">
                    Camera
                  </Label>
                  <Switch
                    id="video-toggle"
                    checked={settings.videoEnabled}
                    onCheckedChange={(checked) =>
                      onSettingsChange({ videoEnabled: checked })
                    }
                  />
                </div>
              </div>
            </div>

            {/* Settings panel */}
            <div className="space-y-4">
              <div className="flex items-center justify-between">
                <h3 className="text-lg font-semibold text-white">Settings</h3>
                <Button
                  variant="ghost"
                  size="sm"
                  onClick={() => setShowSettings(!showSettings)}
                  className="gap-2"
                >
                  <Settings className="h-4 w-4" />
                  {showSettings ? 'Hide' : 'Show'}
                </Button>
              </div>

              {showSettings && (
                <DeviceSelector
                  settings={settings}
                  onSettingsChange={onSettingsChange}
                />
              )}

              {!showSettings && (
                <div className="rounded-lg bg-gray-800 p-4">
                  <p className="text-sm text-gray-400">
                    Click "Show" to configure your camera and microphone settings.
                  </p>
                </div>
              )}
            </div>
          </div>

          {/* Join button */}
          <div className="flex justify-end pt-4">
            <Button
              size="lg"
              onClick={onJoin}
              disabled={isJoining}
              className="gap-2 px-8"
            >
              <LogIn className="h-5 w-5" />
              {isJoining ? 'Joining...' : 'Join Now'}
            </Button>
          </div>
        </CardContent>
      </Card>
    </div>
  );
};
