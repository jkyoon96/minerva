'use client';

import { Card, CardContent } from '@/components/ui/card';
import { RiskLevel } from '@/types/analytics';
import { AlertTriangle, AlertCircle, Info, CheckCircle } from 'lucide-react';
import { cn } from '@/lib/utils';

interface RiskIndicatorProps {
  level: RiskLevel;
  count: number;
  label: string;
  description?: string;
  onClick?: () => void;
}

export function RiskIndicator({ level, count, label, description, onClick }: RiskIndicatorProps) {
  const config = {
    [RiskLevel.CRITICAL]: {
      icon: AlertTriangle,
      bgColor: 'bg-red-100 hover:bg-red-200',
      textColor: 'text-red-700',
      iconColor: 'text-red-600',
    },
    [RiskLevel.HIGH]: {
      icon: AlertCircle,
      bgColor: 'bg-orange-100 hover:bg-orange-200',
      textColor: 'text-orange-700',
      iconColor: 'text-orange-600',
    },
    [RiskLevel.MEDIUM]: {
      icon: AlertCircle,
      bgColor: 'bg-yellow-100 hover:bg-yellow-200',
      textColor: 'text-yellow-700',
      iconColor: 'text-yellow-600',
    },
    [RiskLevel.LOW]: {
      icon: Info,
      bgColor: 'bg-blue-100 hover:bg-blue-200',
      textColor: 'text-blue-700',
      iconColor: 'text-blue-600',
    },
    [RiskLevel.NONE]: {
      icon: CheckCircle,
      bgColor: 'bg-green-100 hover:bg-green-200',
      textColor: 'text-green-700',
      iconColor: 'text-green-600',
    },
  };

  const { icon: Icon, bgColor, textColor, iconColor } = config[level];

  return (
    <Card
      className={cn(
        'transition-colors',
        bgColor,
        onClick && 'cursor-pointer'
      )}
      onClick={onClick}
    >
      <CardContent className="p-6">
        <div className="flex items-center gap-4">
          <div className={cn('rounded-full p-3', bgColor)}>
            <Icon className={cn('h-6 w-6', iconColor)} />
          </div>
          <div className="flex-1">
            <div className={cn('text-3xl font-bold', textColor)}>{count}</div>
            <div className={cn('text-sm font-medium', textColor)}>{label}</div>
            {description && (
              <div className="mt-1 text-xs opacity-75">{description}</div>
            )}
          </div>
        </div>
      </CardContent>
    </Card>
  );
}
