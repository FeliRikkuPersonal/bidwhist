// src/context/_tests_/UIDisplayContext.test.jsx

import React from 'react';
import { render, screen } from '@testing-library/react';
import { describe, it, expect, vi } from 'vitest';
import {
  UIDisplayProvider,
  useUIDisplay,
} from '../UIDisplayContext';

/* Test consumer that exposes context values for inspection */
function TestConsumer() {
  const {
    handMap,
    showGameScreen,
    setShowGameScreen,
    setHandFor,
    queueAnimationFromResponse,
    animationQueue,
  } = useUIDisplay();

  React.useEffect(() => {
    setShowGameScreen(true);
    setHandFor('south', ['5H', 'AS']);
    queueAnimationFromResponse({ animationQueue: ['ANIM_1', 'ANIM_2'] });
  }, []);

  return (
    <div>
      <div data-testid="gameScreen">{showGameScreen ? 'visible' : 'hidden'}</div>
      <div data-testid="southHand">{handMap.south.join(',')}</div>
      <div data-testid="queue">{animationQueue.join('|')}</div>
    </div>
  );
}

describe('UIDisplayContext', () => {
  it('initializes and updates values correctly', () => {
    render(
      <UIDisplayProvider>
        <TestConsumer />
      </UIDisplayProvider>
    );

    expect(screen.getByTestId('gameScreen').textContent).toBe('visible');
    expect(screen.getByTestId('southHand').textContent).toBe('5H,AS');
    expect(screen.getByTestId('queue').textContent).toBe('ANIM_1|ANIM_2');
  });

  it('returns undefined if useUIDisplay is used outside provider', () => {
    let value;
    function BrokenConsumer() {
      value = useUIDisplay(); // will be undefined if no provider
      return null;
    }

    render(<BrokenConsumer />);
    expect(value).toBe(null);
  });
});

