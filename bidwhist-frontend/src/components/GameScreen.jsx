// src/components/GameScreen.jsx
import React, { useEffect, useState } from 'react';
import '../css/GameScreen.css';
import '../css/Card.css';
import '../css/PlayerZone.css';
import AnimatedCard from './AnimatedCard';
import { getCardImage } from '../utils/CardUtils';
import { StackedDeck } from '../components/StackedDeck';
import { getPositionMap } from '../utils/PositionUtils';
import ShuffleAnimation from '../animations/ShuffleAnimation';
import { dealCardsClockwise } from '../animations/DealAnimation';
import { useZoneRefs } from '../context/RefContext';
import { delay } from '../utils/TimeUtils';
import CardPlayZone from './CardPlayZone';
import PlayerZone from './PlayerZone';
import BidModal from './BidModal';
import { useState } from 'react';
import axios from 'axios';

export default function GameScreen({
    gameState,
    playerName,
    shuffledDeck,
    setGameState,
    showStackedDeck,
    setShowStackedDeck,
    setAnimatedCards,
    showShuffle,
    setShowShuffle,
    animatedCards,
    deckPosition,
    setDeckPosition
}) {



    const { get } = useZoneRefs();
    const [showBidModal, setShowBidModal] = useState(false);


    const backendPositions = gameState?.players.map(p => p.position);
    const viewerPosition = gameState?.playerPosition;

    console.log("Backend positions:", backendPositions);
    console.log("Viewer position:", viewerPosition);

    const positionToDirection = getPositionMap(backendPositions, viewerPosition);
    const allRefsReady = ['south', 'west', 'north', 'east'].every(dir =>
        get(dir)?.current
    );


    // ⏳ Run intro sequence after screen + refs are ready
    useEffect(() => {
        let dealData

        const runIntroSequence = async () => {
            if (!shuffledDeck || !playerName || !allRefsReady) {
                console.log("▶️ runIntroSequence start");
                console.log("shuffledDeck:", !!shuffledDeck);
                console.log("playerName:", playerName);
                console.log("allRefsReady:", allRefsReady);
                return;
            }

            setShowShuffle(true);
            await delay(1000);

            setShowStackedDeck(true);
            await delay(1000);

            const dealData = await fetch('/api/game/deal', { method: 'POST' }).then(r => r.json());


            await delay(300);

            const cards = dealData.players.flatMap(p =>
                (p.hand || []).map(card => ({ ...card, owner: p.position }))
            );

            const players = dealData.players.map(p => p.position);
            const positionMap = getPositionMap(backendPositions, gameState?.playerPosition);

            dealCardsClockwise(
                players,
                cards,
                { x: window.innerWidth / 2, y: window.innerHeight / 2 },
                positionMap,
                setAnimatedCards,
                () => {
                    setShowStackedDeck(false);
                    console.log("🎉 Deal animation complete");
                    setGameState(dealData);
                },
                get
            );
        };

        runIntroSequence();
    }, [shuffledDeck, playerName, allRefsReady]);

    useEffect(() => {
        const updateDeckPos = () => {
            const x = document.documentElement.clientWidth / 2;
            const y = document.documentElement.clientHeight / 2;
            setDeckPosition({ x, y });
            console.log('🃏 Centered deckPosition:', { x, y });
        };

        updateDeckPos(); // Initial
        window.addEventListener('resize', updateDeckPos);
        return () => window.removeEventListener('resize', updateDeckPos);
    }, []);

    useEffect(() => {
        const onResize = () => {
            const dropZone = get('CardPlayZone-south')?.current;
            const bounds = dropZone?.getBoundingClientRect();
            console.log('Resize → new bounds:', bounds);
        };

        window.addEventListener('resize', onResize);
        return () => window.removeEventListener('resize', onResize);
    }, []);

    // When phase becomes "Bid", open the modal:
    useEffect(() => {
        if (gameState?.phase === 'BID' && gameState.playerPosition === currentPlayerTurn) {
            setShowBidModal(true);
        }
    }, [gameState]);

    const handleBidSubmit = async ({ bid, no }) => {
        try {
            const response = await axios.post('/bid', {
                player: gameState.playerPosition,
                bid,
                noTrump: no,
            });
            console.log("Bid submitted:", response.data);
        } catch (err) {
            console.error("Bid failed", err);
        }
    };

    const getPlayerProps = (direction) => {
        if (!gameState || !positionToDirection) return { direction, name: "", cards: [], showHand: false };

        const entry = Object.entries(positionToDirection).find(
            ([_, dir]) => dir === direction
        );

        if (!entry) return { direction, name: "", cards: [], showHand: false };

        const [position] = entry;
        const player = gameState.players.find(p => p.position === position);

        const isViewer = position === gameState.playerPosition;
        const isBiddingOrBeyond = ["BID", "PLAY", "KITTY", "RESULT"].includes(gameState.phase);

        return {
            key: position,
            props: {
                direction,
                position,
                name: player?.name || direction.toUpperCase(),
                cards: player?.hand || [],
                showHand: isViewer && isBiddingOrBeyond,
            }
        };
    };

    const northPlayer = getPlayerProps("north");
    const westPlayer = getPlayerProps("west");
    const eastPlayer = getPlayerProps("east");
    const southPlayer = getPlayerProps("south");

    console.log("🧩 ZONE PROPS");
    console.log("NORTH:", northPlayer);
    console.log("WEST:", westPlayer);
    console.log("EAST:", eastPlayer);
    console.log("SOUTH:", southPlayer);

    return (
        <div className="game-screen-container">
            <div className="game-grid">
                {/* Top row */}
                <div className="grid-item top-left">
                    <div className="placeholder-zone">Other Team Tricks</div>
                </div>

                <div className="grid-item top-center">
                    <PlayerZone {...northPlayer.props} />
                </div>
                <div className="grid-item top-right">
                    <div className="placeholder-zone"> empty </div>
                </div>

                {/* Middle row */}
                <div className="grid-item middle-left">
                    <PlayerZone {...westPlayer.props} />
                </div>

                <div className="grid-item center">
                    <CardPlayZone
                        playerPosition="south"
                        onCardPlayed={(card) => console.log("Played:", card)}
                    />
                </div>

                <div className="grid-item middle-right">
                    <PlayerZone {...eastPlayer.props} />
                </div>

                {/* Bottom row */}
                <div className="grid-item bottom-left">
                    <div className="placeholder-zone">Kitty & Discard</div>
                </div>

                <div className="grid-item bottom-center">
                    <PlayerZone {...southPlayer.props} />
                </div>

                <div className="grid-item bottom-right">
                    <div className="placeholder-zone">Your Tricks</div>
                </div>
            </div>
            <div className="floating-card-layer">

                {showShuffle && (
                    <ShuffleAnimation
                        cards={shuffledDeck}
                        viewerName={playerName}
                        deckPosition={deckPosition}
                        onComplete={() => {
                            setShowShuffle(false);
                        }}
                    />
                )}
                {showStackedDeck &&
                    <StackedDeck
                        cards={shuffledDeck}
                        deckPosition={deckPosition}
                        viewerName={playerName}
                    />}
                {showBidModal && (
                    <BidModal
                        onSubmit={handleBidSubmit}
                        onClose={() => setShowBidModal(false)}
                    />
                )}


                {/* Future: add animated transitions here */}
                {animatedCards.map((card, i) => (
                    <AnimatedCard
                        key={card.id}
                        card={card}
                        from={card.from}
                        to={card.to}
                        viewerName={playerName}
                        contextPhase={gameState?.phase} // e.g., 'SHUFFLING', 'KITTY_ASSIGNMENT', 'PLAYING'
                        zIndex={i}
                    />
                ))}
            </div>
        </div>
    );
};

