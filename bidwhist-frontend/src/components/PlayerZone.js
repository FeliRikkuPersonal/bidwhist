import React, { useState } from 'react';
import '../PlayerZone.css';

function PlayerZone({ position, name, showHand, cards = [] }) {
    const [draggingCardIndex, setDraggingCardIndex] = useState(null);

    return (
        <div className={`player-zone ${position}`}>
            {["west", "east"].includes(position) && <div className="player-name">{name}</div>}
            <div className={`player-hand ${position}`}>
                {showHand
                    ? cards.map((card, i) => (
                        <img
                            key={i}
                            src={`static/img/deck/${card}`}
                            alt={card}
                            className="card-img"
                            draggable
                            onDragStart={(e) => {
                                if (position === 'south') {
                                    e.dataTransfer.setData('text/plain', card);
                                    setDraggingCardIndex(i);
                                }
                            }}
                            onDragEnd={() => setDraggingCardIndex(null)}
                        />
                    ))
                    : cards.map((_, i) => (
                        <img
                            key={i}
                            src="static/img/deck/Deck_Back.png"
                            alt="Card Back"
                            className="card-img"
                            style={{visibility: draggingCardIndex === i ? 'hidden' : 'visible'}}
                        />
                    ))}
            </div>
            {["north", "south"].includes(position) && <div className="player-name">{name}</div>}
        </div>
    );
}

export default PlayerZone;
