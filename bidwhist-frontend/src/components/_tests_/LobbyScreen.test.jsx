// src/components/_tests_/LobbyScreen.test.jsx

import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, fireEvent } from '@testing-library/react';
import LobbyScreen from '../LobbyScreen';

/* ─────────── Mocks ─────────── */
const mockSetShowLobby = vi.fn();
const mockSetShowGameScreen = vi.fn();
const mockSetBackendPositions = vi.fn();

vi.mock('../../context/GameStateContext', () => ({
  useGameState: () => ({
    players: [
      { name: 'Alice', position: 'south' },
      { name: 'Bob', position: 'west' },
      { name: 'Carol', position: 'north' },
      { name: 'Dave', position: 'east' },
    ],
  }),
}));

vi.mock('../../context/PositionContext', () => ({
  usePositionContext: () => ({
    backendPositions: {
      south: 'Alice',
      west: 'Bob',
      north: 'Carol',
      east: 'Dave',
    },
    setBackendPositions: mockSetBackendPositions,
  }),
}));

vi.mock('../../context/UIDisplayContext', () => ({
  useUIDisplay: () => ({
    setShowLobby: mockSetShowLobby,
    setShowGameScreen: mockSetShowGameScreen,
  }),
}));

/* ─────────── Test Suite ─────────── */
describe('LobbyScreen (minimal behavior)', () => {
  beforeEach(() => {
    vi.clearAllMocks();
    localStorage.clear();
  });

  it('renders lobby screen and player names', () => {
    render(<LobbyScreen gameId="GAME123" />);

    expect(screen.getByText(/Lobby Code/i)).toHaveTextContent('GAME123');
    expect(screen.getByText(/Waiting for players/i)).toHaveTextContent('(4/4)');
    expect(screen.getByText('Alice')).toBeInTheDocument();
    expect(screen.getByText('Bob')).toBeInTheDocument();
    expect(screen.getByText('Carol')).toBeInTheDocument();
    expect(screen.getByText('Dave')).toBeInTheDocument();
  });

  it('renders "Ready" button when all players and positions are present, and responds to click', () => {
    render(<LobbyScreen gameId="GAME456" />);

    const readyBtn = screen.getByRole('button', { name: /ready/i });
    expect(readyBtn).toBeInTheDocument();

    fireEvent.click(readyBtn);

    expect(mockSetShowLobby).toHaveBeenCalledWith(false);
    expect(mockSetShowGameScreen).toHaveBeenCalledWith(true);
  });
});
