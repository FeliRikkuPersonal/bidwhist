// src/utils/gameApi.js

export async function startNewGame({ viewerPosition, gameId, savedMode, API, sessionKey }) {
  try {
    const res = await fetch(`${API}/game/newGame`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ player: viewerPosition, gameId, savedMode, sessionKey }),
    });

    const data = await res.json();

    if (!res.ok) {
      return { success: false, message: data.message || 'Failed to start new game.' };
    }

    return {
      success: true,
      gameData: data,
      isMyTurn: data.currentTurnIndex === viewerPosition && data.phase === 'PLAY',
    };
  } catch (error) {
    console.error('Network error:', error);
    return { success: false, message: 'Network error' };
  }
}
