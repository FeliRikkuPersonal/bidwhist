// src/context/GameStateContext.js
import { createContext, useContext, useState } from 'react';

const GameStateContext = createContext(null);

export const useGameState = () => useContext(GameStateContext);

export const GameStateProvider = ({ children }) => {
  const [players, setPlayers] = useState([]); // List<PlayerView>
  const [kitty, setKitty] = useState([]); // List<Card>
  const [currentTurnIndex, setCurrentTurnIndex] = useState(0);
  const [phase, setPhase] = useState("START"); // GamePhase
  const [trumpSuit, setTrumpSuit] = useState(null); // Suit
  const [bidType, setBidType] = useState(null); // BidType
  const [winningPlayerName, setWinningPlayerName] = useState(null); // string
  const [highestBid, setHighestBid] = useState(null); // InitialBid
  const [shuffledDeck, setShuffledDeck] = useState([]); // List<Card>
  const [playerPosition, setPlayerPosition] = useState(null); // PlayerPos
  const [firstBidder, setFirstBidder] = useState(null); // PlayerPos
  const [bidTurnIndex, setBidTurnIndex] = useState(0);
  const [bids, setBids] = useState([]); // List<InitialBid>

  const updateFromResponse = (response) => {
    setPlayers(response.players || []);
    setKitty(response.kitty || []);
    setCurrentTurnIndex(response.currentTurnIndex ?? 0);
    setPhase(response.phase || "START");
    setTrumpSuit(response.trumpSuit || null);
    setBidType(response.bidType || null);
    setWinningPlayerName(response.winningPlayerName || null);
    setHighestBid(response.highestBid || null);
    setShuffledDeck(response.shuffledDeck || []);
    setPlayerPosition(response.playerPosition || null);
    setFirstBidder(response.firstBidder || null);
    setBidTurnIndex(response.bidTurnIndex ?? 0);
    setBids(response.bids || []);
  };

  return (
    <GameStateContext.Provider
      value={{
        players,
        kitty,
        currentTurnIndex,
        phase,
        trumpSuit,
        bidType,
        winningPlayerName,
        highestBid,
        shuffledDeck,
        playerPosition,
        firstBidder,
        bidTurnIndex,
        bids,
        updateFromResponse
      }}
    >
      {children}
    </GameStateContext.Provider>
  );
};