// src/test-utils/AllProviders.jsx
import React from 'react';
import { AlertProvider } from '../context/AlertContext';
import { RefProvider } from '../context/RefContext'; // ✅ Add this
import { GameStateProvider } from '../context/GameStateContext';
import { PositionProvider } from '../context/PositionContext';
import { UIDisplayProvider } from '../context/UIDisplayContext';

export const AllProviders = ({ children }) => (
  <AlertProvider>
    <RefProvider>
      <GameStateProvider>
        <PositionProvider>
          <UIDisplayProvider>
            {children}
          </UIDisplayProvider>
        </PositionProvider>
      </GameStateProvider>
    </RefProvider>
  </AlertProvider>
);
