// src/components/_tests_/CardPlayZone.test.jsx

import React from 'react';
import { render, fireEvent, act, waitFor, screen } from '@testing-library/react';
import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest';
import CardPlayZone from '../CardPlayZone';

// ✅ Global fetch stub
beforeAll(() => {
  vi.stubGlobal('fetch', vi.fn().mockResolvedValue({ ok: true, json: () => ({}) }));
});

afterEach(() => {
  vi.clearAllMocks();
});

// ✅ Mocks
vi.mock('../../context/UIDisplayContext', () => ({
  useUIDisplay: () => ({
    setHandFor: vi.fn(),
    setShowHands: vi.fn(),
    setShowBidding: vi.fn(),
    deckPosition: { x: 0, y: 0 },
    setDeckPosition: vi.fn(),
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

vi.mock('../../context/PositionContext', () => ({
  usePositionContext: () => ({
    debugLog: vi.fn(),
    playerName: 'Alice',
    viewerPosition: 'P1',
    backendPositions: {
      P1: 'south',
      P2: 'west',
      P3: 'north',
      P4: 'east',
    },
    positionToDirection: {
      P1: 'south',
      P2: 'west',
      P3: 'north',
      P4: 'east',
    },
  }),
}));

vi.mock('../../context/GameStateContext', () => ({
  useGameState: () => ({
    gameId: 'test-game-id',
    players: [],
    setKitty: vi.fn(),
    bids: [],
    winningPlayerName: '',
    setWinningBid: vi.fn(),
    bidWinnerPos: '',
  }),
}));

vi.mock('../../context/RefContext', () => ({
  useZoneRefs: () => ({
    register: vi.fn(),
    get: () => ({ current: document.createElement('div') }),
  }),
}));

vi.mock('../../animations/DealAnimation', () => ({
  dealCardsClockwise: vi.fn(),
}));

vi.mock('../../animations/PlayCardAnimation', () => ({
  default: () => <div>PlayCardAnimation</div>,
}));

vi.mock('../AnimatedCard', () => ({
  default: () => <div>AnimatedCard</div>,
}));

vi.mock('../BiddingPanel', () => ({
  default: () => <div>BiddingPanel</div>,
}));

vi.mock('../BidTypePanel', () => ({
  default: () => <div>BidTypePanel</div>,
}));

// ✅ Reusable drop zone refs
const mockDropZoneRef = { current: document.createElement('div') };
const mockYourTrickRef = { current: document.createElement('div') };
const mockTheirTrickRef = { current: document.createElement('div') };

describe('<CardPlayZone />', () => {
  it('renders without crashing', () => {
    render(
      <CardPlayZone
        dropZoneRef={mockDropZoneRef}
        yourTrickRef={mockYourTrickRef}
        theirTrickRef={mockTheirTrickRef}
        onCardPlayed={() => {}}
      />
    );

    expect(screen.getByText('BiddingPanel')).toBeInTheDocument();
    expect(screen.getByText('BidTypePanel')).toBeInTheDocument();
  });

  it('handles card drop correctly', async () => {
    const mockCard = { cardImage: '2_of_hearts.png', suit: 'hearts', rank: 2 };
    const onCardPlayed = vi.fn();

    const { container } = render(
      <CardPlayZone
        dropZoneRef={mockDropZoneRef}
        yourTrickRef={mockYourTrickRef}
        theirTrickRef={mockTheirTrickRef}
        onCardPlayed={onCardPlayed}
      />
    );

    const dropZone = container.querySelector('.drop-zone.south');
    expect(dropZone).not.toBeNull();

    // Mock drag/drop data
    const dataTransfer = {
      getData: vi.fn(() => JSON.stringify(mockCard)),
    };

    // Simulate drag and drop
await act(async () => {
  fireEvent.dragEnter(dropZone);  // triggers isOver = true
});
await act(async () => {
  fireEvent.drop(dropZone, {
    dataTransfer,
    preventDefault: vi.fn(),
  });
});


    await waitFor(() => {
      expect(fetch).toHaveBeenCalledWith(
        expect.stringContaining('/game/play'),
        expect.objectContaining({
          method: 'POST',
          body: JSON.stringify({
            gameId: 'test-game-id',
            player: 'P1',
            card: mockCard,
          }),
        })
      );
    });
  });
});
