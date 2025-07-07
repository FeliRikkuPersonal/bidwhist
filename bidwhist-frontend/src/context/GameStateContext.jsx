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
  const [firstBidder, setFirstBidder] = useState(null); // PlayerPos
  const [bidTurnIndex, setBidTurnIndex] = useState(0);
  const [bids, setBids] = useState([]); // List<InitialBid>
  const [winningBid, setWinningBid] = useState(null);
  const [lobbySize, setLobbySize] = useState(0);
  const [mode, setMode] = useState('single');
  const [difficulty, setDifficulty] = useState('EASY'); // NEW: default to EASY
  const [gameId, setGameId] = useState(null);

  const updateFromResponse = (response) => {
    if ('players' in response) setPlayers(response.players);
    if ('kitty' in response) setKitty(response.kitty);
    if ('currentTurnIndex' in response) setCurrentTurnIndex(response.currentTurnIndex);
    if ('phase' in response) setPhase(response.phase);
    if ('trumpSuit' in response) setTrumpSuit(response.trumpSuit);
    if ('bidType' in response) setBidType(response.bidType);
    if ('winningPlayerName' in response) setWinningPlayerName(response.winningPlayerName);
    if ('highestBid' in response) setHighestBid(response.highestBid);
    if ('shuffledDeck' in response) setShuffledDeck(response.shuffledDeck);
    if ('firstBidder' in response) setFirstBidder(response.firstBidder);
    if ('bidTurnIndex' in response) setBidTurnIndex(response.bidTurnIndex);
    if ('bids' in response) setBids(response.bids);
    if ('winningBid' in response) setWinningBid(response.winningBid);
    if ('lobbySize' in response) setLobbySize(response.lobbySize);
    if (!gameId && 'gameId' in response && response.gameId) setGameId(response.gameId);
  };

  const debugLog = () => {
    console.log("[GameStateContext Snapshot]", {
      players,
      kitty,
      currentTurnIndex,
      phase,
      trumpSuit,
      bidType,
      winningPlayerName,
      highestBid,
      shuffledDeck,
      firstBidder,
      bidTurnIndex,
      bids,
      winningBid,
      lobbySize,
      mode,
      difficulty,
      gameId
    });
  }


  return (
    <GameStateContext.Provider
    value={{
      players,
      setPlayers,
      kitty,
      setKitty,
      currentTurnIndex,
      setCurrentTurnIndex,
      phase,
      setPhase,
      trumpSuit,
      setTrumpSuit,
      bidType,
      setBidType,
      winningPlayerName,
      setWinningPlayerName,
      highestBid,
      setHighestBid,
      shuffledDeck,
      setShuffledDeck,
      firstBidder,
      setFirstBidder,
      bidTurnIndex,
      setBidTurnIndex,
      bids,
      setBids,
      winningBid,
      setWinningBid,
      lobbySize,
      setLobbySize,
      mode,
      setMode,
      difficulty,
      setDifficulty,
      gameId,
      setGameId,
      updateFromResponse,
      debugLog,
    }}
    >
      {children}
    </GameStateContext.Provider>
  );
};