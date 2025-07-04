import { createContext, useContext, useRef, useMemo } from 'react';

const RefContext = createContext();

export function RefProvider({ children }) {
  const refs = useRef(new Map());

  const register = (key, refObj) => {
    refs.current.set(key, refObj);
  };

  const get = (key) => refs.current.get(key);

  const debug = () => {
    console.log('[🔍 RefContext snapshot]', Array.from(refs.current.entries()));
  };

  const contextValue = useMemo(() => ({ register, get, debug }), []);

  return (
    <RefContext.Provider value={contextValue}>
      {children}
    </RefContext.Provider>
  );
}

export function useZoneRefs() {
  const context = useContext(RefContext);
  if (!context) {
    throw new Error('useZoneRefs must be used within a RefProvider');
  }
  return context;
}
