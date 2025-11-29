'use client';

import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { StudentCluster } from '@/types/analytics';
import { Users } from 'lucide-react';

interface ClusterViewProps {
  clusters: StudentCluster[];
  onClusterClick?: (clusterId: number) => void;
}

export function ClusterView({ clusters, onClusterClick }: ClusterViewProps) {
  const getConnectivityColor = (connectivity: StudentCluster['internalConnectivity']) => {
    const colors = {
      HIGH: 'text-green-600 bg-green-100',
      MEDIUM: 'text-yellow-600 bg-yellow-100',
      LOW: 'text-red-600 bg-red-100',
    };
    return colors[connectivity];
  };

  return (
    <Card>
      <CardHeader>
        <CardTitle>Student Clusters</CardTitle>
        <p className="text-sm text-muted-foreground">
          Auto-detected discussion groups based on interaction patterns
        </p>
      </CardHeader>
      <CardContent className="space-y-4">
        {clusters.map((cluster) => (
          <div
            key={cluster.id}
            className="cursor-pointer rounded-lg border p-4 transition-colors hover:bg-muted"
            onClick={() => onClusterClick?.(cluster.id)}
          >
            <div className="flex items-start justify-between">
              <div className="flex items-center gap-3">
                <div
                  className="h-4 w-4 rounded-full"
                  style={{ backgroundColor: cluster.color }}
                />
                <div>
                  <h3 className="font-semibold">{cluster.name}</h3>
                  <p className="text-sm text-muted-foreground">
                    Leader: {cluster.leaderName}
                  </p>
                </div>
              </div>
              <div className="text-right">
                <div className="flex items-center gap-2">
                  <Users className="h-4 w-4 text-muted-foreground" />
                  <span className="font-semibold">{cluster.size}</span>
                </div>
              </div>
            </div>

            <div className="mt-3 flex items-center gap-2">
              <Badge className={getConnectivityColor(cluster.internalConnectivity)}>
                {cluster.internalConnectivity} Connectivity
              </Badge>
            </div>

            {cluster.characteristics.length > 0 && (
              <div className="mt-3">
                <p className="text-xs text-muted-foreground">Characteristics:</p>
                <ul className="mt-1 space-y-1">
                  {cluster.characteristics.map((char, idx) => (
                    <li key={idx} className="text-sm">
                      • {char}
                    </li>
                  ))}
                </ul>
              </div>
            )}
          </div>
        ))}
      </CardContent>
    </Card>
  );
}
