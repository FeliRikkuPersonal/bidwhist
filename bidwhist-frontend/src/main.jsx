import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import { RefProvider } from './context/RefContext.jsx'
import './css/index.css'
import App from './App.jsx'

createRoot(document.getElementById('root')).render(
  <StrictMode>
    <RefProvider>
      <App />
    </RefProvider>
  </StrictMode>,
)
