import React, { useEffect } from 'react';
import { getCardImage } from '../utils/CardUtils';
import '../css/Animations.css';

export default function ShuffleAnimation({ cards, viewerName, deckPosition, onComplete }) {

  useEffect(() => {
    const timeout = setTimeout(() => {
      if (onComplete) onComplete();
    }, 2000); // Match your CSS animation duration
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