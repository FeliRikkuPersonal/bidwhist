// src/components/GameScreen.jsx

import React, { useRef, useEffect } from 'react';
import '../css/GameScreen.css';
import '../css/Card.css';
import '../css/PlayerZone.css';

import CardPlayZone from './CardPlayZone';
import PlayerZone from './PlayerZone';

import { useGameState } from '../context/GameStateContext.jsx';
import { usePositionContext } from '../context/PositionContext.jsx';
import { useUIDisplay } from '../context/UIDisplayContext.jsx';
import { useZoneRefs } from '../context/RefContext.jsx';
import { useAlert } from '../context/AlertContext.jsx';
import handleQuit from '../utils/handleQuit.js';

/**
 * Main game interface layout and logic. Displays all player zones, trick piles, and
 * handles the discard submission phase. Utilizes context state to drive all layout logic.
 *
 * @returns {JSX.Element} The full game screen layout
 */
export default function GameScreen({bidType}) {
  const { gameId, updateFromResponse, players } = useGameState();
  const { positionToDirection, viewerPosition } = usePositionContext();

  const {
    handMap,
    awardKitty,
    setAwardKitty,
    discardPile,
    setDiscardPile,
    teamATricks,
    teamBTricks,
  } = useUIDisplay();

  const { showAlert } = useAlert();
  const { register } = useZoneRefs();

  const dropZoneRef = useRef();
  const yourTrickRef = useRef();
  const theirTrickRef = useRef();
  const southZoneRef = useRef();

  /**
   * Retrieves player props for a given direction (e.g., north, south).
   * Includes player name, position, and hand.
   */
  const getPlayerProps = (direction) => {
    if (!positionToDirection) {
      console.warn('Missing positionToDirection');
      return { direction, name: '', cards: [], showHand: false };
    }

    const entry = Object.entries(positionToDirection).find(([_, dir]) => dir === direction);
    if (!entry) {
      console.warn(`No position found for direction: ${direction}`, positionToDirection);
      return { direction, name: '', cards: [], showHand: false };
    }

    const [position] = entry;
    const player = players.find((p) => p.position === position);

    return {
      key: position,
      props: {
        direction,
        position,
        name: player?.name || direction.toUpperCase(),
        cards: handMap[direction] || [],
        revealHand: direction === 'south',
      },
    };
  };

  const playerProps = {
    north: getPlayerProps('north'),
    west: getPlayerProps('west'),
    east: getPlayerProps('east'),
    south: getPlayerProps('south'),
  };

  /**
   * Registers the south zone ref for animation positioning.
   */
  useEffect(() => {
    if (southZoneRef.current) {
      register('zone-south', southZoneRef);
    }
  }, [southZoneRef]);

  /**
   * Submits selected cards as discards (must be 6).
   * POSTs to /game/kitty and updates game state.
   */
  const discard = async () => {
    if (discardPile.length !== 6) {
      showAlert('You must discard exactly 6 cards.');
      return;
    }

    const payload = {
      gameId,
      player: viewerPosition,
      discards: discardPile,
    };

    const API = import.meta.env.VITE_API_URL;

    try {
      const res = await fetch(`${API}/game/kitty`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload),
      });

      const data = await res.json();

      if (res.ok) {
        updateFromResponse(data);
        setDiscardPile([]);
        setAwardKitty(false);
      } else {
        console.error('Discard failed:', data);
      }
    } catch (error) {
      console.error('Network error:', error);
    }
  };

  /**
   * Renders the full 3x3 grid of the game table with player zones and trick placeholders.
   */
  return (
    <div className="game-screen-container">
      <div className="game-grid">
        {/* Top row */}
        <div className="grid-item top-left">
          <div className="placeholder-zone" ref={theirTrickRef}>
            {[...Array(teamBTricks)].map((_, i) => (
              <img
                key={i}
                src="/static/img/deck/Deck_Back.png"
                alt="Card Back"
                className="card-img"
                style={{
                  position: 'absolute',
                  left: `calc(50% + ${i * -15}px)`,
                  top: '50%',
                  transform: 'translateY(-50%)',
                  zIndex: i,
                }}
              />
            ))}
          </div>
        </div>

        <div className="grid-item top-center">
          <PlayerZone {...playerProps.north.props} />
        </div>

        <div className="grid-item top-right" >
        {bidType &&<button className="index-button quit-button" onClick={handleQuit}>Quit</button>}
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
            onCardPlayed={(card) => console.log('Played:', card)}
          />
        </div>

        <div className="grid-item middle-right">
          <PlayerZone {...playerProps.east.props} />
        </div>

        {/* Bottom row */}
        <div className="grid-item bottom-left">
        
        </div>


        <div className="grid-item bottom-center">
          <PlayerZone ref={southZoneRef} dropZoneRef={dropZoneRef} {...playerProps.south.props} />
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
                key={i}
                src="/static/img/deck/Deck_Back.png"
                alt="Card Back"
                className="card-img"
                style={{
                  position: 'absolute',
                  left: `calc(50% + ${i * -15}px)`,
                  top: '50%',
                  transform: 'translateY(-50%)',
                  zIndex: i,
                }}
              />
            ))}
          </div>
        </div>
      </div>
    </div>
  );
}
