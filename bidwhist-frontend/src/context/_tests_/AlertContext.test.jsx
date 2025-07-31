// src/context/_tests_/AlertContext.test.jsx

import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import { describe, test, expect, vi } from 'vitest';
import { AlertProvider, useAlert } from '../AlertContext';

// ✅ Mock Alert component to track props without triggering real behavior
vi.mock('../../components/Alert', () => ({
  default: ({ message, type, onClose }) => (
    <div data-testid="mock-alert">
      <p>{type}: {message}</p>
      <button onClick={onClose}>Dismiss</button>
    </div>
  )
}));

// ✅ Minimal consumer to test context behavior
const AlertTester = () => {
  const { showAlert } = useAlert();
  return (
    <button onClick={() => showAlert('Test message', 'success')}>
      Trigger Alert
    </button>
  );
};

describe('AlertContext', () => {
  test('displays alert when showAlert is called', () => {
    render(
      <AlertProvider>
        <AlertTester />
      </AlertProvider>
    );

    fireEvent.click(screen.getByText('Trigger Alert'));

    const alert = screen.getByTestId('mock-alert');
    expect(alert).toBeInTheDocument();
    expect(alert).toHaveTextContent('success: Test message');
  });

  test('closes alert when onClose is called', () => {
    render(
      <AlertProvider>
        <AlertTester />
      </AlertProvider>
    );

    fireEvent.click(screen.getByText('Trigger Alert'));
    expect(screen.getByTestId('mock-alert')).toBeInTheDocument();

    fireEvent.click(screen.getByText('Dismiss'));
    expect(screen.queryByTestId('mock-alert')).not.toBeInTheDocument();
  });
});
