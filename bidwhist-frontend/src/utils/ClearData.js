// src/utils/ClearData.js

/**
 * Removes only volatile game session data while preserving:
 * - player identity
 * - positioning
 * - settings (mode, difficulty)
 * - showGameScreen / activeGame (so resume flow is possible)
 */
export function clearCurrentGameData() {
  const keysToClear = [
    // UIDisplayContext
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

    // GameStateContext
    'kitty',
  ];

  keysToClear.forEach((key) => localStorage.removeItem(key));
  console.log('[🧹 Cleared current game session data]');
}

/**
 * Removes all persisted game data, including player identity, UI layout, and settings.
 * Use this when fully leaving the game or logging out.
 */
export function clearAllGameData() {
  const keysToClear = [
    // UIDisplayContext
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

    // PositionContext
    'playerName',
    'viewerPosition',
    'viewerTeam',
    'backendPositions',

    // GameStateContext
    'kitty',
    'mode',
    'difficulty',
    'activeGame',
  ];

  keysToClear.forEach((key) => localStorage.removeItem(key));
  console.log('[🧨 Cleared all game data]');
}
