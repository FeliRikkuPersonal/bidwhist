// src/components/LobbyScreen.jsx

import React from 'react';
import { useGameState } from '../context/GameStateContext';
import { usePositionContext } from '../context/PositionContext';
import { delay } from '../utils/TimeUtils';

/**
 * Displays the multiplayer lobby screen while waiting for players to join.
 * Automatically hides once 4 players are present to allow game transition.
 *
 * @param {string} gameId - The current lobby/game code
 * @returns {JSX.Element|null} The lobby UI or null if full
 */
function LobbyScreen({ gameId }) {
  const { players } = useGameState();
  const API = import.meta.env.VITE_API_URL;

  // Hide lobby once all players have joined
  if (players.length === 4) {
    delay(3000);

    return null;
  }

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
    </div>
  );
}

export default LobbyScreen;
