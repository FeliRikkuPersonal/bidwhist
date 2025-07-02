// src/components/CardPlayZone.js
import React from 'react';
import { useState, useRef, useEffect } from 'react';
import { useZoneRefs } from '../context/RefContext';
import ShuffleAnimation from '../animations/ShuffleAnimation';
import AnimatedCard from './AnimatedCard';
import { getPositionMap } from '../utils/PositionUtils';
import { StackedDeck } from './StackedDeck';
import { delay } from '../utils/TimeUtils';
import { dealCardsClockwise } from '../animations/DealAnimation';
import { usePlayerContext } from '../context/PlayerContext';
import '../css/CardPlayZone.css';

export default function CardPlayZone({
    playerName,
    showStackedDeck,
    setShowStackedDeck,
    setGameState,
    gameState,
    backendPositions,
    onCardPlayed,
}) {
    const [isOver, setIsOver] = useState(false);
    const [playedCard, setPlayedCard] = useState(null);
    const [deckPosition, setDeckPosition] = useState({ x: 0, y: 0 });
    const [animatedCards, setAnimatedCards] = useState([]);
    const [shuffledDeck, setShuffledDeck] = useState([]);
    const [showShuffle, setShowShuffle] = useState(false);
    const [hasRunIntro, setHasRunIntro] = useState(false);

    const { playerProps, positionToDirection, viewerPosition } = usePlayerContext();

    const localRef = useRef();
    const { register, get } = useZoneRefs();

    const allRefsReady = ['south', 'west', 'north', 'east'].every(dir =>
        get(dir)?.current
    );

    useEffect(() => {
        if (!playerName || !allRefsReady || hasRunIntro) return;
        if (gameState?.phase !== 'START') return;

        const runIntroSequence = async () => {
            setHasRunIntro(true); // ✅ prevent reruns

            try {
                // ⬇️ Shuffle
                const shuffleRes = await fetch('/api/game/shuffle', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ playerName })
                });

                const shuffleData = await shuffleRes.json();

                if (!shuffleRes.ok || shuffleData.phase !== 'SHUFFLE') {
                    console.error("❌ Shuffle failed or did not update phase:", shuffleData);
                    return;
                }

                console.log('[CardPlayZone] Shuffle started successfully:', shuffleData);
                setGameState(shuffleData);
                setShuffledDeck(shuffleData.shuffledDeck);

                console.log(`Current center: ${deckPosition.x}, ${deckPosition.y}`)

                // ⬇️ Animate shuffle → stacked deck
                setShowShuffle(true);
                await delay(1000);

                await delay(1000);

                // ⬇️ Deal
                const dealRes = await fetch('/api/game/deal', { method: 'POST' });
                const dealData = await dealRes.json();

                if (!dealRes.ok || !dealData.players) {
                    console.error("❌ Invalid dealData:", dealData);
                    return;
                }

                const cards = dealData.players.flatMap(p =>
                    (p.hand || []).map(card => ({ ...card, owner: p.position }))
                );
                console.log("Dealt cards to animate:", cards);
                const players = dealData.players.map(p => p.position);
                const positionMap = getPositionMap(backendPositions, viewerPosition);

                setGameState(dealData); // ✅ So PlayerZone has correct hands

                // ⬇️ Give React a tick to rerender
                setTimeout(() => {
                    dealCardsClockwise(
                        players,
                        cards,
                        deckPosition,
                        positionMap,
                        setAnimatedCards,
                        () => {
                            setShowStackedDeck(false);
                            console.log("🎉 Deal animation complete");
                        },
                        get
                    );
                }, 50);
            } catch (err) {
                console.error("🔥 Error during intro sequence:", err);
            }
        };

        runIntroSequence();
    }, [playerName, allRefsReady, hasRunIntro, gameState, backendPositions, viewerPosition, deckPosition]);

    const updatePositions = () => {
        const bounds = localRef.current?.getBoundingClientRect();
        const parentBounds = document.querySelector('.floating-card-layer')?.getBoundingClientRect();

        if (bounds && parentBounds) {
            const centerX = bounds.left + bounds.width / 2 - parentBounds.left;
            const centerY = bounds.top + bounds.height / 2 - parentBounds.top;
            setDeckPosition({ x: centerX, y: centerY });
            console.log('🃏 deckPosition relative to .floating-card-layer:', { x: centerX, y: centerY });

            // Optional quadrant debug
            const quadrantPositions = {
                north: { x: centerX, y: bounds.top + bounds.height * 0.25 - parentBounds.top },
                south: { x: centerX, y: bounds.top + bounds.height * 0.75 - parentBounds.top },
                west: { x: bounds.left + bounds.width * 0.25 - parentBounds.left, y: centerY },
                east: { x: bounds.left + bounds.width * 0.75 - parentBounds.left, y: centerY },
            };
            console.log("🧭 Calculated quadrant target centers:", quadrantPositions);
        }
    };

    useEffect(() => {
        updatePositions();
        window.addEventListener('resize', updatePositions);
        return () => window.removeEventListener('resize', updatePositions);
    }, []);

    useEffect(() => {
        if (showShuffle) {
            updatePositions();
        }
    }, [showShuffle]);

    useEffect(() => {
        register(`CardPlayZone-${positionToDirection[viewerPosition]}`, localRef);
    }, [positionToDirection[viewerPosition], register]);

    const handleDrop = (e) => {
        e.preventDefault();
        const fullPath = e.dataTransfer.getData('text/plain');
        const cardName = fullPath.split('/').pop();
        console.log("Parsed card name:", cardName); // ✅

        if (positionToDirection[viewerPosition] === 'south') {
            setPlayedCard(cardName);
            onCardPlayed?.(cardName);
        }

        setIsOver(false);
    };


    if (positionToDirection[viewerPosition] !== 'south') return null;

    return (
        <div ref={localRef} className="card-play-zone">
            <div
                className={`drop-zone ${positionToDirection[viewerPosition]} ${isOver ? 'highlight' : ''}`}
                onDragOver={(e) => e.preventDefault()}
                onDragEnter={() => positionToDirection[viewerPosition] === 'south' && setIsOver(true)}
                onDragLeave={() => positionToDirection[viewerPosition] === 'south' && setIsOver(false)}
                onDrop={positionToDirection[viewerPosition] === 'south' ? handleDrop : undefined}
            >
            </div>

            {/* ✅ Floating card animation layer */}
            <div className="floating-card-layer">
                {showShuffle && (
                    <ShuffleAnimation
                        cards={shuffledDeck}
                        viewerName={playerName}
                        deckPosition={deckPosition}
                        onComplete={() => setShowShuffle(false)}
                    />
                )}

                {showStackedDeck && (
                    <StackedDeck
                        cards={shuffledDeck}
                        viewerName={playerName}
                        deckPosition={deckPosition}
                    />
                )}

                {playedCard && (
                    <img
                        src={`/static/img/deck/${playedCard}`}
                        alt="Played card"
                        className="card-img"
                    />
                )}
                {animatedCards.map((card, i) => (
                    <AnimatedCard
                        key={card.id}
                        card={card}
                        from={card.from}
                        to={card.to}
                        viewerName={playerName}
                        contextPhase={gameState?.phase}
                        zIndex={i}
                    />
                ))}
            </div>
        </div>
    );
}
