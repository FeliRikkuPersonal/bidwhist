// src/components/_tests_/BidZone.test.jsx

import { render, screen } from '@testing-library/react';
import BidZone from '../BidZone';
import React from 'react';
import { vi } from 'vitest';

/* 🧪 Mock GameStateContext */
vi.mock('../../context/GameStateContext', () => ({
  useGameState: () => ({
    phase: 'KITTY',
    bids: [
      { player: 'P1', value: 5, isNo: false, passed: false },
      { player: 'P2', passed: true },
    ],
  }),
}));

/* 🧪 Mock UIDisplayContext */
vi.mock('../../context/UIDisplayContext', () => ({
  useUIDisplay: () => ({
    bidPhase: true,
    kittyPhase: false,
    setKittyPhase: vi.fn(),
  }),
}));

describe('BidZone', () => {
  it('renders bid history when bidPhase is true', () => {
    render(<BidZone />);

    expect(screen.getByText('Player Bids')).toBeInTheDocument();
    expect(screen.getByText('P1 bids 5')).toBeInTheDocument();
    expect(screen.getByText('P2 passes')).toBeInTheDocument();
  });
});
