'use client';

import { useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import { useRouter } from 'next/navigation';
import analyticsApi from '@/lib/api/analytics';
import { StudentReportCard } from '@/components/analytics/reports/student-report-card';
import { CourseReportCard } from '@/components/analytics/reports/course-report-card';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { Card, CardContent } from '@/components/ui/card';
import { FileText } from 'lucide-react';

export default function ReportsPage() {
  const router = useRouter();
  const [activeTab, setActiveTab] = useState('student');

  // Mock data - in production, fetch from API
  const { data: studentReports = [] } = useQuery({
    queryKey: ['analytics', 'reports', 'students'],
    queryFn: async () => {
      // Mock implementation - replace with actual API call
      return [];
    },
  });

  const { data: courseReports = [] } = useQuery({
    queryKey: ['analytics', 'reports', 'courses'],
    queryFn: async () => {
      // Mock implementation - replace with actual API call
      return [];
    },
  });

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-3xl font-bold">Learning Reports</h1>
        <p className="text-muted-foreground">
          Comprehensive analysis reports for students and courses
        </p>
      </div>

      <Tabs value={activeTab} onValueChange={setActiveTab}>
        <TabsList>
          <TabsTrigger value="student">Student Reports</TabsTrigger>
          <TabsTrigger value="course">Course Reports</TabsTrigger>
        </TabsList>

        <TabsContent value="student" className="mt-6 space-y-4">
          {studentReports.length > 0 ? (
            <div className="grid gap-4 md:grid-cols-2">
              {studentReports.map((report: any) => (
                <StudentReportCard
                  key={report.id}
                  report={report}
                  onViewDetails={() => router.push(`/analytics/reports/student/${report.studentId}`)}
                />
              ))}
            </div>
          ) : (
            <Card>
              <CardContent className="flex flex-col items-center justify-center py-12">
                <FileText className="mb-4 h-12 w-12 text-muted-foreground" />
                <h3 className="mb-2 text-lg font-semibold">No Student Reports</h3>
                <p className="text-center text-sm text-muted-foreground">
                  Student reports will appear here once the semester progresses
                </p>
              </CardContent>
            </Card>
          )}
        </TabsContent>

        <TabsContent value="course" className="mt-6 space-y-4">
          {courseReports.length > 0 ? (
            <div className="grid gap-4 md:grid-cols-2">
              {courseReports.map((report: any) => (
                <CourseReportCard
                  key={report.id}
                  report={report}
                  onViewDetails={() => router.push(`/analytics/reports/course/${report.courseId}`)}
                />
              ))}
            </div>
          ) : (
            <Card>
              <CardContent className="flex flex-col items-center justify-center py-12">
                <FileText className="mb-4 h-12 w-12 text-muted-foreground" />
                <h3 className="mb-2 text-lg font-semibold">No Course Reports</h3>
                <p className="text-center text-sm text-muted-foreground">
                  Course reports will appear here once data is available
                </p>
              </CardContent>
            </Card>
          )}
        </TabsContent>
      </Tabs>
    </div>
  );
}
