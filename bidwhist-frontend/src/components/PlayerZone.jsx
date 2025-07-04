import React, { useState, useEffect, useRef, useImperativeHandle, forwardRef } from 'react';
import { useZoneRefs } from '../context/RefContext.jsx';
import '../css/PlayerZone.css';
import { useGameState } from '../context/GameStateContext.jsx';
import { usePositionContext } from '../context/PositionContext.jsx';
import { useUIDisplay } from '../context/UIDisplayContext.jsx';

const PlayerZone = forwardRef(({ position, direction, name, revealHand, cards = [] }, ref) => {
  const { debugLog: logGameState, phase } = useGameState();
  const { debugLog: logPosition } = usePositionContext();
  const { debugLog: logUI, showHands } = useUIDisplay();

  {/* comment out until debuglog is deleted }
  const { phase } = useGameState();
  */}
  const [draggingCardIndex, setDraggingCardIndex] = useState(null);
  const zoneRef = useRef();
  const { register, debug: logZoneRef } = useZoneRefs();

  useImperativeHandle(ref, () => ({
    getPosition: () => zoneRef.current?.getBoundingClientRect()
  }));

  useEffect(() => {
    if (zoneRef.current) {
      console.log(`[PlayerZone] Registering ref for direction: ${direction}`);
      register(direction, zoneRef);
    }
  }, [direction, register]);

  return (
    <div ref={zoneRef} className={`player-zone ${direction}`}>
      {["west", "east"].includes(direction) && <div className="player-name">{name}</div>}

      {showHands && (
        <div className={`player-hand ${direction}`}>
          {revealHand
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
            ))}
        </div>
      )}

      {["north", "south"].includes(direction) && <div className="player-name">{name}</div>}
    </div>
  );
})


export default PlayerZone;
