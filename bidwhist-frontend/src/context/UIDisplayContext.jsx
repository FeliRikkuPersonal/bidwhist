import { createContext, useContext, useState } from 'react';


export const UIDisplayContext = createContext(null);

export function UIDisplayProvider({ children }) {
  const [showGameScreen, setShowGameScreen] = useState(false);
  const [showAnimatedCards, setShowAnimatedCards] = useState(false);
  const [deckPosition, setDeckPosition] = useState({ x: 0, y: 0 });
  const [animatedCards, setAnimatedCards] = useState([]);
  const [showShuffle, setShowShuffle] = useState(false);
  const [showHands, setShowHands] = useState(false);
  const [showBidding, setShowBidding] = useState(false);
  const [bidPhase, setBidPhase] = useState(false);

    const debugLog = () => {
    console.log("[🧠 UIDisplayContext Snapshot]", {
      showGameScreen,
      showAnimatedCards,
      deckPosition,
      animatedCards,
      showShuffle,
      showHands,
      showBidding,
      bidPhase,
    });
  }

  return (
    <UIDisplayContext.Provider value={{
      showGameScreen,
      setShowGameScreen,
      showAnimatedCards,
      setShowAnimatedCards,
      deckPosition,
      setDeckPosition,
      animatedCards,
      setAnimatedCards,
      showShuffle,
      setShowShuffle,
      showHands,
      setShowHands,
      showBidding,
      setShowBidding,
      bidPhase,
      setBidPhase,
      debugLog,
    }}>
      {children}
    </UIDisplayContext.Provider>
  );
}

export const useUIDisplay = () => useContext(UIDisplayContext);
