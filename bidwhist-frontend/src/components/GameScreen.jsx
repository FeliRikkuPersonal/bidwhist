// src/components/GameScreen.jsx
import React from 'react';
import '../css/GameScreen.css';
import CardPlayZone from './CardPlayZone';
import PlayerZone from './PlayerZone';


export default function GameScreen({ gameState }) {
    return (
        <div className="game-screen">

            <div className="center-area">
                <CardPlayZone playerPosition="south" onCardPlayed={(card) => console.log("Played:", card)} />
            </div>
            <div className="bottom-bar">
                <PlayerZone gameState={gameState} playerName={gameState?.playerName || "You"} />
            </div>
        </div>
    );
}
