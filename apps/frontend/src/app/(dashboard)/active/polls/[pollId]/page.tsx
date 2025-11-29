'use client';

import React, { useEffect, useState } from 'react';
import { useParams, useRouter } from 'next/navigation';
import { ArrowLeft } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { PollResponse } from '@/components/active/poll-response';
import { PollResults } from '@/components/active/poll-results';
import { Poll, PollResult, PollResponseRequest } from '@/types/active';
import { pollApi } from '@/lib/api/active';

export default function PollDetailPage() {
  const params = useParams();
  const router = useRouter();
  const pollId = Number(params.pollId);
  const [poll, setPoll] = useState<Poll | null>(null);
  const [result, setResult] = useState<PollResult | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const [pollData, resultData] = await Promise.all([
          pollApi.getPoll(pollId),
          pollApi.getPollResults(pollId),
        ]);
        setPoll(pollData);
        setResult(resultData);
      } catch (error) {
        console.error('Failed to fetch poll:', error);
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, [pollId]);

  const handleSubmitResponse = async (response: PollResponseRequest) => {
    try {
      await pollApi.respondToPoll(pollId, response);
      const updatedResult = await pollApi.getPollResults(pollId);
      setResult(updatedResult);
    } catch (error) {
      console.error('Failed to submit response:', error);
    }
  };

  if (loading || !poll) {
    return <div className="container mx-auto py-6">Loading...</div>;
  }

  return (
    <div className="container mx-auto py-6 space-y-6 max-w-4xl">
      <div className="flex items-center gap-4">
        <Button variant="ghost" size="icon" onClick={() => router.back()}>
          <ArrowLeft className="h-5 w-5" />
        </Button>
        <div>
          <h1 className="text-3xl font-bold text-gray-900">{poll.question}</h1>
          <p className="text-gray-600 mt-1">{poll.description}</p>
        </div>
      </div>

      <Tabs defaultValue="respond">
        <TabsList>
          <TabsTrigger value="respond">Respond</TabsTrigger>
          <TabsTrigger value="results">Results</TabsTrigger>
        </TabsList>

        <TabsContent value="respond" className="mt-6">
          <PollResponse poll={poll} onSubmit={handleSubmitResponse} />
        </TabsContent>

        <TabsContent value="results" className="mt-6">
          {result && <PollResults result={result} />}
        </TabsContent>
      </Tabs>
    </div>
  );
}
