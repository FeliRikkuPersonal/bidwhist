// src/animations/__tests__/PlayCardAnimation.test.jsx

import { describe, it, vi, expect, beforeEach, afterEach } from 'vitest';
import { render, screen, act } from '@testing-library/react';
import PlayCardAnimation from '../PlayCardAnimation';
import React from 'react';

describe('PlayCardAnimation', () => {
  let fromRef, toRef, onComplete;

  beforeEach(() => {
    fromRef = {
      current: {
        getBoundingClientRect: () => ({
          left: 100,
          top: 100,
          width: 50,
          height: 70,
        }),
      },
    };

    toRef = {
      current: {
        getBoundingClientRect: () => ({
          left: 300,
          top: 400,
          width: 50,
          height: 70,
        }),
      },
    };

    const container = document.createElement('div');
    container.className = 'floating-card-layer';
    Object.defineProperty(container, 'getBoundingClientRect', {
      value: () => ({
        left: 50,
        top: 50,
        width: 800,
        height: 600,
      }),
    });
    document.body.appendChild(container);

    onComplete = vi.fn();
    vi.useFakeTimers();
  });

  afterEach(() => {
    vi.useRealTimers();
    document.querySelector('.floating-card-layer')?.remove();
  });

  it('animates card and calls onComplete', async () => {
    const card = { cardImage: '7S', owner: 'P1' };

    render(
      <PlayCardAnimation
        card={card}
        fromRef={fromRef}
        toRef={toRef}
        onComplete={onComplete}
        direction="south"
      />
    );

    // Advance past first timeout (10ms delay)
    await act(() => {
      vi.advanceTimersByTimeAsync(15); // triggers animation start
    });

    // Advance past second timeout (650ms for animation)
    await act(() => {
      vi.advanceTimersByTimeAsync(700); // triggers onComplete
    });

    // Check the card rendered
    const img = screen.getByAltText('Played card');
    expect(img).toBeInTheDocument();
    expect(img.src).toContain('7S');

    // Confirm the animation completed
    expect(onComplete).toHaveBeenCalled();
  });
});
