// src/context/_tests_/GameStateContext.test.jsx

import React, { useEffect } from 'react';
import { describe, it, expect, beforeEach, vi } from 'vitest';
import { render, screen, act, waitFor } from '@testing-library/react';

import { GameStateProvider, useGameState } from '../GameStateContext';

function TestComponent({ onReady }) {
  const {
    players,
    kitty,
    mode,
    difficulty,
    activeGame,
    forcedBid,
    updateFromResponse,
    clearGameStateContext,
  } = useGameState();

  useEffect(() => {
    updateFromResponse({
      players: [{ name: 'Alice' }],
      kitty: ['K♦'],
      mode: 'multiplayer',
      difficulty: 'HARD',
      activeGame: true,
      forcedBid: true,
    });

    setTimeout(() => {
      clearGameStateContext();
      onReady?.(); // Notify test when done
    }, 50);
  }, []);

  return (
    <div>
      <div data-testid="players">{JSON.stringify(players)}</div>
      <div data-testid="kitty">{JSON.stringify(kitty)}</div>
      <div data-testid="mode">{mode}</div>
      <div data-testid="difficulty">{difficulty}</div>
      <div data-testid="activeGame">{String(activeGame)}</div>
      <div data-testid="forcedBid">{String(forcedBid)}</div>
    </div>
  );
}

describe('GameStateContext', () => {
  beforeEach(() => {
    localStorage.clear();
    localStorage.setItem('mode', JSON.stringify('multiplayer'));
    localStorage.setItem('difficulty', JSON.stringify('HARD'));
    localStorage.setItem('activeGame', JSON.stringify('true'));
  });


  it('initializes and clears game state as expected', async () => {
    render(
      <GameStateProvider>
        <TestComponent />
      </GameStateProvider>
    );

    await waitFor(() => {
      expect(screen.getByTestId('players').textContent).toBe('[{"name":"Alice"}]');
      expect(screen.getByTestId('kitty').textContent).toBe('[]');
      expect(screen.getByTestId('mode').textContent).toBe('multiplayer');
      expect(screen.getByTestId('difficulty').textContent).toBe('HARD');
      expect(screen.getByTestId('activeGame').textContent).toBe('true');
      expect(screen.getByTestId('forcedBid').textContent).toBe('false');
    });
  });
});
