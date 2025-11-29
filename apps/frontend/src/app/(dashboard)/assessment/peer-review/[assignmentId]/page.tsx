'use client';

import React, { useEffect } from 'react';
import { useParams } from 'next/navigation';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { PeerReviewForm } from '@/components/assessment/peer-review-form';
import { ReviewsReceived } from '@/components/assessment/reviews-received';
import { ReviewsGiven } from '@/components/assessment/reviews-given';
import { PeerResultSummary } from '@/components/assessment/peer-result-summary';
import { useAssessmentStore } from '@/stores/assessmentStore';
import { peerReviewApi } from '@/lib/api/assessment';

export default function PeerReviewPage() {
  const params = useParams();
  const assignmentId = parseInt(params.assignmentId as string);
  const { peerReviewRubric, peerReviewResults, setPeerReviewRubric, setPeerReviewResults } = useAssessmentStore();

  useEffect(() => {
    loadPeerReview();
  }, [assignmentId]);

  const loadPeerReview = async () => {
    try {
      const [rubric, results] = await Promise.all([
        peerReviewApi.getRubric(assignmentId),
        peerReviewApi.getResults(1), // TODO: Get submission ID
      ]);
      setPeerReviewRubric(rubric);
      setPeerReviewResults(results);
    } catch (error) {
      console.error('Failed to load peer review:', error);
    }
  };

  const handleSubmitReview = async (scores: any[], comment: string) => {
    try {
      await peerReviewApi.submitReview({
        assignmentId,
        submissionId: 1,
        rubricScores: scores,
        overallComment: comment,
      });
    } catch (error) {
      console.error('Failed to submit review:', error);
    }
  };

  return (
    <div className="container mx-auto py-6 space-y-6">
      <h1 className="text-3xl font-bold">Peer Review</h1>

      <Tabs defaultValue="give">
        <TabsList>
          <TabsTrigger value="give">Give Review</TabsTrigger>
          <TabsTrigger value="received">Reviews Received</TabsTrigger>
          <TabsTrigger value="given">Reviews Given</TabsTrigger>
        </TabsList>

        <TabsContent value="give" className="mt-6">
          {peerReviewRubric && <PeerReviewForm rubric={peerReviewRubric} onSubmit={handleSubmitReview} />}
        </TabsContent>

        <TabsContent value="received" className="mt-6">
          <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
            <div className="lg:col-span-2">
              {peerReviewResults && <ReviewsReceived reviews={peerReviewResults.reviewsReceived} />}
            </div>
            <div className="lg:col-span-1">
              {peerReviewResults && <PeerResultSummary result={peerReviewResults} />}
            </div>
          </div>
        </TabsContent>

        <TabsContent value="given" className="mt-6">
          {peerReviewResults && <ReviewsGiven reviews={peerReviewResults.reviewsGiven} />}
        </TabsContent>
      </Tabs>
    </div>
  );
}
