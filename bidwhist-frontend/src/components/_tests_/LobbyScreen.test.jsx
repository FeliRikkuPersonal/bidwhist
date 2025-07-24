// src/components/_tests_/LobbyScreen.test.jsx

import { describe, it, expect, vi } from 'vitest';
import { render, screen, cleanup } from '@testing-library/react';

describe('LobbyScreen', () => {
  afterEach(() => {
    cleanup();
    vi.resetModules();
  });

  it('renders lobby code and players when fewer than 4 players', async () => {
    vi.doMock('../../context/GameStateContext', () => ({
      useGameState: () => ({
        players: [{ name: 'Alice' }, { name: 'Bob' }],
      }),
    }));

    const { default: LobbyScreen } = await import('../LobbyScreen');
    render(<LobbyScreen gameId="ABCD1234" />);

    expect(screen.getByText('Lobby Code:')).toBeInTheDocument();
    expect(screen.getByText('Alice')).toBeInTheDocument();
    expect(screen.getByText('Bob')).toBeInTheDocument();
  });

  it('renders nothing if 4 players have joined', async () => {
    vi.doMock('../../context/GameStateContext', () => ({
      useGameState: () => ({
        players: [
          { name: 'Alice' },
          { name: 'Bob' },
          { name: 'Charlie' },
          { name: 'Dana' },
        ],
      }),
    }));

    const { default: LobbyScreen } = await import('../LobbyScreen');
    const { container } = render(<LobbyScreen gameId="XYZ1234" />);
    expect(container).toBeEmptyDOMElement();
  });
});
