// src/components/__tests__/FinalScorePanel.test.jsx

import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import FinalScorePanel from '../FinalScorePanel';
import { vi } from 'vitest';
import { useGameState } from '../../context/GameStateContext';

vi.mock('../../context/GameStateContext', () => ({
  useGameState: vi.fn(),
}));

describe('FinalScorePanel', () => {
  const mockScores = [
    { team: 'Hearts', points: 250 },
    { team: 'Spades', points: 180 },
  ];

  it('does not render when finalScore is false', () => {
    useGameState.mockReturnValue({ finalScore: false });

    const { container } = render(
      <FinalScorePanel scores={mockScores} onNewGame={vi.fn()} />
    );

    expect(container.firstChild).toBeEmptyDOMElement();
  });

  it('renders scores and buttons when finalScore is true', () => {
    useGameState.mockReturnValue({ finalScore: true });

    render(<FinalScorePanel scores={mockScores} onNewGame={vi.fn()} />);

    expect(screen.getByText('Final Score')).toBeInTheDocument();
    expect(screen.getByText(/Team 1 \(Hearts\): 250/)).toBeInTheDocument();
    expect(screen.getByText(/Team 2 \(Spades\): 180/)).toBeInTheDocument();

    const buttons = screen.getAllByRole('button');
    expect(buttons).toHaveLength(2);
    expect(buttons[0]).toHaveTextContent('New Game');
    expect(buttons[1]).toHaveTextContent('Close');
  });

  it('calls onNewGame when either button is clicked', () => {
    useGameState.mockReturnValue({ finalScore: true });

    const mockNewGame = vi.fn();
    render(<FinalScorePanel scores={mockScores} onNewGame={mockNewGame} />);

    fireEvent.click(screen.getByText('New Game'));
    fireEvent.click(screen.getByText('Close'));

    expect(mockNewGame).toHaveBeenCalledTimes(2);
  });
});
