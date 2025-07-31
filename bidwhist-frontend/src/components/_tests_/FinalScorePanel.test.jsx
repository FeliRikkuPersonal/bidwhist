// src/components/_tests_/FinalScorePanel.test.jsx
import React from 'react';
import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, fireEvent } from '@testing-library/react';

// ✅ Fix: Define the mock *inside* the factory function
vi.mock('../../utils/handleQuit.js', () => {
  return {
    default: vi.fn(), // ✅ exported as default, still spy-able
  };
});

// ⬇️ Now import after mocking is set up
import FinalScorePanel from '../FinalScorePanel';

// ⬇️ You can now grab the mock like this
import handleQuit from '../../utils/handleQuit.js';

// Mock context hooks
const mockClearGameStateContext = vi.fn();
const mockClearUIContext = vi.fn();

vi.mock('../../context/GameStateContext', () => ({
  useGameState: () => ({
    teamAScore: 72,
    teamBScore: 45,
    clearGameStateContext: mockClearGameStateContext,
  }),
}));

vi.mock('../../context/UIDisplayContext', () => ({
  useUIDisplay: () => ({
    showFinalScore: true,
    clearUIContext: mockClearUIContext,
  }),
}));

vi.mock('../../context/PositionContext', () => ({
  usePositionContext: () => ({
    viewerPosition: 'P1',
  }),
}));

beforeEach(() => {
  vi.clearAllMocks();
  global.confirm = vi.fn(() => true);
  delete window.location;
  window.location = { reload: vi.fn() };
  localStorage.setItem('gameId', JSON.stringify('game123'));
  localStorage.setItem('mode', JSON.stringify('single'));
});

describe('FinalScorePanel', () => {
  it('renders final score and team results', () => {
    render(<FinalScorePanel />);
    expect(screen.getByText(/final score/i)).toBeInTheDocument();
    expect(screen.getByText(/team a: 72/i)).toBeInTheDocument();
    expect(screen.getByText(/team b: 45/i)).toBeInTheDocument();
  });

  it('calls handleQuit with correct args on Close', () => {
    render(<FinalScorePanel />);
    const closeBtn = screen.getByRole('button', { name: /close/i });
    fireEvent.click(closeBtn);

    expect(handleQuit).toHaveBeenCalled();
    const callArgs = handleQuit.mock.calls[0][0];
    expect(callArgs.viewerPosition).toBe('P1');
    expect(callArgs.savedGameId).toBe('game123');
    expect(callArgs.savedMode).toBe('single');
    expect(callArgs.clearUIContext).toBe(mockClearUIContext);
    expect(callArgs.clearGameStateContext).toBe(mockClearGameStateContext);
  });

  it('calls onNewGame prop when New Game is clicked', () => {
    const mockNewGame = vi.fn();
    render(<FinalScorePanel onNewGame={mockNewGame} />);
    const newGameBtn = screen.getByRole('button', { name: /new game/i });
    fireEvent.click(newGameBtn);
    expect(mockNewGame).toHaveBeenCalled();
  });
});
