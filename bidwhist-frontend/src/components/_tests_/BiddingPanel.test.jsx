// src/components/_tests_/BiddingPanel.test.jsx

Object.defineProperty(import.meta, 'env', {
  value: { VITE_API_URL: 'http://localhost:1234' },
});

import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import BiddingPanel from '../BiddingPanel';

import { useGameState } from '../../context/GameStateContext';
import { useUIDisplay } from '../../context/UIDisplayContext';
import { usePositionContext } from '../../context/PositionContext';

vi.mock('../../context/GameStateContext');
vi.mock('../../context/UIDisplayContext');
vi.mock('../../context/PositionContext');

global.fetch = vi.fn(() =>
  Promise.resolve({
    ok: true,
    json: () =>
      Promise.resolve({
        bids: [],
        phase: 'next-phase',
        firstBidder: 'P2',
        currentTurnIndex: 1,
      }),
  })
);

describe('BiddingPanel component', () => {
  const mockCloseBidding = vi.fn();
  const mockOnBidPlaced = vi.fn();

  beforeEach(() => {
    vi.clearAllMocks();

    useGameState.mockReturnValue({
      gameId: 'test-game-id',
      bids: [],
      bidTurnIndex: 0,
      currentTurnIndex: 0,
      setBids: vi.fn(),
      setPhase: vi.fn(),
      setFirstBidder: vi.fn(),
      setCurrentTurnIndex: vi.fn(),
      debugLog: () => {},
    });

    useUIDisplay.mockReturnValue({
      bidPhase: true,
      showBidding: true,
      setBidPhase: vi.fn(),
      setShowBidding: vi.fn(),
      setShowFinalizeBid: vi.fn(),
      debugLog: () => {},
    });

    usePositionContext.mockReturnValue({
      viewerPosition: 'P1',
      debugLog: () => {},
    });
  });

  test('renders bidding UI when visible', () => {
    render(<BiddingPanel closeBidding={mockCloseBidding} onBidPlaced={mockOnBidPlaced} />);
    expect(screen.getByText('Place Your Bid')).toBeInTheDocument();
    expect(screen.getByText('Set Bid')).toBeInTheDocument();
  });

  test('updates bid input and checkbox', () => {
    render(<BiddingPanel closeBidding={mockCloseBidding} onBidPlaced={mockOnBidPlaced} />);
    const input = screen.getByPlaceholderText(/Enter bid/i);
    fireEvent.change(input, { target: { value: '6' } });
    expect(input.value).toBe('6');

    const checkbox = screen.getByRole('checkbox');
    fireEvent.click(checkbox);
    expect(checkbox.checked).toBe(true);
  });

  test('calls fetch when "Pass" is clicked', async () => {
    render(<BiddingPanel closeBidding={mockCloseBidding} onBidPlaced={mockOnBidPlaced} />);
    const passButton = screen.getByText('Pass');
    fireEvent.click(passButton);

    // Wait for fetch to be called
    expect(fetch).toHaveBeenCalledWith(expect.stringContaining('/game/bid'), expect.any(Object));
  });
});
