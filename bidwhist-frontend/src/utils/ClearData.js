// src/utils/ClearData.js

/**
 * Removes only volatile game session data while preserving:
 * - player identity
 * - positioning
 * - settings (mode, difficulty)
 * - showGameScreen / activeGame (so resume flow is possible)
 */
export async function clearCurrentGameData({ clearUIContext, clearGameStateContext }) {
  if (clearUIContext) clearUIContext();
  if (clearGameStateContext) clearGameStateContext();

  const keysToClear = [
    'handMap',
    'showBidding',
    'bidPhase',
    'kittyPhase',
    'showFinalizeBid',
    'awardKitty',
    'discardPile',
    'teamATricks',
    'teamBTricks',
    'showFinalScore',
    'animatedCards',
    'showHands',
    'showGameScreen',
    'playedCardsByDirection',
    'kitty',
  ];

  keysToClear.forEach((key) => localStorage.removeItem(key));
  console.log('[🧹 Cleared current game session data]');
}

export async function clearAllGameData({ clearUIContext, clearGameStateContext }) {
  if (clearUIContext) clearUIContext();
  if (clearGameStateContext) clearGameStateContext();

  const keysToClear = [
    'showGameScreen',
    'showLobby',
    'loadGame',
    'handMap',
    'showBidding',
    'bidPhase',
    'kittyPhase',
    'showFinalizeBid',
    'awardKitty',
    'discardPile',
    'animationQueue',
    'teamATricks',
    'teamBTricks',
    'animatedCards',
    'playedCardPosition',
    'playedCardsByDirection',
    'ShowAnimatedCards',
    'showHands',
    'showShuffle',
    'playerName',
    'viewerPosition',
    'viewerTeam',
    'backendPositions',
    'kitty',
    'difficulty',
    'activeGame',
  ];

  keysToClear.forEach((key) => localStorage.removeItem(key));
  console.log('[🧨 Cleared all game data]');
}
