// src/components/_tests_/ModeSelector.test.jsx

import { render, screen, fireEvent, within } from '@testing-library/react';
import ModeSelector from '../ModeSelector';
import { GameStateProvider } from '../../context/GameStateContext';
import { PositionProvider } from '../../context/PositionContext';

/* Helper to wrap with required providers */
function renderWithProviders(ui) {
  return render(
    <GameStateProvider>
      <PositionProvider>{ui}</PositionProvider>
    </GameStateProvider>
  );
}

describe('ModeSelector', () => {
  test('toggles to multiplayer and shows multiplayer inputs', () => {
    const { container } = renderWithProviders(<ModeSelector />);

    // Find and click the multiplayer toggle
    const toggle = container.querySelector('.switch input[type="checkbox"]');
    expect(toggle).toBeInTheDocument();
    fireEvent.click(toggle);

    // Scope to the visible multiplayer form
    const multiForm = container.querySelector('.mode-form.multi');
    expect(multiForm).toBeInTheDocument();
    expect(multiForm).toBeVisible();

    // Query within the multiplayer form
    const nameInput = within(multiForm).getByPlaceholderText('Enter your name');
    const codeInput = within(multiForm).getByPlaceholderText('Lobby Code (to join)');
    const joinBtn = within(multiForm).getByText('Join Game');
    const createBtn = within(multiForm).getByText('Create Lobby');

    expect(nameInput).toBeVisible();
    expect(codeInput).toBeVisible();
    expect(joinBtn).toBeVisible();
    expect(createBtn).toBeVisible();
  });
});
