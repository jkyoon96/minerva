'use client';

import React, { useEffect } from 'react';
import { FeedbackCard } from '@/components/assessment/feedback-card';
import { ResourceRecommendation } from '@/components/assessment/resource-recommendation';
import { ImprovementTips } from '@/components/assessment/improvement-tips';
import { useAssessmentStore } from '@/stores/assessmentStore';
import { feedbackApi } from '@/lib/api/assessment';

export default function FeedbackPage() {
  const { feedbacks, setFeedbacks } = useAssessmentStore();

  useEffect(() => {
    loadFeedback();
  }, []);

  const loadFeedback = async () => {
    try {
      const studentId = 1; // TODO: Get from auth context
      const courseId = 1;
      const data = await feedbackApi.getStudentFeedback(studentId, courseId);
      setFeedbacks(data);
    } catch (error) {
      console.error('Failed to load feedback:', error);
    }
  };

  return (
    <div className="container mx-auto py-6 space-y-6">
      <h1 className="text-3xl font-bold">My Feedback</h1>
      
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <div className="lg:col-span-2 space-y-6">
          {feedbacks.map((feedback) => (
            <FeedbackCard key={feedback.id} feedback={feedback} />
          ))}
        </div>
        <div className="lg:col-span-1 space-y-6">
          {feedbacks[0]?.resources && <ResourceRecommendation resources={feedbacks[0].resources} />}
          <ImprovementTips suggestions={[]} />
        </div>
      </div>
    </div>
  );
}
