// Path: src/main.jsx

import React, { StrictMode } from 'react';
import { createRoot } from 'react-dom/client';

import './css/index.css';
import App from './App.jsx';

import { RefProvider } from './context/RefContext.jsx';
import { GameStateProvider } from './context/GameStateContext.jsx';
import { PositionProvider } from './context/PositionContext.jsx';
import { UIDisplayProvider } from './context/UIDisplayContext.jsx';
import { AlertProvider } from './context/AlertContext.jsx';

/**
 * Renders the <App /> component wrapped in all necessary context providers.
 * Uses React 18's createRoot API and mounts to #root in index.html.
 */
createRoot(document.getElementById('root')).render(
  <StrictMode>
    <AlertProvider>
      <RefProvider>
        <GameStateProvider>
          <PositionProvider>
            <UIDisplayProvider>
              <App />
            </UIDisplayProvider>
          </PositionProvider>
        </GameStateProvider>
      </RefProvider>
    </AlertProvider>
  </StrictMode>
);
