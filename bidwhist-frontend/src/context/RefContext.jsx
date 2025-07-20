// src/context/RefContext.jsx

import { createContext, useContext, useRef, useMemo } from 'react';

const RefContext = createContext();

/**
 * RefProvider gives access to the `register`, `get`, and `debug` methods
 * for managing DOM element refs across the app.
 *
 * @param {React.ReactNode} children - Components that consume this context
 * @returns {JSX.Element} Context provider with shared ref registry
 */
export function RefProvider({ children }) {
  const refs = useRef(new Map());

  /**
   * Registers a DOM ref under a unique key.
   *
   * @param {string} key - Identifier for the ref (e.g. "play-north")
   * @param {object} refObj - React ref object to store
   */
  const register = (key, refObj) => {
    if (refObj?.current) {
      refs.current.set(key, refObj);
    } else {
      console.warn(`[register] Attempted to register ${key} with invalid ref`, refObj);
    }
  };

  /**
   * Retrieves a previously registered ref.
   *
   * @param {string} key - Ref identifier
   * @returns {object|undefined} Ref object or undefined if not found
   */
  const get = (key) => refs.current.get(key);

  /**
   * Logs the current state of all registered refs to the console.
   */
  const debug = () => {
    console.log('[🔍 RefContext snapshot]', Array.from(refs.current.entries()));
  };

  const contextValue = useMemo(() => ({ register, get, debug }), []);

  return <RefContext.Provider value={contextValue}>{children}</RefContext.Provider>;
}

/**
 * Custom hook for accessing the RefContext.
 *
 * @returns {{ register: Function, get: Function, debug: Function }}
 * @throws Will throw an error if used outside of RefProvider
 */
export function useZoneRefs() {
  const context = useContext(RefContext);
  if (!context) {
    throw new Error('useZoneRefs must be used within a RefProvider');
  }
  return context;
}
