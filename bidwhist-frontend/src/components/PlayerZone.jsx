// src/components/PlayerZone.jsx
import React, { useState, useEffect, useRef, useImperativeHandle, forwardRef } from 'react';
import { useZoneRefs } from '../context/RefContext.jsx';
import '../css/PlayerZone.css';
import '../css/CardPlayZone.css';
import '../css/Card.css';
import { useGameState } from '../context/GameStateContext.jsx';
import { usePositionContext } from '../context/PositionContext.jsx';
import { useUIDisplay } from '../context/UIDisplayContext.jsx';


/* The PlayerZone component represents a player's area in the game,
   where their hand of cards is displayed and interactable. */
const PlayerZone = forwardRef(({ direction, name, revealHand, cards = [] }, ref) => {
  const { kitty } = useGameState();
  const {
    showHands,
    awardKitty,
    myTurn,
    setDiscardPile,
    setSelectedCard,
    playedCard,
    playedCardPosition
  } = useUIDisplay();

  const [draggingCardIndex, setDraggingCardIndex] = useState(null);
  const [selectedIndices, setSelectedIndices] = useState([]);

  // --- Refs ---
  /* Refs for the hand, play area, and zone. */
  const handRef = useRef();
  const playRef = useRef();
  const zoneRef = useRef();
  const { register } = useZoneRefs();

  // --- Combining Hand and Kitty ---
  /* Combines the player's cards and the kitty (discarded cards), if applicable, into one array to display. */
  const fullHand = [...cards, ...(awardKitty ? kitty : [])];


  // --- handleCardClick Function ---
  /* Handles card selection or deselection, and manages discard pile when in awardKitty mode. */
  const handleCardClick = (index, card) => {
    if (awardKitty) {
      if (selectedIndices.includes(index)) {
        // Deselect
        setSelectedIndices(prev => prev.filter(i => i !== index));
        setDiscardPile(prev => prev.filter(c => c.cardImage !== card.cardImage));
      } else if (selectedIndices.length < 6) {
        // Select
        setSelectedIndices(prev => [...prev, index]);
        setDiscardPile(prev => [...prev, card]);
      }
    } else {
      // Only 1 card allowed
      if (selectedIndices.includes(index)) {
        setSelectedIndices([]);
        setSelectedCard([]); // Clear
      } else {
        setSelectedIndices([index]);
        setSelectedCard([card]);
      }
    }
  };


  // --- useEffect for Registering Refs ---
  /* Registers the hand and play area refs upon mount and whenever the `direction` prop changes. */
  useEffect(() => {
    if (handRef.current) {
      register(`hand-${direction}`, handRef);

      const attemptRegisterOrigin = () => {
        const firstCard = handRef.current.querySelector('.card-img');
        if (firstCard) {
          register(`card-origin-${direction}`, { current: firstCard });
        } else {
          // Retry in 100ms
          setTimeout(attemptRegisterOrigin, 100);
        }
      };
      attemptRegisterOrigin();
    }
    if (playRef.current) {
      register(`play-${direction}`, playRef);
    }
  }, [direction, register]);


  // --- useEffect for registering refs on direction change ---
  useEffect(() => {
    if (handRef.current) {
      register(`hand-${direction}`, handRef);
    }
    if (playRef.current) {
      register(`play-${direction}`, playRef);
    }
  }, [direction, register]);


  // --- useImperativeHandle ---
  /* Exposes the `getPosition` method, which retrieves the current bounding rectangle of the 
  player zone. */
  useImperativeHandle(ref, () => ({
    getPosition: () => zoneRef.current?.getBoundingClientRect()
  }));


  // --- JSX Rendering ---
  /* The JSX rendering logic for the PlayerZone component. It conditionally renders the player's
  hand of cards, either as face-down cards (in the case of non-reveal) or face-up cards. */
  return (
    <div ref={zoneRef} className={`player-zone ${direction}`}>
      <div ref={playRef} className={`play-slot ${direction}`}></div>

      {["west", "east"].includes(direction) && <div className="player-name">{name}</div>}
      <div ref={handRef} className={`player-hand ${direction}`}>
        {showHands &&
          (revealHand
            ? fullHand
              .filter(c => !(playedCard?.cardImage === c.cardImage && direction === 'south'))
              .map((card, i) => (
                <img
                  key={i}
                  src={`/static/img/deck/${card.cardImage}`}
                  alt="card"
                  className="card-img draggable"
                  style={{
                    transform: playedCard?.cardImage === card.cardImage
                      ? `translate(${playedCardPosition.x}px, ${playedCardPosition.y}px)`
                      : selectedIndices.includes(i)
                        ? 'translateY(-30px)'
                        : 'translateY(0)',
                    border: selectedIndices.includes(i) ? '2px solid gold' : 'none',
                    opacity: playedCard?.cardImage === card.cardImage ? 0.9 : 1,
                    transition: 'transform 0.2s, border 0.2s, opacity 0.2s',
                    position: playedCard?.cardImage === card.cardImage ? 'absolute' : 'relative',
                    left: playedCard?.cardImage === card.cardImage ? playedCardPosition.x : undefined,
                    top: playedCard?.cardImage === card.cardImage ? playedCardPosition.y : undefined,
                  }}

                  onClick={() => handleCardClick(i, card)}
                  draggable={myTurn && direction === 'south'}
                  onDragStart={(e) => {
                    e.dataTransfer.setData('application/json', JSON.stringify(card));
                    setDraggingCardIndex(i);
                  }}
                  onDragEnd={() => setDraggingCardIndex(null)}
                />
              ))
            : cards.map((_, i) => (
              <img
                key={i}
                src="/static/img/deck/Deck_Back.png"
                alt="Card Back"
                className="card-img"
                style={{ visibility: draggingCardIndex === i ? 'hidden' : 'visible' }}
              />
            )))}
      </div>


      {["north", "south"].includes(direction) && <div className="player-name">{name}</div>}
    </div>
  );
})


export default PlayerZone;
