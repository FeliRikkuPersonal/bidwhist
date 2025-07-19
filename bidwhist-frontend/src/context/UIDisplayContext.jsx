import { createContext, useContext, useState } from 'react';


export const UIDisplayContext = createContext(null);

export function UIDisplayProvider({ children }) {
  const [handMap, setHandMap] = useState({
    north: [],
    south: [],
    east: [],
    west: [],
  });
  const [showGameScreen, setShowGameScreen] = useState(false);
  const [showAnimatedCards, setShowAnimatedCards] = useState(false);
  const [deckPosition, setDeckPosition] = useState({ x: 0, y: 0 });
  const [playedCardPosition, setPlayedCardPosition] = useState(null);
  const [animatedCards, setAnimatedCards] = useState([]);
  const [showShuffle, setShowShuffle] = useState(false);
  const [showHands, setShowHands] = useState(false);
  const [showBidding, setShowBidding] = useState(false);
  const [bidPhase, setBidPhase] = useState(false);
  const [kittyPhase, setKittyPhase] = useState(false);
  const [showFinalizeBid, setShowFinalizeBid] = useState(false);
  const [awardKitty, setAwardKitty] = useState(false);
  const [myTurn, setMyTurn] = useState(false);
  const [discardPile, setDiscardPile] = useState([]);
  const [selectedCard, setSelectedCard] = useState([]);
  const [loadGame, setLoadGame] = useState(false);
  const [showLobby, setShowLobby] = useState(false);
  const [playedCard, setPlayedCard] = useState(null);
  const [animationQueue, setAnimationQueue] = useState([]);
  const [teamATricks, setTeamATricks] = useState(0);
  const [teamBTricks, setTeamBTricks] = useState(0);

  const queueAnimationFromResponse = (response) => {
    if ('animationQueue' in response) setAnimationQueue(response.animationQueue);
  }

  const debugLog = () => {
    console.log("[🧠 UIDisplayContext Snapshot]", {
      showGameScreen,
      showAnimatedCards,
      deckPosition,
      playedCardPosition,
      animatedCards,
      showShuffle,
      showHands,
      showBidding,
      bidPhase,
      kittyPhase,
      showFinalizeBid,
      awardKitty,
      myTurn,
      discardPile,
      selectedCard,
      loadGame,
      showLobby,
      playedCard,
      teamATricks,
      teamBTricks,
    });
  }

  const setHandFor = (direction, hand) => {
    setHandMap(prev => ({ ...prev, [direction]: hand }));
  };


  return (
    <UIDisplayContext.Provider value={{
      handMap,
      setHandMap,
      showGameScreen,
      setShowGameScreen,
      showAnimatedCards,
      setShowAnimatedCards,
      deckPosition,
      setDeckPosition,
      playedCardPosition,
      setPlayedCardPosition,
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
      kittyPhase,
      setKittyPhase,
      showFinalizeBid,
      setShowFinalizeBid,
      awardKitty,
      setAwardKitty,
      myTurn,
      setMyTurn,
      discardPile,
      setDiscardPile,
      selectedCard,
      setSelectedCard,
      loadGame,
      setLoadGame,
      showLobby,
      setShowLobby,
      playedCard,
      setPlayedCard,
      animationQueue,
      setAnimationQueue,
      teamATricks,
      setTeamATricks,
      teamBTricks,
      setTeamBTricks,
      setHandFor,
      queueAnimationFromResponse,
      debugLog,
    }}>
      {children}
    </UIDisplayContext.Provider>
  );
}

export const useUIDisplay = () => useContext(UIDisplayContext);
