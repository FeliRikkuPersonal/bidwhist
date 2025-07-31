// src/components/_tests_/HamburgerMenu.test.jsx

import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, fireEvent } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import HamburgerMenu from '../HamburgerMenu';

// ✅ Patch GameState so UIDisplayProvider doesn't crash
vi.mock('../../context/GameStateContext', async (importOriginal) => {
  const actual = await importOriginal();
  return {
    ...actual,
    useGameState: () => ({
      setLeadSuit: vi.fn(),
      updateFromResponse: vi.fn(),
      clearGameStateContext: vi.fn(),
      gameId: 'mock-game-id',
      players: [],
    }),
    GameStateProvider: actual.GameStateProvider,
  };
});

// ✅ Patch AlertContext so useThrowAlert doesn't crash
vi.mock('../../context/AlertContext', async (importOriginal) => {
  const actual = await importOriginal();
  return {
    ...actual,
    useAlert: () => ({
      showAlert: vi.fn(),
    }),
    AlertProvider: actual.AlertProvider,
  };
});

import { UIDisplayProvider } from '../../context/UIDisplayContext';
import { PositionProvider } from '../../context/PositionContext';

function Wrapper({ children }) {
  return (
    <BrowserRouter>
      <UIDisplayProvider>
        <PositionProvider>{children}</PositionProvider>
      </UIDisplayProvider>
    </BrowserRouter>
  );
}

function renderWithProviders(ui) {
  return render(ui, { wrapper: Wrapper });
}

beforeEach(() => {
  localStorage.setItem('mode', JSON.stringify('normal'));
  localStorage.setItem('viewerPosition', JSON.stringify('P1'));
});

describe('HamburgerMenu (stable minimal)', () => {
  it('renders the hamburger icon', () => {
    renderWithProviders(<HamburgerMenu />);
    expect(screen.getByRole('button')).toBeInTheDocument();
  });

  it('toggles menu on click', () => {
    renderWithProviders(<HamburgerMenu />);
    const button = screen.getByRole('button');

    fireEvent.click(button);
    expect(screen.getByText(/Home/i)).toBeInTheDocument();

    fireEvent.click(button);
    expect(screen.queryByText(/Home/i)).not.toBeInTheDocument();
  });
});
