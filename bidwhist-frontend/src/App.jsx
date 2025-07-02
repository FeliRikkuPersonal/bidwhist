// src/App.jsx
import { useState } from 'react';
import './css/index.css';
import './css/Card.css';
import './css/Animations.css';
import ModeSelector from "./components/ModeSelector";
import GameScreen from './components/GameScreen';
import Scoreboard from './components/Scoreboard';
import { getPositionMap } from './utils/PositionUtils';

function App() {
  const [playerName, setPlayerName] = useState('');
  const [gameState, setGameState] = useState(null);
  const [showStackedDeck, setShowStackedDeck] = useState(false);
  const [animatedCards, setAnimatedCards] = useState([]);
  const [deckPosition, setDeckPosition] = useState({ x: 0, y: 0 })

  const positionMap = gameState?.players
    ? getPositionMap(gameState.players.map(p => p.position), gameState.playerPosition)
    : {};
  const myPosition = gameState?.playerPosition || 'south';

  const onStartGame = (name) => {
    const trimmedName = name.trim();
    if (!trimmedName) {
      console.warn("[App] Name is empty after trimming. Aborting start.");
      return;
    }

    console.log("[App] Starting game for player:", trimmedName);
    setPlayerName(trimmedName);

    fetch('/api/game/start', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ playerName: trimmedName })
    })
      .then(res => res.json())
      .then(data => {
        console.log('[App] Game started successfully:', data);
        setGameState(data);
      })
      .catch(err => {
        console.error('[App] Error starting game:', err);
      });
  };

  return (
    <div className="index-wrapper">
      <div className="scoreboard-container">
        <Scoreboard gameState={gameState} />
      </div>
      <div className="index-container">
        {gameState ? (
          <GameScreen
            gameState={gameState}
            playerName={playerName}
            setGameState={setGameState}
            showStackedDeck={showStackedDeck}
            setShowStackedDeck={setShowStackedDeck}
            setAnimatedCards={setAnimatedCards}
            animatedCards={animatedCards}
            deckPosition={deckPosition}
            setDeckPosition={setDeckPosition}
          />
        ) : (
          <ModeSelector
            playerName={playerName}
            setPlayerName={setPlayerName}
            onStartGame={onStartGame}
          />
        )}
      </div>
    </div>
  );
}

export default App;