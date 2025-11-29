'use client';

import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Line, LineChart, ResponsiveContainer, Tooltip, XAxis, YAxis, Legend, CartesianGrid } from 'recharts';

interface LearningChartProps {
  data: {
    week: number;
    participationScore?: number;
    quizScore?: number;
    classAverage?: number;
    [key: string]: any;
  }[];
  title?: string;
  description?: string;
}

export function LearningChart({ data, title = 'Weekly Progress', description }: LearningChartProps) {
  return (
    <Card>
      <CardHeader>
        <CardTitle>{title}</CardTitle>
        {description && <p className="text-sm text-muted-foreground">{description}</p>}
      </CardHeader>
      <CardContent>
        <ResponsiveContainer width="100%" height={300}>
          <LineChart data={data}>
            <CartesianGrid strokeDasharray="3 3" className="stroke-muted" />
            <XAxis dataKey="week" label={{ value: 'Week', position: 'insideBottom', offset: -5 }} />
            <YAxis label={{ value: 'Score', angle: -90, position: 'insideLeft' }} />
            <Tooltip
              contentStyle={{
                backgroundColor: 'hsl(var(--card))',
                border: '1px solid hsl(var(--border))',
                borderRadius: '6px',
              }}
            />
            <Legend />
            {data[0]?.participationScore !== undefined && (
              <Line
                type="monotone"
                dataKey="participationScore"
                stroke="hsl(var(--primary))"
                name="Participation"
                strokeWidth={2}
                dot={{ r: 4 }}
              />
            )}
            {data[0]?.quizScore !== undefined && (
              <Line
                type="monotone"
                dataKey="quizScore"
                stroke="hsl(var(--chart-2))"
                name="Quiz Score"
                strokeWidth={2}
                dot={{ r: 4 }}
              />
            )}
            {data[0]?.classAverage !== undefined && (
              <Line
                type="monotone"
                dataKey="classAverage"
                stroke="hsl(var(--muted-foreground))"
                name="Class Average"
                strokeWidth={2}
                strokeDasharray="5 5"
                dot={{ r: 3 }}
              />
            )}
          </LineChart>
        </ResponsiveContainer>
      </CardContent>
    </Card>
  );
}
