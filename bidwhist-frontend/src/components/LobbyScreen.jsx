// src/components/LobbyScreen.jsx

import React, { useEffect, useState } from 'react';
import { useGameState } from '../context/GameStateContext';
import { usePositionContext } from '../context/PositionContext';
import { useUIDisplay } from '../context/UIDisplayContext';
import { delay } from '../utils/TimeUtils';
import '../css/LobbyScreen.css';
/**
 * Displays the multiplayer lobby screen while waiting for players to join.
 * Automatically hides once 4 players are present to allow game transition.
 *
 * @param {string} gameId - The current lobby/game code
 * @returns {JSX.Element|null} The lobby UI or null if full
 */
function LobbyScreen({ gameId }) {
  const { players } = useGameState();
  const { backendPositions, setBackendPositions } = usePositionContext();
  const { setShowLobby, setShowGameScreen } = useUIDisplay();
  const API = import.meta.env.VITE_API_URL;

  const [backendSet, setBackendSet] = useState(false);
  const [playersPresent, setPlayersPresent] = useState(false);

  useEffect(() => {
    setBackendSet(Object.keys(backendPositions).length === 4);
    setPlayersPresent(players.length === 4);
  }, [backendPositions, players]);

  // Set backend positions once all players are added
  useEffect(() => {
    if (playersPresent) {
      const positionNameMap = Object.fromEntries(players.map((p) => [p.position, p.name]));
      setBackendPositions(positionNameMap);
    }
  });

  // Prep game when ready
  const handleReady = () => {
    if (backendSet) {
      setShowLobby(false);
      setShowGameScreen(true);
    }
  };

  return (
    <div className="lobby-screen">
      <h2>
        Lobby Code: <strong>{gameId}</strong>
      </h2>
      <p>Waiting for players... ({players.length}/4)</p>
      <ul>
        {players.map((p, i) => (
          <li key={i}>{p.name}</li>
        ))}
      </ul>
      {backendSet && (
        <button className="ready-button" onClick={handleReady}>
          Ready
        </button>
      )}
    </div>
  );
}

export default LobbyScreen;
