'use client';

import React, { useState } from 'react';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Textarea } from '@/components/ui/textarea';
import { Label } from '@/components/ui/label';
import { Slider } from '@/components/ui/slider';
import { PeerReviewRubric } from '@/types/assessment';

interface PeerReviewFormProps {
  rubric: PeerReviewRubric;
  onSubmit: (scores: any[], comment: string) => void;
}

export const PeerReviewForm: React.FC<PeerReviewFormProps> = ({ rubric, onSubmit }) => {
  const [scores, setScores] = useState<Record<number, { score: number; comment: string }>>({});
  const [overallComment, setOverallComment] = useState('');

  const handleSubmit = () => {
    const rubricScores = Object.entries(scores).map(([criteriaId, data]) => ({
      criteriaId: parseInt(criteriaId),
      score: data.score,
      comment: data.comment,
    }));
    onSubmit(rubricScores, overallComment);
  };

  return (
    <Card>
      <CardHeader>
        <CardTitle>Peer Review Form</CardTitle>
      </CardHeader>
      <CardContent className="space-y-6">
        {rubric.criteria.map((criteria) => (
          <div key={criteria.id} className="space-y-3 p-4 border rounded-lg">
            <div>
              <Label className="text-base font-semibold">{criteria.name}</Label>
              <p className="text-sm text-gray-600 mt-1">{criteria.description}</p>
            </div>
            
            <div className="space-y-2">
              <Label>Score: {scores[criteria.id]?.score || 0} / {criteria.maxScore}</Label>
              <Slider
                value={[scores[criteria.id]?.score || 0]}
                onValueChange={([value]) => setScores({ ...scores, [criteria.id]: { ...scores[criteria.id], score: value }})}
                max={criteria.maxScore}
                step={0.5}
              />
            </div>

            <div>
              <Label>Comment (optional)</Label>
              <Textarea
                value={scores[criteria.id]?.comment || ''}
                onChange={(e) => setScores({ ...scores, [criteria.id]: { ...scores[criteria.id], comment: e.target.value }})}
                rows={2}
              />
            </div>
          </div>
        ))}

        <div>
          <Label>Overall Comment</Label>
          <Textarea value={overallComment} onChange={(e) => setOverallComment(e.target.value)} rows={4} />
        </div>

        <Button onClick={handleSubmit} className="w-full">Submit Review</Button>
      </CardContent>
    </Card>
  );
};
