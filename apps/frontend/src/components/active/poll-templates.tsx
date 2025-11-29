'use client';

import React from 'react';
import { Check } from 'lucide-react';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';
import { PollTemplate } from '@/types/active';

interface PollTemplatesProps {
  templates: PollTemplate[];
  onSelect: (template: PollTemplate) => void;
  selectedId?: number;
}

export const PollTemplates: React.FC<PollTemplatesProps> = ({ templates, onSelect, selectedId }) => {
  return (
    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
      {templates.map((template) => (
        <Card
          key={template.id}
          className={`cursor-pointer transition-all ${
            selectedId === template.id ? 'ring-2 ring-blue-500' : 'hover:shadow-md'
          }`}
          onClick={() => onSelect(template)}
        >
          <CardHeader>
            <div className="flex items-start justify-between">
              <CardTitle className="text-base">{template.name}</CardTitle>
              {selectedId === template.id && (
                <Check className="h-5 w-5 text-blue-500" />
              )}
            </div>
          </CardHeader>
          <CardContent>
            <p className="text-sm text-gray-600 mb-3">{template.description}</p>
            <Badge variant="outline" className="mb-2">{template.pollType}</Badge>
            <div className="flex flex-wrap gap-1 mt-2">
              {template.tags.map((tag, index) => (
                <Badge key={index} variant="secondary" className="text-xs">{tag}</Badge>
              ))}
            </div>
          </CardContent>
        </Card>
      ))}
    </div>
  );
};
