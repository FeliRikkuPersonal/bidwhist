import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import { RefProvider } from './context/RefContext.jsx'
import './css/index.css'
import App from './App.jsx'
import { GameStateProvider } from './context/GameStateContext.js'
import { PositionProvider } from './context/PositionContext.js'

createRoot(document.getElementById('root')).render(
  <StrictMode>
    <RefProvider>
      <GameStateProvider>
        <PositionProvider>
          <App />
        </PositionProvider>
      </GameStateProvider>
    </RefProvider>
  </StrictMode>,
)
