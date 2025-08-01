// src/components/_tests_/AnimatedCard.test.jsx

import React from 'react';
import { render, screen, act, cleanup } from '@testing-library/react';
import AnimatedCard from '../AnimatedCard';
import * as CardUtils from '../../utils/CardUtils';
import { vi, describe, test, expect, beforeEach, afterEach } from 'vitest';
import { usePositionContext } from '../../context/PositionContext';

/* 🧪 Preserve real module, only mock usePositionContext */
vi.mock('../../context/PositionContext', async () => {
  const actual = await vi.importActual('../../context/PositionContext');
  return {
    ...actual,
    usePositionContext: vi.fn(),
  };
});

/* 🧪 Mock getCardImage */
vi.mock('../../utils/CardUtils', () => ({
  getCardImage: vi.fn(),
}));

vi.useFakeTimers();

describe('AnimatedCard component', () => {
  const mockCard = { suit: 'SPADES', rank: 'ACE' };
  const from = { x: 100, y: 200 };
  const to = { x: 300, y: 400 };
  const mockImageSrc = '/mocked/path/to/ace_spades.png';

  beforeEach(() => {
    usePositionContext.mockReturnValue({ viewerName: 'Alice' });
    CardUtils.getCardImage.mockReturnValue(mockImageSrc);
  });

  afterEach(() => {
    vi.clearAllTimers();
    vi.clearAllMocks();
    cleanup();
  });

  test('renders the card at the initial position', () => {
    render(<AnimatedCard card={mockCard} from={from} to={to} />);
    const cardImg = screen.getByAltText('Animated card');
    expect(cardImg).toHaveStyle(`left: ${from.x}px`);
    expect(cardImg).toHaveStyle(`top: ${from.y}px`);
    expect(cardImg).toHaveAttribute('src', mockImageSrc);
  });

  test('moves the card to the destination and calls onComplete', () => {
    const onComplete = vi.fn();

    render(<AnimatedCard card={mockCard} from={from} to={to} onComplete={onComplete} />);

    act(() => {
      vi.advanceTimersByTime(700);
    });

    const cardImg = screen.getByAltText('Animated card');
    expect(cardImg).toHaveStyle(`left: ${to.x}px`);
    expect(cardImg).toHaveStyle(`top: ${to.y}px`);
    expect(onComplete).toHaveBeenCalledTimes(1);
  });
});
