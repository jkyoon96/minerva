'use client';

import React, { useState } from 'react';
import {
  Pencil,
  Highlighter,
  Eraser,
  Type,
  Square,
  Circle,
  Minus,
  ArrowRight,
  Undo2,
  Redo2,
  Trash2,
  Download,
} from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Separator } from '@/components/ui/separator';
import { Popover, PopoverContent, PopoverTrigger } from '@/components/ui/popover';
import { Label } from '@/components/ui/label';
import { Slider } from '@/components/ui/slider';
import { DrawingTool } from '@/types/active';
import { useActiveStore, activeSelectors } from '@/stores/activeStore';
import { cn } from '@/lib/utils';

interface WhiteboardToolbarProps {
  onClear?: () => void;
  onExport?: () => void;
}

const TOOLS = [
  { tool: DrawingTool.PEN, icon: Pencil, label: 'Pen' },
  { tool: DrawingTool.HIGHLIGHTER, icon: Highlighter, label: 'Highlighter' },
  { tool: DrawingTool.ERASER, icon: Eraser, label: 'Eraser' },
  { tool: DrawingTool.TEXT, icon: Type, label: 'Text' },
  { tool: DrawingTool.RECTANGLE, icon: Square, label: 'Rectangle' },
  { tool: DrawingTool.CIRCLE, icon: Circle, label: 'Circle' },
  { tool: DrawingTool.LINE, icon: Minus, label: 'Line' },
  { tool: DrawingTool.ARROW, icon: ArrowRight, label: 'Arrow' },
];

const COLORS = [
  '#000000', // Black
  '#EF4444', // Red
  '#3B82F6', // Blue
  '#10B981', // Green
  '#F59E0B', // Yellow
  '#8B5CF6', // Purple
  '#EC4899', // Pink
  '#FFFFFF', // White
];

export const WhiteboardToolbar: React.FC<WhiteboardToolbarProps> = ({ onClear, onExport }) => {
  const { whiteboardUIState, setWhiteboardUIState, undo, redo } = useActiveStore();
  const canUndo = activeSelectors.canUndo(useActiveStore());
  const canRedo = activeSelectors.canRedo(useActiveStore());

  const [showColorPicker, setShowColorPicker] = useState(false);

  const { currentTool, currentStyle } = whiteboardUIState;

  const handleToolChange = (tool: DrawingTool) => {
    setWhiteboardUIState({ currentTool: tool });
  };

  const handleColorChange = (color: string) => {
    setWhiteboardUIState({
      currentStyle: {
        ...currentStyle,
        color,
      },
    });
    setShowColorPicker(false);
  };

  const handleWidthChange = (value: number[]) => {
    setWhiteboardUIState({
      currentStyle: {
        ...currentStyle,
        width: value[0],
      },
    });
  };

  const handleOpacityChange = (value: number[]) => {
    setWhiteboardUIState({
      currentStyle: {
        ...currentStyle,
        opacity: value[0] / 100,
      },
    });
  };

  return (
    <div className="flex items-center gap-2 p-3 bg-white border rounded-lg shadow-sm">
      {/* Drawing Tools */}
      <div className="flex gap-1">
        {TOOLS.map(({ tool, icon: Icon, label }) => (
          <Button
            key={tool}
            variant={currentTool === tool ? 'default' : 'ghost'}
            size="icon"
            onClick={() => handleToolChange(tool)}
            title={label}
          >
            <Icon className="h-4 w-4" />
          </Button>
        ))}
      </div>

      <Separator orientation="vertical" className="h-8" />

      {/* Color Picker */}
      <Popover open={showColorPicker} onOpenChange={setShowColorPicker}>
        <PopoverTrigger asChild>
          <Button variant="outline" size="icon" title="Color">
            <div
              className="w-6 h-6 rounded border-2 border-gray-300"
              style={{ backgroundColor: currentStyle.color }}
            />
          </Button>
        </PopoverTrigger>
        <PopoverContent className="w-auto p-3">
          <div className="grid grid-cols-4 gap-2">
            {COLORS.map((color) => (
              <button
                key={color}
                className={cn(
                  'w-8 h-8 rounded border-2 transition-transform hover:scale-110',
                  color === currentStyle.color ? 'border-blue-500 scale-110' : 'border-gray-300'
                )}
                style={{ backgroundColor: color }}
                onClick={() => handleColorChange(color)}
              />
            ))}
          </div>
        </PopoverContent>
      </Popover>

      {/* Stroke Width */}
      <Popover>
        <PopoverTrigger asChild>
          <Button variant="outline" size="sm" title="Stroke Width">
            <div className="flex items-center gap-2">
              <div
                className="rounded-full bg-gray-900"
                style={{
                  width: `${Math.min(currentStyle.width * 2, 12)}px`,
                  height: `${Math.min(currentStyle.width * 2, 12)}px`,
                }}
              />
              <span className="text-xs">{currentStyle.width}px</span>
            </div>
          </Button>
        </PopoverTrigger>
        <PopoverContent className="w-64">
          <div className="space-y-2">
            <Label>Stroke Width</Label>
            <Slider
              value={[currentStyle.width]}
              onValueChange={handleWidthChange}
              min={1}
              max={20}
              step={1}
            />
          </div>
        </PopoverContent>
      </Popover>

      {/* Opacity */}
      {currentTool === DrawingTool.HIGHLIGHTER && (
        <Popover>
          <PopoverTrigger asChild>
            <Button variant="outline" size="sm" title="Opacity">
              {Math.round(currentStyle.opacity * 100)}%
            </Button>
          </PopoverTrigger>
          <PopoverContent className="w-64">
            <div className="space-y-2">
              <Label>Opacity</Label>
              <Slider
                value={[currentStyle.opacity * 100]}
                onValueChange={handleOpacityChange}
                min={10}
                max={100}
                step={5}
              />
            </div>
          </PopoverContent>
        </Popover>
      )}

      <Separator orientation="vertical" className="h-8" />

      {/* Undo/Redo */}
      <Button variant="ghost" size="icon" onClick={undo} disabled={!canUndo} title="Undo">
        <Undo2 className="h-4 w-4" />
      </Button>
      <Button variant="ghost" size="icon" onClick={redo} disabled={!canRedo} title="Redo">
        <Redo2 className="h-4 w-4" />
      </Button>

      <Separator orientation="vertical" className="h-8" />

      {/* Clear */}
      <Button variant="ghost" size="icon" onClick={onClear} title="Clear Canvas">
        <Trash2 className="h-4 w-4" />
      </Button>

      {/* Export */}
      <Button variant="ghost" size="icon" onClick={onExport} title="Export">
        <Download className="h-4 w-4" />
      </Button>
    </div>
  );
};
