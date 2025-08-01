// src/components/_tests_/GameScreen.test.jsx

import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import GameScreen from '../GameScreen';
import { AllProviders } from '../../test-utils/AllProviders';
import React from 'react';

// ✅ Mock GameStateContext with hook AND provider
vi.mock('../../context/GameStateContext', () => ({
  useGameState: () => ({
    gameId: 'abc123',
    updateFromResponse: vi.fn(),
    players: [
      { position: 'N', name: 'Alice', team: 'A' },
      { position: 'S', name: 'Bob', team: 'A' },
      { position: 'E', name: 'Charlie', team: 'B' },
      { position: 'W', name: 'Dana', team: 'B' },
    ],
  }),
  GameStateProvider: ({ children }) => <div>{children}</div>,
}));

// ✅ Mock PositionContext with hook AND provider
vi.mock('../../context/PositionContext', () => ({
  usePositionContext: () => ({
    viewerPosition: 'S',
    positionToDirection: {
      N: 'north',
      S: 'south',
      E: 'east',
      W: 'west',
    },
  }),
  PositionProvider: ({ children }) => <div>{children}</div>,
}));

// ✅ Mock AlertContext with hook AND provider
vi.mock('../../context/AlertContext', () => ({
  useAlert: () => ({
    showAlert: vi.fn(),
  }),
  AlertProvider: ({ children }) => <div>{children}</div>,
}));

// ✅ Mock UIDisplayContext with hook AND provider
vi.mock('../../context/UIDisplayContext', () => ({
  useUIDisplay: () => ({
    handMap: {
      south: [
        { suit: 'H', rank: 'A' },
        { suit: 'S', rank: 'K' },
      ],
    },
    awardKitty: true,
    setAwardKitty: vi.fn(),
    discardPile: [
      { suit: 'H', rank: 'A' },
      { suit: 'S', rank: 'K' },
      { suit: 'C', rank: '2' },
      { suit: 'D', rank: '3' },
      { suit: 'H', rank: '4' },
      { suit: 'S', rank: '5' },
    ],
    setDiscardPile: vi.fn(),
    teamATricks: 2,
    teamBTricks: 1,
    playedCardsByDirection: {
      south: { suit: 'S', rank: 'A' },
      west: { suit: 'C', rank: 'K' },
      north: null,
      east: null,
    },

    setDeckPosition: vi.fn(),
    setHandFor: vi.fn(),
    setShowHands: vi.fn(),
    setShowBidding: vi.fn(),
    deckPosition: { x: 0, y: 0 },
    setPlayedCard: vi.fn(),
    setPlayedCardPosition: vi.fn(),
    animatedCards: [],
    setAnimatedCards: vi.fn(),
    showAnimatedCards: false,
    setShowAnimatedCards: vi.fn(),
    setBidPhase: vi.fn(),
    setShowFinalizeBid: vi.fn(),
    animationQueue: [],
    setTeamATricks: vi.fn(),
    setTeamBTricks: vi.fn(),
  }),
  UIDisplayProvider: ({ children }) => <div>{children}</div>,
}));

// ✅ Mock RefContext with hook AND provider
vi.mock('../../context/RefContext', () => ({
  useZoneRefs: () => ({
    register: vi.fn(),
    setDeckPosition: vi.fn(),
    get: vi.fn(() => ({ current: null })), // <- ✅ Mock added
  }),
  RefProvider: ({ children }) => <div>{children}</div>,
}));

beforeEach(() => {
  global.fetch = vi.fn(() =>
    Promise.resolve({
      ok: true,
      json: () => Promise.resolve({ message: 'Success' }),
    })
  );
});

afterEach(() => {
  vi.resetAllMocks();
});

describe('GameScreen', () => {
  it('renders Submit button and sends discard request when clicked', async () => {
    render(
      <AllProviders>
        <GameScreen />
      </AllProviders>
    );

    const button = screen.getByText('Submit');
    expect(button).toBeInTheDocument();

    fireEvent.click(button);

    await waitFor(() => {
      expect(global.fetch).toHaveBeenCalledWith(
        expect.stringContaining('/game/kitty'),
        expect.objectContaining({ method: 'POST' })
      );
    });
  });
});
