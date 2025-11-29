'use client';

import { Card, CardContent, CardHeader } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';
import { RiskAlert, AlertStatus, RiskLevel } from '@/types/analytics';
import { formatDistanceToNow } from 'date-fns';
import { AlertTriangle, CheckCircle, X } from 'lucide-react';

interface AlertCardProps {
  alert: RiskAlert;
  onAcknowledge?: (alertId: number) => void;
  onResolve?: (alertId: number) => void;
  onDismiss?: (alertId: number) => void;
}

export function AlertCard({ alert, onAcknowledge, onResolve, onDismiss }: AlertCardProps) {
  const getSeverityColor = (severity: RiskLevel) => {
    const colors = {
      [RiskLevel.CRITICAL]: 'destructive',
      [RiskLevel.HIGH]: 'destructive',
      [RiskLevel.MEDIUM]: 'secondary',
      [RiskLevel.LOW]: 'outline',
      [RiskLevel.NONE]: 'outline',
    };
    return colors[severity] as 'destructive' | 'secondary' | 'outline';
  };

  const getStatusBadge = (status: AlertStatus) => {
    const variants = {
      [AlertStatus.ACTIVE]: { variant: 'destructive' as const, label: 'Active' },
      [AlertStatus.ACKNOWLEDGED]: { variant: 'secondary' as const, label: 'Acknowledged' },
      [AlertStatus.RESOLVED]: { variant: 'outline' as const, label: 'Resolved' },
      [AlertStatus.DISMISSED]: { variant: 'outline' as const, label: 'Dismissed' },
    };
    const config = variants[status];
    return <Badge variant={config.variant}>{config.label}</Badge>;
  };

  return (
    <Card className="border-l-4 border-l-destructive">
      <CardHeader className="pb-3">
        <div className="flex items-start justify-between">
          <div className="flex items-start gap-3">
            <AlertTriangle className="mt-0.5 h-5 w-5 text-destructive" />
            <div>
              <div className="flex items-center gap-2">
                <h3 className="font-semibold">{alert.studentName}</h3>
                <Badge variant={getSeverityColor(alert.severity)}>{alert.severity}</Badge>
                {getStatusBadge(alert.status)}
              </div>
              <p className="mt-1 text-sm text-muted-foreground">
                {formatDistanceToNow(new Date(alert.triggeredAt), { addSuffix: true })}
              </p>
            </div>
          </div>
        </div>
      </CardHeader>
      <CardContent className="space-y-4">
        <div>
          <p className="font-medium">{alert.message}</p>
          {alert.details && Object.keys(alert.details).length > 0 && (
            <div className="mt-2 rounded-lg bg-muted p-3">
              <ul className="space-y-1 text-sm">
                {Object.entries(alert.details).map(([key, value], idx) => (
                  <li key={idx}>
                    <span className="font-medium">{key}:</span>{' '}
                    {Array.isArray(value) ? value.join(', ') : String(value)}
                  </li>
                ))}
              </ul>
            </div>
          )}
        </div>

        {alert.status === AlertStatus.ACKNOWLEDGED && alert.acknowledgedByName && (
          <div className="text-sm text-muted-foreground">
            Acknowledged by {alert.acknowledgedByName}
            {alert.note && (
              <p className="mt-1 rounded bg-muted p-2 text-xs">Note: {alert.note}</p>
            )}
          </div>
        )}

        {alert.status === AlertStatus.RESOLVED && alert.resolvedByName && (
          <div className="flex items-center gap-2 text-sm text-green-600">
            <CheckCircle className="h-4 w-4" />
            Resolved by {alert.resolvedByName}
          </div>
        )}

        {alert.status === AlertStatus.ACTIVE && (
          <div className="flex gap-2">
            {onAcknowledge && (
              <Button
                size="sm"
                variant="outline"
                onClick={() => onAcknowledge(alert.id)}
              >
                Acknowledge
              </Button>
            )}
            {onResolve && (
              <Button size="sm" onClick={() => onResolve(alert.id)}>
                <CheckCircle className="mr-2 h-4 w-4" />
                Mark Resolved
              </Button>
            )}
            {onDismiss && (
              <Button
                size="sm"
                variant="ghost"
                onClick={() => onDismiss(alert.id)}
              >
                <X className="mr-2 h-4 w-4" />
                Dismiss
              </Button>
            )}
          </div>
        )}
      </CardContent>
    </Card>
  );
}
