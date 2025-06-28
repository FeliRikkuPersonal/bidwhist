// src/App.jsx
import { useState } from 'react';
import './css/index.css';
import ModeSelector from "./components/ModeSelector";
import GameScreen from './components/GameScreen';
import Scoreboard from './components/Scoreboard';

function App() {
  const [playerName, setPlayerName] = useState('');
  const [gameState, setGameState] = useState(null);

  const onStartGame = (name) => {
    const trimmedName = name.trim();
    if (!trimmedName) {

      //debug log
      console.warn("[App] Name is empty after trimming. Aborting start.");

      return;
    }


    //debug log
    console.log("[App] Starting game for player:", trimmedName);

    setPlayerName(trimmedName);

    fetch('/api/game/start', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ playerName: trimmedName })
    })
      .then(res => {

        //debug logg
        console.log("[App] Received response status:", res.status);

        return res.json();
      })

      .then(data => {

        //debug log
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
          <GameScreen gameState={gameState} />
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