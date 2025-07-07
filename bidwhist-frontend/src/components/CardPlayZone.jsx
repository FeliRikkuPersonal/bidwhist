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
import { useUIDisplay } from '../context/UIDisplayContext.jsx';
import { useGameState } from '../context/GameStateContext.jsx';
import { usePositionContext } from '../context/PositionContext.jsx';
import BidTypePanel from './BidTypePanel.jsx';

export default function CardPlayZone({ dropZoneRef, onCardPlayed }) {
    const {
        debugLog: logUI,
        setShowHands,
        showGameScreen,
        setShowBidding,
        deckPosition,
        setDeckPosition,
        playedCard,
        setPlayedCard,
        playedCardPosition,
        setPlayedCardPosition,
        animatedCards,
        setAnimatedCards,
        showShuffle,
        setShowShuffle,
        showAnimatedCards,
        setShowAnimatedCards,
        setBidPhase,
        setShowFinalizeBid,
    } = useUIDisplay();
    const {
        debugLog: logPosition,
        playerName,
        viewerPosition,
        backendPositions,
        positionToDirection,
    } = usePositionContext();
    const {
        gameId,
        debugLog: logGameState,
        players,
        bids,
        updateFromResponse,
        highestBid,
        shuffledDeck,
        setShuffledDeck,
        setPhase,
        currentTurnIndex,
        winningPlayerName,
    } = useGameState();

    const [isOver, setIsOver] = useState(false);
    const [hasRunIntro, setHasRunIntro] = useState(false);
    const [newRound, setNewRound] = useState(false);
    const [dropPos, setDropPos] = useState(null);

    const localRef = useRef();
    const { register, get, debugLog: zoneRefLog } = useZoneRefs();

    const allRefsReady = ['south', 'west', 'north', 'east'].every(dir =>
        get(dir)?.current
    );

    // 1. Shuffle and intro sequence
    useEffect(() => {
        if (!playerName || hasRunIntro || !viewerPosition || !showGameScreen) return;
        if (!allRefsReady) return;

        // If we already have a shuffled deck, and we haven't animated it yet
        if (shuffledDeck && shuffledDeck.length > 0) {
            setHasRunIntro(true); // prevent rerun

            const runIntroAnimation = async () => {
                setShowShuffle(true);
                await delay(1500);
                setNewRound(true); // trigger next phase logic
            };

            runIntroAnimation();
        }
    }, [shuffledDeck, playerName, viewerPosition, showGameScreen, allRefsReady, hasRunIntro]);


    useEffect(() => {
        if (!newRound || !players || players.length === 0) return;

        // Check if all players have hands dealt (from polled backend state)
        const hasHands = players.every(p => p.hand && p.hand.length > 0);
        if (!hasHands) return;

        const runDealAnimation = async () => {
            setNewRound(false);

            // Flatten all cards with owners
            const cards = players.flatMap(p =>
                (p.hand || []).map(card => ({ ...card, owner: p.position }))
            );
            console.log("🃏 Dealing cards:", cards);

            const playerPositions = players.map(p => p.position);
            const positionMap = getPositionMap(backendPositions, viewerPosition);

            // Animate clockwise deal
            setTimeout(() => {
                dealCardsClockwise(
                    playerPositions,
                    cards,
                    positionMap,
                    () => console.log("🎉 Deal animation complete"),
                    get,
                    deckPosition,
                    setAnimatedCards,
                    setShowAnimatedCards,
                    setBidPhase
                );
            }, 50);

            // Final UI adjustments
            await delay(7000);
            setShowHands(true);
            await delay(10000);
            logUI();
        };

        runDealAnimation();

    }, [newRound, players, viewerPosition, backendPositions, deckPosition]);

    const updatePositions = () => {
        const bounds = localRef.current?.getBoundingClientRect();
        const parentBounds = document.querySelector('.floating-card-layer')?.getBoundingClientRect();

        if (bounds && parentBounds) {
            const centerX = bounds.left + bounds.width / 2 - parentBounds.left;
            const centerY = bounds.top + bounds.height / 2 - parentBounds.top;
            setDeckPosition({ x: centerX, y: centerY });

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

    useEffect(() => {
        const bidsComplete = (bids?.length === 4);
        const iWonBid = (winningPlayerName === playerName);

        setShowFinalizeBid(bidsComplete && iWonBid);

        logUI();
        logPosition();
        logGameState();
    }, [bids, winningPlayerName, playerName])


    const handleDrop = async (e) => {
        e.preventDefault();

        const rawData = e.dataTransfer.getData('application/json');
        if (!rawData) {
            console.warn("No drag data found");
            return;
        }

        const card = JSON.parse(rawData);
        console.log("Dropped card:", card);

        // Only continue if actually over drop zone
        if (!isOver) {
            console.log("Drop ignored: not over drop-zone");
            return;
        }

        // Backend call to submit card play
        try {
            const res = await fetch('/api/game/play', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    gameId: gameId,
                    player: viewerPosition,
                    card: card
                })
            });

            if (!res.ok) {
                console.error("Backend rejected card play");
                return;
            }
        } catch (err) {
            console.error("Failed to send playCard:", err);
            return;
        }

        // Track for visual rendering
        if (positionToDirection[viewerPosition] === 'south') {
            setPlayedCard(card); // store full card object
            onCardPlayed?.(card);
        }

        if (dropZoneRef.current && localRef.current) {
            const rect = dropZoneRef.current.getBoundingClientRect();
            const parentRect = localRef.current.getBoundingClientRect();

            setPlayedCardPosition({
                x: rect.left - parentRect.left,
                y: rect.top - parentRect.top,
            });
        }

        setIsOver(false);
    };



    if (positionToDirection[viewerPosition] !== 'south') return null;

    return (
        <div ref={localRef} className="card-play-zone">
            <div
                ref={dropZoneRef}
                className={`drop-zone south ${isOver ? 'highlight' : ''}`}
                onDragOver={(e) => e.preventDefault()}
                onDragEnter={() => setIsOver(true)}
                onDragLeave={() => setIsOver(false)}
                onDrop={handleDrop}
            >
            </div>

            {/* ✅ Floating card animation layer */}
            <div className="floating-card-layer">
                {showShuffle && (
                    <ShuffleAnimation
                        cards={shuffledDeck}
                        onComplete={() =>
                            setShowShuffle(false)}
                    />
                )}
                <BiddingPanel closeBidding={() => setShowBidding(false)} />
                <BidTypePanel closeBidTypePanel={() => setShowFinalizeBid(false)} />
                {playedCard && playedCardPosition && (
                    <img
                        src={`/static/img/deck/${playedCard}`}
                        alt="Played card"
                        className="card-img"
                        style={{
                            position: 'absolute',
                            left: playedCardPosition.x,
                            top: playedCardPosition.y,
                            zIndex: 11, // above drop zone
                            transition: 'transform 0.2s ease'
                        }}
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
                                zIndex={i}
                            />
                        ))}
                    </div>
                )}
            </div>
        </div>
    );
}
