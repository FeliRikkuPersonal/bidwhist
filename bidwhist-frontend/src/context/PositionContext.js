import { createContext, useContext, useState } from 'react';

export const PositionContext = createContext(null);

export function PositionProvider({ children }) {
  const [viewerPosition, setViewerPosition] = useState(null);
  const [backendPositions, setBackendPositions] = useState([]);
  
  const positionToDirection = backendPositions.length && viewerPosition
    ? getPositionMap(backendPositions, viewerPosition)
    : {};

  const directionToPosition = Object.fromEntries(
    Object.entries(positionToDirection).map(([pos, dir]) => [dir, pos])
  );

  return (
    <PositionContext.Provider value={{
      viewerPosition,
      setViewerPosition,
      backendPositions,
      setBackendPositions,
      positionToDirection,
      directionToPosition
    }}>
      {children}
    </PositionContext.Provider>
  );
}

export const usePositionContext = () => useContext(PositionContext);
