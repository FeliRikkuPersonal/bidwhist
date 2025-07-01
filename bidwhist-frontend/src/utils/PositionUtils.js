// src/utils/PositionUtils.js

/**
 * Given an ordered list of players and the current viewer's position,
 * assign directions: south (you), then clockwise.
 */
export function getPositionMap(backendPositions, viewerPosition) {
  const directions = ['south', 'west', 'north', 'east'];
  const viewerIndex = backendPositions.indexOf(viewerPosition);

  if (viewerIndex === -1) {
    console.warn(`Viewer position ${viewerPosition} not found in backend positions`);
    return {};
  }

  const rotated = [...backendPositions.slice(viewerIndex), ...backendPositions.slice(0, viewerIndex)];

  const map = {};
  for (let i = 0; i < rotated.length; i++) {
    map[rotated[i]] = directions[i];
  }

  return map;
}
