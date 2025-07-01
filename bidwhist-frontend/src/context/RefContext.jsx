// src/context/RefContext.js
import { createContext, useContext, useRef } from 'react';

const RefContext = createContext();

export function RefProvider({ children }) {
  const refs = useRef(new Map());

  const register = (key, refObj) => {
    refs.current.set(key, refObj);
  };

  const get = (key) => refs.current.get(key);

  return (
    <RefContext.Provider value={{ register, get }}>
      {children}
    </RefContext.Provider>
  );
}

export function useZoneRefs() {
  return useContext(RefContext);
}
