// src/components/GameScreen.jsx
import { useRef } from 'react';
import '../css/GameScreen.css';
import '../css/Card.css';
import '../css/PlayerZone.css';
import CardPlayZone from './CardPlayZone';
import PlayerZone from './PlayerZone';
import BidZone from './BidZone.jsx';
import { useGameState } from '../context/GameStateContext.jsx';
import { usePositionContext } from '../context/PositionContext.jsx';
import { useUIDisplay } from '../context/UIDisplayContext.jsx';
import { useAlert } from '../context/AlertContext.jsx'


// --- GameScreen Component ---
/* The GameScreen component manages the main game interface, including player zones, card play zones, 
   bid zone, and handling discard actions. It uses various context providers to manage state and interactions. */
export default function GameScreen() {

    const {
        gameId,
        updateFromResponse,
        players,
    } = useGameState();

    const {
        positionToDirection,
        viewerPosition,
    } = usePositionContext();

    const {
        awardKitty,
        setAwardKitty,
        discardPile,
        setDiscardPile,
        teamATricks,
        teamBTricks,
    } = useUIDisplay();

    const { showAlert } = useAlert();
    const dropZoneRef = useRef();
    const yourTrickRef = useRef();
    const theirTrickRef = useRef();


    // --- getPlayerProps Function ---
    /* Retrieves the player properties (name, cards, etc.) for a given direction (north, south, east, west). 
       This function checks the current positions and assigns the corresponding player data. */
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


        // Return player properties for this direction
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


    // --- discard Function ---
    /* Handles the discard action, ensuring exactly 6 cards are selected before submitting. */
    const discard = async () => {
        if (discardPile.length != 6) {
            showAlert('You must discard exactly 6 cards.');
            return;
        }

        const payload = {
            gameId: gameId,
            player: viewerPosition,
            discards: discardPile,
        };

        const API = process.env.REACT_APP_API_URL;

        // Make API call to submit the discard pile
        try {
            const res = await fetch(`${API}/api/game/kitty`, {
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


    // --- JSX Rendering ---
    /* The rendering of the game screen, with the appropriate layout for player zones, cards, and bid zones. */
    return (
        <div className="game-screen-container">
            <div className="game-grid">
                {/* Top row */}
                <div className="grid-item top-left">
                    <div className="placeholder-zone" ref={theirTrickRef}>
                        {[...Array(teamBTricks)].map((_, i) => (
                            <img
                                key={i} src="/static/img/deck/Deck_Back.png"
                                alt="Card Back"
                                className="card-img"
                                style={{
                                    position: 'absolute',
                                    left: `calc(50% + ${i * -15}px)`, // start at center and go left
                                    top: '50%',
                                    transform: 'translateY(-50%)',
                                    zIndex: i,             // stack visually
                                }}
                            />
                        ))}
                    </div>
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
                        yourTrickRef={yourTrickRef}
                        theirTrickRef={theirTrickRef}
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
                    <PlayerZone dropZoneRef={dropZoneRef} {...playerProps.south.props} />
                    {awardKitty && (
                        <button className="index-button settings-button" onClick={discard}>
                            Submit
                        </button>
                    )}
                </div>

                <div className="grid-item bottom-right">
                    <div className="placeholder-zone" ref={yourTrickRef}>
                        {[...Array(teamATricks)].map((_, i) => (
                            <img
                                key={i} src="/static/img/deck/Deck_Back.png"
                                alt="Card Back"
                                className="card-img"
                                style={{
                                    position: 'absolute',
                                    left: `calc(50% + ${i * -15}px)`, // start at center and go left
                                    top: '50%',
                                    transform: 'translateY(-50%)',
                                    zIndex: i,             // stack visually
                                }}
                            />
                        ))}
                    </div>
                </div>
            </div>
        </div>

    );
};

