// src/components/CardPlayZone.js
import React from 'react';
import { useState } from 'react';
import '../css/CardPlayZone.css';

export default function CardPlayZone({ playerPosition, onCardPlayed }) {
    const [isOver, setIsOver] = useState(false);
    const [playedCard, setPlayedCard] = useState(null);

    const handleDrop = (e) => {
        e.preventDefault();
        const fullPath = e.dataTransfer.getData('text/plain');
        const cardName = fullPath.split('/').pop();
        console.log("Parsed card name:", cardName); // ✅

        if (playerPosition === 'south') {
            setPlayedCard(cardName);
            onCardPlayed?.(cardName);
        }

        setIsOver(false);
    };

    if (playerPosition !== 'south') return null;

    return (
        <div className={`card-play-zone ${playerPosition}`}>
            <div
                className={`drop-zone ${playerPosition} ${isOver ? 'highlight' : ''}`}
                onDragOver={(e) => e.preventDefault()}
                onDragEnter={() => playerPosition === 'south' && setIsOver(true)}
                onDragLeave={() => playerPosition === 'south' && setIsOver(false)}
                onDrop={playerPosition === 'south' ? handleDrop : undefined}
            >
                {playedCard && (
                    <img
                        src={`/static/img/deck/${playedCard}`}
                        alt="Played card"
                        className="card-img"
                    />
                )}
            </div>
        </div>
    );
}
