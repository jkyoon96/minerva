'use client';

/**
 * Layout selector component for switching between different view modes
 */

import React from 'react';
import { Grid3x3, Users, Presentation } from 'lucide-react';
import { Button } from '@/components/ui/button';
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu';
import { LayoutType } from '@/types/seminar';
import { cn } from '@/lib/utils';

interface LayoutSelectorProps {
  currentLayout: LayoutType;
  onLayoutChange: (layout: LayoutType) => void;
  disabled?: boolean;
  className?: string;
}

const layouts = [
  {
    type: LayoutType.GALLERY,
    icon: Grid3x3,
    label: 'Gallery View',
    description: 'See all participants',
  },
  {
    type: LayoutType.SPEAKER,
    icon: Presentation,
    label: 'Speaker View',
    description: 'Focus on active speaker',
  },
  {
    type: LayoutType.SIDEBAR,
    icon: Users,
    label: 'Sidebar View',
    description: 'Speaker with sidebar',
  },
];

export const LayoutSelector: React.FC<LayoutSelectorProps> = ({
  currentLayout,
  onLayoutChange,
  disabled = false,
  className,
}) => {
  const currentLayoutInfo = layouts.find((l) => l.type === currentLayout);
  const Icon = currentLayoutInfo?.icon || Grid3x3;

  return (
    <DropdownMenu>
      <DropdownMenuTrigger asChild>
        <Button
          variant="secondary"
          size="lg"
          disabled={disabled}
          className={cn('gap-2', className)}
          title="Change layout"
        >
          <Icon className="h-5 w-5" />
          <span className="hidden md:inline">{currentLayoutInfo?.label}</span>
        </Button>
      </DropdownMenuTrigger>
      <DropdownMenuContent align="end" className="w-56">
        {layouts.map((layout) => {
          const LayoutIcon = layout.icon;
          return (
            <DropdownMenuItem
              key={layout.type}
              onClick={() => onLayoutChange(layout.type)}
              className={cn(
                'flex flex-col items-start',
                currentLayout === layout.type && 'bg-gray-800',
              )}
            >
              <div className="flex items-center gap-2">
                <LayoutIcon className="h-4 w-4" />
                <span className="font-medium">{layout.label}</span>
              </div>
              <span className="mt-1 text-xs text-gray-500">
                {layout.description}
              </span>
            </DropdownMenuItem>
          );
        })}
      </DropdownMenuContent>
    </DropdownMenu>
  );
};
