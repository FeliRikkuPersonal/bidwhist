// src/components/GameScreen.jsx
import '../css/GameScreen.css';
import '../css/Card.css';
import '../css/PlayerZone.css';
import CardPlayZone from './CardPlayZone';
import PlayerZone from './PlayerZone';
import { useGameState } from '../context/GameStateContext';
import { usePositionContext } from '../context/PositionContext';

export default function GameScreen() {
    const {
        gameState,
        showHands,
    } = useGameState();
    const {
        positionToDirection,
        viewerPosition,
    } = usePositionContext();

    const getPlayerProps = (direction) => {
        if (!gameState || !positionToDirection) {
            return { direction, name: "", cards: [], showHand: false };
        }

        const entry = Object.entries(positionToDirection).find(
            ([_, dir]) => dir === direction
        );

        if (!entry) {
            return { direction, name: "", cards: [], showHand: false };
        }

        const [position] = entry;
        const player = gameState.players.find(p => p.position === position);

        const isViewer = position === viewerPosition;

        return {
            key: position,
            props: {
                direction,
                position,
                name: player?.name || direction.toUpperCase(),
                cards: player?.hand || [],
                showHand: isViewer && showHands,
            }
        };
    };

    const playerProps = {
        north: getPlayerProps("north"),
        west: getPlayerProps("west"),
        east: getPlayerProps("east"),
        south: getPlayerProps("south"),
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
                    <PlayerZone {...playerProps.south.props} />
                </div>

                <div className="grid-item bottom-right">
                    <div className="placeholder-zone">Your Tricks</div>
                </div>
            </div>
        </div>

    );
};

