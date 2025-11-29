'use client';

import React from 'react';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { CheckCircle, Clock } from 'lucide-react';

export const ReviewsGiven = ({ reviews }: any) => (
  <Card>
    <CardHeader>
      <CardTitle>Reviews Given</CardTitle>
    </CardHeader>
    <CardContent className="space-y-3">
      {reviews.map((review: any, i: number) => (
        <div key={i} className="p-4 border rounded-lg">
          <div className="flex items-center justify-between mb-2">
            <span className="font-medium">{review.authorName || 'Anonymous Submission'}</span>
            <Badge variant={review.status === 'COMPLETED' ? 'default' : 'secondary'}>
              {review.status === 'COMPLETED' ? <CheckCircle className="h-3 w-3 mr-1" /> : <Clock className="h-3 w-3 mr-1" />}
              {review.status}
            </Badge>
          </div>
          {review.submittedAt && (
            <p className="text-xs text-gray-500">Submitted {new Date(review.submittedAt).toLocaleDateString()}</p>
          )}
        </div>
      ))}
    </CardContent>
  </Card>
);
