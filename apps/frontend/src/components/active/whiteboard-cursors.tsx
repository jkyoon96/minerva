'use client';

import React from 'react';
import { CursorPosition } from '@/types/active';
import { useActiveStore } from '@/stores/activeStore';

export const WhiteboardCursors: React.FC = () => {
  const { cursors } = useActiveStore();

  return (
    <>
      {Array.from(cursors.values()).map((cursor) => (
        <div
          key={cursor.userId}
          className="absolute pointer-events-none"
          style={{
            left: cursor.x,
            top: cursor.y,
            transform: 'translate(-50%, -50%)',
          }}
        >
          <div
            className="w-3 h-3 rounded-full border-2 border-white"
            style={{ backgroundColor: cursor.color }}
          />
          <div
            className="absolute left-4 top-0 text-xs font-medium whitespace-nowrap px-1.5 py-0.5 rounded"
            style={{ backgroundColor: cursor.color, color: 'white' }}
          >
            {cursor.userName}
          </div>
        </div>
      ))}
    </>
  );
};
