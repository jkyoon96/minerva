'use client';

/**
 * Device selector component for choosing camera and microphone
 */

import React, { useEffect } from 'react';
import { Camera, Mic, Volume2, RefreshCw } from 'lucide-react';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select';
import { Button } from '@/components/ui/button';
import { Label } from '@/components/ui/label';
import { useMediaDevices } from '@/hooks/useMediaDevices';
import { MediaSettings } from '@/types/seminar';
import { cn } from '@/lib/utils';

interface DeviceSelectorProps {
  settings: MediaSettings;
  onSettingsChange: (settings: Partial<MediaSettings>) => void;
  className?: string;
}

export const DeviceSelector: React.FC<DeviceSelectorProps> = ({
  settings,
  onSettingsChange,
  className,
}) => {
  const {
    audioInputs,
    videoInputs,
    audioOutputs,
    permissionGranted,
    requestPermissions,
    enumerateDevices,
  } = useMediaDevices();

  // Request permissions on mount
  useEffect(() => {
    if (!permissionGranted) {
      requestPermissions();
    }
  }, [permissionGranted, requestPermissions]);

  return (
    <div className={cn('space-y-4', className)}>
      {/* Camera */}
      <div className="space-y-2">
        <Label className="flex items-center gap-2 text-white">
          <Camera className="h-4 w-4" />
          Camera
        </Label>
        <Select
          value={settings.videoDeviceId || 'default'}
          onValueChange={(value) =>
            onSettingsChange({
              videoDeviceId: value === 'default' ? undefined : value,
            })
          }
          disabled={videoInputs.length === 0}
        >
          <SelectTrigger className="bg-gray-800 border-gray-700 text-white">
            <SelectValue placeholder="Select camera" />
          </SelectTrigger>
          <SelectContent>
            <SelectItem value="default">Default Camera</SelectItem>
            {videoInputs.map((device) => (
              <SelectItem key={device.deviceId} value={device.deviceId}>
                {device.label}
              </SelectItem>
            ))}
          </SelectContent>
        </Select>
      </div>

      {/* Microphone */}
      <div className="space-y-2">
        <Label className="flex items-center gap-2 text-white">
          <Mic className="h-4 w-4" />
          Microphone
        </Label>
        <Select
          value={settings.audioDeviceId || 'default'}
          onValueChange={(value) =>
            onSettingsChange({
              audioDeviceId: value === 'default' ? undefined : value,
            })
          }
          disabled={audioInputs.length === 0}
        >
          <SelectTrigger className="bg-gray-800 border-gray-700 text-white">
            <SelectValue placeholder="Select microphone" />
          </SelectTrigger>
          <SelectContent>
            <SelectItem value="default">Default Microphone</SelectItem>
            {audioInputs.map((device) => (
              <SelectItem key={device.deviceId} value={device.deviceId}>
                {device.label}
              </SelectItem>
            ))}
          </SelectContent>
        </Select>
      </div>

      {/* Speakers (if available) */}
      {audioOutputs.length > 0 && (
        <div className="space-y-2">
          <Label className="flex items-center gap-2 text-white">
            <Volume2 className="h-4 w-4" />
            Speakers
          </Label>
          <Select defaultValue="default" disabled={audioOutputs.length === 0}>
            <SelectTrigger className="bg-gray-800 border-gray-700 text-white">
              <SelectValue placeholder="Select speakers" />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value="default">Default Speakers</SelectItem>
              {audioOutputs.map((device) => (
                <SelectItem key={device.deviceId} value={device.deviceId}>
                  {device.label}
                </SelectItem>
              ))}
            </SelectContent>
          </Select>
        </div>
      )}

      {/* Refresh button */}
      <Button
        variant="outline"
        size="sm"
        onClick={enumerateDevices}
        className="w-full gap-2 border-gray-700 text-white hover:bg-gray-800"
      >
        <RefreshCw className="h-4 w-4" />
        Refresh Devices
      </Button>

      {!permissionGranted && (
        <div className="rounded-lg bg-yellow-500/10 border border-yellow-500/20 p-3">
          <p className="text-sm text-yellow-500">
            Please grant camera and microphone permissions to continue.
          </p>
          <Button
            variant="outline"
            size="sm"
            onClick={requestPermissions}
            className="mt-2 w-full border-yellow-500/20 text-yellow-500"
          >
            Grant Permissions
          </Button>
        </div>
      )}
    </div>
  );
};
