import { createContext, useContext, useRef, useMemo } from 'react';

// --- RefContext creation ---
/* Create a new context to manage the refs across different components. */
const RefContext = createContext();

// --- RefProvider Component ---
/* 
* The RefProvider component will provide access to the `register`, `get`, and `debug` 
* functions for registering and accessing refs throughout the app. 
*/
export function RefProvider({ children }) {

  // --- Refs Map ---
  /* 
  * Initialize a `Map` to store the refs. This allows registering multiple refs with 
  * unique keys. 
  * */
  const refs = useRef(new Map());

  // --- register function ---
  /* 
  * This function registers a ref with a specific key. It checks if the ref object is 
  * valid before adding it to the `Map`. 
  */
  const register = (key, refObj) => {
    if (refObj && refObj.current) {
      refs.current.set(key, refObj); // Add the ref to the map with the specified key
    } else {
      console.warn(`[register] Attempted to register ${key} with invalid ref`, refObj);
    }
  };

  // --- get function ---
  /* This function retrieves the ref object by its key. */
  const get = (key) => refs.current.get(key);

  // --- debug function ---
  /* This function logs the current state of the `refs` map to the console, useful for debugging. */
  const debug = () => {
    console.log('[🔍 RefContext snapshot]', Array.from(refs.current.entries()));
  };

  // --- Memoizing the context value ---
  /* The `useMemo` hook ensures that the context value only re-renders if necessary. */
  const contextValue = useMemo(() => ({ register, get, debug }), []);

  return (
    <RefContext.Provider value={contextValue}>
      {children} 
    </RefContext.Provider>
  );
}

// --- useZoneRefs Hook ---
/* A custom hook to access the `RefContext` inside any component that is a descendant of `RefProvider`. */
export function useZoneRefs() {
  const context = useContext(RefContext);

  // --- Error Handling ---
  /* If the hook is used outside the `RefProvider`, an error is thrown. */
  if (!context) {
    throw new Error('useZoneRefs must be used within a RefProvider');
  }

  return context; // Return the context value containing `register`, `get`, and `debug`
}
