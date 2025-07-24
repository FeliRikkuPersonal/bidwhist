// src/components/FinalScorePanel.jsx

import React from 'react';
import { useGameState } from '../context/GameStateContext';

/**
 * Displays the final score overlay at the end of a game.
 *
 * @param {{ team: string, points: number }[]} scores - An array of team scores
 * @param {Function} onNewGame - Callback to trigger a new game (or close the panel)
 * @returns {JSX.Element} Final score modal if `finalScore` is true
 */
export default function FinalScorePanel({ scores, onNewGame }) {
  const { finalScore } = useGameState();

  return (
    <div>
      {finalScore && (
        <div className="final-score-overlay">
          <div className="final-score-panel">
            <h2>Final Score</h2>
            <div className="score-panel-text">
              Team 1 ({scores[0].team}): {scores[0].points}
            </div>
            <div className="score-panel-text">
              Team 2 ({scores[1].team}): {scores[1].points}
            </div>
            <div className="settings-actions">
              <button className="index-button score-button" onClick={onNewGame}>
                New Game
              </button>
              <button className="index-button score-button" onClick={onNewGame}>
                Close
              </button>
              {/* TODO: Separate `Close` into its own handler */}
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
