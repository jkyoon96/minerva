'use client';

import React from 'react';
import { CheckCircle, XCircle, Clock, AlertCircle } from 'lucide-react';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { TestCase, TestCaseStatus } from '@/types/assessment';
import { cn } from '@/lib/utils';

interface TestCaseListProps {
  testCases: TestCase[];
  results?: { testCaseId: number; status: TestCaseStatus; earnedPoints: number }[];
  className?: string;
}

const STATUS_CONFIG = {
  [TestCaseStatus.PASSED]: { icon: CheckCircle, color: 'text-green-600', bg: 'bg-green-50' },
  [TestCaseStatus.FAILED]: { icon: XCircle, color: 'text-red-600', bg: 'bg-red-50' },
  [TestCaseStatus.ERROR]: { icon: AlertCircle, color: 'text-orange-600', bg: 'bg-orange-50' },
  [TestCaseStatus.TIMEOUT]: { icon: Clock, color: 'text-gray-600', bg: 'bg-gray-50' },
};

export const TestCaseList: React.FC<TestCaseListProps> = ({ testCases, results, className }) => {
  return (
    <Card className={className}>
      <CardHeader>
        <CardTitle>Test Cases</CardTitle>
        <p className="text-sm text-gray-600">{testCases.length} test cases</p>
      </CardHeader>
      <CardContent className="space-y-3">
        {testCases.map((testCase) => {
          const result = results?.find((r) => r.testCaseId === testCase.id);
          const config = result ? STATUS_CONFIG[result.status] : null;
          const Icon = config?.icon;

          return (
            <div
              key={testCase.id}
              className={cn('p-4 rounded-lg border-2', config?.bg, 'border-gray-200')}
            >
              <div className="flex items-start justify-between mb-3">
                <div className="flex items-center gap-2">
                  {Icon && <Icon className={cn('h-5 w-5', config.color)} />}
                  <h4 className="font-semibold">{testCase.name}</h4>
                </div>
                <Badge variant={testCase.isHidden ? 'secondary' : 'outline'}>
                  {testCase.points} pts
                </Badge>
              </div>
              
              {!testCase.isHidden && (
                <div className="space-y-2 text-sm">
                  <div>
                    <p className="text-gray-600 font-medium">Input:</p>
                    <pre className="bg-gray-100 p-2 rounded mt-1 text-xs overflow-x-auto">
                      {testCase.input}
                    </pre>
                  </div>
                  <div>
                    <p className="text-gray-600 font-medium">Expected Output:</p>
                    <pre className="bg-gray-100 p-2 rounded mt-1 text-xs overflow-x-auto">
                      {testCase.expectedOutput}
                    </pre>
                  </div>
                </div>
              )}

              {result && (
                <div className="mt-3 pt-3 border-t">
                  <div className="flex items-center justify-between text-sm">
                    <span className={cn('font-medium', config.color)}>{result.status}</span>
                    <span>{result.earnedPoints}/{testCase.points} pts</span>
                  </div>
                </div>
              )}
            </div>
          );
        })}
      </CardContent>
    </Card>
  );
};
