'use client';

import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';
import { RiskStudent, RiskLevel } from '@/types/analytics';
import { AlertTriangle, Mail, FileText } from 'lucide-react';
import { Progress } from '@/components/ui/progress';

interface RiskStudentListProps {
  students: RiskStudent[];
  onStudentClick?: (studentId: number) => void;
  onEmailClick?: (studentId: number) => void;
  onAddNote?: (studentId: number) => void;
}

export function RiskStudentList({
  students,
  onStudentClick,
  onEmailClick,
  onAddNote,
}: RiskStudentListProps) {
  const getRiskColor = (level: RiskLevel) => {
    const colors = {
      [RiskLevel.NONE]: 'bg-green-100 text-green-700 border-green-200',
      [RiskLevel.LOW]: 'bg-blue-100 text-blue-700 border-blue-200',
      [RiskLevel.MEDIUM]: 'bg-yellow-100 text-yellow-700 border-yellow-200',
      [RiskLevel.HIGH]: 'bg-orange-100 text-orange-700 border-orange-200',
      [RiskLevel.CRITICAL]: 'bg-red-100 text-red-700 border-red-200',
    };
    return colors[level];
  };

  const getRiskLabel = (level: RiskLevel) => {
    const labels = {
      [RiskLevel.NONE]: 'Normal',
      [RiskLevel.LOW]: 'Low Risk',
      [RiskLevel.MEDIUM]: 'Medium Risk',
      [RiskLevel.HIGH]: 'High Risk',
      [RiskLevel.CRITICAL]: 'Critical',
    };
    return labels[level];
  };

  return (
    <div className="space-y-4">
      {students.map((student) => (
        <Card
          key={student.studentId}
          className={`border-2 ${getRiskColor(student.riskLevel)}`}
        >
          <CardHeader>
            <div className="flex items-start justify-between">
              <div className="flex-1">
                <div className="flex items-center gap-2">
                  <AlertTriangle className="h-5 w-5" />
                  <CardTitle className="text-lg">{student.studentName}</CardTitle>
                  <Badge variant="outline">{getRiskLabel(student.riskLevel)}</Badge>
                </div>
                <p className="mt-1 text-sm text-muted-foreground">{student.email}</p>
              </div>
              <div className="text-right">
                <div className="text-2xl font-bold">{student.riskScore}</div>
                <div className="text-xs text-muted-foreground">Risk Score</div>
              </div>
            </div>
          </CardHeader>
          <CardContent className="space-y-4">
            {/* Risk Score Progress */}
            <div>
              <Progress value={student.riskScore} className="h-2" />
              <p className="mt-1 text-xs text-muted-foreground">
                {student.riskScore < 50
                  ? 'Low risk'
                  : student.riskScore < 75
                    ? 'Moderate risk'
                    : 'High risk - immediate attention needed'}
              </p>
            </div>

            {/* Risk Indicators */}
            <div>
              <h4 className="mb-2 text-sm font-semibold">Alert Indicators:</h4>
              <ul className="space-y-1">
                {student.indicators.map((indicator, idx) => (
                  <li key={idx} className="flex items-start gap-2 text-sm">
                    <span className="text-muted-foreground">•</span>
                    <span>{indicator.details.join(', ')}</span>
                  </li>
                ))}
              </ul>
            </div>

            {/* Last Activity */}
            <div className="text-sm text-muted-foreground">
              Last activity:{' '}
              {new Date(student.lastActivity).toLocaleDateString('ko-KR', {
                year: 'numeric',
                month: 'short',
                day: 'numeric',
              })}
            </div>

            {/* Recommended Actions */}
            <div className="rounded-lg bg-muted p-3">
              <h4 className="mb-2 text-sm font-semibold">Recommended Actions:</h4>
              <ol className="space-y-1 text-sm">
                <li>1. Schedule 1:1 meeting with student</li>
                <li>2. Notify academic advisor</li>
                <li>3. Create personalized learning plan</li>
              </ol>
            </div>

            {/* Action Buttons */}
            <div className="flex gap-2">
              <Button
                size="sm"
                variant="outline"
                onClick={(e) => {
                  e.stopPropagation();
                  onEmailClick?.(student.studentId);
                }}
              >
                <Mail className="mr-2 h-4 w-4" />
                Send Email
              </Button>
              <Button
                size="sm"
                variant="outline"
                onClick={(e) => {
                  e.stopPropagation();
                  onAddNote?.(student.studentId);
                }}
              >
                <FileText className="mr-2 h-4 w-4" />
                Add Note
              </Button>
              <Button
                size="sm"
                onClick={(e) => {
                  e.stopPropagation();
                  onStudentClick?.(student.studentId);
                }}
              >
                View Details
              </Button>
            </div>
          </CardContent>
        </Card>
      ))}
    </div>
  );
}
