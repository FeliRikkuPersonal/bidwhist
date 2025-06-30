import { useEffect, useState } from "react";

import '../css/Card.css';
import { getCardImage } from "../utils/CardUtils";
import { getRef } from "../utils/RefRegistry";

export default function AnimatedCard({ card, from, to, viewerName, contextPhase }) {
  const [style, setStyle] = useState({
    position: 'absolute',
    top: from.y,
    left: from.x,
    transform: 'translate(-50%, -50%)',
    transition: 'transform 0.8s ease-in-out',
  });

  useEffect(() => {
    const dx = to.x - from.x;
    const dy = to.y - from.y;
    setStyle(prev => ({
      ...prev,
      transform: `translate(calc(-50% + ${dx}px), calc(-50% + ${dy}px))`
    }));
  }, [from, to]);

  const image = getCardImage(card, viewerName);

  return (
    <img src={image} className="card-img" style={style} alt="card" />
  );
}