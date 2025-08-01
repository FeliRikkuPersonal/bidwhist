// src/context/__tests__/RefContext.test.jsx

import React, { useRef, useEffect } from 'react';
import { render, screen } from '@testing-library/react';
import { RefProvider, useZoneRefs } from '../RefContext';
import '@testing-library/jest-dom';
import { vi } from 'vitest';
import { AllProviders } from '../../test-utils/AllProviders';

const TestComponent = () => {
  const myRef = useRef(null);
  const { register, get, debug } = useZoneRefs();

  useEffect(() => {
    register('my-zone', myRef);
    debug();
  }, [register, debug]);

  const handleClick = () => {
    const result = get('my-zone');
    if (result) {
      result.current.textContent = 'Ref was accessed!';
    }
  };

  return (
    <>
      <div ref={myRef} data-testid="refTarget">
        Waiting...
      </div>
      <button onClick={handleClick}>Trigger Get</button>
    </>
  );
};

describe('RefContext', () => {
  it('registers and retrieves refs correctly', () => {
    render(
      <AllProviders>
        <TestComponent />
      </AllProviders>
    );

    const button = screen.getByText('Trigger Get');
    button.click();

    const target = screen.getByTestId('refTarget');
    expect(target).toHaveTextContent('Ref was accessed!');
  });

  it('throws an error if used outside RefProvider', () => {
    const originalError = console.error;
    console.error = vi.fn(); // Silence expected React context warning

    function TestThrowsOutsideProvider() {
      useZoneRefs(); // should throw
      return null;
    }

    // ❌ REMOVE AllProviders — we want it outside RefProvider
    expect(() => render(<TestThrowsOutsideProvider />)).toThrow(
      'useZoneRefs must be used within a RefProvider'
    );

    console.error = originalError;
  });

  it('warns when trying to register invalid ref', () => {
    const warnSpy = vi.spyOn(console, 'warn').mockImplementation(() => {});
    const InvalidRefComponent = () => {
      const { register } = useZoneRefs();
      useEffect(() => {
        register('bad-ref', null);
      }, []);
      return null;
    };

    render(
      <AllProviders>
        <InvalidRefComponent />
      </AllProviders>
    );

    expect(warnSpy).toHaveBeenCalledWith(
      '[register] Attempted to register bad-ref with invalid ref',
      null
    );

    warnSpy.mockRestore();
  });
});
