'use client';

import React from 'react';
import { CheckCircle, XCircle, AlertCircle, Clock, Zap } from 'lucide-react';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { Progress } from '@/components/ui/progress';
import { CodeExecutionResult, TestCaseStatus } from '@/types/assessment';
import { cn } from '@/lib/utils';
import { format } from 'date-fns';

interface ExecutionResultProps {
  result: CodeExecutionResult;
  className?: string;
}

export const ExecutionResult: React.FC<ExecutionResultProps> = ({ result, className }) => {
  const passRate = (result.passedTests / result.totalTests) * 100;
  const isSuccess = result.status === 'SUCCESS';

  return (
    <Card className={className}>
      <CardHeader>
        <div className="flex items-center justify-between">
          <CardTitle className="flex items-center gap-2">
            <Zap className="h-5 w-5 text-yellow-500" />
            Execution Results
          </CardTitle>
          <Badge className={cn(isSuccess ? 'bg-green-500' : 'bg-red-500', 'text-white')}>
            {result.status}
          </Badge>
        </div>
        <p className="text-sm text-gray-600">
          Executed {format(new Date(result.executedAt), 'MMM d, HH:mm:ss')}
        </p>
      </CardHeader>

      <CardContent className="space-y-4">
        <div className="grid grid-cols-3 gap-4">
          <div className="text-center p-3 bg-green-50 rounded-lg">
            <CheckCircle className="h-6 w-6 text-green-600 mx-auto mb-1" />
            <p className="text-2xl font-bold text-green-600">{result.passedTests}</p>
            <p className="text-xs text-gray-600">Passed</p>
          </div>
          <div className="text-center p-3 bg-red-50 rounded-lg">
            <XCircle className="h-6 w-6 text-red-600 mx-auto mb-1" />
            <p className="text-2xl font-bold text-red-600">{result.failedTests}</p>
            <p className="text-xs text-gray-600">Failed</p>
          </div>
          <div className="text-center p-3 bg-blue-50 rounded-lg">
            <div className="text-2xl font-bold text-blue-600">{passRate.toFixed(0)}%</div>
            <p className="text-xs text-gray-600">Pass Rate</p>
          </div>
        </div>

        <div>
          <div className="flex justify-between text-sm mb-2">
            <span className="text-gray-600">Overall Progress</span>
            <span className="font-medium">{result.passedTests}/{result.totalTests}</span>
          </div>
          <Progress value={passRate} className="h-2" />
        </div>

        <div className="p-4 bg-gray-50 rounded-lg">
          <div className="flex justify-between text-sm">
            <span className="text-gray-600">Final Score</span>
            <span className="text-lg font-bold">{result.score}/{result.totalPoints} pts</span>
          </div>
        </div>

        {result.compilationError && (
          <div className="p-3 bg-red-50 border border-red-200 rounded-lg">
            <div className="flex items-start gap-2">
              <AlertCircle className="h-5 w-5 text-red-600 flex-shrink-0 mt-0.5" />
              <div className="flex-1">
                <p className="font-medium text-red-900 mb-1">Compilation Error</p>
                <pre className="text-xs text-red-700 overflow-x-auto">{result.compilationError}</pre>
              </div>
            </div>
          </div>
        )}

        {result.testResults && result.testResults.length > 0 && (
          <div className="space-y-2">
            <h4 className="text-sm font-semibold">Test Results</h4>
            {result.testResults.map((test) => (
              <div key={test.testCaseId} className="p-3 border rounded-lg">
                <div className="flex items-center justify-between mb-2">
                  <div className="flex items-center gap-2">
                    {test.status === TestCaseStatus.PASSED ? (
                      <CheckCircle className="h-4 w-4 text-green-600" />
                    ) : (
                      <XCircle className="h-4 w-4 text-red-600" />
                    )}
                    <span className="font-medium text-sm">{test.testCaseName}</span>
                  </div>
                  <Badge variant="outline">{test.earnedPoints}/{test.points} pts</Badge>
                </div>
                <div className="text-xs text-gray-600 flex items-center gap-4">
                  <span className="flex items-center gap-1">
                    <Clock className="h-3 w-3" />
                    {test.executionTime}ms
                  </span>
                  <span>{(test.memoryUsed / 1024).toFixed(2)} KB</span>
                </div>
                {test.errorMessage && (
                  <div className="mt-2 p-2 bg-red-50 rounded text-xs text-red-700">
                    {test.errorMessage}
                  </div>
                )}
              </div>
            ))}
          </div>
        )}
      </CardContent>
    </Card>
  );
};
