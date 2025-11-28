import React from 'react';
import { Input } from '@/components/ui/input';
import { FormField } from './FormField';

interface FormInputProps extends React.InputHTMLAttributes<HTMLInputElement> {
  label?: string;
  error?: string;
  description?: string;
}

/**
 * 검증 기능이 있는 폼 입력 컴포넌트
 * - FormField로 래핑된 Input
 * - 에러 상태 표시
 */
export const FormInput = React.forwardRef<HTMLInputElement, FormInputProps>(
  ({ label, error, description, required, className, ...props }, ref) => {
    return (
      <FormField
        label={label}
        error={error}
        required={required}
        description={description}
        className={className}
      >
        <Input ref={ref} aria-invalid={!!error} {...props} />
      </FormField>
    );
  }
);

FormInput.displayName = 'FormInput';
