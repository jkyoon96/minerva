'use client';

import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { StudentInteractionDetail, InteractionType } from '@/types/analytics';
import { Users, MessageSquare, TrendingUp } from 'lucide-react';
import { Progress } from '@/components/ui/progress';

interface NodeDetailProps {
  detail: StudentInteractionDetail;
}

export function NodeDetail({ detail }: NodeDetailProps) {
  const getRoleColor = (role: string) => {
    const colors = {
      HUB: 'bg-blue-100 text-blue-700',
      BRIDGE: 'bg-green-100 text-green-700',
      PERIPHERAL: 'bg-orange-100 text-orange-700',
      ISOLATED: 'bg-red-100 text-red-700',
    };
    return colors[role as keyof typeof colors] || 'bg-gray-100 text-gray-700';
  };

  const getInteractionTypeLabel = (type: InteractionType) => {
    const labels = {
      [InteractionType.QUESTION]: 'Questions',
      [InteractionType.ANSWER]: 'Answers',
      [InteractionType.COMMENT]: 'Comments',
      [InteractionType.AGREEMENT]: 'Agreements',
      [InteractionType.DISAGREEMENT]: 'Disagreements',
      [InteractionType.DISCUSSION]: 'Discussions',
    };
    return labels[type];
  };

  return (
    <Card>
      <CardHeader>
        <div className="flex items-start justify-between">
          <div>
            <CardTitle>{detail.studentName}</CardTitle>
            <p className="text-sm text-muted-foreground">Network Role</p>
          </div>
          <Badge className={getRoleColor(detail.role)}>{detail.role}</Badge>
        </div>
      </CardHeader>
      <CardContent className="space-y-6">
        {/* Network Metrics */}
        <div className="grid grid-cols-3 gap-4">
          <div className="text-center">
            <div className="text-2xl font-bold">{detail.centrality.toFixed(2)}</div>
            <div className="text-xs text-muted-foreground">Centrality</div>
          </div>
          <div className="text-center">
            <div className="text-2xl font-bold">{detail.connections}</div>
            <div className="text-xs text-muted-foreground">Connections</div>
          </div>
          <div className="text-2xl font-bold">{detail.statistics.totalInteractions}</div>
            <div className="text-xs text-muted-foreground">Interactions</div>
          </div>
        </div>

        {/* Interaction Statistics */}
        <div>
          <h4 className="mb-3 flex items-center gap-2 text-sm font-semibold">
            <MessageSquare className="h-4 w-4" />
            Interaction Statistics
          </h4>
          <div className="space-y-2">
            <div className="flex items-center justify-between text-sm">
              <span className="text-muted-foreground">Initiated</span>
              <span className="font-medium">{detail.statistics.initiatedInteractions}</span>
            </div>
            <div className="flex items-center justify-between text-sm">
              <span className="text-muted-foreground">Responded</span>
              <span className="font-medium">{detail.statistics.respondedInteractions}</span>
            </div>
          </div>
        </div>

        {/* Top Interactions */}
        <div>
          <h4 className="mb-3 flex items-center gap-2 text-sm font-semibold">
            <Users className="h-4 w-4" />
            Top Interaction Partners
          </h4>
          <div className="space-y-2">
            {detail.topInteractions.slice(0, 5).map((interaction, idx) => (
              <div key={idx} className="flex items-center justify-between">
                <span className="text-sm">{interaction.partnerName}</span>
                <Badge variant="secondary">{interaction.count} interactions</Badge>
              </div>
            ))}
          </div>
        </div>

        {/* Interaction Type Breakdown */}
        <div>
          <h4 className="mb-3 text-sm font-semibold">Interaction Types</h4>
          <div className="space-y-3">
            {detail.interactionTypeBreakdown.map((breakdown, idx) => (
              <div key={idx} className="space-y-1">
                <div className="flex items-center justify-between text-sm">
                  <span>{getInteractionTypeLabel(breakdown.type)}</span>
                  <span className="font-medium">
                    {breakdown.count} ({breakdown.percentage.toFixed(0)}%)
                  </span>
                </div>
                <Progress value={breakdown.percentage} className="h-2" />
              </div>
            ))}
          </div>
        </div>

        {/* Strengths & Growth Opportunities */}
        <div className="grid gap-4 md:grid-cols-2">
          <div>
            <h4 className="mb-2 flex items-center gap-2 text-sm font-semibold text-green-600">
              <TrendingUp className="h-4 w-4" />
              Strengths
            </h4>
            <ul className="space-y-1">
              {detail.strengths.map((strength, idx) => (
                <li key={idx} className="text-sm">
                  • {strength}
                </li>
              ))}
            </ul>
          </div>
          <div>
            <h4 className="mb-2 text-sm font-semibold text-orange-600">Growth Opportunities</h4>
            <ul className="space-y-1">
              {detail.growthOpportunities.map((opportunity, idx) => (
                <li key={idx} className="text-sm">
                  • {opportunity}
                </li>
              ))}
            </ul>
          </div>
        </div>

        {/* Recommended Role */}
        <div className="rounded-lg bg-muted p-4">
          <h4 className="mb-1 text-sm font-semibold">Recommended Role</h4>
          <p className="text-sm">{detail.recommendedRole}</p>
        </div>
      </CardContent>
    </Card>
  );
}
