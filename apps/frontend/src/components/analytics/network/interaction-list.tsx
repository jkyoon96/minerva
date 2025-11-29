'use client';

import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { NetworkEdge, InteractionType } from '@/types/analytics';
import { ArrowRight } from 'lucide-react';

interface InteractionListProps {
  edges: NetworkEdge[];
  studentNames: Map<number, string>;
  limit?: number;
}

export function InteractionList({ edges, studentNames, limit = 20 }: InteractionListProps) {
  const sortedEdges = [...edges]
    .sort((a, b) => b.weight - a.weight)
    .slice(0, limit);

  const getTypeColor = (type: InteractionType) => {
    const colors = {
      [InteractionType.QUESTION]: 'bg-blue-100 text-blue-700',
      [InteractionType.ANSWER]: 'bg-green-100 text-green-700',
      [InteractionType.COMMENT]: 'bg-purple-100 text-purple-700',
      [InteractionType.AGREEMENT]: 'bg-emerald-100 text-emerald-700',
      [InteractionType.DISAGREEMENT]: 'bg-red-100 text-red-700',
      [InteractionType.DISCUSSION]: 'bg-orange-100 text-orange-700',
    };
    return colors[type];
  };

  return (
    <Card>
      <CardHeader>
        <CardTitle>Top Interactions</CardTitle>
        <p className="text-sm text-muted-foreground">
          Showing {sortedEdges.length} most frequent interactions
        </p>
      </CardHeader>
      <CardContent className="space-y-3">
        {sortedEdges.map((edge, idx) => (
          <div
            key={edge.id}
            className="flex items-center justify-between rounded-lg border p-3"
          >
            <div className="flex flex-1 items-center gap-3">
              <span className="text-sm text-muted-foreground">#{idx + 1}</span>
              <div className="flex flex-1 items-center gap-2">
                <span className="font-medium">{studentNames.get(edge.sourceId)}</span>
                <ArrowRight className="h-4 w-4 text-muted-foreground" />
                <span className="font-medium">{studentNames.get(edge.targetId)}</span>
              </div>
            </div>

            <div className="flex items-center gap-2">
              <Badge variant="secondary">{edge.weight} times</Badge>
              {edge.interactionTypes.length > 0 && (
                <div className="flex gap-1">
                  {edge.interactionTypes.slice(0, 2).map((typeInfo, idx) => (
                    <Badge
                      key={idx}
                      className={`text-xs ${getTypeColor(typeInfo.type)}`}
                    >
                      {typeInfo.type.toLowerCase()}: {typeInfo.count}
                    </Badge>
                  ))}
                </div>
              )}
            </div>
          </div>
        ))}

        {edges.length === 0 && (
          <div className="py-12 text-center text-muted-foreground">
            No interactions recorded yet
          </div>
        )}
      </CardContent>
    </Card>
  );
}
