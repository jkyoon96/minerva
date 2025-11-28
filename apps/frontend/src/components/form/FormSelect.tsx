import React from 'react';
import {
  Select,
  SelectContent,
  SelectGroup,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select';
import { FormField } from './FormField';

interface SelectOption {
  label: string;
  value: string;
}

interface FormSelectProps {
  label?: string;
  error?: string;
  description?: string;
  required?: boolean;
  placeholder?: string;
  options: SelectOption[];
  value?: string;
  onValueChange?: (value: string) => void;
  className?: string;
}

/**
 * 검증 기능이 있는 폼 셀렉트 컴포넌트
 * - FormField로 래핑된 Select
 * - 옵션 배열을 통한 간편한 사용
 */
export function FormSelect({
  label,
  error,
  description,
  required,
  placeholder = '선택하세요',
  options,
  value,
  onValueChange,
  className,
}: FormSelectProps) {
  return (
    <FormField
      label={label}
      error={error}
      required={required}
      description={description}
      className={className}
    >
      <Select value={value} onValueChange={onValueChange}>
        <SelectTrigger aria-invalid={!!error}>
          <SelectValue placeholder={placeholder} />
        </SelectTrigger>
        <SelectContent>
          <SelectGroup>
            {options.map((option) => (
              <SelectItem key={option.value} value={option.value}>
                {option.label}
              </SelectItem>
            ))}
          </SelectGroup>
        </SelectContent>
      </Select>
    </FormField>
  );
}
