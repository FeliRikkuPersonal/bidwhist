// src/components/_tests_/GameScreen.test.jsx

import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import GameScreen from '../GameScreen';

// ✅ Mock *before* importing the component
vi.mock('../../context/GameStateContext', () => ({
  useGameState: () => ({
    gameId: 'abc123',
    updateFromResponse: vi.fn(),
    players: [
      { position: 'N', name: 'Alice' },
      { position: 'S', name: 'Bob' },
      { position: 'E', name: 'Charlie' },
      { position: 'W', name: 'Dana' },
    ],
  }),
}));

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
}));

vi.mock('../../context/UIDisplayContext', () => ({
  useUIDisplay: () => ({
    handMap: {
      south: [{ suit: 'H', rank: 'A' }, { suit: 'S', rank: 'K' }],
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

    /* 🧠 Add ALL of these or your test will crash again */
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
}));


vi.mock('../../context/AlertContext', () => ({
  useAlert: () => ({
    showAlert: vi.fn(),
  }),
}));

// ✅ THIS mock prevents CardPlayZone crash
vi.mock('../../context/RefContext', () => ({
  useZoneRefs: () => ({
    register: vi.fn(),
    setDeckPosition: vi.fn(), // Must be defined
  }),
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
    render(<GameScreen />);

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
