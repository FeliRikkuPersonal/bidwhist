// src/context/__tests__/GameStateContext.test.jsx

import React from 'react';
import { render, screen } from '@testing-library/react';
import { GameStateProvider, useGameState } from '../GameStateContext';
import '@testing-library/jest-dom';

/* Test component to access and mutate context */
const TestComponent = () => {
  const {
    players,
    setPlayers,
    phase,
    setPhase,
    updateFromResponse,
  } = useGameState();

  React.useEffect(() => {
    setPlayers([{ name: 'Player A' }]);
    setPhase('BID');
    updateFromResponse({ phase: 'PLAY', currentTurnIndex: 2 });
  }, []);

  return (
    <div>
      <div data-testid="players">{players[0]?.name}</div>
      <div data-testid="phase">{phase}</div>
    </div>
  );
};

describe('GameStateContext', () => {
  test('provides initial state and allows updates', () => {
    render(
      <GameStateProvider>
        <TestComponent />
      </GameStateProvider>
    );

    expect(screen.getByTestId('players')).toHaveTextContent('Player A');
    expect(screen.getByTestId('phase')).toHaveTextContent('PLAY');
  });

  test('throws error if useGameState is used outside provider', () => {
    // Temporarily suppress console error for clean output
    const originalError = console.error;
    console.error = () => {};

    expect(() => {
      render(<TestComponent />);
    }).toThrow();

    console.error = originalError;
  });
});
