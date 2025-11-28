'use client';

/**
 * Chat panel component for real-time messaging
 */

import React, { useState, useRef, useEffect } from 'react';
import { Send, Search, Smile, X } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { ScrollArea } from '@/components/ui/scroll-area';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select';
import { ChatMessage } from './chat-message';
import { ChatMessage as ChatMessageType, Participant } from '@/types/seminar';
import { cn } from '@/lib/utils';

interface ChatPanelProps {
  messages: ChatMessageType[];
  participants: Participant[];
  currentUserId: number;
  onSendMessage: (content: string, recipientId?: number) => void;
  onDeleteMessage?: (messageId: number) => void;
  className?: string;
}

export const ChatPanel: React.FC<ChatPanelProps> = ({
  messages,
  participants,
  currentUserId,
  onSendMessage,
  onDeleteMessage,
  className,
}) => {
  const [messageText, setMessageText] = useState('');
  const [searchQuery, setSearchQuery] = useState('');
  const [selectedRecipient, setSelectedRecipient] = useState<number | undefined>(
    undefined,
  );
  const scrollRef = useRef<HTMLDivElement>(null);
  const inputRef = useRef<HTMLInputElement>(null);

  // Auto-scroll to bottom when new messages arrive
  useEffect(() => {
    if (scrollRef.current) {
      scrollRef.current.scrollTop = scrollRef.current.scrollHeight;
    }
  }, [messages]);

  // Filter messages by search query
  const filteredMessages = messages.filter((msg) => {
    if (!searchQuery) return true;
    return (
      msg.content.toLowerCase().includes(searchQuery.toLowerCase()) ||
      msg.senderName.toLowerCase().includes(searchQuery.toLowerCase())
    );
  });

  const handleSendMessage = () => {
    if (!messageText.trim()) return;

    onSendMessage(messageText, selectedRecipient);
    setMessageText('');
    inputRef.current?.focus();
  };

  const handleKeyPress = (e: React.KeyboardEvent) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault();
      handleSendMessage();
    }
  };

  return (
    <div className={cn('flex h-full flex-col bg-gray-900', className)}>
      {/* Header */}
      <div className="border-b border-gray-800 p-4">
        <h3 className="text-lg font-semibold text-white">Chat</h3>

        {/* Search */}
        <div className="mt-3 relative">
          <Search className="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-gray-500" />
          <Input
            type="text"
            placeholder="Search messages..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            className="pl-9 bg-gray-800 border-gray-700 text-white"
          />
        </div>

        {/* Recipient selector */}
        <div className="mt-3">
          <Select
            value={selectedRecipient?.toString() || 'everyone'}
            onValueChange={(value) =>
              setSelectedRecipient(value === 'everyone' ? undefined : Number(value))
            }
          >
            <SelectTrigger className="bg-gray-800 border-gray-700 text-white">
              <SelectValue />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value="everyone">Everyone</SelectItem>
              {participants
                .filter((p) => p.userId !== currentUserId)
                .map((participant) => (
                  <SelectItem key={participant.id} value={participant.userId.toString()}>
                    {participant.userName} (Private)
                  </SelectItem>
                ))}
            </SelectContent>
          </Select>
        </div>
      </div>

      {/* Messages */}
      <ScrollArea className="flex-1 px-2" ref={scrollRef}>
        <div className="space-y-1">
          {filteredMessages.map((message) => (
            <ChatMessage
              key={message.id}
              message={message}
              isOwn={message.senderId === currentUserId}
              onDelete={onDeleteMessage}
            />
          ))}
        </div>

        {filteredMessages.length === 0 && (
          <div className="flex h-full items-center justify-center text-gray-500">
            {searchQuery ? 'No messages found' : 'No messages yet'}
          </div>
        )}
      </ScrollArea>

      {/* Input */}
      <div className="border-t border-gray-800 p-4">
        <div className="flex gap-2">
          <Input
            ref={inputRef}
            type="text"
            placeholder={
              selectedRecipient
                ? `Send private message to ${
                    participants.find((p) => p.userId === selectedRecipient)?.userName
                  }...`
                : 'Send a message...'
            }
            value={messageText}
            onChange={(e) => setMessageText(e.target.value)}
            onKeyPress={handleKeyPress}
            className="flex-1 bg-gray-800 border-gray-700 text-white"
          />
          {/* Emoji picker button (placeholder) */}
          <Button
            variant="ghost"
            size="icon"
            className="text-gray-400 hover:text-white"
            title="Add emoji"
          >
            <Smile className="h-5 w-5" />
          </Button>
          <Button
            onClick={handleSendMessage}
            disabled={!messageText.trim()}
            size="icon"
            title="Send message"
          >
            <Send className="h-5 w-5" />
          </Button>
        </div>
      </div>
    </div>
  );
};
