// src/App.js

import './App.css';
import ModeSelector from './components/ModeSelector';

/**
 * Landing version of the app. Displays a welcome message and lets the user
 * select a game mode to begin.
 */
function App() {
  return (
    <div className="index-wrapper">
      <div className="index-container">
        <h1 className="h1-welcome">Welcome to Bid Whist Online!</h1>
        <ModeSelector />
      </div>
    </div>
  );
}

export default App;
