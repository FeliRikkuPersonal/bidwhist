// src/components/AnimatedCard.jsx
import { useEffect, useState } from "react";

import '../css/Card.css';
import { getCardImage } from "../utils/CardUtils";
import { useGameState } from "../context/GameStateContext.jsx";
import { usePositionContext } from "../context/PositionContext.jsx";
import { useUIDisplay } from "../context/UIDisplayContext.jsx";

export default function AnimatedCard({ card, from, to, zIndex }) {
  const { debugLog: logGameState } = useGameState();
const { debugLog: logPosition } = usePositionContext();
const { debugLog: logUI } = useUIDisplay();


  const { viewerName } = usePositionContext();
  
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

    requestAnimationFrame(() => {
      setStyle(prev => ({
        ...prev,
        transform: `translate(calc(-50% + ${dx}px), calc(-50% + ${dy}px))`
      }));
    });
  }, [from, to]);

  const image = getCardImage(card, viewerName);

  return (
    <>
      <img src={image} className="card-img" style={style} alt="card" />
    </>
  );
}