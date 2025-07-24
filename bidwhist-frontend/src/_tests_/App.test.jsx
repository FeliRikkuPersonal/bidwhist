// src/__tests__/App.test.jsx

import React from 'react';
import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest';
import { render, screen, act } from '@testing-library/react';
import App from '../App.jsx';

// ✅ Correct mock format for ESM components
vi.mock('../components/GameScreen', () => ({
  default: () => <div>Game Screen</div>,
}));
vi.mock('../components/ModeSelector', () => ({
  default: () => <div>Mocked ModeSelector</div>,
}));
vi.mock('../components/LobbyScreen', () => ({
  default: () => <div>Lobby Screen</div>,
}));
vi.mock('../components/Scoreboard', () => ({
  default: ({ bidType }) => <div>Scoreboard {bidType}</div>,
}));

// ✅ Import contexts to be spied on
import * as GameState from '../context/GameStateContext';
import * as PositionContext from '../context/PositionContext';
import * as UIDisplay from '../context/UIDisplayContext';

// ✅ Fake fetch response
const mockFetchResponse = {
  players: [
    { name: 'Alice', position: 'P1' },
    { name: 'Bob', position: 'P2' },
    { name: 'Carol', position: 'P3' },
    { name: 'Dan', position: 'P4' },
  ],
  playerPosition: 'P1',
  viewerName: 'Alice',
  currentTurnIndex: 0,
  phase: 'BID',
  shuffledDeck: [],
  firstBidder: 'P2',
  bidTurnIndex: 0,
};

describe('<App />', () => {
  beforeEach(() => {
    // ✅ Mock global fetch
    vi.stubGlobal('fetch', vi.fn().mockResolvedValue({
      ok: true,
      json: () => Promise.resolve(mockFetchResponse),
    }));

    // ✅ Stub context hooks
    vi.spyOn(GameState, 'useGameState').mockReturnValue({
      updateFromResponse: vi.fn(),
      gameId: '',
      setGameId: vi.fn(),
      setPlayers: vi.fn(),
      currentTurnIndex: 0,
      setCurrentTurnIndex: vi.fn(),
      phase: 'BID',
      setPhase: vi.fn(),
      setShuffledDeck: vi.fn(),
      setFirstBidder: vi.fn(),
      setBidTurnIndex: vi.fn(),
      mode: '',
      setMode: vi.fn(),
      difficulty: 'easy',
      lobbySize: 0,
      activeGame: true,
      setActiveGame: vi.fn(),
      bidType: 'uptown',
    });

    vi.spyOn(PositionContext, 'usePositionContext').mockReturnValue({
      playerName: '',
      setPlayerName: vi.fn(),
      viewerPosition: '',
      setViewerPosition: vi.fn(),
      backendPositions: {},
      setBackendPositions: vi.fn(),
    });

    vi.spyOn(UIDisplay, 'useUIDisplay').mockReturnValue({
      setShowAnimatedCards: vi.fn(),
      showGameScreen: false,
      setShowGameScreen: vi.fn(),
      setMyTurn: vi.fn(),
      loadGame: false,
      setLoadGame: vi.fn(),
      showLobby: false,
      setShowLobby: vi.fn(),
      queueAnimationFromResponse: vi.fn(),
    });

    vi.useFakeTimers();
  });

  afterEach(() => {
    vi.clearAllMocks();
    vi.useRealTimers();
  });

  it('renders the mocked ModeSelector by default', () => {
    render(<App />);
    expect(screen.getByText('Mocked ModeSelector')).toBeInTheDocument();
  });

  // ❓ Optional: if your app has a button or trigger for startGame, this test will need adjustments
  it.skip('calls startGame and shows GameScreen after fetch', async () => {
    render(<App />);

    const startBtn = screen.getByText('Start Game');
    await act(async () => {
      startBtn.click();
      vi.runAllTimers();
    });

    expect(global.fetch).toHaveBeenCalledWith(
      expect.stringMatching(/\/game\/start/),
      expect.objectContaining({
        method: 'POST',
        body: JSON.stringify({
          playerName: 'Alice',
          difficulty: 'easy',
          gameId: '123',
        }),
      })
    );

    expect(await screen.findByText('Game Screen')).toBeInTheDocument();
  });
});
