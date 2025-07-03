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
import { PlayerContext } from '../context/PlayerContext';
import { useZoneRefs } from '../context/RefContext';
import { delay } from '../utils/TimeUtils';
import CardPlayZone from './CardPlayZone';
import PlayerZone from './PlayerZone';

export default function GameScreen({
    gameState,
    playerName,
    viewerPosition,
    shuffledDeck,
    setGameState,
    showStackedDeck,
    setShowStackedDeck,
    setAnimatedCards,
    showShuffle,
    setShowShuffle,
    animatedCards,
    deckPosition,
    setDeckPosition,
    showAnimatedCards,
    setShowAnimatedCards,
}) {



    const { get } = useZoneRefs();
    const backendPositions = gameState?.players.map(p => p.position);

    console.log("gameState.playerPosition:", gameState?.playerPosition);
    console.log("backendPositions:", backendPositions);


    const positionToDirection = backendPositions && viewerPosition
        ? getPositionMap(backendPositions, viewerPosition)
        : null;

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



    return (

            <div className="game-screen-container">
                <div className="game-grid">
                    {/* Top row */}
                    <div className="grid-item top-left">
                        <div className="placeholder-zone">Other Team Tricks</div>
                    </div>

                    <div className="grid-item top-center">
                        <PlayerZone {...playerProps.north.props} />
                    </div>
                    <div className="grid-item top-right">
                        <div className="placeholder-zone"> empty </div>
                    </div>

                    {/* Middle row */}
                    <div className="grid-item middle-left">
                        <PlayerZone {...playerProps.west.props} />
                    </div>

                    <div className="grid-item center">
                        <CardPlayZone
                            playerName={playerName}
                            showStackedDeck={showStackedDeck}
                            setShowStackedDeck={setShowStackedDeck}
                            setGameState={setGameState}
                            gameState={gameState}
                            backendPositions={backendPositions}
                            onCardPlayed={(card) => console.log("Played:", card)}
                            showAnimatedCards={showAnimatedCards}
                            setShowAnimatedCards={setShowAnimatedCards}
                        />
                    </div>

                    <div className="grid-item middle-right">
                        <PlayerZone {...playerProps.east.props} />
                    </div>

                    {/* Bottom row */}
                    <div className="grid-item bottom-left">
                        <div className="placeholder-zone">Kitty & Discard</div>
                    </div>

                    <div className="grid-item bottom-center">
                        <PlayerZone {...playerProps.south.props} />
                    </div>

                    <div className="grid-item bottom-right">
                        <div className="placeholder-zone">Your Tricks</div>
                    </div>
                </div>
            </div>

    );
};

