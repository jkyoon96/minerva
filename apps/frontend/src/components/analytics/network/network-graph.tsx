'use client';

import { useEffect, useRef } from 'react';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { NetworkNode, NetworkEdge } from '@/types/analytics';

interface NetworkGraphProps {
  nodes: NetworkNode[];
  edges: NetworkEdge[];
  onNodeClick?: (nodeId: number) => void;
}

export function NetworkGraph({ nodes, edges, onNodeClick }: NetworkGraphProps) {
  const canvasRef = useRef<HTMLCanvasElement>(null);

  useEffect(() => {
    const canvas = canvasRef.current;
    if (!canvas) return;

    const ctx = canvas.getContext('2d');
    if (!ctx) return;

    // Set canvas size
    const rect = canvas.getBoundingClientRect();
    canvas.width = rect.width;
    canvas.height = rect.height;

    // Clear canvas
    ctx.clearRect(0, 0, canvas.width, canvas.height);

    // Simple force-directed layout simulation
    const positions = new Map<number, { x: number; y: number }>();
    const center = { x: canvas.width / 2, y: canvas.height / 2 };
    const radius = Math.min(canvas.width, canvas.height) / 3;

    // Position nodes in a circle based on centrality
    nodes.forEach((node, idx) => {
      const angle = (idx / nodes.length) * 2 * Math.PI;
      const r = radius * (1 - node.centrality * 0.5); // Higher centrality = closer to center
      positions.set(node.id, {
        x: center.x + r * Math.cos(angle),
        y: center.y + r * Math.sin(angle),
      });
    });

    // Draw edges
    ctx.strokeStyle = '#e5e7eb';
    ctx.lineWidth = 1;
    edges.forEach((edge) => {
      const source = positions.get(edge.sourceId);
      const target = positions.get(edge.targetId);
      if (!source || !target) return;

      ctx.beginPath();
      ctx.moveTo(source.x, source.y);
      ctx.lineTo(target.x, target.y);
      ctx.globalAlpha = Math.min(edge.weight / 20, 1); // Transparency based on weight
      ctx.stroke();
      ctx.globalAlpha = 1;
    });

    // Draw nodes
    nodes.forEach((node) => {
      const pos = positions.get(node.id);
      if (!pos) return;

      const size = 5 + node.centrality * 15; // Size based on centrality

      // Node circle
      ctx.beginPath();
      ctx.arc(pos.x, pos.y, size, 0, 2 * Math.PI);

      // Color based on role
      const colors = {
        HUB: '#3b82f6', // blue
        BRIDGE: '#10b981', // green
        PERIPHERAL: '#f59e0b', // orange
        ISOLATED: '#ef4444', // red
      };
      ctx.fillStyle = colors[node.role];
      ctx.fill();

      // Border
      ctx.strokeStyle = '#fff';
      ctx.lineWidth = 2;
      ctx.stroke();

      // Label (for important nodes)
      if (node.centrality > 0.6 || node.role === 'ISOLATED') {
        ctx.fillStyle = '#000';
        ctx.font = '12px sans-serif';
        ctx.textAlign = 'center';
        ctx.fillText(node.studentName, pos.x, pos.y - size - 5);
      }
    });
  }, [nodes, edges]);

  const handleCanvasClick = (e: React.MouseEvent<HTMLCanvasElement>) => {
    if (!onNodeClick) return;

    const canvas = canvasRef.current;
    if (!canvas) return;

    const rect = canvas.getBoundingClientRect();
    const x = e.clientX - rect.left;
    const y = e.clientY - rect.top;

    // Simple hit detection (this is a simplified version)
    // In production, you'd use a proper force-directed layout library like D3.js
    console.log('Canvas clicked at', x, y);
  };

  return (
    <Card>
      <CardHeader>
        <CardTitle>Interaction Network</CardTitle>
        <div className="flex gap-4 text-xs">
          <div className="flex items-center gap-2">
            <div className="h-3 w-3 rounded-full bg-blue-500" />
            <span>Hub</span>
          </div>
          <div className="flex items-center gap-2">
            <div className="h-3 w-3 rounded-full bg-green-500" />
            <span>Bridge</span>
          </div>
          <div className="flex items-center gap-2">
            <div className="h-3 w-3 rounded-full bg-orange-500" />
            <span>Peripheral</span>
          </div>
          <div className="flex items-center gap-2">
            <div className="h-3 w-3 rounded-full bg-red-500" />
            <span>Isolated</span>
          </div>
        </div>
      </CardHeader>
      <CardContent>
        <canvas
          ref={canvasRef}
          className="h-[500px] w-full cursor-pointer"
          onClick={handleCanvasClick}
        />
        <p className="mt-4 text-center text-sm text-muted-foreground">
          Node size represents centrality. Click on nodes for details.
        </p>
      </CardContent>
    </Card>
  );
}
