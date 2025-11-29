'use client';

import React from 'react';
import { Input } from '@/components/ui/input';
import { PollType } from '@/types/active';

interface PollOptionInputProps {
  pollType: PollType;
  value: string;
  onChange: (value: string) => void;
  placeholder?: string;
}

export const PollOptionInput: React.FC<PollOptionInputProps> = ({
  pollType,
  value,
  onChange,
  placeholder,
}) => {
  return (
    <Input
      value={value}
      onChange={(e) => onChange(e.target.value)}
      placeholder={placeholder || 'Enter option text'}
    />
  );
};
