'use client';

import React, { useEffect } from 'react';
import { ParticipationDashboardComponent } from '@/components/assessment/participation-dashboard';
import { ParticipationChart } from '@/components/assessment/participation-chart';
import { EventHistory } from '@/components/assessment/event-history';
import { useAssessmentStore } from '@/stores/assessmentStore';
import { participationApi } from '@/lib/api/assessment';

export default function ParticipationPage() {
  const { participationDashboard, setParticipationDashboard } = useAssessmentStore();

  useEffect(() => {
    loadDashboard();
  }, []);

  const loadDashboard = async () => {
    try {
      const studentId = 1;
      const courseId = 1;
      const data = await participationApi.getDashboard(studentId, courseId);
      setParticipationDashboard(data);
    } catch (error) {
      console.error('Failed to load participation:', error);
    }
  };

  if (!participationDashboard) {
    return <div className="container mx-auto py-12 text-center">Loading...</div>;
  }

  return (
    <div className="container mx-auto py-6 space-y-6">
      <h1 className="text-3xl font-bold">My Participation</h1>
      
      <ParticipationDashboardComponent dashboard={participationDashboard} />

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <ParticipationChart eventSummary={participationDashboard.eventSummary} />
        <EventHistory events={participationDashboard.recentEvents} />
      </div>
    </div>
  );
}
