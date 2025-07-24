// src/animations/__tests__/PlayCardAnimation.test.jsx

import React, { useRef } from 'react';
import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest';
import { render, screen, act, cleanup } from '@testing-library/react';
import PlayCardAnimation from '../PlayCardAnimation';

describe('PlayCardAnimation', () => {
  let fromRef, toRef;

  beforeEach(() => {
    vi.useFakeTimers();

    // Set up fake bounding boxes for both refs and the container
    const fakeRect = { left: 100, top: 200, width: 60, height: 90 };
    const containerRect = { left: 50, top: 100 };

    vi.spyOn(document, 'querySelector').mockReturnValue({
      getBoundingClientRect: () => containerRect,
    });

    fromRef = { current: { getBoundingClientRect: () => fakeRect } };
    toRef = { current: { getBoundingClientRect: () => fakeRect } };
  });

  afterEach(() => {
    cleanup();
    vi.clearAllTimers();
    vi.restoreAllMocks();
  });

  it('renders the animated card and calls onComplete after timeout', () => {
    const onComplete = vi.fn();

    act(() => {
      render(
        <PlayCardAnimation
          card={{ cardImage: '5H.png' }}
          fromRef={fromRef}
          toRef={toRef}
          onComplete={onComplete}
        />
      );
    });

    const img = screen.getByAltText('Played card');
    expect(img).toBeInTheDocument();
    expect(img).toHaveAttribute('src', '/static/img/deck/5H.png');

    // Fast-forward the animation duration (700ms)
    act(() => {
      vi.advanceTimersByTime(700);
    });

    expect(onComplete).toHaveBeenCalled();
  });

  it('does not crash or call onComplete if refs are missing', () => {
    const onComplete = vi.fn();

    act(() => {
      render(
        <PlayCardAnimation
          card={{ cardImage: '8C.png' }}
          fromRef={null}
          toRef={null}
          onComplete={onComplete}
        />
      );
    });

    // Should not render anything
    expect(screen.queryByAltText('Played card')).toBeNull();

    // Wait a moment to confirm nothing fires
    act(() => {
      vi.advanceTimersByTime(1000);
    });

    expect(onComplete).not.toHaveBeenCalled();
  });
});
