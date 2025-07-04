import React, { useEffect } from 'react';
import { getCardImage } from '../utils/CardUtils';
import '../css/Animations.css';
import { usePositionContext } from '../context/PositionContext.jsx';
import { useUIDisplay } from '../context/UIDisplayContext.jsx';

function ShuffleAnimation({ cards, onComplete }) {
  const { viewerName } = usePositionContext();
  const { deckPosition } = useUIDisplay();

  useEffect(() => {
    const timeout = setTimeout(() => {
      if (onComplete) onComplete();
    }, 1500); // Match your CSS animation duration
    return () => clearTimeout(timeout);
  }, [onComplete]);

  return (
    <>
      {cards.map((card, i) => {
        const dx = Math.random() * 250 - 100;
        const dy = Math.random() * 250 - 100;

        return (
          <img
            key={i}
            src={getCardImage(card, viewerName, 'SHUFFLING')}
            className="card-img card-shuffle"
            style={{
              top: deckPosition.y,
              left: deckPosition.x,
              transform: 'translate(-50%, -50%)',
              '--dx': `${dx}px`,
              '--dy': `${dy}px`,
              zIndex: i
            }}
            alt={`card-${i}`}
          />
        );
      })}
    </>
  );
}

export default ShuffleAnimation;