import React from 'react';
import { render, screen, act } from '@testing-library/react';
import { describe, it, expect, vi } from 'vitest';
import { UIDisplayProvider, useUIDisplay } from '../UIDisplayContext';
import { AllProviders } from '../../test-utils/AllProviders';

function FullTestConsumer() {
  const {
    showGameScreen,
    setShowGameScreen,
    handMap,
    setHandFor,
    animationQueue,
    queueAnimationFromResponse,
    discardPile,
    setDiscardPile,
    teamATricks,
    setTeamATricks,
    clearUIContext,
    setMyTurn,
    myTurn,
  } = useUIDisplay();

  React.useEffect(() => {
    act(() => {
      setShowGameScreen(true);
      setHandFor('north', ['JH']);
      queueAnimationFromResponse({
        animationQueue: [{ type: 'FADE' }, { type: 'FLIP' }],
      });
      setDiscardPile(['4D']);
      setTeamATricks(3);
      setMyTurn(true);
    });
  }, []);

  return (
    <div>
      <div data-testid="screen">{showGameScreen ? 'Game' : 'Lobby'}</div>
      <div data-testid="northHand">{handMap.north.join(',')}</div>
      <div data-testid="queue">{animationQueue.map((a) => a.type).join('|')}</div>
      <div data-testid="discard">{discardPile.join(',')}</div>
      <div data-testid="tricks">{teamATricks}</div>
      <div data-testid="myTurn">{myTurn.toString()}</div>
      <button onClick={() => act(() => clearUIContext())} data-testid="clear">
        Reset
      </button>
    </div>
  );
}

describe('UIDisplayContext (Full Suite)', () => {
  it('correctly sets and updates context values', async () => {
    render(
      <AllProviders>
        <FullTestConsumer />
      </AllProviders>
    );

    // Wait for the state to settle
    const screenText = await screen.findByTestId('screen');
    expect(screenText.textContent).toBe('Game');

    expect(screen.getByTestId('northHand').textContent).toBe('JH');
    expect(screen.getByTestId('queue').textContent).toBe('FADE|FLIP');
    expect(screen.getByTestId('discard').textContent).toBe('4D');
    expect(screen.getByTestId('tricks').textContent).toBe('3');
    expect(screen.getByTestId('myTurn').textContent).toBe('true');
  });

  it('resets values via clearUIContext()', () => {
    render(
      <AllProviders>
        <FullTestConsumer />
      </AllProviders>
    );

    act(() => {
      screen.getByTestId('clear').click();
    });

    expect(screen.getByTestId('discard').textContent).toBe('');
    expect(screen.getByTestId('myTurn').textContent).toBe('false');
    expect(screen.getByTestId('tricks').textContent).toBe('0');
  });

  it('returns null if useUIDisplay is used outside provider', () => {
    let value;
    function NoProvider() {
      value = useUIDisplay();
      return null;
    }

    render(<NoProvider />);
    expect(value).toBe(null);
  });
});
