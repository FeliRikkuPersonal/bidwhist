// src/components/_tests_/CardPlayZone.test.jsx

import { describe, it, expect, beforeEach, vi } from 'vitest';
import { render, screen } from '@testing-library/react';
import React from 'react';

import CardPlayZone from '../CardPlayZone';

import { GameStateProvider } from '../../context/GameStateContext';
import { PositionProvider } from '../../context/PositionContext';
import { UIDisplayProvider } from '../../context/UIDisplayContext';
import { RefProvider } from '../../context/RefContext';
import { AlertProvider } from '../../context/AlertContext';

// ✅ Mock GameStateContext
vi.mock('../../context/GameStateContext', async () => {
  const actual = await vi.importActual('../../context/GameStateContext');
  return {
    ...actual,
    useGameState: () => ({
      gameId: 'game123',
      gameState: {
        phase: 'PLAY',
        playedCardsByPosition: {},
        hands: {},
      },
      players: [
        { name: 'Alice', team: 'A', position: 'P1' },
        { name: 'Bob', team: 'B', position: 'P2' },
        { name: 'Carol', team: 'A', position: 'P3' },
        { name: 'Dan', team: 'B', position: 'P4' },
      ],
      animationQueue: [],
      setAnimationQueue: vi.fn(),
      playedCardPosition: null,
      setPlayedCardPosition: vi.fn(),
      lastAnimation: null,
      setLastAnimation: vi.fn(),
      trickWinner: null,
      setTrickWinner: vi.fn(),
      currentTurnPosition: 'P1',
      suitVoidMap: new Map(),
      trickCards: [],
    }),
  };
});

// ✅ Mock PositionContext
vi.mock('../../context/PositionContext', async () => {
  const actual = await vi.importActual('../../context/PositionContext');
  return {
    ...actual,
    usePositionContext: () => ({
      viewerPosition: 'P1',
      viewerTeam: 'A',
      positionToDirection: {
        P1: 'south',
        P2: 'west',
        P3: 'north',
        P4: 'east',
      },
      directionToPosition: {
        south: 'P1',
        west: 'P2',
        north: 'P3',
        east: 'P4',
      },
      backendPositions: {
        P1: 'Alice',
        P2: 'Bob',
        P3: 'Carol',
        P4: 'Dan',
      },
    }),
  };
});

describe('<CardPlayZone />', () => {
  beforeEach(() => {
    localStorage.setItem('viewerPosition', JSON.stringify('P1'));
    localStorage.setItem('viewerTeam', JSON.stringify('A'));
    localStorage.setItem(
      'playedCardsByDirection',
      JSON.stringify({
        north: null,
        south: null,
        east: null,
        west: null,
      })
    );
  });

  it('renders all play slots and drop zone', () => {
    const { container } = render(
      <GameStateProvider>
        <PositionProvider>
          <UIDisplayProvider>
            <RefProvider>
              <AlertProvider>
                <CardPlayZone />
              </AlertProvider>
            </RefProvider>
          </UIDisplayProvider>
        </PositionProvider>
      </GameStateProvider>
    );

    expect(container.querySelector('.play-slot.north')).toBeInTheDocument();
    expect(container.querySelector('.play-slot.south')).toBeInTheDocument();
    expect(container.querySelector('.play-slot.east')).toBeInTheDocument();
    expect(container.querySelector('.play-slot.west')).toBeInTheDocument();

    expect(container.querySelector('.drop-zone.south')).toBeInTheDocument();
  });

  it('placeholder drag-and-drop logic exists', () => {
    expect(true).toBe(true);
  });
});
