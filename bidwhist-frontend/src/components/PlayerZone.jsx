// src/components/PlayerZone.jsx

import React, { useState, useEffect, useRef, useImperativeHandle, forwardRef } from 'react';
import { useZoneRefs } from '../context/RefContext.jsx';
import { useUIDisplay } from '../context/UIDisplayContext.jsx';
import { useThrowAlert } from '../hooks/useThrowAlert.js';

import '../css/PlayerZone.css';
import '../css/CardPlayZone.css';
import '../css/Card.css';

/**
 * PlayerZone represents the visual and interactive area for a player's cards.
 * It manages card selection, drag-and-drop, and animation ref registration.
 *
 * @param {string} direction - One of 'north', 'south', 'east', 'west'
 * @param {string} name - Player's display name
 * @param {boolean} revealHand - Whether the player's hand should be face-up
 * @param {Array} cards - List of card objects for this player
 * @param {React.Ref} ref - Optional forwarded ref for exposing layout position
 * @returns {JSX.Element}
 */
const PlayerZone = forwardRef(({ direction, name, revealHand, cards = [] }, ref) => {
  const {
    showHands,
    awardKitty,
    myTurn,
    setDiscardPile,
    setSelectedCard,
    playedCard,
    playedCardPosition,
  } = useUIDisplay();

  const [draggingCardIndex, setDraggingCardIndex] = useState(null);
  const [selectedIndices, setSelectedIndices] = useState([]);

  const handRef = useRef();
  const playRef = useRef();
  const zoneRef = useRef();
  const { register } = useZoneRefs();

    const throwAlert = useThrowAlert();

  /**
   * Handles clicks on cards for both discard and regular play modes.
   */
  const handleCardClick = (index, card) => {
    if (awardKitty) {
      if (selectedIndices.includes(index)) {
        setSelectedIndices((prev) => prev.filter((i) => i !== index));
        setDiscardPile((prev) => prev.filter((c) => c.cardImage !== card.cardImage));
      } else if (selectedIndices.length < 6) {
        setSelectedIndices((prev) => [...prev, index]);
        setDiscardPile((prev) => [...prev, card]);
      }
    } else {
      if (selectedIndices.includes(index)) {
        setSelectedIndices([]);
        setSelectedCard([]);
      } else {
        setSelectedIndices([index]);
        setSelectedCard([card]);
      }
    }
  };

  /**
   * Registers refs with the shared zone system for animation use.
   */
  useEffect(() => {
    if (handRef.current) {
      register(`hand-${direction}`, handRef);

      const attemptRegisterOrigin = () => {
        const firstCard = handRef.current.querySelector('.card-img');
        if (firstCard) {
          register(`card-origin-${direction}`, { current: firstCard });
        } else {
          setTimeout(attemptRegisterOrigin, 100);
        }
      };
      attemptRegisterOrigin();
    }

    if (playRef.current) {
      register(`play-${direction}`, playRef);
    }

    if (ref && typeof ref === 'object' && ref.current) {
      register(`zone-${direction}`, ref);
    }
  }, [direction, register, ref]);


  /**
   * Exposes getPosition() to parent via ref.
   */
  useImperativeHandle(ref, () => ({
    getPosition: () => zoneRef.current?.getBoundingClientRect(),
  }));

  useEffect(() => {
    console.log(`[${direction}] cards prop:`, cards);
  }, [cards]);

  /**
   * Renders a player’s card zone including their name, play area, and hand.
   */
  return (
    <div ref={zoneRef} className={`player-zone ${direction}`}>
      <div ref={playRef} className={`play-slot ${direction}`} />

      {['west', 'east'].includes(direction) && <div className="player-name">{name}</div>}

      <div ref={handRef} className={`player-hand ${direction}`}>
        {showHands &&
          (revealHand
            ? cards
                .filter((c) => !(playedCard?.cardImage === c.cardImage && direction === 'south'))
                .map((card, i) => (
                  <img
                    key={i}
                    src={`/static/img/deck/${card.cardImage}`}
                    alt="card"
                    className="card-img draggable"
                    style={{
                      transform:
                        playedCard?.cardImage === card.cardImage
                          ? `translate(${playedCardPosition.x}px, ${playedCardPosition.y}px)`
                          : selectedIndices.includes(i)
                            ? 'translateY(-30px)'
                            : 'translateY(0)',
                      border: selectedIndices.includes(i) ? '2px solid gold' : 'none',
                      opacity: playedCard?.cardImage === card.cardImage ? 0.9 : 1,
                      transition: 'transform 0.2s, border 0.2s, opacity 0.2s',
                      position: playedCard?.cardImage === card.cardImage ? 'absolute' : 'relative',
                      left:
                        playedCard?.cardImage === card.cardImage ? playedCardPosition.x : undefined,
                      top:
                        playedCard?.cardImage === card.cardImage ? playedCardPosition.y : undefined,
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
                  style={{
                    visibility: draggingCardIndex === i ? 'hidden' : 'visible',
                  }}
                />
              )))}
      </div>

      {['north', 'south'].includes(direction) && <div className="player-name">{name}</div>}
    </div>
  );
});

export default PlayerZone;
