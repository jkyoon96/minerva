'use client';

import React from 'react';
import { useParams } from 'next/navigation';
import { Users, MessageSquare } from 'lucide-react';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';

export default function BreakoutRoomPage() {
  const params = useParams();
  const roomId = params.roomId;

  return (
    <div className="container mx-auto py-6 space-y-6">
      <div>
        <h1 className="text-3xl font-bold text-gray-900">Breakout Room {roomId}</h1>
        <p className="text-gray-600 mt-1">Collaborate with your team</p>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <div className="lg:col-span-2">
          <Card>
            <CardHeader>
              <CardTitle>Discussion Area</CardTitle>
            </CardHeader>
            <CardContent className="h-96 flex items-center justify-center bg-gray-50">
              <p className="text-gray-500">Video/Collaboration area</p>
            </CardContent>
          </Card>
        </div>

        <div className="space-y-4">
          <Card>
            <CardHeader>
              <CardTitle className="flex items-center gap-2">
                <Users className="h-5 w-5" />
                Participants
              </CardTitle>
            </CardHeader>
            <CardContent>
              <p className="text-sm text-gray-500">No participants yet</p>
            </CardContent>
          </Card>

          <Card>
            <CardHeader>
              <CardTitle className="flex items-center gap-2">
                <MessageSquare className="h-5 w-5" />
                Chat
              </CardTitle>
            </CardHeader>
            <CardContent>
              <p className="text-sm text-gray-500">No messages</p>
            </CardContent>
          </Card>
        </div>
      </div>

      <div className="flex justify-center">
        <Button variant="outline" size="lg">
          Return to Main Room
        </Button>
      </div>
    </div>
  );
}
