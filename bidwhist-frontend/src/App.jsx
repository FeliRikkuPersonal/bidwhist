// src/App.jsx
import { useState, useEffect } from 'react';
import './css/index.css';
import './css/Card.css';
import './css/Animations.css';
import ModeSelector from "./components/ModeSelector";
import GameScreen from './components/GameScreen';
import Scoreboard from './components/Scoreboard';
import AnimatedCard from './components/AnimatedCard';
import ShuffleAnimation from './animations/ShuffleAnimation';
import { dealCardsClockwise } from './animations/DealAnimation';
import { getPositionMap } from './utils/PositionUtils';
import { getCardImage } from './utils/CardUtils';

function App() {
  const [playerName, setPlayerName] = useState('');
  const [gameState, setGameState] = useState(null);
  const [animatedCards, setAnimatedCards] = useState([]);
  const [showShuffle, setShowShuffle] = useState(false);
  const [shuffledDeck, setShuffledDeck] = useState([]);
  const [showStackedDeck, setShowStackedDeck] = useState(true);
  const [deckPosition, setDeckPosition] = useState({ x: 0, y: 0 });

  const positionMap = gameState?.players ? getPositionMap(gameState.players, playerName) : {};
  const myPosition = positionMap[playerName] || 'south';

  const onStartGame = (name) => {
    const trimmedName = name.trim();
    if (!trimmedName) {
      console.warn("[App] Name is empty after trimming. Aborting start.");
      return;
    }

    console.log("[App] Starting game for player:", trimmedName);
    setPlayerName(trimmedName);

    fetch('/api/game/start', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ playerName: trimmedName })
    })
      .then(res => res.json())
      .then(data => {
        console.log('[App] Game started successfully:', data);

        if (data.shuffledDeck) {
          setShuffledDeck(data.shuffledDeck);
          runIntroSequence(data.shuffledDeck, trimmedName);  // 👈 hand off to animation runner
        }
      })
      .catch(err => {
        console.error('[App] Error starting game:', err);
      });
  };

  const runIntroSequence = async (deck, playerName) => {
    setShowShuffle(true);

    await delay(1000);
    setShowShuffle(false);
    setShowStackedDeck(true);

    await delay(300);

    const dealData = await fetch('/api/game/deal', { method: 'POST' }).then(r => r.json());
    setGameState(dealData);

    const players = dealData.players.map(p => p.position);
    const cards = dealData.players.flatMap(p =>
      p.hand.map(card => ({ ...card, owner: p.position }))
    );
    const positionMap = getPositionMap(players, playerName);

    dealCardsClockwise(players, cards, deckPosition, positionMap, setAnimatedCards, () => {
      setShowStackedDeck(false);
      console.log("🎉 Deal animation complete");
    });
  };


  useEffect(() => {
    function updatePosition() {
      const x = window.innerWidth / 2;
      const y = window.innerHeight / 2;
      setDeckPosition({ x, y });
    }

    updatePosition(); // Initial call
    window.addEventListener('resize', updatePosition);
    return () => window.removeEventListener('resize', updatePosition);
  }, []);

  useEffect(() => {
    const onResize = () => {
      const dropZone = getRef('CardPlayZone-south')?.current;
      const bounds = dropZone?.getBoundingClientRect();
      console.log('Resize → new bounds:', bounds);
    };

    window.addEventListener('resize', onResize);
    return () => window.removeEventListener('resize', onResize);
  }, []);

  function delay(ms) {
    return new Promise(resolve => setTimeout(resolve, ms));
  }

  return (
    <div className="index-wrapper">
      <div className="scoreboard-container">
        <Scoreboard gameState={gameState} />
      </div>
      <div className="index-container">
        {gameState ? (
          <GameScreen gameState={gameState} />
        ) : (
          <ModeSelector
            playerName={playerName}
            setPlayerName={setPlayerName}
            onStartGame={onStartGame}
          />
        )}
      </div>
      <div className="floating-card-layer">

        {showShuffle && (
          <ShuffleAnimation
            cards={shuffledDeck}
            viewerName={playerName}
            deckPosition={deckPosition}
            onComplete={() => {
              setShowShuffle(false);
              setShowStackedDeck(true);

            }}
          />
        )}
        {showStackedDeck && shuffledDeck.map((card, i) => (
          <img
            key={i}
            src={getCardImage(card, playerName, 'SHUFFLING')}
            alt="Card back"
            className="card-img stacked-card"
            style={{
              position: 'absolute',
              top: deckPosition.y,
              left: deckPosition.x,
              transform: `translate(-50%, -50%)`,
              zIndex: i,
            }}
          />
        ))}

        {/* Future: add animated transitions here */}
        {animatedCards.map(card => (
          <AnimatedCard
            key={card.id}
            card={card}
            from={card.from}
            to={card.to}
            viewerName={playerName}
            contextPhase={gamePhase} // e.g., 'SHUFFLING', 'KITTY_ASSIGNMENT', 'PLAYING'
          />
        ))}


      </div>
    </div>

  );
}


export default App;