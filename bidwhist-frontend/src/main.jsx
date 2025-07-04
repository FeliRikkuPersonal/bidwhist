import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import { RefProvider } from './context/RefContext.jsx'
import './css/index.css'
import App from './App.jsx'
import { GameStateProvider } from './context/GameStateContext.jsx'
import { PositionProvider } from './context/PositionContext.jsx'
import { UIDisplayProvider } from './context/UIDisplayContext.jsx'

createRoot(document.getElementById('root')).render(
  <StrictMode>
    <RefProvider>
      <GameStateProvider>
        <PositionProvider>
          <UIDisplayProvider>
            <App />
          </UIDisplayProvider>
        </PositionProvider>
      </GameStateProvider>
    </RefProvider>
  </StrictMode>,
)
