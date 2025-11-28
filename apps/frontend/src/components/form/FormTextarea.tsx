import React from 'react';
import { Textarea } from '@/components/ui/textarea';
import { FormField } from './FormField';

interface FormTextareaProps extends React.TextareaHTMLAttributes<HTMLTextAreaElement> {
  label?: string;
  error?: string;
  description?: string;
}

/**
 * 검증 기능이 있는 폼 텍스트에리어 컴포넌트
 * - FormField로 래핑된 Textarea
 * - 에러 상태 표시
 */
export const FormTextarea = React.forwardRef<HTMLTextAreaElement, FormTextareaProps>(
  ({ label, error, description, required, className, ...props }, ref) => {
    return (
      <FormField
        label={label}
        error={error}
        required={required}
        description={description}
        className={className}
      >
        <Textarea ref={ref} aria-invalid={!!error} {...props} />
      </FormField>
    );
  }
);

FormTextarea.displayName = 'FormTextarea';
