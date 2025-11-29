'use client';

import { Card, CardContent } from '@/components/ui/card';
import { LearningMetric } from '@/types/analytics';
import { TrendingUp, TrendingDown, Minus } from 'lucide-react';
import { cn } from '@/lib/utils';

interface LiveMetricsProps {
  metrics: LearningMetric[];
}

export function LiveMetrics({ metrics }: LiveMetricsProps) {
  const getTrendIcon = (trend: LearningMetric['trend']) => {
    if (trend === 'UP') return <TrendingUp className="h-4 w-4 text-green-600" />;
    if (trend === 'DOWN') return <TrendingDown className="h-4 w-4 text-red-600" />;
    return <Minus className="h-4 w-4 text-gray-400" />;
  };

  const getTrendColor = (trend: LearningMetric['trend']) => {
    if (trend === 'UP') return 'text-green-600';
    if (trend === 'DOWN') return 'text-red-600';
    return 'text-gray-600';
  };

  return (
    <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-4">
      {metrics.map((metric, idx) => (
        <Card key={idx}>
          <CardContent className="p-6">
            <div className="flex items-center justify-between">
              <p className="text-sm font-medium text-muted-foreground">{metric.label}</p>
              {getTrendIcon(metric.trend)}
            </div>
            <div className="mt-2 flex items-baseline gap-2">
              <span className="text-3xl font-bold">{metric.value}</span>
              {metric.percentageChange !== 0 && (
                <span className={cn('text-sm font-medium', getTrendColor(metric.trend))}>
                  {metric.percentageChange > 0 && '+'}
                  {metric.percentageChange.toFixed(1)}%
                </span>
              )}
            </div>
            {metric.comparison && (
              <p className="mt-1 text-xs text-muted-foreground">
                {metric.comparison.type === 'CLASS_AVERAGE' ? 'Class avg: ' : 'Previous: '}
                {metric.comparison.value}
              </p>
            )}
          </CardContent>
        </Card>
      ))}
    </div>
  );
}
