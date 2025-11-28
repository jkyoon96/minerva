/**
 * Media devices hook for accessing camera, microphone, and screen sharing
 */

import { useState, useEffect, useCallback } from 'react';
import { MediaDevice, MediaSettings } from '@/types/seminar';

export const useMediaDevices = () => {
  const [devices, setDevices] = useState<MediaDevice[]>([]);
  const [audioInputs, setAudioInputs] = useState<MediaDevice[]>([]);
  const [videoInputs, setVideoInputs] = useState<MediaDevice[]>([]);
  const [audioOutputs, setAudioOutputs] = useState<MediaDevice[]>([]);
  const [permissionGranted, setPermissionGranted] = useState(false);
  const [error, setError] = useState<string | null>(null);

  // Enumerate devices
  const enumerateDevices = useCallback(async () => {
    try {
      const deviceList = await navigator.mediaDevices.enumerateDevices();
      const formattedDevices: MediaDevice[] = deviceList.map((device) => ({
        deviceId: device.deviceId,
        label: device.label || `${device.kind} (${device.deviceId.slice(0, 8)})`,
        kind: device.kind as 'audioinput' | 'videoinput' | 'audiooutput',
      }));

      setDevices(formattedDevices);

      // Categorize devices
      setAudioInputs(formattedDevices.filter((d) => d.kind === 'audioinput'));
      setVideoInputs(formattedDevices.filter((d) => d.kind === 'videoinput'));
      setAudioOutputs(formattedDevices.filter((d) => d.kind === 'audiooutput'));

      setPermissionGranted(true);
      setError(null);
    } catch (err) {
      console.error('Failed to enumerate devices:', err);
      setError('Failed to access media devices');
      setPermissionGranted(false);
    }
  }, []);

  // Request permissions and enumerate devices
  const requestPermissions = useCallback(async () => {
    try {
      // Request both audio and video permissions
      const stream = await navigator.mediaDevices.getUserMedia({
        audio: true,
        video: true,
      });

      // Stop the stream immediately (we just needed permissions)
      stream.getTracks().forEach((track) => track.stop());

      // Now enumerate devices (labels will be available)
      await enumerateDevices();

      return true;
    } catch (err) {
      console.error('Permission denied:', err);
      setError('Permission to access camera/microphone was denied');
      setPermissionGranted(false);
      return false;
    }
  }, [enumerateDevices]);

  // Get user media stream
  const getUserMedia = useCallback(
    async (settings: MediaSettings): Promise<MediaStream | null> => {
      try {
        const constraints: MediaStreamConstraints = {
          audio: settings.audioEnabled
            ? settings.audioDeviceId
              ? { deviceId: { exact: settings.audioDeviceId } }
              : true
            : false,
          video: settings.videoEnabled
            ? settings.videoDeviceId
              ? {
                  deviceId: { exact: settings.videoDeviceId },
                  width: { ideal: 1280 },
                  height: { ideal: 720 },
                  frameRate: { ideal: 30 },
                }
              : {
                  width: { ideal: 1280 },
                  height: { ideal: 720 },
                  frameRate: { ideal: 30 },
                }
            : false,
        };

        const stream = await navigator.mediaDevices.getUserMedia(constraints);
        setError(null);
        return stream;
      } catch (err) {
        console.error('Failed to get user media:', err);
        setError('Failed to access camera/microphone');
        return null;
      }
    },
    [],
  );

  // Get display media (screen share)
  const getDisplayMedia = useCallback(async (): Promise<MediaStream | null> => {
    try {
      const stream = await navigator.mediaDevices.getDisplayMedia({
        video: {
          cursor: 'always',
          displaySurface: 'monitor',
        } as any,
        audio: false,
      });

      setError(null);
      return stream;
    } catch (err) {
      if ((err as Error).name === 'NotAllowedError') {
        console.log('User cancelled screen share');
      } else {
        console.error('Failed to get display media:', err);
        setError('Failed to start screen sharing');
      }
      return null;
    }
  }, []);

  // Toggle audio track
  const toggleAudio = useCallback((stream: MediaStream, enabled: boolean) => {
    stream.getAudioTracks().forEach((track) => {
      track.enabled = enabled;
    });
  }, []);

  // Toggle video track
  const toggleVideo = useCallback((stream: MediaStream, enabled: boolean) => {
    stream.getVideoTracks().forEach((track) => {
      track.enabled = enabled;
    });
  }, []);

  // Stop all tracks in a stream
  const stopStream = useCallback((stream: MediaStream) => {
    stream.getTracks().forEach((track) => track.stop());
  }, []);

  // Listen for device changes
  useEffect(() => {
    const handleDeviceChange = () => {
      enumerateDevices();
    };

    navigator.mediaDevices.addEventListener('devicechange', handleDeviceChange);

    // Initial enumeration
    enumerateDevices();

    return () => {
      navigator.mediaDevices.removeEventListener('devicechange', handleDeviceChange);
    };
  }, [enumerateDevices]);

  return {
    devices,
    audioInputs,
    videoInputs,
    audioOutputs,
    permissionGranted,
    error,
    requestPermissions,
    getUserMedia,
    getDisplayMedia,
    toggleAudio,
    toggleVideo,
    stopStream,
    enumerateDevices,
  };
};
