// src/animations/PlayCardAnimation.jsx

import React, { useEffect, useRef, useState } from 'react';

/**
 * Animates a card moving from a `fromRef` to a `toRef`.
 * Calls `onComplete` when done.
 */
export default function PlayCardAnimation({ card, fromRef, toRef, onComplete, direction }) {
  const [style, setStyle] = useState(null);
  const imgRef = useRef();

  // Helper: Rotation angle based on direction
  const getRotation = (dir) => {
    switch (dir) {
      case 'north':
        return '180deg';
      case 'east':
        return '270deg';
      case 'west':
        return '90deg';
      default:
        return '0deg'; // south
    }
  };

  useEffect(() => {
    if (!fromRef?.current || !toRef?.current) return;

    const container = document.querySelector('.floating-card-layer')?.getBoundingClientRect();
    if (!container) return;

    const fromRect = fromRef.current.getBoundingClientRect();
    const toRect = toRef.current.getBoundingClientRect();

    const fromX = fromRect.left + fromRect.width / 2 - container.left;
    const fromY = fromRect.top + fromRect.height / 2 - container.top;
    const toX = toRect.left + toRect.width / 2 - container.left;
    const toY = toRect.top + toRect.height / 2 - container.top;
    const rotation = getRotation(direction);

    // Initial position without transition
    setStyle({
      position: 'absolute',
      left: fromX,
      top: fromY,
      transform: `translate(-50%, -50%) rotate(${rotation})`,
      transition: 'none',
      zIndex: 20,
      opacity: 1,
    });

    // Animate to new position
    const timeout = setTimeout(() => {
      setStyle({
        position: 'absolute',
        left: toX,
        top: toY,
        transform: `translate(-50%, -50%) rotate(${rotation})`,
        transition: 'left 0.6s ease, top 0.6s ease',
        zIndex: 20,
        opacity: 1,
      });

      setTimeout(() => {
        onComplete?.();
      }, 650);
    }, 10);

    return () => clearTimeout(timeout);
  }, [fromRef, toRef, onComplete, direction]);

  if (!style) return null;

  return (
    <img
      ref={imgRef}
      src={`/static/img/deck/${card.cardImage}`}
      alt="Played card"
      className="card-img"
      style={style}
    />
  );
}
