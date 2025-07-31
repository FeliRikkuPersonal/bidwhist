// src/components/_tests_/BiddingPanel.test.jsx

import React from 'react';
import { render, screen } from '@testing-library/react';
import { vi, describe, test, beforeEach, expect } from 'vitest';
import BiddingPanel from '../BiddingPanel';

/* Mock all necessary hooks and utils */
vi.mock('../../context/GameStateContext', () => ({
  useGameState: vi.fn(),
}));
vi.mock('../../context/UIDisplayContext', () => ({
  useUIDisplay: vi.fn(),
}));
vi.mock('../../context/PositionContext', () => ({
  usePositionContext: vi.fn(),
}));
vi.mock('../../hooks/useThrowAlert', () => ({
  useThrowAlert: () => vi.fn(),
}));
vi.mock('../../utils/handleQuit', () => ({
  default: vi.fn(),
}));

/* Get mock references to override later */
import { useGameState } from '../../context/GameStateContext';
import { useUIDisplay } from '../../context/UIDisplayContext';
import { usePositionContext } from '../../context/PositionContext';

global.fetch = vi.fn(() =>
  Promise.resolve({
    ok: true,
    json: () => Promise.resolve({}),
  })
);

/* Mock API URL */
Object.defineProperty(import.meta, 'env', {
  value: { VITE_API_URL: 'http://localhost:1234' },
});

describe('BiddingPanel', () => {
  const mockCloseBidding = vi.fn();
  const mockOnBidPlaced = vi.fn();

  beforeEach(() => {
    vi.clearAllMocks();

    localStorage.setItem('mode', JSON.stringify('single'));

    useGameState.mockReturnValue({
      gameId: 'test-game',
      bids: [],
      bidTurnIndex: 1, // P2
      setBids: vi.fn(),
      setPhase: vi.fn(),
      setFirstBidder: vi.fn(),
      setCurrentTurnIndex: vi.fn(),
      forcedBid: false,
      setForcedBid: vi.fn(),
      clearGameStateContext: vi.fn(),
      debugLog: () => {},
    });

    useUIDisplay.mockReturnValue({
      bidPhase: 'BID',
      showBidding: true, // ✅ Force render
      setShowBidding: vi.fn(),
      setBidPhase: vi.fn(),
      clearUIContext: vi.fn(),
      debugLog: () => {},
    });

    usePositionContext.mockReturnValue({
      viewerPosition: 'P2',
      debugLog: () => {},
    });
  });

  test('renders bidding UI when viewer is current turn', () => {
    render(<BiddingPanel closeBidding={mockCloseBidding} onBidPlaced={mockOnBidPlaced} />);

    expect(screen.getByText('Place Your Bid')).toBeInTheDocument();
    expect(screen.getByPlaceholderText('Enter bid (4-7)')).toBeInTheDocument();
    expect(screen.getByText('Set Bid')).toBeInTheDocument();
    expect(screen.getByText('Pass')).toBeInTheDocument();
    expect(screen.getByText('Quit')).toBeInTheDocument();
  });
});
