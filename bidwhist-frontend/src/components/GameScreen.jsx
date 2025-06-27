// src/components/GameScreen.jsx
import React from 'react';
import CardPlayZone from './CardPlayZone';
import PlayerZone from './PlayerZone';
import Scoreboard from './Scoreboard';

export default function GameScreen({ gameState }) {
  return (
    <div className="game-screen">
      <Scoreboard gameState={gameState} />
      <CardPlayZone playerPosition="south" onCardPlayed={(card) => console.log("Played:", card)} />
      <PlayerZone gameState={gameState} />
    </div>
  );
}
