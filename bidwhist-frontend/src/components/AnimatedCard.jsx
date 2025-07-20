// src/components/AnimatedCard.jsx

import { useEffect, useState } from 'react';
import '../css/Card.css';

import { getCardImage } from '../utils/CardUtils';
import { usePositionContext } from '../context/PositionContext.jsx';

/*
*
* AnimatedCard handles the animation of a card moving from a source position (from)
* to a destination position (to) with a smooth transition. It optionally triggers
* a callback when the animation is complete.
*
*/
export default function AnimatedCard({ card, from, to, zIndex = 10, onComplete }) {
  const { viewerName } = usePositionContext();

  /*
  * Local state to control the inline CSS styles for animation
  */
  const [style, setStyle] = useState({
    position: 'absolute',
    left: from.x,
    top: from.y,
    transform: 'translate(-50%, -50%)',
    zIndex,
    transition: 'left 0.6s ease, top 0.6s ease',
  });

  /*
  * Triggers the animation on mount and invokes the onComplete callback after delay
  */
  useEffect(() => {
    requestAnimationFrame(() => {
      setStyle((prev) => ({
        ...prev,
        left: to.x,
        top: to.y,
      }));
    });

    const timeout = setTimeout(() => {
      onComplete?.();
    }, 700);

    return () => clearTimeout(timeout);
  }, [to.x, to.y, onComplete]);

  /* retrieve image source based on viewer and card data */
  const image = getCardImage(card, viewerName);

  /*
  * Render the animated card image with calculated styles
  */
  return <img src={image} alt="Animated card" className="card-img" style={style} />;
}
