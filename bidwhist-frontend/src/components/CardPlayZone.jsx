// src/components/CardPlayZone.js
import React from 'react';
import { useState, useRef, useEffect } from 'react';
import { useZoneRefs } from '../context/RefContext';
import ShuffleAnimation from '../animations/ShuffleAnimation';
import AnimatedCard from './AnimatedCard';
import { getPositionMap } from '../utils/PositionUtils';
import BiddingPanel from './BiddingPanel';
import { delay } from '../utils/TimeUtils';
import { dealCardsClockwise } from '../animations/DealAnimation';
import '../css/CardPlayZone.css';
import { useUIDisplay } from '../context/UIDisplayContext';
import { useGameState } from '../context/GameStateContext';
import { usePositionContext } from '../context/PositionContext';

export default function CardPlayZone(onCardPlayed) {
    const {
        setShowHands,
        showBidding,
        setShowBidding,
        deckPosition,
        setDeckPosition,
        animatedCards,
        showAnimatedCards,
        setShowAnimatedCards,
        setAnimatedCards,
    } = useUIDisplay();
    const {
        playerName,
        viewerPosition,
        positionToDirection,
    } = usePositionContext;
    const {
        gameState,
        shuffledDeck,
        setShuffledDeck,
        setPhase,
    } = useGameState();

    const [isOver, setIsOver] = useState(false);
    const [playedCard, setPlayedCard] = useState(null);
    const [hasRunIntro, setHasRunIntro] = useState(false);
    const [newRound, setNewRound] = useState(false);
    const [bidPhase, setBidPhase] = useState(false);

    console.log(`[CardPlayZone] Viewer position: ${viewerPosition}`);

    const localRef = useRef();
    const { register, get } = useZoneRefs();

    const allRefsReady = ['south', 'west', 'north', 'east'].every(dir =>
        get(dir)?.current
    );

    // 1. Shuffle and intro sequence
    useEffect(() => {
        if (!playerName || !allRefsReady || hasRunIntro) return;
        if (gameState?.phase !== 'START' && gameState?.phase !== 'NEXT_ROUND') return;

        const runIntroSequence = async () => {
            setHasRunIntro(true); // ✅ prevent reruns

            try {
                // ⬇️ Shuffle
                const shuffleRes = await fetch('/api/game/shuffle', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ playerPosition: viewerPosition })
                });

                const shuffleData = await shuffleRes.json();

                if (!shuffleRes.ok || shuffleData.phase !== 'SHUFFLE') {
                    console.error("❌ Shuffle failed or did not update phase:", shuffleData);
                    return;
                }

                console.log('[CardPlayZone] Shuffle started successfully:', shuffleData);
                setShuffledDeck(shuffleData.shuffledDeck);
                setBidPhase(shuffleData.phase);
                console.log('Gamestate Updated - Shuffle');

                setShuffledDeck(shuffleData.shuffledDeck);

                console.log(`Current center: ${deckPosition.x}, ${deckPosition.y}`);

                setShowShuffle(true);
                await delay(1500);
                setNewRound(true); // ⬅️ Triggers next useEffect

            } catch (err) {
                console.error("🔥 Error during intro sequence:", err);
            }
        };

        runIntroSequence();
    }, [playerName, allRefsReady, hasRunIntro, gameState, deckPosition]);


    useEffect(() => {
        if (!newRound) return;

        const runDealSequence = async () => {
            try {
                const dealRes = await fetch('/api/game/deal', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ player: viewerPosition })
                });
                const dealData = await dealRes.json();

                setNewRound(false);

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

                setGameState(dealData); // ✅ PlayerZone updated

                // Start card animations
                setTimeout(() => {
                    dealCardsClockwise(
                        players,
                        cards,
                        deckPosition,
                        positionMap,
                        setAnimatedCards,
                        showAnimatedCards,
                        setShowAnimatedCards,
                        () => {
                            console.log("🎉 Deal animation complete");
                        },
                        get
                    );
                }, 50);

                // Wait 8 seconds before pre-bid phase (or use animation duration)
                await delay(8000);
                setShowHands(true);

                // 👇 Now initiate pre-bid
                const preBidRes = await fetch('/api/game/pre-bid', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ player: viewerPosition }) // viewerPosition = "P1", "P2", etc.
                });
                const preBidData = await preBidRes.json();

                if (!preBidRes.ok) {
                    console.error("❌ Pre-bid failed:", preBidData);
                    return;
                }

                // ✅ Only update phase without touching playerPosition
                setPhase(preBidData.phase);
                setBidPhase(true);

            } catch (err) {
                console.error("🔥 Error during deal or pre-bid:", err);
            }
        };

        runDealSequence();

    }, [newRound, backendPositions, viewerPosition, deckPosition]);


    useEffect(() => {
        if (!bidPhase) return;

        const turnPlayerPos = ['P1', 'P2', 'P3', 'P4'][gameState?.currentTurnIndex];

        if (viewerPosition === turnPlayerPos) {
            setShowBidding(true);
        }


    });


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
        if (!viewerPosition || !positionToDirection) return;

        const direction = positionToDirection[viewerPosition];
        if (!direction) return;

        console.log(`Checking CardPlayZone: ${direction}`);
        register(`CardPlayZone-${direction}`, localRef);
    }, [viewerPosition, positionToDirection, register]);


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
                        onComplete={() =>
                            setShowShuffle(false)}
                    />
                )}

                {showBidding && (
                    <BiddingPanel closeBidding={() => setShowBidding(false)} />
                )}

                {playedCard && (
                    <img
                        src={`/static/img/deck/${playedCard}`}
                        alt="Played card"
                        className="card-img"
                    />
                )}
                {showAnimatedCards && (
                    <div>
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
                )}
            </div>
        </div>
    );
}
