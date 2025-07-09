import React, { useEffect, useRef, useState } from 'react';

export default function PlayCardAnimation({ card, fromRef, toRef, onComplete }) {
  const [style, setStyle] = useState(null);
  const imgRef = useRef();

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

    // Set initial position (off-screen)
    setStyle({
      position: 'absolute',
      left: fromX,
      top: fromY,
      transform: 'translate(-50%, -50%)',
      transition: 'left 0.6s ease, top 0.6s ease, opacity 0.3s ease',
      zIndex: 20,
      opacity: 1,
    });

    // Delay to ensure initial style is committed
    requestAnimationFrame(() => {
      // Optional layout flush
      void imgRef.current?.offsetWidth;

      setStyle(prev => ({
        ...prev,
        left: toX,
        top: toY,
      }));
    });

    const timeout = setTimeout(() => {
      onComplete?.();
    }, 700);

    return () => clearTimeout(timeout);
  }, [fromRef, toRef, onComplete]);

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
