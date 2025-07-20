// src/utils/PositionUtils.js

/**
 * Maps player backend positions (e.g., 'P1', 'P2') to table directions
 * relative to the viewer ('south', 'west', 'north', 'east'), rotating clockwise.
 *
 * @param {Object} backendPositions - Map of backend positions to player names
 * @param {string} viewerPosition - The backend position of the current viewer (e.g., 'P2')
 * @returns {Object} Map of backend positions to relative directions
 */
export function getPositionMap(backendPositions, viewerPosition) {
  const directions = ['south', 'west', 'north', 'east'];

  const positions = Object.keys(backendPositions); // ['P1', 'P2', 'P3', 'P4']
  const viewerIndex = positions.indexOf(viewerPosition);

  if (viewerIndex === -1) {
    console.warn(`Viewer position ${viewerPosition} not found in backend positions`);
    return {};
  }

  const rotated = [...positions.slice(viewerIndex), ...positions.slice(0, viewerIndex)];

  const map = {};
  for (let i = 0; i < rotated.length; i++) {
    map[rotated[i]] = directions[i];
  }

  return map;
}
