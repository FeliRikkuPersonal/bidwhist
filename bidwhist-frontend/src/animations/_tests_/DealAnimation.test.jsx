// src/animations/__tests__/DealAnimation.test.js

import { describe, it, vi, expect, beforeEach } from 'vitest';
import { dealCardsClockwise } from '../DealAnimation';

describe('dealCardsClockwise', () => {
  const playerPositions = ['P1', 'P2', 'P3', 'P4'];
  const positionMap = { P1: 'south', P2: 'west', P3: 'north', P4: 'east' };
  const deckPosition = { x: 100, y: 100 };
  const mockSessionKey = 'session-xyz';

  const mockGet = vi.fn();
  const mockSetAnimatedCards = vi.fn();
  const mockSetShowAnimatedCards = vi.fn();
  const mockSetBidPhase = vi.fn();
  const mockSetPlayedCardsByDirection = vi.fn();
  const mockSetShowHands = vi.fn();
  const mockOnComplete = vi.fn();

  // Fake DOM bounds
  const fakeCardRect = {
    left: 300,
    top: 200,
    width: 50,
    height: 70,
  };

  const fakeParentBounds = {
    left: 100,
    top: 100,
  };

  // Add fake DOM structure
  beforeEach(() => {
    mockSetAnimatedCards.mockReset();
    mockOnComplete.mockReset();

    // Create fake floating card layer div
    const fakeLayer = document.createElement('div');
    fakeLayer.className = 'floating-card-layer';
    document.body.appendChild(fakeLayer);

    vi.spyOn(document, 'querySelector').mockImplementation((selector) => {
      if (selector === '.floating-card-layer') {
        return {
          getBoundingClientRect: () => fakeParentBounds,
        };
      }
      return null;
    });

    mockGet.mockImplementation((key) => ({
      current: {
        getBoundingClientRect: () => fakeCardRect,
      },
    }));
  });

  it('animates all cards and triggers completion', async () => {
    const cards = [];

    // Simulate 2 cards per player (total: 8)
    playerPositions.forEach((pos, i) => {
      cards.push({ owner: pos, cardImage: `${pos}-1` }, { owner: pos, cardImage: `${pos}-2` });
    });

    dealCardsClockwise(
      playerPositions,
      cards,
      positionMap,
      mockOnComplete,
      mockGet,
      deckPosition,
      mockSetAnimatedCards,
      mockSetShowAnimatedCards,
      mockSetBidPhase,
      mockSessionKey,
      mockSetPlayedCardsByDirection,
      mockSetShowHands
    );

    // Immediately sets empty played card map
    expect(mockSetPlayedCardsByDirection).toHaveBeenCalledWith({
      north: null,
      south: null,
      east: null,
      west: null,
    });

    // Wait for the final timeout (8 cards x 120ms + 500 buffer)
    await new Promise((resolve) => setTimeout(resolve, 1600));

    // Should have been called at least once
    expect(mockSetAnimatedCards).toHaveBeenCalled();
    expect(mockSetAnimatedCards.mock.calls.length).toBeGreaterThan(0);

    expect(mockOnComplete).toHaveBeenCalled();
    expect(mockSetShowAnimatedCards).toHaveBeenCalledWith(false);
    expect(mockSetBidPhase).toHaveBeenCalledWith(true);
    expect(mockSetShowHands).toHaveBeenCalledWith(true);
  });
});
