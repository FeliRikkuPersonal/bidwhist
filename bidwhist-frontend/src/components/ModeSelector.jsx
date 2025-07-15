// src/compnents/ModeSelector.jsx

import "../css/ModeSelector.css";
import { useState } from "react";
import { useGameState } from "../context/GameStateContext";
import { usePositionContext } from "../context/PositionContext";

/* The ModeSelector component handles the logic for selecting the game mode (single-player or multiplayer),
   entering player name, setting difficulty, joining an existing game, or creating a new game. */
function ModeSelector({ onStartGame }) {
  const { setGameId, mode, setMode, difficulty, setDifficulty } =
    useGameState();

  const { setPlayerName } = usePositionContext();

  const [newPlayerName, setNewPlayerName] = useState("");
  const [lobbyCode, setLobbyCode] = useState("");
  const API = import.meta.env.VITE_API_URL;

  // --- handleStart Function ---
  /* Handles starting the game in single-player mode. Generates a random game ID, 
       sets the game ID in the context, and calls the `onStartGame` function passed as a prop. */
  const handleStart = () => {
    const trimmedName = newPlayerName.trim();

    if (trimmedName) {
      const code = Math.random().toString(36).substring(2, 8).toUpperCase();
      setPlayerName(trimmedName);
      setGameId(code);
      onStartGame(trimmedName, difficulty, code);
    }
  };

  // --- handleJoin Function ---
  /* Handles joining an existing multiplayer game. It sends a POST request with the player's name and lobby code. */
  const handleJoin = async () => {
    if (!newPlayerName.trim() || !lobbyCode.trim()) return;
    setPlayerName(newPlayerName.trim());
    setGameId(lobbyCode.trim());

    try {
      const res = await fetch(`${API}/game/join`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          gameId: lobbyCode.trim(),
          playerName: newPlayerName.trim(),
        }),
      });

      const data = await res.json();
      setGameId(lobbyCode);

      if (res.ok) {
        console.log("Joined game:", data);
        // Navigate to game screen or update app state as needed
        setMode("multiplayer");
      } else {
        console.error("Failed to join game:", data);
      }
    } catch (err) {
      console.error("Network error joining game:", err);
    }
  };

  // --- handleCreate Function ---
  /* Handles creating a new multiplayer game. It generates a new lobby code and sends a POST request to create the game. */
  const handleCreate = async () => {
    if (!newPlayerName.trim()) return;

    const code = Math.random()
      .toString(36)
      .substring(2, 8)
      .toUpperCase()
      .trim();
    setLobbyCode(code);
    setGameId(code);
    setPlayerName(newPlayerName.trim());

    try {
      const res = await fetch(`${API}/game/create-multiplayer`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          gameId: code,
          playerName: newPlayerName.trim(),
        }),
      });

      const data = await res.json();

      if (res.ok) {
        console.log("✅ Game created:", data);
        // Navigate to game screen or save gameId/player info as needed
        setMode("multiplayer");
      } else {
        console.error("❌ Failed to create game:", data);
      }
    } catch (err) {
      console.error("🔥 Network error creating game:", err);
    }
  };

  // --- JSX Rendering ---
  /* Renders the mode selector interface, including the input for player name, difficulty, and 
    buttons for starting, joining, or creating a game. */
  return (
    <>
      <h1 className="h1-welcome">Welcome to Bid Whist Online!</h1>
      <div className="mode-slider-toggle">
        <label className="switch">
          <input
            type="checkbox"
            onChange={(e) => setMode(e.target.checked ? "multi" : "single")}
          />
          <span className="slider"></span>
          <div className="labels">
            <span>Single</span>
            <span>Multi</span>
          </div>
        </label>
      </div>

      <div className={"mode-form-frame"}>
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

            {/* 🎯 NEW: Difficulty selector */}
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
