'use client';

/**
 * Grades overview page
 */

import React, { useState, useEffect } from 'react';
import { useRouter } from 'next/navigation';
import { FileText, Search, Filter } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { GradingResultCard } from '@/components/assessment/grading-result-card';
import { AnswerStatistics } from '@/components/assessment/answer-statistics';
import { useAssessmentStore } from '@/stores/assessmentStore';
import { autoGradingApi } from '@/lib/api/assessment';

export default function GradesPage() {
  const router = useRouter();
  const { autoGradingResults, setAutoGradingResults } = useAssessmentStore();
  const [searchQuery, setSearchQuery] = useState('');
  const [activeTab, setActiveTab] = useState<'results' | 'statistics'>('results');
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    loadGrades();
  }, []);

  const loadGrades = async () => {
    try {
      setIsLoading(true);
      // TODO: Get actual assignment ID from context/params
      const assignmentId = 1;
      const results = await autoGradingApi.getAssignmentResults(assignmentId);
      setAutoGradingResults(results);
    } catch (error) {
      console.error('Failed to load grades:', error);
    } finally {
      setIsLoading(false);
    }
  };

  const filteredResults = autoGradingResults.filter((result) =>
    result.submission.studentName.toLowerCase().includes(searchQuery.toLowerCase())
  );

  return (
    <div className="container mx-auto py-6 space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold text-gray-900">Grades</h1>
          <p className="text-gray-600 mt-1">View and manage assignment grades</p>
        </div>
        <Button onClick={() => router.push('/assessment/grades/export')}>
          <FileText className="h-4 w-4 mr-2" />
          Export Grades
        </Button>
      </div>

      <div className="flex gap-4">
        <div className="flex-1">
          <Input
            placeholder="Search by student name..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            className="w-full"
          />
        </div>
        <Button variant="outline">
          <Filter className="h-4 w-4 mr-2" />
          Filter
        </Button>
      </div>

      <Tabs value={activeTab} onValueChange={(v: any) => setActiveTab(v)}>
        <TabsList>
          <TabsTrigger value="results">Individual Results</TabsTrigger>
          <TabsTrigger value="statistics">Statistics</TabsTrigger>
        </TabsList>

        <TabsContent value="results" className="mt-6">
          {isLoading ? (
            <div className="text-center py-12">Loading...</div>
          ) : filteredResults.length === 0 ? (
            <div className="text-center py-12">
              <p className="text-gray-500">No results found</p>
            </div>
          ) : (
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
              {filteredResults.map((result) => (
                <GradingResultCard
                  key={result.submission.id}
                  result={result}
                  onClick={() => router.push(`/assessment/grades/${result.submission.id}`)}
                />
              ))}
            </div>
          )}
        </TabsContent>

        <TabsContent value="statistics" className="mt-6">
          <AnswerStatistics statistics={[]} />
        </TabsContent>
      </Tabs>
    </div>
  );
}
