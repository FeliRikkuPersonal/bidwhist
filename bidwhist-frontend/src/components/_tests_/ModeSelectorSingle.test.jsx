// src/components/_tests_/ModeSelector.test.jsx

import { render, screen, within } from '@testing-library/react';
import ModeSelector from '../ModeSelector';
import { GameStateProvider } from '../../context/GameStateContext';
import { PositionProvider } from '../../context/PositionContext';

/* Helper to wrap ModeSelector with required context */
function renderWithProviders(ui) {
  return render(
    <GameStateProvider>
      <PositionProvider>{ui}</PositionProvider>
    </GameStateProvider>
  );
}

describe('ModeSelector', () => {
  test('renders single player inputs by default', () => {
    const { container } = renderWithProviders(<ModeSelector />);

    // Scope to the visible single-player form
    const singleForm = container.querySelector('.mode-form.single');
    expect(singleForm).toBeInTheDocument();
    expect(singleForm).toBeVisible();

    // Verify inputs exist in that scope
    const nameInput = within(singleForm).getByPlaceholderText('Enter your name');
    const difficultySelect = within(singleForm).getByRole('combobox');
    const startButton = within(singleForm).getByText('Start Game');

    expect(nameInput).toBeVisible();
    expect(difficultySelect).toBeVisible();
    expect(startButton).toBeVisible();
  });
});
