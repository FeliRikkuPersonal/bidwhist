// src/components/__tests__/Alert.test.jsx

import React from 'react';
import { render, screen, fireEvent, act } from '@testing-library/react';
import Alert from '../Alert';

vi.useFakeTimers();

describe('Alert component', () => {
  test('renders the message and correct type class', () => {
    render(
      
        <Alert message="Success!" type="persist" onClose={() => { }} />
      
    );
    expect(screen.getByText('Success!')).toBeInTheDocument();
    expect(screen.getByRole('button')).toHaveClass('close-btn');
    expect(screen.getByText('Success!').parentElement).toHaveClass('alert persist');
  });

  test('calls onClose when close button is clicked', () => {
    const onClose = vi.fn();
    render(
      
        <Alert message="Click to close" type="persist" onClose={onClose} />
      
    );
    fireEvent.click(screen.getByRole('button'));
    expect(onClose).toHaveBeenCalledTimes(1);
  });

  test('resets timer when message changes', () => {
    const onClose = vi.fn();
    const { rerender } = render(
      
        <Alert message="First message" type="success" onClose={onClose} />
      
    );

    act(() => {
      vi.advanceTimersByTime(3000);
    });

    rerender(
      
        <Alert message="New message" type="success" onClose={onClose} />
      
    );

    act(() => {
      vi.advanceTimersByTime(3000); // not enough yet
    });
    expect(onClose).not.toHaveBeenCalled();

    act(() => {
      vi.advanceTimersByTime(2000); // now full 5s
    });
    expect(onClose).toHaveBeenCalledTimes(1);
  });

  test('auto-dismisses after 5 seconds for success alert', () => {
    const onClose = vi.fn();
    render(
      
        <Alert message="Auto close" type="success" onClose={onClose} />
      
    );

    act(() => {
      vi.advanceTimersByTime(5000);
    });

    expect(onClose).toHaveBeenCalledTimes(1);
  });

});
