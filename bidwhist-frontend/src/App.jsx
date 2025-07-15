// src/App.jsx

import { useEffect, useCallback } from "react";
import "./css/index.css";
import "./css/Card.css";
import "./css/Animations.css";
import ModeSelector from "./components/ModeSelector";
import GameScreen from "./components/GameScreen";
import Scoreboard from "./components/Scoreboard";
import LobbyScreen from "./components/LobbyScreen.jsx";
import { useUIDisplay } from "./context/UIDisplayContext.jsx";
import { useGameState } from "./context/GameStateContext.jsx";
import { usePositionContext } from "./context/PositionContext.jsx";

/*
 * Includes handling game start, updating game state from the backend, and rendering
 * either the Lobby or Game Screen.
 */
function App() {
  const {
    updateFromResponse,
    gameId,
    setGameId,
    setPlayers,
    currentTurnIndex,
    setCurrentTurnIndex,
    phase,
    setPhase,
    setShuffledDeck,
    setFirstBidder,
    setBidTurnIndex,
    mode,
    setMode,
    difficulty,
    lobbySize,
  } = useGameState();

  const {
    playerName,
    setPlayerName,
    viewerPosition,
    setViewerPosition,
    backendPositions,
    setBackendPositions,
  } = usePositionContext();

  const {
    setShowAnimatedCards,
    showGameScreen,
    setShowGameScreen,
    setMyTurn,
    loadGame,
    setLoadGame,
    showLobby,
    setShowLobby,
    queueAnimationFromResponse,
  } = useUIDisplay();

  const API = import.meta.env.VITE_API_URL;

  // --- onStartGame Function ---
  /*
   * Called when the user starts the game. Validates the player's name and
   * sends the game start request to the backend.
   */
  const onStartGame = useCallback(
    async (name, difficulty, code) => {
      const trimmedName = name.trim();
      if (!trimmedName) {
        console.warn("[App] Name is empty after trimming. Aborting start.");
        return;
      }

      setPlayerName(trimmedName); // Sets local name immediately for UI

      try {
        const res = await fetch(`${API}/game/start`, {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({
            playerName: trimmedName,
            difficulty: difficulty,
            gameId: code,
          }),
        });

        const data = await res.json();

        if (!res.ok || !data.players || !data.playerPosition) {
          console.error("[App] Invalid start response:", data);
          return;
        }

        // GameState context setters
        setMode("single");
        setPlayers(data.players);
        setCurrentTurnIndex(data.currentTurnIndex);
        setPhase(data.phase);
        setShuffledDeck(data.shuffledDeck);
        setFirstBidder(data.firstBidder);
        setBidTurnIndex(data.bidTurnIndex);

        // Position context
        setViewerPosition(data.playerPosition);
        setPlayerName(data.viewerName); // From backend, possibly cleaned/formatted
        const positionNameMap = Object.fromEntries(
          data.players.map((p) => [p.position, p.name])
        );
        setBackendPositions(positionNameMap);

        // UI Display (delayed to allow context propagation)
        setTimeout(() => {
          setShowGameScreen(true);
          setShowAnimatedCards(true);
        }, 50);
      } catch (err) {
        console.error("[App] Error starting game:", err);
      }
    },
    [
      setPlayerName,
      setMode,
      setPlayers,
      setCurrentTurnIndex,
      setPhase,
      setShuffledDeck,
      setFirstBidder,
      setBidTurnIndex,
      setViewerPosition,
      setBackendPositions,
      setShowGameScreen,
      setShowAnimatedCards,
      difficulty,
    ]
  );

  // --- useEffect for URL Parameters ---
  /*
   * Extracts gameId and playerPosition from URL parameters and updates the context if they
   * are found.
   */
  useEffect(() => {
    const params = new URLSearchParams(window.location.search);
    const gameIdFromUrl = params.get("gameId");
    const posFromUrl = params.get("playerPosition");

    console.log("[URL Sync] Extracted from URL:", {
      gameIdFromUrl,
      posFromUrl,
    });

    if (gameIdFromUrl) setGameId(gameIdFromUrl);
    console.log("[URL Sync] setGameId:", gameIdFromUrl);

    if (posFromUrl) setViewerPosition(posFromUrl);
    console.log("[URL Sync] setViewerPosition:", posFromUrl);
  }, []);

  // --- useEffect to Sync URL with GameState ---
  // Updates the URL with the latest gameId and playerPosition if they change.
  useEffect(() => {
    if (!gameId || !viewerPosition) return;

    const currentUrl = new URL(window.location.href);
    const urlGameId = currentUrl.searchParams.get("gameId");
    const urlPos = currentUrl.searchParams.get("playerPosition");

    if (urlGameId !== gameId || urlPos !== viewerPosition) {
      window.history.pushState(
        {},
        "",
        `/app?gameId=${gameId}&playerPosition=${viewerPosition}`
      );
      console.log("🌐 URL updated with gameId and playerPosition");
    }
  }, [gameId, viewerPosition]);

  // --- Polling for Game State ---
  // Starts a polling loop that fetches the game state from the server every 2 seconds.
  useEffect(() => {
    if (!playerName || !gameId) return;

    const interval = setInterval(async () => {
      try {
        const res = await fetch(`${API}/game/state`, {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify({
            player: viewerPosition,
            gameId: gameId,
          }),
        });

        const data = await res.json();
        const positions = Object.keys(backendPositions); // ['P1', 'P2', 'P3', 'P4']
        const viewerIndex = positions.indexOf(viewerPosition);

        updateFromResponse(data);
        queueAnimationFromResponse(data);
        setMyTurn(currentTurnIndex === viewerIndex && phase === "PLAY");
      } catch (err) {
        console.error("Polling failed:", err);
      }
    }, 2000); // Every 2 seconds

    return () => clearInterval(interval);
  }, [viewerPosition, currentTurnIndex, phase, gameId]);

  // --- useEffect for Mode, Lobby, and Game State Updates ---
  // Controls the visibility of different screens based on game mode and lobby size.
  useEffect(() => {
    if (mode === "multiplayer") {
      setShowLobby(lobbySize > 0 && lobbySize < 4);
      setShowGameScreen(lobbySize == 4);
    }

    const isSinglePlayer = mode === "single";
    const validPositions = ["P1", "P2", "P3", "P4"];

    const singlePlayerReady =
      showGameScreen &&
      validPositions.includes(viewerPosition) &&
      isSinglePlayer;
    const multiPlayerReady =
      showGameScreen &&
      validPositions.includes(viewerPosition) &&
      !isSinglePlayer;

    setLoadGame(singlePlayerReady || multiPlayerReady);
  }, [mode, lobbySize, showGameScreen, viewerPosition]);

  // --- Return JSX for App Rendering ---
  /*
   * Renders the appropriate screen (ModeSelector, LobbyScreen, or GameScreen) based
   * on the game state. */
  return (
    <div className="index-wrapper">
      <div className="scoreboard-container">
        <Scoreboard />
      </div>
      <div className="index-container">
        {loadGame ? (
          <GameScreen />
        ) : showLobby ? (
          <LobbyScreen gameId={gameId} playerName={playerName} />
        ) : (
          <ModeSelector
            playerName={playerName}
            setPlayerName={setPlayerName}
            onStartGame={onStartGame}
          />
        )}
      </div>
    </div>
  );
}

export default App;
