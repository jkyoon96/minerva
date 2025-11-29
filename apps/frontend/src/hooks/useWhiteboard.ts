/**
 * Custom hook for whiteboard canvas drawing logic
 */

import { useRef, useCallback, useEffect } from 'react';
import { DrawingTool, DrawingElement, Point, DrawingStyle } from '@/types/active';
import { useActiveStore } from '@/stores/activeStore';

interface UseWhiteboardOptions {
  onElementAdded?: (element: DrawingElement) => void;
  onElementUpdated?: (elementId: string, updates: Partial<DrawingElement>) => void;
  onCursorMove?: (position: Point) => void;
}

export const useWhiteboard = (options: UseWhiteboardOptions = {}) => {
  const canvasRef = useRef<HTMLCanvasElement>(null);
  const contextRef = useRef<CanvasRenderingContext2D | null>(null);
  const isDrawingRef = useRef(false);
  const currentPathRef = useRef<Point[]>([]);
  const currentElementIdRef = useRef<string>('');

  const { whiteboardUIState, setWhiteboardUIState, addDrawingElement, whiteboard } = useActiveStore();

  /**
   * Initialize canvas
   */
  const initCanvas = useCallback((width: number, height: number) => {
    const canvas = canvasRef.current;
    if (!canvas) return;

    // Set canvas size
    canvas.width = width;
    canvas.height = height;

    // Get context
    const context = canvas.getContext('2d');
    if (!context) return;

    // Configure context
    context.lineCap = 'round';
    context.lineJoin = 'round';

    contextRef.current = context;

    // Redraw existing elements
    redrawCanvas();
  }, []);

  /**
   * Redraw all elements on canvas
   */
  const redrawCanvas = useCallback(() => {
    const canvas = canvasRef.current;
    const context = contextRef.current;
    if (!canvas || !context || !whiteboard) return;

    // Clear canvas
    context.clearRect(0, 0, canvas.width, canvas.height);

    // Draw background if exists
    if (whiteboard.backgroundUrl) {
      const img = new Image();
      img.src = whiteboard.backgroundUrl;
      img.onload = () => {
        context.drawImage(img, 0, 0, canvas.width, canvas.height);
        drawElements();
      };
    } else {
      drawElements();
    }

    function drawElements() {
      if (!context || !whiteboard) return;

      whiteboard.elements.forEach((element) => {
        drawElement(context, element);
      });
    }
  }, [whiteboard]);

  /**
   * Draw a single element
   */
  const drawElement = useCallback((context: CanvasRenderingContext2D, element: DrawingElement) => {
    const { tool, points, style, text } = element;

    context.strokeStyle = style.color;
    context.lineWidth = style.width;
    context.globalAlpha = style.opacity;

    if (tool === DrawingTool.PEN || tool === DrawingTool.HIGHLIGHTER) {
      if (points.length < 2) return;

      context.beginPath();
      context.moveTo(points[0].x, points[0].y);

      for (let i = 1; i < points.length; i++) {
        context.lineTo(points[i].x, points[i].y);
      }

      context.stroke();
    } else if (tool === DrawingTool.LINE || tool === DrawingTool.ARROW) {
      if (points.length < 2) return;

      const start = points[0];
      const end = points[points.length - 1];

      context.beginPath();
      context.moveTo(start.x, start.y);
      context.lineTo(end.x, end.y);
      context.stroke();

      // Draw arrowhead for arrow tool
      if (tool === DrawingTool.ARROW) {
        const angle = Math.atan2(end.y - start.y, end.x - start.x);
        const arrowLength = 15;

        context.beginPath();
        context.moveTo(end.x, end.y);
        context.lineTo(
          end.x - arrowLength * Math.cos(angle - Math.PI / 6),
          end.y - arrowLength * Math.sin(angle - Math.PI / 6),
        );
        context.moveTo(end.x, end.y);
        context.lineTo(
          end.x - arrowLength * Math.cos(angle + Math.PI / 6),
          end.y - arrowLength * Math.sin(angle + Math.PI / 6),
        );
        context.stroke();
      }
    } else if (tool === DrawingTool.RECTANGLE) {
      if (points.length < 2) return;

      const start = points[0];
      const end = points[points.length - 1];
      const width = end.x - start.x;
      const height = end.y - start.y;

      context.strokeRect(start.x, start.y, width, height);
    } else if (tool === DrawingTool.CIRCLE) {
      if (points.length < 2) return;

      const start = points[0];
      const end = points[points.length - 1];
      const radius = Math.sqrt(Math.pow(end.x - start.x, 2) + Math.pow(end.y - start.y, 2));

      context.beginPath();
      context.arc(start.x, start.y, radius, 0, 2 * Math.PI);
      context.stroke();
    } else if (tool === DrawingTool.TEXT && text) {
      if (points.length < 1) return;

      const pos = points[0];
      context.font = `${style.width * 8}px sans-serif`;
      context.fillStyle = style.color;
      context.fillText(text, pos.x, pos.y);
    } else if (tool === DrawingTool.ERASER) {
      // Eraser is handled by removing elements, not drawing
    }

    context.globalAlpha = 1;
  }, []);

  /**
   * Get canvas coordinates from mouse event
   */
  const getCanvasCoordinates = useCallback((e: MouseEvent | React.MouseEvent<HTMLCanvasElement>): Point => {
    const canvas = canvasRef.current;
    if (!canvas) return { x: 0, y: 0 };

    const rect = canvas.getBoundingClientRect();
    return {
      x: e.clientX - rect.left,
      y: e.clientY - rect.top,
    };
  }, []);

  /**
   * Start drawing
   */
  const startDrawing = useCallback(
    (e: React.MouseEvent<HTMLCanvasElement>) => {
      const point = getCanvasCoordinates(e);

      if (whiteboardUIState.currentTool === DrawingTool.ERASER) {
        // Handle eraser - find and remove element at this point
        // This would require checking which element contains this point
        return;
      }

      isDrawingRef.current = true;
      currentPathRef.current = [point];
      currentElementIdRef.current = `element-${Date.now()}-${Math.random()}`;

      setWhiteboardUIState({ isDrawing: true });
    },
    [getCanvasCoordinates, whiteboardUIState.currentTool, setWhiteboardUIState],
  );

  /**
   * Draw
   */
  const draw = useCallback(
    (e: React.MouseEvent<HTMLCanvasElement>) => {
      if (!isDrawingRef.current) {
        // Just emit cursor position
        const point = getCanvasCoordinates(e);
        options.onCursorMove?.(point);
        return;
      }

      const point = getCanvasCoordinates(e);
      currentPathRef.current.push(point);

      const context = contextRef.current;
      if (!context) return;

      // Draw preview
      const { currentTool, currentStyle } = whiteboardUIState;

      // For pen and highlighter, draw incrementally
      if (currentTool === DrawingTool.PEN || currentTool === DrawingTool.HIGHLIGHTER) {
        context.strokeStyle = currentStyle.color;
        context.lineWidth = currentStyle.width;
        context.globalAlpha = currentStyle.opacity;

        const prevPoint = currentPathRef.current[currentPathRef.current.length - 2];
        if (prevPoint) {
          context.beginPath();
          context.moveTo(prevPoint.x, prevPoint.y);
          context.lineTo(point.x, point.y);
          context.stroke();
        }

        context.globalAlpha = 1;
      } else {
        // For shapes, redraw with updated endpoint
        redrawCanvas();

        const tempElement: DrawingElement = {
          id: currentElementIdRef.current,
          tool: currentTool,
          points: currentPathRef.current,
          style: currentStyle,
          userId: 0, // Will be set by server
          userName: '',
          timestamp: Date.now(),
        };

        drawElement(context, tempElement);
      }
    },
    [getCanvasCoordinates, whiteboardUIState, redrawCanvas, drawElement, options],
  );

  /**
   * End drawing
   */
  const endDrawing = useCallback(() => {
    if (!isDrawingRef.current) return;

    isDrawingRef.current = false;
    setWhiteboardUIState({ isDrawing: false });

    // Create element
    const element: DrawingElement = {
      id: currentElementIdRef.current,
      tool: whiteboardUIState.currentTool,
      points: [...currentPathRef.current],
      style: { ...whiteboardUIState.currentStyle },
      userId: 0, // Will be set by server
      userName: '',
      timestamp: Date.now(),
    };

    // Add to store
    addDrawingElement(element);

    // Notify parent
    options.onElementAdded?.(element);

    // Reset
    currentPathRef.current = [];
    currentElementIdRef.current = '';
  }, [whiteboardUIState, setWhiteboardUIState, addDrawingElement, options]);

  /**
   * Change tool
   */
  const setTool = useCallback(
    (tool: DrawingTool) => {
      setWhiteboardUIState({ currentTool: tool });
    },
    [setWhiteboardUIState],
  );

  /**
   * Change style
   */
  const setStyle = useCallback(
    (style: Partial<DrawingStyle>) => {
      setWhiteboardUIState({
        currentStyle: {
          ...whiteboardUIState.currentStyle,
          ...style,
        },
      });
    },
    [whiteboardUIState.currentStyle, setWhiteboardUIState],
  );

  /**
   * Clear canvas
   */
  const clear = useCallback(() => {
    const context = contextRef.current;
    const canvas = canvasRef.current;
    if (!context || !canvas) return;

    context.clearRect(0, 0, canvas.width, canvas.height);
  }, []);

  /**
   * Export canvas as data URL
   */
  const exportAsDataURL = useCallback((type: 'png' | 'jpeg' = 'png'): string => {
    const canvas = canvasRef.current;
    if (!canvas) return '';

    return canvas.toDataURL(`image/${type}`);
  }, []);

  /**
   * Export canvas as blob
   */
  const exportAsBlob = useCallback(
    (type: 'png' | 'jpeg' = 'png'): Promise<Blob | null> => {
      const canvas = canvasRef.current;
      if (!canvas) return Promise.resolve(null);

      return new Promise((resolve) => {
        canvas.toBlob(
          (blob) => {
            resolve(blob);
          },
          `image/${type}`,
          0.95,
        );
      });
    },
    [],
  );

  // Redraw canvas when elements change
  useEffect(() => {
    redrawCanvas();
  }, [whiteboard?.elements, redrawCanvas]);

  return {
    canvasRef,
    initCanvas,
    startDrawing,
    draw,
    endDrawing,
    setTool,
    setStyle,
    clear,
    redrawCanvas,
    exportAsDataURL,
    exportAsBlob,
    currentTool: whiteboardUIState.currentTool,
    currentStyle: whiteboardUIState.currentStyle,
    isDrawing: whiteboardUIState.isDrawing,
  };
};
