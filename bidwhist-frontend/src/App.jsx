// src/App.jsx
import React from 'react';
import './css/index.css';
import './css/Card.css';
import './css/Animations.css';
import ModeSelector from "./components/ModeSelector";
import GameScreen from './components/GameScreen';
import Scoreboard from './components/Scoreboard';
import { useUIDisplay } from './context/UIDisplayContext.jsx';
import { useGameState } from './context/GameStateContext.jsx';
import { usePositionContext } from './context/PositionContext.jsx';

function App() {
  const { debugLog: logGameState } = useGameState();
  const { debugLog: logPosition } = usePositionContext();
  const { debugLog: logUI } = useUIDisplay();

  const {
    setPlayers,
    setCurrentTurnIndex,
    setPhase,
    setShuffledDeck,
    setFirstBidder,
    setBidTurnIndex,
  } = useGameState();
  const {
    playerName,
    setPlayerName,
    viewerPosition,
    setViewerPosition,
    setBackendPositions,
  } = usePositionContext();
  const {
    setShowAnimatedCards,
    showGameScreen,
    setShowGameScreen
  } = useUIDisplay();

  const onStartGame = async (name) => {
    const trimmedName = name.trim();
    if (!trimmedName) {
      console.warn("[App] Name is empty after trimming. Aborting start.");
      return;
    }

    console.log("[App] Starting game for player:", trimmedName);
    setPlayerName(trimmedName); // Sets local name immediately for UI

    try {
      const res = await fetch('/api/game/start', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ playerName: trimmedName }),
      });

      const data = await res.json();

      if (!res.ok || !data.players || !data.playerPosition) {
        console.error("[App] Invalid start response:", data);
        return;
      }

      console.log('[App] Game started successfully:', data);

      // GameState context setters
      setPlayers(data.players);
      setCurrentTurnIndex(data.currentTurnIndex);
      setPhase(data.phase);
      setShuffledDeck(data.shuffledDeck);
      setFirstBidder(data.firstBidder);
      setBidTurnIndex(data.bidTurnIndex);

      // Position context
      setViewerPosition(data.playerPosition);
      setPlayerName(data.viewerName); // From backend, possibly cleaned/formatted
      const positionNameMap = Object.fromEntries(
        data.players.map(p => [p.position, p.name])
      );
      setBackendPositions(positionNameMap);
      console.log('[App] Set backendPositions:', positionNameMap);

      // UI Display (delayed to allow context propagation)
      setTimeout(() => {
        setShowGameScreen(true);
        setShowAnimatedCards(true);
      }, 50);



    } catch (err) {
      console.error('[App] Error starting game:', err);
    }
  };

  const validPositions = ['P1', 'P2', 'P3', 'P4'];
  const loadGame = showGameScreen && validPositions.includes(viewerPosition);


  return (
    <div className="index-wrapper">
      <div className="scoreboard-container">
        <Scoreboard />
      </div>
      <div className="index-container">
        {loadGame ? (
          <GameScreen />
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