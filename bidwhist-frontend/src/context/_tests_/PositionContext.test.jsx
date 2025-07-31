// src/context/__tests__/PositionContext.test.jsx

import React from 'react';
import { render, screen } from '@testing-library/react';
import { PositionProvider, usePositionContext } from '../PositionContext';
import { AlertProvider } from '../../context/AlertContext';
import { RefProvider } from '../../context/RefContext';
import { UIDisplayProvider } from '../../context/UIDisplayContext';
import { vi } from 'vitest';
import '@testing-library/jest-dom';

// ✅ Mock GameStateContext with both useGameState and GameStateProvider
vi.mock('../GameStateContext', async () => {
  return {
    useGameState: () => ({
      players: [
        { name: 'Alice', position: 'P1', team: 'A' },
        { name: 'Bob', position: 'P2', team: 'B' },
        { name: 'Carol', position: 'P3', team: 'A' },
        { name: 'Dave', position: 'P4', team: 'B' },
      ],
    }),
    GameStateProvider: ({ children }) => <>{children}</>,
  };
});

// ✅ Inline test-only AllProviders to avoid dependency on broken shared one
const AllProviders = ({ children }) => {
  return (
    <AlertProvider>
      <RefProvider>
        <UIDisplayProvider>
          <PositionProvider>
            {children}
          </PositionProvider>
        </UIDisplayProvider>
      </RefProvider>
    </AlertProvider>
  );
};

/* 🧪 Test component to inspect values from PositionContext */
const TestComponent = () => {
  const {
    setViewerPosition,
    setBackendPositions,
    positionToDirection,
    directionToPosition,
    frontendPositions,
    playerTeam,
  } = usePositionContext();

  React.useEffect(() => {
    setViewerPosition('P1');
    setBackendPositions({
      P1: 'Alice',
      P2: 'Bob',
      P3: 'Carol',
      P4: 'Dave',
    });
  }, []);

  return (
    <div>
      <div data-testid="south">{frontendPositions.south?.name}</div>
      <div data-testid="west">{frontendPositions.west?.name}</div>
      <div data-testid="north">{frontendPositions.north?.name}</div>
      <div data-testid="east">{frontendPositions.east?.name}</div>
      <div data-testid="teamA">{playerTeam.P1}</div>
      <div data-testid="teamB">{playerTeam.P2}</div>
      <div data-testid="revSouth">{directionToPosition.south}</div>
      <div data-testid="clockwiseSouth">{positionToDirection.P1}</div>
    </div>
  );
};

describe('PositionContext', () => {
  it('provides correct direction mapping and team assignment', async () => {
    render(
      <AllProviders>
        <TestComponent />
      </AllProviders>
    );

    expect(await screen.findByTestId('south')).toHaveTextContent('Alice');
    expect(screen.getByTestId('west')).toHaveTextContent('Bob');
    expect(screen.getByTestId('north')).toHaveTextContent('Carol');
    expect(screen.getByTestId('east')).toHaveTextContent('Dave');

    expect(screen.getByTestId('teamA')).toHaveTextContent('A');
    expect(screen.getByTestId('teamB')).toHaveTextContent('B');

    expect(screen.getByTestId('revSouth')).toHaveTextContent('P1');
    expect(screen.getByTestId('clockwiseSouth')).toHaveTextContent('south');
  });

it('returns null if usePositionContext is used outside provider', () => {
  let contextValue;
  const BrokenConsumer = () => {
    contextValue = usePositionContext();
    return null;
  };

  render(<BrokenConsumer />);
  expect(contextValue).toBeNull();
});

});
