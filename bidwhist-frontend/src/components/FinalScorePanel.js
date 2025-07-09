import React from 'react';
import '../FinalScorePanel.css';

export default function FinalScorePanel({ scores, onNewGame}) {
    return (
        <div className="final-score-overlay">
            <div className="final-score-panel">
                <h2>Final Score</h2>
                <div className="score-panel-text">Team 1 ({scores[0].team}): {scores[0].points}</div>
                <div className="score-panel-text">Team 2 ({scores[1].team}): {scores[1].points}</div>
                <div className="settings-actions">
                  <button className="index-button score-button" onClick={onNewGame}>New Game</button>
                  <button className="index-button score-button" onClick={onNewGame}>Close</button> {/* Update to close panel */}
                </div>
            </div>
        </div>
    );
}