'use client';

/**
 * Polls list page
 */

import React, { useState } from 'react';
import { useRouter } from 'next/navigation';
import { Plus, Filter } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { PollCard } from '@/components/active/poll-card';
import { useActiveStore } from '@/stores/activeStore';
import { PollStatus } from '@/types/active';
import { pollApi } from '@/lib/api/active';

export default function PollsPage() {
  const router = useRouter();
  const { polls } = useActiveStore();
  const [searchQuery, setSearchQuery] = useState('');
  const [activeTab, setActiveTab] = useState<'all' | 'active' | 'ended'>('all');

  const filteredPolls = polls.filter((poll) => {
    const matchesSearch = poll.question.toLowerCase().includes(searchQuery.toLowerCase());
    const matchesTab =
      activeTab === 'all' ||
      (activeTab === 'active' && poll.status === PollStatus.ACTIVE) ||
      (activeTab === 'ended' && poll.status === PollStatus.ENDED);
    return matchesSearch && matchesTab;
  });

  const handleCreatePoll = () => {
    router.push('/active/polls/new');
  };

  const handleViewPoll = (pollId: number) => {
    router.push(`/active/polls/${pollId}`);
  };

  const handleStartPoll = async (pollId: number) => {
    try {
      await pollApi.startPoll(pollId);
    } catch (error) {
      console.error('Failed to start poll:', error);
    }
  };

  const handleEndPoll = async (pollId: number) => {
    try {
      await pollApi.endPoll(pollId);
    } catch (error) {
      console.error('Failed to end poll:', error);
    }
  };

  return (
    <div className="container mx-auto py-6 space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold text-gray-900">Polls & Voting</h1>
          <p className="text-gray-600 mt-1">Create and manage interactive polls</p>
        </div>
        <Button onClick={handleCreatePoll}>
          <Plus className="h-4 w-4 mr-2" />
          Create Poll
        </Button>
      </div>

      <div className="flex gap-4">
        <div className="flex-1">
          <Input
            placeholder="Search polls..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
          />
        </div>
        <Button variant="outline">
          <Filter className="h-4 w-4 mr-2" />
          Filter
        </Button>
      </div>

      <Tabs value={activeTab} onValueChange={(v: any) => setActiveTab(v)}>
        <TabsList>
          <TabsTrigger value="all">All Polls</TabsTrigger>
          <TabsTrigger value="active">Active</TabsTrigger>
          <TabsTrigger value="ended">Ended</TabsTrigger>
        </TabsList>

        <TabsContent value={activeTab} className="mt-6">
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {filteredPolls.map((poll) => (
              <PollCard
                key={poll.id}
                poll={poll}
                onView={() => handleViewPoll(poll.id)}
                onStart={() => handleStartPoll(poll.id)}
                onEnd={() => handleEndPoll(poll.id)}
                isHost
              />
            ))}
          </div>

          {filteredPolls.length === 0 && (
            <div className="text-center py-12">
              <p className="text-gray-500">No polls found</p>
              <Button variant="outline" className="mt-4" onClick={handleCreatePoll}>
                Create your first poll
              </Button>
            </div>
          )}
        </TabsContent>
      </Tabs>
    </div>
  );
}
