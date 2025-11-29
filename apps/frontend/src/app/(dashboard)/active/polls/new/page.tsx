'use client';

/**
 * Create new poll page
 */

import React, { useState } from 'react';
import { useRouter } from 'next/navigation';
import { ArrowLeft } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { PollForm } from '@/components/active/poll-form';
import { PollTemplates } from '@/components/active/poll-templates';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { PollCreateRequest, PollTemplate } from '@/types/active';
import { pollApi } from '@/lib/api/active';

export default function NewPollPage() {
  const router = useRouter();
  const [templates, setTemplates] = useState<PollTemplate[]>([]);
  const [selectedTemplate, setSelectedTemplate] = useState<PollTemplate | null>(null);

  const handleSubmit = async (data: PollCreateRequest) => {
    try {
      await pollApi.createPoll(data);
      router.push('/active/polls');
    } catch (error) {
      console.error('Failed to create poll:', error);
    }
  };

  const handleTemplateSelect = (template: PollTemplate) => {
    setSelectedTemplate(template);
  };

  return (
    <div className="container mx-auto py-6 space-y-6 max-w-4xl">
      <div className="flex items-center gap-4">
        <Button variant="ghost" size="icon" onClick={() => router.back()}>
          <ArrowLeft className="h-5 w-5" />
        </Button>
        <div>
          <h1 className="text-3xl font-bold text-gray-900">Create New Poll</h1>
          <p className="text-gray-600 mt-1">Create a poll from scratch or use a template</p>
        </div>
      </div>

      <Tabs defaultValue="create">
        <TabsList>
          <TabsTrigger value="create">Create from Scratch</TabsTrigger>
          <TabsTrigger value="templates">Use Template</TabsTrigger>
        </TabsList>

        <TabsContent value="create" className="mt-6">
          <PollForm
            courseId={1}
            onSubmit={handleSubmit}
            onCancel={() => router.back()}
          />
        </TabsContent>

        <TabsContent value="templates" className="mt-6">
          <div className="space-y-6">
            <PollTemplates
              templates={templates}
              onSelect={handleTemplateSelect}
              selectedId={selectedTemplate?.id}
            />
            {selectedTemplate && (
              <PollForm
                courseId={1}
                initialData={{
                  question: selectedTemplate.question,
                  pollType: selectedTemplate.pollType,
                  options: selectedTemplate.options,
                }}
                onSubmit={handleSubmit}
                onCancel={() => router.back()}
              />
            )}
          </div>
        </TabsContent>
      </Tabs>
    </div>
  );
}
