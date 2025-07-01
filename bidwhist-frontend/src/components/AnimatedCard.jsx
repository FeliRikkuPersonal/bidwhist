import { useEffect, useState } from "react";

import '../css/Card.css';
import { getCardImage } from "../utils/CardUtils";
import { useZoneRefs } from "../context/RefContext";

export default function AnimatedCard({ card, from, to, viewerName, contextPhase, zIndex }) {
  const [style, setStyle] = useState({
    position: 'absolute',
    top: from.y,
    left: from.x,
    transform: 'translate(-50%, -50%)',
    transition: 'transform 0.8s ease-in-out',
    zIndex,
  });

  useEffect(() => {
    const dx = to.x - from.x;
    const dy = to.y - from.y;

    console.log(`🃏 Card: ${card.cardImage}, owner: ${card.owner}, to: (${to.x}, ${to.y})`);

    setStyle(prev => ({
      ...prev,
      transform: `translate(calc(-50% + ${dx}px), calc(-50% + ${dy}px))`
    }));
  }, [from, to]);

  const image = getCardImage(card, viewerName);

  return (
    <>
      <img src={image} className="card-img" style={style} alt="card" />
    </>
  );
}