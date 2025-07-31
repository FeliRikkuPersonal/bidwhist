// src/__tests__/App.test.jsx

import React from 'react';
import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest';
import { render, screen, act } from '@testing-library/react';
import App from '../App.jsx';
import { AllProviders } from '../test-utils/AllProviders.jsx';

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


describe('<App />', () => {
  beforeEach(() => {
    // ✅ Mock global fetch
    vi.stubGlobal('fetch', vi.fn().mockResolvedValue({
      ok: true,
      json: () => Promise.resolve(mockFetchResponse),
    }));

    // ✅ Fake fetch response
    vi.spyOn(GameState, 'useGameState').mockReturnValue({
      // 🧠 include `players`!
      players: [
        { name: 'Alice', position: 'P1', team: 'A' },
        { name: 'Bob', position: 'P2', team: 'B' },
        { name: 'Carol', position: 'P3', team: 'A' },
        { name: 'Dan', position: 'P4', team: 'B' },
      ],
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
    render(<AllProviders><App /></AllProviders>);
    expect(screen.getByText('Mocked ModeSelector')).toBeInTheDocument();
  });
});
