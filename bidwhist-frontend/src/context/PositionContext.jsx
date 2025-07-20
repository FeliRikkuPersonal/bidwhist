// src/context/PositionContext.jsx

import { createContext, useContext, useState, useEffect, useMemo } from 'react';
import { useGameState } from './GameStateContext';

export const PositionContext = createContext(null);

/**
 * Provides positional mapping between players and directional UI labels.
 * Includes player names, viewer position/team, clockwise mapping logic,
 * and utility transforms between backend/player data and UI layout.
 *
 * @param {React.ReactNode} children - Components that consume the position context
 * @returns {JSX.Element} Context provider for player positioning and mapping
 */
export function PositionProvider({ children }) {
  const [playerName, setPlayerName] = useState(null);
  const [viewerPosition, setViewerPosition] = useState(null);
  const [viewerTeam, setViewerTeam] = useState(null);
  const [backendPositions, setBackendPositions] = useState({}); // backend position → name

  const viewerDirection = 'south'; // always fixed as the local user's direction

  const { players } = useGameState();

  const clockwiseOrder = ['P1', 'P2', 'P3', 'P4'];

  /**
   * Maps backend position keys to UI-facing directions (south, west, north, east)
   */
  const positionToDirection = useMemo(() => {
    if (!viewerPosition || Object.keys(backendPositions).length === 0) return {};

    const index = clockwiseOrder.indexOf(viewerPosition);
    if (index === -1) return {};

    const rotated = [
      clockwiseOrder[index],
      clockwiseOrder[(index + 1) % 4],
      clockwiseOrder[(index + 2) % 4],
      clockwiseOrder[(index + 3) % 4],
    ];

    return {
      [rotated[0]]: 'south',
      [rotated[1]]: 'west',
      [rotated[2]]: 'north',
      [rotated[3]]: 'east',
    };
  }, [viewerPosition, backendPositions]);

  /**
   * Inverse of positionToDirection: UI direction → backend position
   */
  const directionToPosition = useMemo(() => {
    return Object.fromEntries(
      Object.entries(positionToDirection).map(([pos, dir]) => [dir, pos])
    );
  }, [positionToDirection]);

  /**
   * Combines direction-to-position mapping with player names for the frontend.
   * Returns an object like: { south: { position: 'P1', name: 'Alice' }, ... }
   */
  const frontendPositions = useMemo(() => {
    return Object.entries(directionToPosition).reduce((acc, [dir, pos]) => {
      acc[dir] = {
        position: pos,
        name: backendPositions[pos] || '',
      };
      return acc;
    }, {});
  }, [directionToPosition, backendPositions]);

  /**
   * Maps player position to team (e.g., { P1: 'A', P3: 'A', P2: 'B', P4: 'B' })
   */
  const playerTeam = useMemo(() => {
    const map = {};
    players.forEach(({ position, team }) => {
      if (position && team) {
        map[position] = team;
      }
    });
    return map;
  }, [players]);

  /**
   * Logs a snapshot of the current position context values to console.
   */
  const debugLog = () => {
    console.log('[🧠 PositionContext Snapshot]', {
      playerName,
      viewerPosition,
      viewerTeam,
      viewerDirection,
      backendPositions,
      positionToDirection,
      directionToPosition,
      frontendPositions,
    });
  };

  useEffect(() => {
    console.log('[📢 backendPositions changed]:', backendPositions);
  }, [backendPositions]);

  return (
    <PositionContext.Provider
      value={{
        playerName,
        setPlayerName,
        viewerPosition,
        setViewerPosition,
        viewerTeam,
        setViewerTeam,
        backendPositions,
        setBackendPositions,
        viewerDirection,
        positionToDirection,
        directionToPosition,
        frontendPositions,
        playerTeam,
        debugLog,
      }}
    >
      {children}
    </PositionContext.Provider>
  );
}

/**
 * Hook to consume position context within any descendant component of the provider.
 *
 * @returns {object} Context value including position mappings and handlers
 * @throws Will throw if used outside of PositionProvider
 */
export const usePositionContext = () => useContext(PositionContext);
