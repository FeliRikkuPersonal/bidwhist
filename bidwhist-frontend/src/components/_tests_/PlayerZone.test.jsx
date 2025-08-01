// src/components/_tests_/PlayerZone.test.jsx

import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, fireEvent } from '@testing-library/react';
import PlayerZone from '../PlayerZone';

/* ─────────── Mock Dependencies ─────────── */
const mockSetDiscardPile = vi.fn();
const mockSetSelectedCard = vi.fn();
const mockRegister = vi.fn();
const mockThrowAlert = vi.fn();

vi.mock('../../context/UIDisplayContext', () => ({
  useUIDisplay: () => ({
    showHands: true,
    awardKitty: false,
    myTurn: true,
    setDiscardPile: mockSetDiscardPile,
    setSelectedCard: mockSetSelectedCard,
    playedCard: null,
    playedCardPosition: { x: 0, y: 0 },
  }),
}));

vi.mock('../../context/RefContext', () => ({
  useZoneRefs: () => ({
    register: mockRegister,
  }),
}));

vi.mock('../../hooks/useThrowAlert', () => ({
  useThrowAlert: () => mockThrowAlert,
}));

/* ─────────── Sample Props ─────────── */
const mockCards = [
  { cardImage: 'AS.png' }, // Ace of Spades
  { cardImage: 'KD.png' }, // King of Diamonds
];

/* ─────────── Test Suite ─────────── */
describe('PlayerZone (minimal behavior)', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('renders player zone with face-up cards and name (south)', () => {
    render(<PlayerZone direction="south" name="Test Player" revealHand={true} cards={mockCards} />);

    expect(screen.getByText('Test Player')).toBeInTheDocument();
    const images = screen.getAllByRole('img');
    expect(images).toHaveLength(2);
    expect(images[0]).toHaveAttribute('src', '/static/img/deck/AS.png');
    expect(images[1]).toHaveAttribute('src', '/static/img/deck/KD.png');
  });

  it('renders card backs when revealHand is false', () => {
    render(<PlayerZone direction="west" name="Player West" revealHand={false} cards={mockCards} />);

    expect(screen.getByText('Player West')).toBeInTheDocument();
    const images = screen.getAllByRole('img');
    expect(images).toHaveLength(2);
    images.forEach((img) => {
      expect(img).toHaveAttribute('src', '/static/img/deck/Deck_Back.png');
    });
  });

  it('highlights card on click when not discarding (awardKitty = false)', () => {
    render(
      <PlayerZone direction="south" name="Click Tester" revealHand={true} cards={mockCards} />
    );

    const firstCard = screen.getAllByRole('img')[0];
    fireEvent.click(firstCard);

    expect(mockSetSelectedCard).toHaveBeenCalledWith([mockCards[0]]);
  });
});
