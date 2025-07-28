// src/context/UIDisplayContext.jsx

import React, { createContext, useContext, useState } from 'react';
import { useLocalStorage } from '../hooks/useLocalStorage';
import { useGameState } from './GameStateContext';

export const UIDisplayContext = createContext(null);

/**
 * Provides shared UI display state across the app, including:
 * - Which screen to show (lobby/game)
 * - Game animations and card visibility
 * - Trick counters, shuffle display, bidding/kitty phases
 * - Hand mappings and played card tracking
 *
 * @param {React.ReactNode} children - Child components that will consume the context
 * @returns {JSX.Element} Context provider wrapping children
 */
export function UIDisplayProvider({ children }) {
  const [handMap, setHandMap] = useLocalStorage('handMap', {
    north: [],
    south: [],
    east: [],
    west: [],
  });

  const [showGameScreen, setShowGameScreen] = useLocalStorage('showGameScreen', false);
  const [showAnimatedCards, setShowAnimatedCards] = useLocalStorage('showAnimatedCards', false);
  const [playedCardPosition, setPlayedCardPosition] = useLocalStorage('playedCardPosition', null);
  const [showShuffle, setShowShuffle] = useLocalStorage('showShuffle', false);
  const [showHands, setShowHands] = useLocalStorage('showHands', false);
  const [showBidding, setShowBidding] = useLocalStorage('showBidding', false);
  const [bidPhase, setBidPhase] = useLocalStorage('bidPhase', false);
  const [kittyPhase, setKittyPhase] = useLocalStorage('kittyPhase', false);
  const [showFinalizeBid, setShowFinalizeBid] = useLocalStorage('showFinalizeBid', false);
  const [awardKitty, setAwardKitty] = useLocalStorage('awardKitty', false);
  const [discardPile, setDiscardPile] = useLocalStorage('discardPile', []);
  const [loadGame, setLoadGame] = useLocalStorage('loadGame', false);
  const [showLobby, setShowLobby] = useLocalStorage('showLobby', false);
  const [teamATricks, setTeamATricks] = useLocalStorage('teamATricks', 0);
  const [teamBTricks, setTeamBTricks] = useLocalStorage('teamBTricks', 0);
  const [animatedCards, setAnimatedCards] = useLocalStorage('animatedCards', []);
  const [showFinalScore, setShowFinalScore] = useLocalStorage('showFinalScore', false);
  const [playedCardsByDirection, setPlayedCardsByDirection] = useLocalStorage('playedCardsByDirection', {
    north: null,
    south: null,
    east: null,
    west: null,
  }); // Visual state of cards on table by direction


  const [animationQueue, setAnimationQueue] = useState([]);
  const [deckPosition, setDeckPosition] = useState({ x: 0, y: 0 });
  const [playedCard, setPlayedCard] = useState(null);
  const [selectedCard, setSelectedCard] = useState([]);
  const [myTurn, setMyTurn] = useState(false);

  const { setLeadSuit } = useGameState();
  /**
   * Updates the animation queue from a backend response.
   * Used during polling to prepare card animations.
   */
  const queueAnimationFromResponse = (response) => {
    if ('animationQueue' in response) setAnimationQueue(response.animationQueue);
    if ('leadSuit' in response) setLeadSuit(response.leadSuit);
  };

  /**
   * Logs the current UI context state to the console for debugging purposes.
   */
  const debugLog = () => {
    console.log('[🧠 UIDisplayContext Snapshot]', {
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
      showFinalScore
    });
  };

  /**
   * Updates the hand for a specific direction (south, west, etc.).
   */
  const setHandFor = (direction, hand) => {
    setHandMap((prev) => ({ ...prev, [direction]: hand }));
  };

  return (
    <UIDisplayContext.Provider
      value={{
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
        playedCardsByDirection,
        setPlayedCardsByDirection,
        animationQueue,
        setAnimationQueue,
        teamATricks,
        setTeamATricks,
        teamBTricks,
        setTeamBTricks,
        showFinalScore,
        setShowFinalScore,
        setHandFor,
        queueAnimationFromResponse,
        debugLog,
      }}
    >
      {children}
    </UIDisplayContext.Provider>
  );
}

/**
 * Hook to consume UIDisplay context in functional components.
 *
 * @returns {object} Context state and handlers related to UI display logic
 */
export const useUIDisplay = () => useContext(UIDisplayContext);
