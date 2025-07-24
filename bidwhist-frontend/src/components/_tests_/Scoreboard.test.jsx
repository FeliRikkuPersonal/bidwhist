// src/components/_tests_/Scoreboard.test.jsx

import React from 'react';
import { render, screen } from '@testing-library/react';
import { vi } from 'vitest';
import Scoreboard from '../Scoreboard';

/* ✅ Mock BidZone component */
vi.mock('../BidZone.jsx', () => ({
  default: () => <div data-testid="mock-bidzone">Mock BidZone</div>,
}));

/* ✅ Mock useGameState and usePositionContext */
vi.mock('../../context/GameStateContext', () => ({
  useGameState: vi.fn(),
}));

vi.mock('../../context/PositionContext', () => ({
  usePositionContext: vi.fn(),
}));

import { useGameState } from '../../context/GameStateContext';
import { usePositionContext } from '../../context/PositionContext';

describe('Scoreboard', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  test('renders correct scores for viewer on Team A (P1)', () => {
    useGameState.mockReturnValue({
      teamAScore: 90,
      teamBScore: 20,
      winningBid: null,
    });

    usePositionContext.mockReturnValue({
      viewerPosition: 'P1',
      playerTeam: {
        P1: 'A',
        P2: 'B',
        P3: 'A',
        P4: 'B',
      },
    });

    render(<Scoreboard />);

    expect(screen.getByText('Team')).toBeInTheDocument();
    expect(screen.getByText('90')).toBeInTheDocument();
    expect(screen.getByText('Opponents')).toBeInTheDocument();
    expect(screen.getByText('20')).toBeInTheDocument();
  });

  test('renders correct scores for viewer on Team B (P2)', () => {
    useGameState.mockReturnValue({
      teamAScore: 60,
      teamBScore: 120,
      winningBid: null,
    });

    usePositionContext.mockReturnValue({
      viewerPosition: 'P2',
      playerTeam: {
        P1: 'A',
        P2: 'B',
        P3: 'A',
        P4: 'B',
      },
    });

    render(<Scoreboard />);

    expect(screen.getByText('120')).toBeInTheDocument(); // my team
    expect(screen.getByText('60')).toBeInTheDocument();  // opponents
  });

  test('displays formatted winning bid when available', () => {
    useGameState.mockReturnValue({
      teamAScore: 0,
      teamBScore: 0,
      winningBid: {
        player: 'P3',
        value: 5,
        type: 'Downtown',
        suit: 'Hearts',
        isNo: false,
      },
    });

    usePositionContext.mockReturnValue({
      viewerPosition: 'P1',
      playerTeam: {
        P1: 'A',
        P2: 'B',
        P3: 'A',
        P4: 'B',
      },
    });

    render(<Scoreboard />);

    expect(screen.getByText('Winning Bid')).toBeInTheDocument();
    expect(screen.getByText('Team A / 5 Downtown Hearts')).toBeInTheDocument();
  });

  test('displays "-No" in winning bid if isNo is true', () => {
    useGameState.mockReturnValue({
      teamAScore: 0,
      teamBScore: 0,
      winningBid: {
        player: 'P2',
        value: 7,
        type: 'Downtown',
        suit: 'Clubs',
        isNo: true,
      },
    });

    usePositionContext.mockReturnValue({
      viewerPosition: 'P1',
      playerTeam: {
        P1: 'A',
        P2: 'B',
        P3: 'A',
        P4: 'B',
      },
    });

    render(<Scoreboard />);

    expect(screen.getByText('Team B / 7-No Downtown Clubs')).toBeInTheDocument();
  });

  test('does not display bid section if no winning bid', () => {
    useGameState.mockReturnValue({
      teamAScore: 0,
      teamBScore: 0,
      winningBid: null,
    });

    usePositionContext.mockReturnValue({
      viewerPosition: 'P1',
      playerTeam: {
        P1: 'A',
        P2: 'B',
        P3: 'A',
        P4: 'B',
      },
    });

    render(<Scoreboard />);

    expect(screen.queryByText('Winning Bid')).not.toBeInTheDocument();
  });

  test('renders BidZone if bidType is provided', () => {
    useGameState.mockReturnValue({
      teamAScore: 0,
      teamBScore: 0,
      winningBid: null,
    });

    usePositionContext.mockReturnValue({
      viewerPosition: 'P1',
      playerTeam: {
        P1: 'A',
        P2: 'B',
        P3: 'A',
        P4: 'B',
      },
    });

    render(<Scoreboard bidType="uptown" />);

    expect(screen.getByTestId('mock-bidzone')).toBeInTheDocument();
  });

  test('handles partial winning bid safely', () => {
    useGameState.mockReturnValue({
      teamAScore: 0,
      teamBScore: 0,
      winningBid: {
        player: 'P4',
        value: 6,
        isNo: false,
      },
    });

    usePositionContext.mockReturnValue({
      viewerPosition: 'P1',
      playerTeam: {
        P1: 'A',
        P2: 'B',
        P3: 'A',
        P4: 'B',
      },
    });

    render(<Scoreboard />);

    expect(screen.getByText(/Team B \/ 6/i)).toBeInTheDocument();
  });
});
