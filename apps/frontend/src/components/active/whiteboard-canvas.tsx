'use client';

import React, { useEffect } from 'react';
import { useWhiteboard } from '@/hooks/useWhiteboard';
import { DrawingElement, Point } from '@/types/active';

interface WhiteboardCanvasProps {
  width: number;
  height: number;
  onElementAdded?: (element: DrawingElement) => void;
  onCursorMove?: (position: Point) => void;
  className?: string;
}

export const WhiteboardCanvas: React.FC<WhiteboardCanvasProps> = ({
  width,
  height,
  onElementAdded,
  onCursorMove,
  className,
}) => {
  const { canvasRef, initCanvas, startDrawing, draw, endDrawing } = useWhiteboard({
    onElementAdded,
    onCursorMove,
  });

  useEffect(() => {
    initCanvas(width, height);
  }, [width, height, initCanvas]);

  return (
    <canvas
      ref={canvasRef}
      className={className}
      onMouseDown={startDrawing}
      onMouseMove={draw}
      onMouseUp={endDrawing}
      onMouseLeave={endDrawing}
      style={{
        cursor: 'crosshair',
        touchAction: 'none',
      }}
    />
  );
};
