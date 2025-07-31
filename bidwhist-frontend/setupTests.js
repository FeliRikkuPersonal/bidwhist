// setupTests.js
import '@testing-library/jest-dom';

/**
 * Global test setup for all Vitest test suites.
 * This file is automatically loaded if set in vitest.config.js as `setupFiles`.
 */
beforeAll(() => {
  // Optional: only mock if not available (mostly for JSDOM environments)
  if (!window.localStorage) {
    vi.stubGlobal('localStorage', {
      getItem: vi.fn(),
      setItem: vi.fn(),
      removeItem: vi.fn(),
      clear: vi.fn(),
    });
  }
});

beforeEach(() => {
  localStorage.setItem('kitty', JSON.stringify([]));
  localStorage.setItem('mode', JSON.stringify('single'));
  localStorage.setItem('difficulty', JSON.stringify('EASY'));
  localStorage.setItem('activeGame', JSON.stringify(false));
  localStorage.setItem('foredBid', JSON.stringify(false));

  localStorage.setItem('playerName', JSON.stringify('Alice'));
  localStorage.setItem('viewerPosition', JSON.stringify('P1'));
  localStorage.setItem('viewerTeam', JSON.stringify('A'));
  localStorage.setItem(
    'backendPositions',
    JSON.stringify({
      P1: 'Alice',
      P2: 'Bob',
      P3: 'Carol',
      P4: 'Dan',
    })
  );
  localStorage.setItem('playedCardsByDirection', JSON.stringify({
  north: { suit: 'H', rank: 'K' },
  south: null,
  east: null,
  west: null,
}));

  localStorage.setItem(
    'handMap',
    JSON.stringify({
      north: [],
      south: [],
      east: [],
      west: [],
    })
  );
  localStorage.setItem('showGameScreen', JSON.stringify(false));
  localStorage.setItem('showAnimatedCards', JSON.stringify(false));
  localStorage.setItem('playedCardPosition', JSON.stringify(null));
  localStorage.setItem('showShuffle', JSON.stringify(false));
  localStorage.setItem('showHands', JSON.stringify(false));
  localStorage.setItem('showBidding', JSON.stringify(false));
  localStorage.setItem('bidPhase', JSON.stringify(false));
  localStorage.setItem('kittyPhase', JSON.stringify(false));
  localStorage.setItem('showFinalizeBid', JSON.stringify(false));
  localStorage.setItem('awardKitty', JSON.stringify(false));
  localStorage.setItem('discardPile', JSON.stringify([]));
  localStorage.setItem('loadGame', JSON.stringify(false));
  localStorage.setItem('showLobby', JSON.stringify(false));
  localStorage.setItem('teamATricks', JSON.stringify(0));
  localStorage.setItem('teamBTricks', JSON.stringify(0));
  localStorage.setItem('animatedCards', JSON.stringify([]));
  localStorage.setItem('showFinalScore', JSON.stringify(false));
  localStorage.setItem(
    'playedCardsByDirection',
    JSON.stringify({
      north: null,
      south: null,
      east: null,
      west: null,
    })
  );
});

afterEach(() => {
  localStorage.clear();
});
