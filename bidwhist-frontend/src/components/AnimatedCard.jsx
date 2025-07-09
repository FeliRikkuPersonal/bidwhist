// src/components/AnimatedCard.jsx
import { useEffect, useState } from "react";
import '../css/Card.css';
import { getCardImage } from "../utils/CardUtils";
import { usePositionContext } from "../context/PositionContext.jsx";

export default function AnimatedCard({ card, from, to, zIndex = 10, onComplete }) {
  const { viewerName } = usePositionContext();

  const [style, setStyle] = useState({
    position: 'absolute',
    left: from.x,
    top: from.y,
    transform: 'translate(-50%, -50%)',
    zIndex,
    transition: 'left 0.6s ease, top 0.6s ease',
  });

  useEffect(() => {
    // Trigger transition on next frame
    requestAnimationFrame(() => {
      setStyle(prev => ({
        ...prev,
        left: to.x,
        top: to.y,
      }));
    });

    // Optional completion callback after animation
    const timeout = setTimeout(() => {
      onComplete?.();
    }, 700);

    return () => clearTimeout(timeout);
  }, [to.x, to.y, onComplete]);

  const image = getCardImage(card, viewerName);

  return (
    <img
      src={image}
      alt="Animated card"
      className="card-img"
      style={style}
    />
  );
}
