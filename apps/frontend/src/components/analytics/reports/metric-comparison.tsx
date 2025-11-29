'use client';

import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Progress } from '@/components/ui/progress';
import { TrendingUp, TrendingDown } from 'lucide-react';

interface MetricComparisonProps {
  metrics: {
    label: string;
    yourValue: number;
    classAverage: number;
    maxValue: number;
  }[];
}

export function MetricComparison({ metrics }: MetricComparisonProps) {
  return (
    <Card>
      <CardHeader>
        <CardTitle>Performance Comparison</CardTitle>
        <p className="text-sm text-muted-foreground">Your scores vs. class average</p>
      </CardHeader>
      <CardContent className="space-y-6">
        {metrics.map((metric, idx) => {
          const yourPercentage = (metric.yourValue / metric.maxValue) * 100;
          const avgPercentage = (metric.classAverage / metric.maxValue) * 100;
          const diff = metric.yourValue - metric.classAverage;
          const isAboveAverage = diff > 0;

          return (
            <div key={idx} className="space-y-2">
              <div className="flex items-center justify-between">
                <span className="font-medium">{metric.label}</span>
                <div className="flex items-center gap-2">
                  {isAboveAverage ? (
                    <TrendingUp className="h-4 w-4 text-green-600" />
                  ) : (
                    <TrendingDown className="h-4 w-4 text-red-600" />
                  )}
                  <span
                    className={`text-sm font-medium ${
                      isAboveAverage ? 'text-green-600' : 'text-red-600'
                    }`}
                  >
                    {diff > 0 && '+'}
                    {diff.toFixed(1)}
                  </span>
                </div>
              </div>

              <div className="space-y-1">
                <div className="flex items-center justify-between text-sm">
                  <span className="text-muted-foreground">You</span>
                  <span className="font-medium">{metric.yourValue.toFixed(1)}</span>
                </div>
                <Progress value={yourPercentage} className="h-2" />
              </div>

              <div className="space-y-1">
                <div className="flex items-center justify-between text-sm">
                  <span className="text-muted-foreground">Class Average</span>
                  <span className="font-medium">{metric.classAverage.toFixed(1)}</span>
                </div>
                <Progress value={avgPercentage} className="h-2 opacity-60" />
              </div>
            </div>
          );
        })}
      </CardContent>
    </Card>
  );
}
