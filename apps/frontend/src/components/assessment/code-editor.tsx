'use client';

/**
 * Code editor component with syntax highlighting simulation
 */

import React, { useState } from 'react';
import { Play, Copy, Check } from 'lucide-react';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import { Badge } from '@/components/ui/badge';
import { ProgrammingLanguage } from '@/types/assessment';
import { cn } from '@/lib/utils';

interface CodeEditorProps {
  code: string;
  language: ProgrammingLanguage;
  onChange?: (code: string) => void;
  onRun?: () => void;
  onLanguageChange?: (language: ProgrammingLanguage) => void;
  readOnly?: boolean;
  isRunning?: boolean;
  className?: string;
}

export const CodeEditor: React.FC<CodeEditorProps> = ({
  code,
  language,
  onChange,
  onRun,
  onLanguageChange,
  readOnly = false,
  isRunning = false,
  className,
}) => {
  const [copied, setCopied] = useState(false);
  const lines = code.split('\n');

  const handleCopy = async () => {
    await navigator.clipboard.writeText(code);
    setCopied(true);
    setTimeout(() => setCopied(false), 2000);
  };

  return (
    <Card className={className}>
      <CardHeader>
        <div className="flex items-center justify-between">
          <CardTitle className="text-base">Code Editor</CardTitle>
          <div className="flex items-center gap-2">
            {!readOnly && onLanguageChange && (
              <Select value={language} onValueChange={onLanguageChange as any}>
                <SelectTrigger className="w-32">
                  <SelectValue />
                </SelectTrigger>
                <SelectContent>
                  {Object.values(ProgrammingLanguage).map((lang) => (
                    <SelectItem key={lang} value={lang}>
                      {lang}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
            )}
            {readOnly && <Badge variant="outline">{language}</Badge>}
            <Button variant="outline" size="sm" onClick={handleCopy}>
              {copied ? <Check className="h-4 w-4" /> : <Copy className="h-4 w-4" />}
            </Button>
            {onRun && (
              <Button size="sm" onClick={onRun} disabled={isRunning}>
                <Play className="h-4 w-4 mr-1" />
                {isRunning ? 'Running...' : 'Run'}
              </Button>
            )}
          </div>
        </div>
      </CardHeader>

      <CardContent>
        <div className="relative">
          <div className="absolute left-0 top-0 bottom-0 w-12 bg-gray-50 border-r flex flex-col text-right pr-2 py-3 text-xs text-gray-500 select-none">
            {lines.map((_, index) => (
              <div key={index} className="leading-6">
                {index + 1}
              </div>
            ))}
          </div>
          <textarea
            value={code}
            onChange={(e) => onChange?.(e.target.value)}
            readOnly={readOnly}
            className={cn(
              'w-full pl-14 pr-4 py-3 font-mono text-sm bg-gray-900 text-gray-100 rounded-lg resize-none focus:outline-none focus:ring-2 focus:ring-purple-500',
              readOnly && 'cursor-default'
            )}
            style={{ minHeight: '400px' }}
            spellCheck={false}
          />
        </div>
      </CardContent>
    </Card>
  );
};
