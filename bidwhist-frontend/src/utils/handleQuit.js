// src/utils/handleQuit.js
import { clearAllGameData } from './ClearData';

const handleQuit = async ({ viewerPosition, gameId, savedMode, API }) => {
  if (confirm('Are you sure you want to quit? This will clear all game data.')) {
    try {
      await fetch(`${API}/game/quit`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ player: viewerPosition, gameId, savedMode }),
      });
    } catch (error) {
      console.error('Quit API failed:', error);
    }

    clearAllGameData();
    window.location.reload();
    window.location.href = '/';
  }
};

export default handleQuit;
