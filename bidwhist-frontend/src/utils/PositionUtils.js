// src/utils/PositionUtils.js

/**
 * Maps player IDs to UI-relative positions (south, west, north, east)
 * based on the viewer's perspective.
 *
 * @param {string[]} players - Ordered list of player IDs (e.g., ["P1", "P2", "P3", "P4"])
 * @param {string} viewerId - The ID of the player viewing the UI
 * @returns {Object} playerId => position (e.g., { P1: "west", P2: "south", ... })
 */
export function getPositionMap(players, viewerId) {
  const viewerIndex = players.indexOf(viewerId);
  const positions = ["south", "west", "north", "east"];

  return players.reduce((map, playerId, i) => {
    const position = positions[(i - viewerIndex + 4) % 4];
    map[playerId] = position;
    return map;
  }, {});
}
