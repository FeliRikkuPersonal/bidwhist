// src/animations/PlayCardAnimation.jsx

import React, { useEffect, useRef, useState } from 'react';

/*
*
* PlayCardAnimation animates a card moving from a source DOM ref to a target DOM ref.
* It calculates absolute screen positions and transitions the card accordingly.
* Calls onComplete after animation is done.
*
*/
export default function PlayCardAnimation({ card, fromRef, toRef, onComplete }) {
  const [style, setStyle] = useState(null);
  const imgRef = useRef();

  /*
  * Initializes the animation: sets start and end positions based on fromRef and toRef
  * Triggers transition to the target location
  */
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

    /* Set initial style at from position */
    setStyle({
      position: 'absolute',
      left: fromX,
      top: fromY,
      transform: 'translate(-50%, -50%)',
      transition: 'left 0.6s ease, top 0.6s ease, opacity 0.3s ease',
      zIndex: 20,
      opacity: 1,
    });

    /* Trigger transition to target after initial frame */
    requestAnimationFrame(() => {
      void imgRef.current?.offsetWidth; // flush layout
      setStyle((prev) => ({
        ...prev,
        left: toX,
        top: toY,
      }));
    });

    /* Invoke callback after animation completes */
    const timeout = setTimeout(() => {
      onComplete?.();
    }, 700);

    return () => clearTimeout(timeout);
  }, [fromRef, toRef, onComplete]);

  if (!style) return null;

  /*
  * Render the animated card image
  */
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
