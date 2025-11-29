'use client';

import { use, useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import analyticsApi from '@/lib/api/analytics';
import { NetworkGraph } from '@/components/analytics/network/network-graph';
import { ClusterView } from '@/components/analytics/network/cluster-view';
import { NodeDetail } from '@/components/analytics/network/node-detail';
import { InteractionList } from '@/components/analytics/network/interaction-list';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';
import { ArrowLeft, Download, Users, Link as LinkIcon } from 'lucide-react';
import Link from 'next/link';
import { Dialog, DialogContent, DialogHeader, DialogTitle } from '@/components/ui/dialog';

export default function NetworkAnalysisPage({
  params,
}: {
  params: Promise<{ courseId: string }>;
}) {
  const { courseId } = use(params);
  const [selectedStudentId, setSelectedStudentId] = useState<number | null>(null);
  const [showStudentDetail, setShowStudentDetail] = useState(false);

  // Fetch network analysis
  const { data: networkAnalysis, isLoading } = useQuery({
    queryKey: ['analytics', 'network', courseId],
    queryFn: () =>
      analyticsApi.network.getNetworkAnalysis({
        courseId: parseInt(courseId),
      }),
  });

  // Fetch student interaction detail
  const { data: studentDetail } = useQuery({
    queryKey: ['analytics', 'network', 'student', selectedStudentId],
    queryFn: () =>
      selectedStudentId
        ? analyticsApi.network.getStudentInteractions({
            studentId: selectedStudentId,
            courseId: parseInt(courseId),
          })
        : null,
    enabled: !!selectedStudentId,
  });

  const handleNodeClick = (nodeId: number) => {
    setSelectedStudentId(nodeId);
    setShowStudentDetail(true);
  };

  if (isLoading) {
    return (
      <div className="flex h-[400px] items-center justify-center">
        <div className="text-center">
          <div className="mb-4 h-12 w-12 animate-spin rounded-full border-4 border-primary border-t-transparent"></div>
          <p className="text-muted-foreground">Loading network analysis...</p>
        </div>
      </div>
    );
  }

  if (!networkAnalysis) {
    return (
      <div className="flex h-[400px] items-center justify-center">
        <p className="text-muted-foreground">Network analysis not available</p>
      </div>
    );
  }

  // Create student name map
  const studentNames = new Map<number, string>();
  networkAnalysis.nodes.forEach((node) => {
    studentNames.set(node.studentId, node.studentName);
  });

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div className="flex items-center gap-4">
          <Link href="/analytics">
            <ArrowLeft className="h-6 w-6 cursor-pointer text-muted-foreground hover:text-foreground" />
          </Link>
          <div>
            <h1 className="text-3xl font-bold">Discussion Network Analysis</h1>
            <p className="text-muted-foreground">
              Interaction patterns and student collaboration clusters
            </p>
          </div>
        </div>
        <Button variant="outline">
          <Download className="mr-2 h-4 w-4" />
          Export Report
        </Button>
      </div>

      {/* Statistics */}
      <div className="grid gap-4 md:grid-cols-4">
        <Card>
          <CardHeader className="pb-3">
            <CardTitle className="text-sm font-medium">Network Nodes</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="flex items-center gap-2">
              <Users className="h-4 w-4 text-muted-foreground" />
              <span className="text-2xl font-bold">{networkAnalysis.statistics.totalNodes}</span>
            </div>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="pb-3">
            <CardTitle className="text-sm font-medium">Interactions</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="flex items-center gap-2">
              <LinkIcon className="h-4 w-4 text-muted-foreground" />
              <span className="text-2xl font-bold">{networkAnalysis.statistics.totalEdges}</span>
            </div>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="pb-3">
            <CardTitle className="text-sm font-medium">Network Density</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">
              {(networkAnalysis.statistics.networkDensity * 100).toFixed(0)}%
            </div>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="pb-3">
            <CardTitle className="text-sm font-medium">Isolated Students</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold text-destructive">
              {networkAnalysis.statistics.isolatedStudents}
            </div>
          </CardContent>
        </Card>
      </div>

      {/* Main Grid */}
      <div className="grid gap-6 lg:grid-cols-3">
        <div className="lg:col-span-2">
          <NetworkGraph
            nodes={networkAnalysis.nodes}
            edges={networkAnalysis.edges}
            onNodeClick={handleNodeClick}
          />
        </div>

        <div>
          <Card>
            <CardHeader>
              <CardTitle>Top Influencers</CardTitle>
            </CardHeader>
            <CardContent className="space-y-3">
              {networkAnalysis.topInfluencers.map((influencer, idx) => (
                <div
                  key={influencer.studentId}
                  className="flex cursor-pointer items-center justify-between rounded-lg border p-3 transition-colors hover:bg-muted"
                  onClick={() => handleNodeClick(influencer.studentId)}
                >
                  <div className="flex items-center gap-3">
                    <div className="flex h-8 w-8 items-center justify-center rounded-full bg-primary text-sm font-bold text-primary-foreground">
                      #{influencer.rank}
                    </div>
                    <div>
                      <div className="font-medium">{influencer.studentName}</div>
                      <div className="text-xs text-muted-foreground">
                        Centrality: {influencer.centrality.toFixed(2)}
                      </div>
                    </div>
                  </div>
                </div>
              ))}
            </CardContent>
          </Card>

          {networkAnalysis.isolatedStudents.length > 0 && (
            <Card className="mt-4">
              <CardHeader>
                <CardTitle className="text-destructive">Isolated Students</CardTitle>
              </CardHeader>
              <CardContent className="space-y-2">
                {networkAnalysis.isolatedStudents.map((student) => (
                  <div
                    key={student.studentId}
                    className="flex items-center justify-between rounded-lg border border-destructive/20 bg-destructive/5 p-3"
                  >
                    <span className="font-medium">{student.studentName}</span>
                    <Badge variant="destructive">
                      {student.connections} connection{student.connections !== 1 ? 's' : ''}
                    </Badge>
                  </div>
                ))}
              </CardContent>
            </Card>
          )}
        </div>
      </div>

      {/* Clusters */}
      <ClusterView clusters={networkAnalysis.clusters} onClusterClick={(id) => console.log(id)} />

      {/* Interaction List */}
      <InteractionList edges={networkAnalysis.edges} studentNames={studentNames} />

      {/* Insights */}
      {networkAnalysis.insights.length > 0 && (
        <Card>
          <CardHeader>
            <CardTitle>Insights & Recommendations</CardTitle>
          </CardHeader>
          <CardContent className="space-y-4">
            {networkAnalysis.insights.map((insight, idx) => (
              <div key={idx} className="rounded-lg border p-4">
                <h4 className="font-semibold">{insight.category}</h4>
                <p className="mt-2 text-sm text-muted-foreground">{insight.description}</p>
                <div className="mt-3 rounded bg-muted p-3">
                  <p className="text-sm font-medium">Recommendation:</p>
                  <p className="mt-1 text-sm">{insight.recommendation}</p>
                </div>
              </div>
            ))}
          </CardContent>
        </Card>
      )}

      {/* Student Detail Modal */}
      <Dialog open={showStudentDetail} onOpenChange={setShowStudentDetail}>
        <DialogContent className="max-w-3xl">
          <DialogHeader>
            <DialogTitle>Student Interaction Detail</DialogTitle>
          </DialogHeader>
          {studentDetail && <NodeDetail detail={studentDetail} />}
        </DialogContent>
      </Dialog>
    </div>
  );
}
