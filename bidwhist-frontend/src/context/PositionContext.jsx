import { createContext, useContext, useState, useEffect, useMemo } from 'react';
import { useGameState } from './GameStateContext';

export const PositionContext = createContext(null);

export function PositionProvider({ children }) {
  const [playerName, setPlayerName] = useState(null);
  const [viewerPosition, setViewerPosition] = useState(null);
  const [viewerTeam, setViewerTeam] = useState(null);
  const [backendPositions, setBackendPositions] = useState({}); // position → name map
  const viewerDirection = 'south'; // always fixed for local user
  const { players } = useGameState(); // ✅ get the list from context


  // CLOCKWISE UTILITY
  const clockwiseOrder = ['P1', 'P2', 'P3', 'P4'];

  const positionToDirection = useMemo(() => {
    if (!viewerPosition || Object.keys(backendPositions).length === 0) return {};

    const index = clockwiseOrder.indexOf(viewerPosition);
    if (index === -1) return {};

    // Rotate clockwise from viewerPosition
    const rotated = [
      clockwiseOrder[index],
      clockwiseOrder[(index + 1) % 4],
      clockwiseOrder[(index + 2) % 4],
      clockwiseOrder[(index + 3) % 4],
    ];

    const directionMap = {
      [rotated[0]]: 'south',
      [rotated[1]]: 'west',
      [rotated[2]]: 'north',
      [rotated[3]]: 'east',
    };

    return directionMap;
  }, [viewerPosition, backendPositions]);

  const directionToPosition = useMemo(() => {
    return Object.fromEntries(
      Object.entries(positionToDirection).map(([pos, dir]) => [dir, pos])
    );
  }, [positionToDirection]);

  const frontendPositions = useMemo(() => {
    // direction → { position, name }
    return Object.entries(directionToPosition).reduce((acc, [dir, pos]) => {
      acc[dir] = {
        position: pos,
        name: backendPositions[pos] || '',
      };
      return acc;
    }, {});
  }, [directionToPosition, backendPositions]);

  const playerTeam = useMemo(() => {
    const map = {};
    players.forEach(({ position, team }) => {
      if (position && team) {
        map[position] = team;
      }
    });
    return map;
  }, [players]);


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
    console.log("[📢 backendPositions changed]:", backendPositions);
  }, [backendPositions]);

  return (
    <PositionContext.Provider value={{
      playerName,
      setPlayerName,
      viewerPosition,
      setViewerPosition,
      viewerTeam,
      setViewerTeam,
      backendPositions,
      setBackendPositions, // expects: { P1: 'Alice', P2: 'AI 1', ... }
      viewerDirection,
      positionToDirection,
      directionToPosition,
      frontendPositions,
      playerTeam,
      debugLog,
    }}>
      {children}
    </PositionContext.Provider>
  );
}

export const usePositionContext = () => useContext(PositionContext);
