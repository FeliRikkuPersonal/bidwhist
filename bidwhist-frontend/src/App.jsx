// src/App.jsx
import './css/index.css';
import './css/Card.css';
import './css/Animations.css';
import ModeSelector from "./components/ModeSelector";
import GameScreen from './components/GameScreen';
import Scoreboard from './components/Scoreboard';
import { useUIDisplay } from './context/UIDisplayContext';
import { useGameState } from './context/GameStateContext';
import { usePositionContext } from './context/PositionContext';

function App() {
  const { 
    gameState,
    setGameState,
  } = useGameState();
  const { 
    playerName,
    setPlayerName,
    setViewerPosition,
    setBackendPositions 
  } = usePositionContext();
  const { setShowAnimatedCards } = useUIDisplay();

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
        setViewerPosition(data.playerPosition);
        setBackendPositions(data.players.map(p => p.position));
        setShowAnimatedCards(true);
      })
      .catch(err => {
        console.error('[App] Error starting game:', err);
      });
  };

  return (
    <div className="index-wrapper">
      <div className="scoreboard-container">
        <Scoreboard/>
      </div>
      <div className="index-container">
        {gameState ? (
          <GameScreen/>
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