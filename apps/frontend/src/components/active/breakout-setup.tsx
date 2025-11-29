'use client';

import React, { useState } from 'react';
import { Users, Settings } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { AssignmentMethod, BreakoutSessionCreateRequest } from '@/types/active';

interface BreakoutSetupProps {
  sessionId: number;
  totalParticipants: number;
  onSubmit: (data: BreakoutSessionCreateRequest) => void;
  onCancel?: () => void;
}

export const BreakoutSetup: React.FC<BreakoutSetupProps> = ({
  sessionId,
  totalParticipants,
  onSubmit,
  onCancel,
}) => {
  const [totalRooms, setTotalRooms] = useState(2);
  const [assignmentMethod, setAssignmentMethod] = useState<AssignmentMethod>(AssignmentMethod.RANDOM);
  const [durationMinutes, setDurationMinutes] = useState(10);
  const [roomNames, setRoomNames] = useState<string[]>(
    Array(2).fill('').map((_, i) => `Room ${i + 1}`)
  );

  const participantsPerRoom = Math.ceil(totalParticipants / totalRooms);

  const handleRoomCountChange = (count: number) => {
    setTotalRooms(count);
    setRoomNames(Array(count).fill('').map((_, i) => roomNames[i] || `Room ${i + 1}`));
  };

  const handleRoomNameChange = (index: number, name: string) => {
    const newNames = [...roomNames];
    newNames[index] = name;
    setRoomNames(newNames);
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();

    onSubmit({
      sessionId,
      totalRooms,
      assignmentMethod,
      durationMinutes,
      roomNames: roomNames.filter(n => n.trim()),
    });
  };

  return (
    <form onSubmit={handleSubmit} className="space-y-6">
      <Card>
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            <Settings className="h-5 w-5" />
            Breakout Room Configuration
          </CardTitle>
        </CardHeader>
        <CardContent className="space-y-4">
          <div className="grid grid-cols-2 gap-4">
            <div className="space-y-2">
              <Label>Number of Rooms</Label>
              <Input
                type="number"
                min={2}
                max={20}
                value={totalRooms}
                onChange={(e) => handleRoomCountChange(Number(e.target.value))}
              />
              <p className="text-xs text-gray-500">
                ~{participantsPerRoom} participants per room
              </p>
            </div>

            <div className="space-y-2">
              <Label>Duration (minutes)</Label>
              <Input
                type="number"
                min={5}
                max={120}
                value={durationMinutes}
                onChange={(e) => setDurationMinutes(Number(e.target.value))}
              />
            </div>
          </div>

          <div className="space-y-2">
            <Label>Assignment Method</Label>
            <Select
              value={assignmentMethod}
              onValueChange={(v) => setAssignmentMethod(v as AssignmentMethod)}
            >
              <SelectTrigger>
                <SelectValue />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value={AssignmentMethod.MANUAL}>Manual Assignment</SelectItem>
                <SelectItem value={AssignmentMethod.RANDOM}>Random Assignment</SelectItem>
                <SelectItem value={AssignmentMethod.BALANCED}>Balanced Assignment</SelectItem>
              </SelectContent>
            </Select>
            <p className="text-xs text-gray-500">
              {assignmentMethod === AssignmentMethod.MANUAL && 'Drag and drop participants to rooms'}
              {assignmentMethod === AssignmentMethod.RANDOM && 'Randomly distribute participants'}
              {assignmentMethod === AssignmentMethod.BALANCED && 'Balance by participation level'}
            </p>
          </div>

          <div className="space-y-2">
            <Label>Room Names</Label>
            <div className="space-y-2">
              {roomNames.map((name, index) => (
                <Input
                  key={index}
                  placeholder={`Room ${index + 1}`}
                  value={name}
                  onChange={(e) => handleRoomNameChange(index, e.target.value)}
                />
              ))}
            </div>
          </div>

          <div className="flex items-center gap-2 p-3 bg-blue-50 rounded-lg">
            <Users className="h-5 w-5 text-blue-600" />
            <span className="text-sm text-blue-900">
              {totalParticipants} participants will be assigned to {totalRooms} rooms
            </span>
          </div>
        </CardContent>
      </Card>

      <div className="flex justify-end gap-2">
        {onCancel && (
          <Button type="button" variant="outline" onClick={onCancel}>
            Cancel
          </Button>
        )}
        <Button type="submit">
          Create Breakout Rooms
        </Button>
      </div>
    </form>
  );
};
