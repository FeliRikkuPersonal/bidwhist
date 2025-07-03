import React, { useState, useEffect, useRef, useImperativeHandle, forwardRef } from 'react';
import { useZoneRefs } from '../context/RefContext';
import '../css/PlayerZone.css';

const PlayerZone = forwardRef(({ position, direction, name, showHand, cards = [], gameState }, ref) => {
  const zoneRef = useRef();
  const [draggingCardIndex, setDraggingCardIndex] = useState(null);
  const visiblePhases = ['PRE_BID', 'BID', 'KITTY', 'PLAY', 'SCORE'];
  const shouldRenderCards = visiblePhases.includes(gameState?.phase);
  const { register } = useZoneRefs();

  useImperativeHandle(ref, () => ({
    getPosition: () => zoneRef.current?.getBoundingClientRect()
  }));

  useEffect(() => {
    if (zoneRef.current) {
      console.log(`[PlayerZone] Registering ref for direction: ${direction}`);
      register(direction, zoneRef);
    }
  }, [direction]);

  useEffect(() => {
    console.log("phase changed:", gameState?.phase);
  }, [gameState?.phase]);

  return (
    <div ref={zoneRef} className={`player-zone ${direction}`}>
      {["west", "east"].includes(direction) && <div className="player-name">{name}</div>}
      <div className={`player-hand ${direction}`}>
        {shouldRenderCards ? (
          showHand
            ? cards.map((card, i) => (
              <img
                key={i}
                src={`/static/img/deck/${card.cardImage}`}
                alt="card"
                className="card-img"
                draggable={direction === 'south'}
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
            ))
        ) : null}
      </div>

      {["north", "south"].includes(direction) && <div className="player-name">{name}</div>}
    </div>
  );
});

export default PlayerZone;
