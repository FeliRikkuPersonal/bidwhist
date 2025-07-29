// src/components/FinalScorePanel.jsx

import React, { useEffect } from 'react';
import { useGameState } from '../context/GameStateContext';
import handleQuit from '../utils/handleQuit.js';
import { usePositionContext } from '../context/PositionContext';
import '../css/FinalScorePanel.css'
import '../css/index.css'
import { useUIDisplay } from '../context/UIDisplayContext.jsx';

/**
 * Displays the final score overlay at the end of a game.
 *
 * @param {{ team: string, points: number }[]} scores - An array of team scores
 * @param {Function} onNewGame - Callback to trigger a new game (or close the panel)
 * @returns {JSX.Element} Final score modal if `showFinalScore` is true
 */
export default function FinalScorePanel({ onNewGame }) {
  const { teamAScore, teamBScore, clearGameStateContext } = useGameState();
  const { showFinalScore, clearUIContext } = useUIDisplay();
  const { viewerPosition } = usePositionContext();
  const savedMode = JSON.parse(localStorage.getItem('mode'));
  const savedGameId = JSON.parse(localStorage.getItem('gameId'));
  const API = import.meta.env.VITE_API_URL; // Server endpoint

  return (
    <div>
      {showFinalScore && (

        <div className="final-score-panel">
          <h2>Final Score</h2>
          <div className="score-panel-text">
            Team A: {teamAScore}
          </div>
          <div className="score-panel-text">
            Team B: {teamBScore}
          </div>
          <div className="settings-actions">
            <button className="index-button score-button" onClick={onNewGame}>
              New Game
            </button>
            <button className="index-button score-button"
              onClick={() =>
                handleQuit({ viewerPosition, savedGameId, savedMode, API, clearUIContext, clearGameStateContext })}>
              Close
            </button>
          </div>
        </div>

      )}
    </div>
  );
}
