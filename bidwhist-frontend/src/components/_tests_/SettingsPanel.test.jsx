// src/components/_tests_/SettingsPanel.test.jsx

import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import { vi } from 'vitest';
import SettingsPanel from '../SettingsPanel';

describe('SettingsPanel', () => {
  test('renders heading and buttons', () => {
    render(<SettingsPanel applySettings={() => {}} closeSettings={() => {}} />);

    expect(screen.getByText('Game Settings')).toBeInTheDocument();
    expect(screen.getByRole('button', { name: 'Apply' })).toBeInTheDocument();
    expect(screen.getByRole('button', { name: 'Close' })).toBeInTheDocument();
  });

  test('calls applySettings when Apply button is clicked', () => {
    const applyMock = vi.fn();
    render(<SettingsPanel applySettings={applyMock} closeSettings={() => {}} />);

    fireEvent.click(screen.getByRole('button', { name: 'Apply' }));
    expect(applyMock).toHaveBeenCalledTimes(1);
  });

  test('calls closeSettings when Close button is clicked', () => {
    const closeMock = vi.fn();
    render(<SettingsPanel applySettings={() => {}} closeSettings={closeMock} />);

    fireEvent.click(screen.getByRole('button', { name: 'Close' }));
    expect(closeMock).toHaveBeenCalledTimes(1);
  });
});
