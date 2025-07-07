// src/components/LobbyScreen.jsx
import React from 'react';
import { useGameState } from '../context/GameStateContext';

function LobbyScreen({ gameId, playerName }) {
    const { players } = useGameState();  // 🔁 Global context state

    // Trigger game start if 4 players are ready
    if (players.length === 4) {

        return null; // Avoid rendering lobby during transition
    }

    return (
        <div className="lobby-screen">
            <h2>Lobby Code: <strong>{gameId}</strong></h2>
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

