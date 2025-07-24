// src/components/ModeSelector.jsx

import '../css/ModeSelector.css';
import React, { useState } from 'react';
import { useGameState } from '../context/GameStateContext';
import { usePositionContext } from '../context/PositionContext';
import { clearAllGameData } from '../utils/ClearData';

/**
 * ModeSelector allows the user to:
 * - Enter a name
 * - Select difficulty (single-player)
 * - Toggle between single and multiplayer
 * - Join or create a multiplayer lobby
 *
 * @param {Function} onStartGame - Callback used to initialize single-player games
 * @returns {JSX.Element} Game mode selection interface
 */
function ModeSelector({ onStartGame }) {
  const { setGameId, mode, setMode, difficulty, setDifficulty } = useGameState();
  const { setPlayerName } = usePositionContext();

  const [newPlayerName, setNewPlayerName] = useState('');
  const [lobbyCode, setLobbyCode] = useState('');
  const API = import.meta.env.VITE_API_URL;

  /**
   * Starts a single-player game using a generated lobby code.
   */
  const handleStart = () => {
    const trimmedName = newPlayerName.trim();
    if (!trimmedName) return;

    const code = Math.random().toString(36).substring(2, 8).toUpperCase();
    clearAllGameData()
    setPlayerName(trimmedName);
    setMode('single');
    setGameId(code);
    onStartGame(trimmedName, difficulty, code);
  };

  /**
   * Joins an existing multiplayer game via POST request to /game/join.
   */
  const handleJoin = async () => {
    if (!newPlayerName.trim() || !lobbyCode.trim()) return;

    setPlayerName(newPlayerName.trim());
    setGameId(lobbyCode.trim());

    try {
      const res = await fetch(`${API}/game/join`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          gameId: lobbyCode.trim(),
          playerName: newPlayerName.trim(),
        }),
      });

      const data = await res.json();
      setGameId(lobbyCode);

      if (res.ok) {
        console.log('Joined game:', data);
        setMode('multiplayer');
      } else {
        console.error('Failed to join game:', data);
      }
    } catch (err) {
      console.error('Network error joining game:', err);
    }
  };

  /**
   * Creates a new multiplayer game and sends POST to /game/create-multiplayer.
   */
  const handleCreate = async () => {
    if (!newPlayerName.trim()) return;

    const code = Math.random().toString(36).substring(2, 8).toUpperCase();
    setLobbyCode(code);
    setGameId(code);
    setPlayerName(newPlayerName.trim());

    try {
      const res = await fetch(`${API}/game/create-multiplayer`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          gameId: code,
          playerName: newPlayerName.trim(),
        }),
      });

      const data = await res.json();

      if (res.ok) {
        console.log('✅ Game created:', data);
        setMode('multiplayer');
      } else {
        console.error('Failed to create game:', data);
      }
    } catch (err) {
      console.error('Network error creating game:', err);
    }
  };

  /**
   * Renders inputs and controls based on selected mode (single vs multiplayer).
   */
  return (
    <>
      <h1 className="h1-welcome">Welcome to Bid Whist Online!</h1>
      <div className="mode-slider-toggle">
        <label className="switch">
          <input type="checkbox" onChange={(e) => setMode(e.target.checked ? 'multi' : 'single')} />
          <span className="slider"></span>
          <div className="labels">
            <span>Single</span>
            <span>Multi</span>
          </div>
        </label>
      </div>

      <div className="mode-form-frame">
        <div className={`mode-form-container ${mode}`}>
          {/* SINGLE PLAYER */}
          <div className="mode-form single">
            <input
              className="index-input-box"
              type="text"
              placeholder="Enter your name"
              value={newPlayerName}
              onChange={(e) => setNewPlayerName(e.target.value)}
            />
            <select
              className="index-input-box"
              value={difficulty}
              onChange={(e) => setDifficulty(e.target.value)}
            >
              <option value="EASY">Easy</option>
              <option value="MEDIUM">Medium</option>
              <option value="HARD">Hard</option>
            </select>
            <button className="index-button" onClick={handleStart}>
              Start Game
            </button>
          </div>

          {/* MULTIPLAYER */}
          <div className="mode-form multi">
            <input
              className="index-input-box"
              type="text"
              placeholder="Enter your name"
              value={newPlayerName}
              onChange={(e) => setNewPlayerName(e.target.value)}
            />
            <input
              className="index-input-box"
              type="text"
              placeholder="Lobby Code (to join)"
              value={lobbyCode}
              onChange={(e) => setLobbyCode(e.target.value)}
            />
            <div className="multiplayer-buttons">
              <button className="index-button" onClick={handleJoin}>
                Join Game
              </button>
              <button className="index-button" onClick={handleCreate}>
                Create Lobby
              </button>
            </div>
          </div>
        </div>
      </div>
    </>
  );
}

export default ModeSelector;
