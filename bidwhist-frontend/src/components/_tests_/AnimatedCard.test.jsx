// src/components/_tests_/AnimatedCard.test.jsx

import React from 'react';
import { render, screen, act } from '@testing-library/react';
import AnimatedCard from '../AnimatedCard';
import { usePositionContext } from '../../context/PositionContext';
import * as CardUtils from '../../utils/CardUtils';

/* 🧪 Mock viewer context */
vi.mock('../../context/PositionContext', () => ({
  usePositionContext: vi.fn()
}));

/* 🧪 Mock getCardImage */
vi.mock('../../utils/CardUtils', () => ({
  getCardImage: vi.fn()
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

    // Wait for the useEffect + setTimeout
    act(() => {
      vi.advanceTimersByTime(700);
    });

    const cardImg = screen.getByAltText('Animated card');
    expect(cardImg).toHaveStyle(`left: ${to.x}px`);
    expect(cardImg).toHaveStyle(`top: ${to.y}px`);
    expect(onComplete).toHaveBeenCalled();
  });
});
