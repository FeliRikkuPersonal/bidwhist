// src/components/HamburgerMenu.jsx

import React, { useState } from 'react';
import handleQuit from '../utils/handleQuit';
import { startNewGame } from '../utils/gameApi'; // adjust path
import { useUIDisplay } from '../context/UIDisplayContext';
import { usePositionContext } from '../context/PositionContext';
import { useGameState } from '../context/GameStateContext';
import { useThrowAlert } from '../hooks/useThrowAlert';
import { clearAllGameData, clearCurrentGameData } from '../utils/ClearData';
import '../css/HamburgerMenu.css'; // Optional styling

const HamburgerMenu = () => {
  const [open, setOpen] = useState(false);
  const {
    setMyTurn,
    queueAnimationFromResponse,
    key,
    setKey,
    clearUIContext,
    setAnimationQueue,
  } = useUIDisplay();
  const { viewerPosition } = usePositionContext();
  const {
    gameId,
    updateFromResponse,
    clearGameStateContext,
  } = useGameState();
  const savedMode = JSON.parse(localStorage.getItem('mode'));

  const throwAlert = useThrowAlert();

  const API = import.meta.env.VITE_API_URL; // Server endpoint

  const toggleMenu = () => setOpen(!open);
  const reloadComponent = () => setKey(prev => prev + 1);

  const handleNewGame = async () => {
    setKey(prev => prev + 1);
    const { success, gameData, isMyTurn, message } = await startNewGame({
      viewerPosition,
      gameId,
      savedMode,
      API,
      sessionKey: key,
    });

    if (success) {
      setAnimationQueue([]);
      await clearCurrentGameData({clearUIContext, clearGameStateContext});
      updateFromResponse(gameData);
      queueAnimationFromResponse(gameData, key);
      setMyTurn(isMyTurn);
      reloadComponent();
    } else {
      throwAlert(message, 'error');
    }
  };


  return (
    <div className="hamburger-container">
      <button className="hamburger-icon" onClick={toggleMenu}>
        {/* Hamburger lines */}
        <div className="bar"></div>
        <div className="bar"></div>
        <div className="bar"></div>
      </button>

      {open && (
        <div className="dropdown-menu">
          <a onClick={ () =>
            {handleQuit({
              viewerPosition, 
              gameId, 
              savedMode, 
              API, 
              clearUIContext, 
              clearGameStateContext});
              toggleMenu();}
          }>Home</a>
          <a onClick={() => {handleNewGame(); toggleMenu();}} >New Game</a>
          <a
            href="/site/index.html"
            target="_blank"
            rel="noopener noreferrer"
            onClick={toggleMenu}>About</a>
        </div>
      )}
    </div>
  );
};

export default HamburgerMenu;
