// src/animations/__tests__/DealAnimation.test.jsx

import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest';
import { dealCardsClockwise } from '../DealAnimation';

describe('dealCardsClockwise', () => {
  beforeEach(() => {
    vi.useFakeTimers();
  });

  afterEach(() => {
    vi.clearAllTimers();
    vi.restoreAllMocks();
  });

  it('schedules cards to animate to the correct positions in clockwise rounds', () => {
    const playerPositions = ['P1', 'P2', 'P3', 'P4'];
    const cards = [
      { owner: 'P1', cardImage: '1H' },
      { owner: 'P2', cardImage: '2H' },
      { owner: 'P3', cardImage: '3H' },
      { owner: 'P4', cardImage: '4H' },
    ];
    const positionMap = { P1: 'south', P2: 'west', P3: 'north', P4: 'east' };

    const fakeRect = { left: 100, top: 200, width: 60, height: 90 };
    const parentRect = { left: 50, top: 100 };

    const get = vi.fn((key) => ({
      current: { getBoundingClientRect: () => fakeRect },
    }));

    vi.spyOn(document, 'querySelector').mockReturnValue({
      getBoundingClientRect: () => parentRect,
    });

    const setAnimatedCards = vi.fn();
    const setShowAnimatedCards = vi.fn();
    const setBidPhase = vi.fn();
    const onComplete = vi.fn();

    dealCardsClockwise(
      playerPositions,
      cards,
      positionMap,
      onComplete,
      get,
      { x: 0, y: 0 },
      setAnimatedCards,
      setShowAnimatedCards,
      setBidPhase
    );

    // Advance all timeouts (4 cards * 120ms each + 500ms buffer)
    vi.advanceTimersByTime(4 * 120 + 500);

    // Should have scheduled 4 animations
    expect(setAnimatedCards).toHaveBeenCalledTimes(4);
    expect(setAnimatedCards.mock.calls[0][0]).toBeInstanceOf(Function);

    // Final callbacks
    expect(onComplete).toHaveBeenCalled();
    expect(setShowAnimatedCards).toHaveBeenCalledWith(false);
    expect(setBidPhase).toHaveBeenCalledWith(true);
  });

  it('logs a warning if a player ref is missing', () => {
    const warnSpy = vi.spyOn(console, 'warn').mockImplementation(() => {});
    const get = vi.fn(() => null); // missing refs

    dealCardsClockwise(
      ['P1'],
      [{ owner: 'P1', cardImage: '1H' }],
      { P1: 'south' },
      undefined,
      get,
      { x: 0, y: 0 },
      vi.fn(),
      vi.fn(),
      vi.fn()
    );

    vi.runAllTimers();
    expect(warnSpy).toHaveBeenCalledWith(expect.stringContaining('Missing ref for player'));
    warnSpy.mockRestore();
  });
});
