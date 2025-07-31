// src/context/GameStateContext.js

import React, { createContext, useContext, useState } from 'react';
import { useLocalStorage } from '../hooks/useLocalStorage';

const GameStateContext = createContext(null);

/**
 * Hook to access the shared game state context.
 *
 * @returns {object} The full game state and its updater functions
 * @throws Will throw if used outside a GameStateProvider
 */
export const useGameState = () => useContext(GameStateContext);

/**
 * Provides full state management for the game session.
 * Includes bidding, turn handling, score tracking, and server sync logic.
 *
 * @param {React.ReactNode} children - Child components
 * @returns {JSX.Element} Provider with game state values
 */
export const GameStateProvider = ({ children }) => {
  const [kitty, setKitty] = useLocalStorage('kitty', []); // List<Card>
  const [mode, setMode] = useLocalStorage('mode', 'single');
  const [difficulty, setDifficulty] = useLocalStorage('difficulty', 'EASY');
  const [activeGame, setActiveGame] = useLocalStorage('activeGame', false);
  const [forcedBid, setForcedBid] = useLocalStorage('forcedBid', false);

  const [players, setPlayers] = useState([]); // List<PlayerView>
  const [currentTurnIndex, setCurrentTurnIndex] = useState(0);
  const [phase, setPhase] = useState('START'); // GamePhase
  const [trumpSuit, setTrumpSuit] = useState(null); // Suit
  const [bidType, setBidType] = useState(null); // BidType
  const [winningPlayerName, setWinningPlayerName] = useState(null);
  const [highestBid, setHighestBid] = useState(null); // InitialBid
  const [shuffledDeck, setShuffledDeck] = useState([]); // List<Card>
  const [firstBidder, setFirstBidder] = useState(null); // PlayerPos
  const [bidTurnIndex, setBidTurnIndex] = useState(0);
  const [bids, setBids] = useState([]); // List<InitialBid>
  const [bidWinnerPos, setBidWinnerPos] = useState(null);
  const [winningBid, setWinningBid] = useState(null);
  const [lobbySize, setLobbySize] = useState(0);
  const [gameId, setGameId] = useState(null);
  const [currentTrick, setCurrentTrick] = useState([]); // List<Card>
  const [leadSuit, setLeadSuit] = useState(null);
  const [completedTricks, setCompletedTricks] = useState([]); // List<Book>
  const [teamAScore, setTeamAScore] = useState(0);
  const [teamBScore, setTeamBScore] = useState(0);
  const [teamATricksWon, setTeamATricksWon] = useState(0);
  const [teamBTricksWon, setTeamBTricksWon] = useState(0);

  /**
   * Clear game state data for new game
   */
  const clearGameStateContext = () => {
    setKitty([]);
    setTrumpSuit(null);
    setBidType(null);
    setWinningPlayerName(null);
    setHighestBid(null);
    setShuffledDeck([]);
    setBids([]);
    setBidWinnerPos(null);
    setWinningBid(null);
    setCurrentTrick([]);
    setLeadSuit(null)
    setCompletedTricks([]);
    setTeamAScore(0);
    setTeamBScore(0);
    setTeamATricksWon(0);
    setTeamBTricksWon(0);
    setForcedBid(false);
  }


  /**
   * Updates game state from a backend response object.
   * Uses key checks to update only the relevant parts.
   *
   * @param {object} response - JSON response from backend polling
   */
  const updateFromResponse = (response) => {
    if ('players' in response ) {
      setPlayers(response.players);
    }
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
    if ('bidWinnerPos' in response) setBidWinnerPos(response.bidWinnerPos);
    if ('lobbySize' in response) setLobbySize(response.lobbySize);
    if (!gameId && 'gameId' in response && response.gameId) setGameId(response.gameId);
    if ('currentTrick' in response) setCurrentTrick(response.currentTrick);
    if ('completedTricks' in response) setCompletedTricks(response.completedTricks);
    if ('teamAScore' in response) setTeamAScore(response.teamAScore);
    if ('teamBScore' in response) setTeamBScore(response.teamBScore);
    if ('teamATricksWon' in response) setTeamATricksWon(response.teamATricksWon);
    if ('teamBTricksWon' in response) setTeamBTricksWon(response.teamBTricksWon);
  };

  /**
   * Outputs the current game state to the console for debugging.
   */
  const debugLog = () => {
    console.log('[GameStateContext Snapshot]', {
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
      gameId,
    });
  };

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
        bidWinnerPos,
        setBidWinnerPos,
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
        currentTrick,
        setCurrentTrick,
        completedTricks,
        leadSuit,
        setLeadSuit,
        setCompletedTricks,
        teamAScore,
        setTeamAScore,
        teamBScore,
        setTeamBScore,
        teamATricksWon,
        setTeamATricksWon,
        teamBTricksWon,
        setTeamBTricksWon,
        activeGame,
        setActiveGame,
        forcedBid,
        setForcedBid,
        clearGameStateContext,
        updateFromResponse,
        debugLog,
      }}
    >
      {children}
    </GameStateContext.Provider>
  );
};
