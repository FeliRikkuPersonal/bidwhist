// src/components/GameScreen.jsx
import React, { useRef } from 'react';
import '../css/GameScreen.css';
import '../css/Card.css';
import '../css/PlayerZone.css';
import CardPlayZone from './CardPlayZone';
import PlayerZone from './PlayerZone';
import BidZone from './BidZone.jsx';
import { useGameState } from '../context/GameStateContext.jsx';
import { usePositionContext } from '../context/PositionContext.jsx';
import { useUIDisplay } from '../context/UIDisplayContext.jsx';

export default function GameScreen() {

    const {
        gameId,
        updateFromResponse,
        players,
        showHands,
    } = useGameState();
    const {
        playerName,
        positionToDirection,
        viewerPosition,
        viewerDirection,
    } = usePositionContext();
    const {
        awardKitty,
        setAwardKitty,
        discardPile,
        setDiscardPile } = useUIDisplay();

    const dropZoneRef = useRef();

    const getPlayerProps = (direction) => {

        if (!positionToDirection) {
            console.warn("Missing positionToDirection");
            return { direction, name: "", cards: [], showHand: false };
        }

        const entry = Object.entries(positionToDirection).find(
            ([_, dir]) => dir === direction
        );

        if (!entry) {
            console.warn(`No position found for direction: ${direction}`, positionToDirection);
            return { direction, name: "", cards: [], showHand: false };
        }

        const [position] = entry;
        const player = players.find(p => p.position === position);



        return {
            key: position,
            props: {
                direction,
                position,
                name: player?.name || direction.toUpperCase(),
                cards: player?.hand || [],
                revealHand: direction === 'south',
            }
        };
    };


    const playerProps = {
        north: getPlayerProps("north"),
        west: getPlayerProps("west"),
        east: getPlayerProps("east"),
        south: getPlayerProps("south"),
    };

    const discard = async () => {
        const payload = {
            gameId: gameId,
            player: viewerPosition,
            discards: discardPile,
        };

        try {
            const res = await fetch('/api/game/kitty', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            });

            const data = await res.json();

            if (res.ok) {
                updateFromResponse(data);
                setDiscardPile([]);
                setAwardKitty(false);
            } else {
                console.error("Discard failed:", data);
            }
        } catch (error) {
            console.error("Network error:", error);
        }
    }

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
                    <BidZone />
                </div>

                {/* Middle row */}
                <div className="grid-item middle-left">
                    <PlayerZone {...playerProps.west.props} />
                </div>

                <div className="grid-item center">
                    <CardPlayZone
                        dropZoneRef={dropZoneRef}
                    onCardPlayed={(card) => console.log("Played:", card)}
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
                    <PlayerZone dropZoneRef = { dropZoneRef } {...playerProps.south.props} />
                    {awardKitty && (
                        <button className="index-button settings-button" onClick={discard}>
                            Submit
                        </button>
                    )}
                </div>

                <div className="grid-item bottom-right">
                    <div className="placeholder-zone">Your Tricks</div>
                </div>
            </div>
        </div>

    );
};

