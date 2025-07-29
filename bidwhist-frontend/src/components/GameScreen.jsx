// src/components/GameScreen.jsx

import React, { useRef, useEffect, useMemo } from 'react';
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
import { useThrowAlert } from '../hooks/useThrowAlert.js';
import handleQuit from '../utils/handleQuit.js';

/**
 * Main game interface layout and logic. Displays all player zones, trick piles, and
 * handles the discard submission phase. Utilizes context state to drive all layout logic.
 *
 * @returns {JSX.Element} The full game screen layout
 */
export default function GameScreen({ bidType }) {

  const { gameId, updateFromResponse, players, clearGameStateContext } = useGameState();
  const { positionToDirection, viewerPosition, clearUIContext } = usePositionContext();

  const savedMode = JSON.parse(localStorage.getItem('mode'));
  const API = import.meta.env.VITE_API_URL; // Server endpoint

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
  const throwAlert = useThrowAlert();
  const { register } = useZoneRefs();

  const dropZoneRef = useRef();
  const yourTrickRef = useRef();
  const theirTrickRef = useRef();
  const southZoneRef = useRef();
  const northZoneRef = useRef();
  const eastZoneRef = useRef();
  const westZoneRef = useRef();


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

  const playerProps = useMemo(() => ({
    north: getPlayerProps('north'),
    west: getPlayerProps('west'),
    east: getPlayerProps('east'),
    south: getPlayerProps('south'),
  }), [players, positionToDirection, handMap]);

  /**
   * Registers the zone refs for animation positioning.
   */
useEffect(() => {
  if (southZoneRef.current) register('zone-south', southZoneRef);
  if (northZoneRef.current) register('zone-north', northZoneRef);
  if (eastZoneRef.current) register('zone-east', eastZoneRef);
  if (westZoneRef.current) register('zone-west', westZoneRef);
}, [register]);


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
        throwAlert(data, 'info');
        console.error('Discard failed:', data);
      }
    } catch (error) {
      showAlert('Network error. Please try again.', 'error');
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

        <div className="grid-item top-center" ref={northZoneRef}>
          <PlayerZone {...playerProps.north.props} />
        </div>

        <div className="grid-item top-right">
          {bidType && (
            <button className="index-button quit-button"
              onClick={() =>
                handleQuit({ viewerPosition, gameId, savedMode, API, clearUIContext, clearGameStateContext })}>
              Quit
            </button>
          )}
        </div>

        {/* Middle row */}
        <div className="grid-item middle-left" ref={westZoneRef}>
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

        <div className="grid-item middle-right" ref={eastZoneRef}>
          <PlayerZone {...playerProps.east.props} />
        </div>

        {/* Bottom row */}
        <div className="grid-item bottom-left">{awardKitty && 'Select 6 cards to discard'}</div>

        <div className="grid-item bottom-center" ref={southZoneRef}>
          <PlayerZone dropZoneRef={dropZoneRef} {...playerProps.south.props} />
        </div>

        <div className="grid-item bottom-right">
          {awardKitty && (
            <button className="index-button settings-button" onClick={discard}>
              Submit
            </button>
          )}
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
