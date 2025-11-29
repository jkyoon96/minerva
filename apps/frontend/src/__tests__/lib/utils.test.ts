import { cn, formatDate, formatDateTime } from '@/lib/utils';

describe('Utils', () => {
  describe('cn (className merger)', () => {
    it('should merge class names correctly', () => {
      const result = cn('class1', 'class2');
      expect(result).toBe('class1 class2');
    });

    it('should handle conditional classes', () => {
      const result = cn('base', true && 'conditional', false && 'hidden');
      expect(result).toBe('base conditional');
    });

    it('should merge Tailwind classes without conflicts', () => {
      const result = cn('px-2 py-1', 'px-4');
      expect(result).toBe('py-1 px-4');
    });

    it('should handle arrays', () => {
      const result = cn(['class1', 'class2'], 'class3');
      expect(result).toBe('class1 class2 class3');
    });

    it('should handle objects', () => {
      const result = cn({
        class1: true,
        class2: false,
        class3: true,
      });
      expect(result).toBe('class1 class3');
    });

    it('should handle undefined and null', () => {
      const result = cn('class1', undefined, null, 'class2');
      expect(result).toBe('class1 class2');
    });

    it('should handle empty input', () => {
      const result = cn();
      expect(result).toBe('');
    });
  });

  describe('formatDate', () => {
    it('should format Date object to Korean format', () => {
      const date = new Date('2024-01-15T10:30:00Z');
      const result = formatDate(date);
      expect(result).toContain('2024');
      expect(result).toMatch(/1월|January/);
      expect(result).toContain('15');
    });

    it('should format date string to Korean format', () => {
      const dateString = '2024-03-20T15:45:00Z';
      const result = formatDate(dateString);
      expect(result).toContain('2024');
      expect(result).toMatch(/3월|March/);
      expect(result).toContain('20');
    });

    it('should handle different dates consistently', () => {
      const date1 = new Date('2024-12-31T23:59:59Z');
      const date2 = '2024-12-31T23:59:59Z';

      const result1 = formatDate(date1);
      const result2 = formatDate(date2);

      // Both should format to the same result
      expect(result1).toBe(result2);
      expect(result1).toContain('2024');
      expect(result1).toMatch(/12월|December/);
      expect(result1).toContain('31');
    });
  });

  describe('formatDateTime', () => {
    it('should format Date object with time in Korean format', () => {
      const date = new Date('2024-01-15T10:30:00Z');
      const result = formatDateTime(date);

      expect(result).toContain('2024');
      expect(result).toMatch(/1월|January/);
      expect(result).toContain('15');
      // Should include time (exact format depends on timezone)
      expect(result).toMatch(/\d{1,2}:\d{2}/);
    });

    it('should format date string with time in Korean format', () => {
      const dateString = '2024-03-20T15:45:00Z';
      const result = formatDateTime(dateString);

      expect(result).toContain('2024');
      expect(result).toMatch(/3월|March/);
      expect(result).toContain('20');
      expect(result).toMatch(/\d{1,2}:\d{2}/);
    });

    it('should handle midnight correctly', () => {
      const date = new Date('2024-06-01T00:00:00Z');
      const result = formatDateTime(date);

      expect(result).toContain('2024');
      expect(result).toMatch(/6월|June/);
      expect(result).toContain('1');
      expect(result).toMatch(/\d{1,2}:\d{2}/);
    });

    it('should handle end of day correctly', () => {
      const date = new Date('2024-06-01T23:59:59Z');
      const result = formatDateTime(date);

      expect(result).toContain('2024');
      expect(result).toMatch(/6월|June/);
      // Date might be different due to timezone
      expect(result).toMatch(/\d{1,2}:\d{2}/);
    });

    it('should be different from formatDate (includes time)', () => {
      const date = new Date('2024-06-01T14:30:00Z');
      const dateResult = formatDate(date);
      const dateTimeResult = formatDateTime(date);

      // DateTime result should be longer (includes time)
      expect(dateTimeResult.length).toBeGreaterThan(dateResult.length);
    });
  });
});
