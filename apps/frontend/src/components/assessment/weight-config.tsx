'use client';

import React, { useState } from 'react';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { ParticipationWeightConfig } from '@/types/assessment';

interface WeightConfigProps {
  config: ParticipationWeightConfig;
  onSave: (weights: any[]) => void;
}

export const WeightConfig: React.FC<WeightConfigProps> = ({ config, onSave }) => {
  const [weights, setWeights] = useState(config.weights);

  return (
    <Card>
      <CardHeader>
        <CardTitle>Configure Participation Weights</CardTitle>
      </CardHeader>
      <CardContent className="space-y-4">
        {weights.map((weight, idx) => (
          <div key={weight.eventType} className="grid grid-cols-3 gap-4">
            <div className="col-span-1">
              <Label>{weight.eventType.replace(/_/g, ' ')}</Label>
            </div>
            <div>
              <Label className="text-xs">Weight (%)</Label>
              <Input
                type="number"
                value={weight.weight}
                onChange={(e) => {
                  const newWeights = [...weights];
                  newWeights[idx].weight = parseFloat(e.target.value);
                  setWeights(newWeights);
                }}
              />
            </div>
            <div>
              <Label className="text-xs">Base Points</Label>
              <Input
                type="number"
                value={weight.basePoints}
                onChange={(e) => {
                  const newWeights = [...weights];
                  newWeights[idx].basePoints = parseFloat(e.target.value);
                  setWeights(newWeights);
                }}
              />
            </div>
          </div>
        ))}
        <Button onClick={() => onSave(weights)} className="w-full">Save Configuration</Button>
      </CardContent>
    </Card>
  );
};
