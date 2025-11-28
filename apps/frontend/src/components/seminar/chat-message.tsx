'use client';

/**
 * Chat message component
 */

import React from 'react';
import { MoreVertical, Trash2 } from 'lucide-react';
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu';
import { Button } from '@/components/ui/button';
import { ChatMessage as ChatMessageType, ChatMessageType as MessageType } from '@/types/seminar';
import { cn } from '@/lib/utils';
import { format } from 'date-fns';

interface ChatMessageProps {
  message: ChatMessageType;
  isOwn: boolean;
  onDelete?: (messageId: number) => void;
  className?: string;
}

export const ChatMessage: React.FC<ChatMessageProps> = ({
  message,
  isOwn,
  onDelete,
  className,
}) => {
  const isSystem = message.messageType === MessageType.SYSTEM;
  const isPrivate = message.messageType === MessageType.PRIVATE;

  if (isSystem) {
    return (
      <div className={cn('flex justify-center py-2', className)}>
        <div className="rounded-full bg-gray-700 px-3 py-1 text-xs text-gray-300">
          {message.content}
        </div>
      </div>
    );
  }

  return (
    <div
      className={cn(
        'group flex gap-3 py-2 px-4 hover:bg-gray-800/50',
        className,
      )}
    >
      {/* Avatar */}
      <div className="flex h-8 w-8 shrink-0 items-center justify-center rounded-full bg-blue-600 text-sm font-semibold text-white">
        {message.senderName.charAt(0).toUpperCase()}
      </div>

      {/* Message content */}
      <div className="min-w-0 flex-1">
        <div className="flex items-baseline gap-2">
          <span className="font-semibold text-white">
            {message.senderName}
          </span>
          {isPrivate && (
            <span className="text-xs text-purple-400">
              to {message.recipientName}
            </span>
          )}
          <span className="text-xs text-gray-500">
            {format(new Date(message.sentAt), 'HH:mm')}
          </span>
        </div>
        <p className="mt-1 break-words text-sm text-gray-300">
          {message.content}
        </p>
      </div>

      {/* Actions menu */}
      {isOwn && onDelete && (
        <div className="shrink-0 opacity-0 transition-opacity group-hover:opacity-100">
          <DropdownMenu>
            <DropdownMenuTrigger asChild>
              <Button
                variant="ghost"
                size="sm"
                className="h-6 w-6 p-0"
              >
                <MoreVertical className="h-4 w-4" />
              </Button>
            </DropdownMenuTrigger>
            <DropdownMenuContent align="end">
              <DropdownMenuItem
                onClick={() => onDelete(message.id)}
                className="text-red-500"
              >
                <Trash2 className="mr-2 h-4 w-4" />
                Delete
              </DropdownMenuItem>
            </DropdownMenuContent>
          </DropdownMenu>
        </div>
      )}
    </div>
  );
};
