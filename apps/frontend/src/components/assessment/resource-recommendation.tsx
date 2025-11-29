'use client';

import React from 'react';
import { BookOpen, Video, FileText, Code, ExternalLink } from 'lucide-react';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';
import { LearningResource } from '@/types/assessment';

interface ResourceRecommendationProps {
  resources: LearningResource[];
  className?: string;
}

const RESOURCE_ICONS = {
  VIDEO: Video,
  ARTICLE: FileText,
  TUTORIAL: BookOpen,
  EXERCISE: Code,
  DOCUMENTATION: BookOpen,
};

export const ResourceRecommendation: React.FC<ResourceRecommendationProps> = ({ resources, className }) => {
  return (
    <Card className={className}>
      <CardHeader>
        <CardTitle className="flex items-center gap-2">
          <BookOpen className="h-5 w-5 text-blue-600" />
          Recommended Resources
        </CardTitle>
        <p className="text-sm text-gray-600">Curated materials to improve your understanding</p>
      </CardHeader>

      <CardContent className="space-y-3">
        {resources.map((resource) => {
          const Icon = RESOURCE_ICONS[resource.type];
          return (
            <div key={resource.id} className="p-4 border rounded-lg hover:shadow-md transition-shadow">
              <div className="flex items-start gap-3">
                <div className="w-10 h-10 bg-blue-50 rounded-lg flex items-center justify-center flex-shrink-0">
                  <Icon className="h-5 w-5 text-blue-600" />
                </div>
                <div className="flex-1">
                  <div className="flex items-start justify-between gap-2 mb-2">
                    <h4 className="font-semibold text-gray-900">{resource.title}</h4>
                    <Badge variant="outline" className="text-xs">{resource.type}</Badge>
                  </div>
                  <p className="text-sm text-gray-600 mb-3">{resource.description}</p>
                  <Button variant="outline" size="sm" asChild>
                    <a href={resource.url} target="_blank" rel="noopener noreferrer">
                      <ExternalLink className="h-3 w-3 mr-1" />
                      View Resource
                    </a>
                  </Button>
                </div>
              </div>
            </div>
          );
        })}
      </CardContent>
    </Card>
  );
};
