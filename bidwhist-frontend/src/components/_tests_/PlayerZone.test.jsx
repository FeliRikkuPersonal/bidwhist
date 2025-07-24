// src/components/_tests_/PlayerZone.test.jsx

import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import '@testing-library/jest-dom';
import PlayerZone from '../PlayerZone';
import { UIDisplayContext } from '../../context/UIDisplayContext';
import { RefProvider } from '../../context/RefContext';

/*
 * Mocks and helpers
 */
const mockRegister = vi.fn();
const mockSetSelectedCard = vi.fn();
const mockSetDiscardPile = vi.fn();

const renderWithProviders = (ui, contextOverrides = {}) => {
  const mockContext = {
    showHands: true,
    awardKitty: false,
    myTurn: true,
    setSelectedCard: mockSetSelectedCard,
    setDiscardPile: mockSetDiscardPile,
    playedCard: null,
    playedCardPosition: { x: 0, y: 0 },
    ...contextOverrides,
  };

  vi.mock('../../context/RefContext', () => {
    return {
      useZoneRefs: () => ({
        register: mockRegister,
        get: vi.fn(),
        debug: vi.fn(),
      }),
      RefProvider: ({ children }) => <>{children}</>,
    };
  });

  return render(
    <RefProvider>
      <UIDisplayContext.Provider value={mockContext}>
        {ui}
      </UIDisplayContext.Provider>
    </RefProvider>
  );

};

const sampleCards = [
  { cardImage: '2C.png' },
  { cardImage: '3C.png' },
  { cardImage: '4C.png' },
];

/*
 * Test Suite
 */
describe('PlayerZone', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  test('renders face-up cards when revealHand=true and showHands=true', () => {
    renderWithProviders(<PlayerZone direction="south" name="Player1" revealHand={true} cards={sampleCards} />);
    const cardImages = screen.getAllByAltText('card');
    expect(cardImages).toHaveLength(sampleCards.length);
    sampleCards.forEach((card) => {
      const cardImages = screen.getAllByAltText('card');
      expect(cardImages).toHaveLength(sampleCards.length);
      sampleCards.forEach((card, index) => {
        expect(cardImages[index]).toHaveAttribute('src', expect.stringContaining(card.cardImage));
      });

    });
  });

  test('renders card backs when revealHand=false', () => {
    renderWithProviders(<PlayerZone direction="east" name="Bot" revealHand={false} cards={sampleCards} />);
    const backs = screen.getAllByAltText('Card Back');
    expect(backs).toHaveLength(sampleCards.length);
    backs.forEach((img) => {
      expect(img).toHaveAttribute('src', expect.stringContaining('Deck_Back.png'));
    });
  });

  test('clicking a card when not in kitty mode selects it', () => {
    renderWithProviders(<PlayerZone direction="south" name="Player1" revealHand={true} cards={sampleCards} />, {
      awardKitty: false,
    });

    const cards = screen.getAllByAltText('card');
    fireEvent.click(cards[1]);

    expect(mockSetSelectedCard).toHaveBeenCalledWith([sampleCards[1]]);
    expect(mockSetDiscardPile).not.toHaveBeenCalled(); // defensive sanity check
  });


  test('clicking multiple cards in kitty mode adds/removes from discard pile', () => {
    renderWithProviders(<PlayerZone direction="south" name="Player1" revealHand={true} cards={sampleCards} />, {
      awardKitty: true,
    });

    const cards = screen.getAllByAltText('card');
    fireEvent.click(cards[0]);
    fireEvent.click(cards[1]);

    const updater1 = mockSetDiscardPile.mock.calls[0][0];
    const pile1 = updater1([]); // simulate: first card added
    expect(pile1).toContainEqual(sampleCards[0]);

    const updater2 = mockSetDiscardPile.mock.calls[1][0];
    const pile2 = updater2(pile1); // simulate: second card added
    expect(pile2).toContainEqual(sampleCards[1]);

    // Toggle off: clicking again should remove card 0
    fireEvent.click(cards[0]);
    const updater3 = mockSetDiscardPile.mock.calls[2][0];
    const pile3 = updater3(pile2);
    expect(pile3).not.toContainEqual(sampleCards[0]);
  });

});
