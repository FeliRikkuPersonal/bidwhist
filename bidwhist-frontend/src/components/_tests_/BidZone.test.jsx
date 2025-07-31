// src/components/_tests_/BidZone.test.jsx

import React from 'react';
import { render, screen } from '@testing-library/react';
import { vi, describe, it, beforeEach, expect } from 'vitest';
import BidZone from '../BidZone';
import { AllProviders } from '../../test-utils/AllProviders';

/* ✅ Correctly mock GameStateContext with players */
vi.mock('../../context/GameStateContext', async (importOriginal) => {
  const actual = await importOriginal();
  return {
    ...actual,
    useGameState: () => ({
      phase: 'KITTY',
      bids: [
        { player: 'P1', value: 6, isNo: true, passed: false },
        { player: 'P2', passed: true },
        { player: 'P3', value: 5, isNo: false, passed: false },
      ],
      players: [
        { position: 'P1', team: 'A', name: 'Alice' },
        { position: 'P2', team: 'B', name: 'Bob' },
        { position: 'P3', team: 'A', name: 'Carol' },
        { position: 'P4', team: 'B', name: 'Dan' },
      ],
    }),
  };
});

/* ✅ Mock UIDisplayContext */
vi.mock('../../context/UIDisplayContext', async (importOriginal) => {
  const actual = await importOriginal();
  return {
    ...actual,
    useUIDisplay: () => ({
      bidPhase: true,
      kittyPhase: true,
    }),
  };
});

/* ✅ Mock PositionContext with getNameFromPosition */
vi.mock('../../context/PositionContext', async (importOriginal) => {
  const actual = await importOriginal();
  return {
    ...actual,
    usePositionContext: () => ({
      getNameFromPosition: (pos) => {
        const names = {
          P1: 'Alice',
          P2: 'Bob',
          P3: 'Carol',
          P4: 'Dan',
        };
        return names[pos] || pos;
      },
    }),
  };
});

describe('BidZone', () => {
  beforeEach(() => {
    localStorage.clear(); // optional cleanup
  });

  it('renders player bids and passes correctly during BIDDING/KITTY phase', () => {
    render(
      <AllProviders>
        <BidZone />
      </AllProviders>
    );

    expect(screen.getByText('Player Bids')).toBeInTheDocument();
    expect(screen.getByText('Alice bids 6 No')).toBeInTheDocument();
    expect(screen.getByText('P2 passes')).toBeInTheDocument(); // fallback for pass
    expect(screen.getByText('Carol bids 5')).toBeInTheDocument();
  });
});
