# Frontend Testing Infrastructure

This directory contains the test infrastructure for the EduForum frontend application.

## Test Structure

```
src/__tests__/
├── components/
│   └── ui/
│       └── button.test.tsx              # UI component tests
├── hooks/
│   └── use-auth.test.ts                 # Custom hooks tests
└── lib/
    └── utils.test.ts                    # Utility function tests
```

## Running Tests

### Run all tests
```bash
npm test
```

### Run tests in watch mode
```bash
npm run test:watch
```

### Run tests with coverage
```bash
npm run test:coverage
```

### Run specific test file
```bash
npm test button.test
```

### Run tests matching pattern
```bash
npm test -- --testNamePattern="login"
```

## Test Configuration

- **Test Runner**: Jest
- **Test Environment**: jsdom (simulates browser environment)
- **Testing Library**: React Testing Library
- **Mocking**: Jest mocks
- **Coverage Tool**: Istanbul (built into Jest)

## Writing Tests

### Component Tests

```tsx
import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { MyComponent } from './MyComponent';

describe('MyComponent', () => {
  it('renders correctly', () => {
    render(<MyComponent />);
    expect(screen.getByText('Hello')).toBeInTheDocument();
  });

  it('handles click events', async () => {
    const handleClick = jest.fn();
    const user = userEvent.setup();

    render(<MyComponent onClick={handleClick} />);
    await user.click(screen.getByRole('button'));

    expect(handleClick).toHaveBeenCalledTimes(1);
  });
});
```

### Hook Tests

```ts
import { renderHook, act } from '@testing-library/react';
import { useMyHook } from './useMyHook';

describe('useMyHook', () => {
  it('returns initial state', () => {
    const { result } = renderHook(() => useMyHook());
    expect(result.current.value).toBe(0);
  });

  it('updates state', () => {
    const { result } = renderHook(() => useMyHook());

    act(() => {
      result.current.increment();
    });

    expect(result.current.value).toBe(1);
  });
});
```

### Utility Function Tests

```ts
import { myUtilFunction } from './utils';

describe('myUtilFunction', () => {
  it('returns expected result', () => {
    expect(myUtilFunction('input')).toBe('expected output');
  });

  it('handles edge cases', () => {
    expect(myUtilFunction('')).toBe('');
    expect(myUtilFunction(null)).toBe(null);
  });
});
```

### Mocking API Calls

```ts
import * as api from '@/lib/api/myApi';

jest.mock('@/lib/api/myApi');
const mockedApi = api as jest.Mocked<typeof api>;

describe('API Tests', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('calls API successfully', async () => {
    mockedApi.fetchData.mockResolvedValueOnce({ data: 'test' });

    const result = await mockedApi.fetchData();

    expect(result.data).toBe('test');
  });
});
```

## Best Practices

1. **Query Priority**:
   - Use `getByRole` when possible (most accessible)
   - Use `getByLabelText` for form elements
   - Use `getByText` for non-interactive elements
   - Avoid `getByTestId` unless necessary

2. **User Interactions**:
   - Use `userEvent` instead of `fireEvent` for realistic interactions
   - Always `await` user events
   - Test user flows, not implementation details

3. **Assertions**:
   - Use semantic queries: `getByRole('button', { name: /submit/i })`
   - Test what users see, not component state
   - Use `toBeInTheDocument()` for presence checks

4. **Async Tests**:
   - Use `waitFor` for async operations
   - Use `findBy*` queries for async elements
   - Set appropriate timeouts for slow operations

5. **Test Organization**:
   - Group related tests with `describe`
   - Use clear, descriptive test names
   - Keep tests focused and isolated
   - Use `beforeEach`/`afterEach` for setup/cleanup

6. **Mocking**:
   - Mock external dependencies (APIs, modules)
   - Don't mock implementation details
   - Clear mocks between tests with `jest.clearAllMocks()`
   - Mock at the module level, not the component level

## Coverage Goals

- **Statements**: Minimum 80%
- **Branches**: Minimum 75%
- **Functions**: Minimum 80%
- **Lines**: Minimum 80%

## Common Queries

### Finding Elements

```tsx
// By role (preferred)
screen.getByRole('button', { name: /submit/i })
screen.getByRole('textbox', { name: /email/i })

// By label text
screen.getByLabelText(/username/i)

// By placeholder
screen.getByPlaceholderText(/enter email/i)

// By text content
screen.getByText(/hello world/i)

// By test ID (last resort)
screen.getByTestId('custom-element')
```

### Async Queries

```tsx
// Wait for element to appear
const element = await screen.findByText(/loaded/i);

// Wait for condition
await waitFor(() => {
  expect(screen.getByText(/success/i)).toBeInTheDocument();
});

// Wait for element to disappear
await waitForElementToBeRemoved(() => screen.queryByText(/loading/i));
```

## Troubleshooting

### "Not wrapped in act(...)" Warning
- Wrap state updates in `act()`
- Use `waitFor` for async operations
- Ensure all updates complete before test ends

### "Unable to find element" Error
- Check if element is actually rendered
- Verify query selector is correct
- Use `screen.debug()` to see current DOM
- Check if element appears asynchronously (use `findBy*`)

### Mock Not Working
- Ensure mock is defined before import
- Check mock path matches actual import
- Clear mocks between tests
- Verify mock return values

### Style/CSS Issues
- CSS modules are mocked (identity-obj-proxy)
- Regular CSS is ignored
- Use className checks, not style checks
- Test behavior, not styling
