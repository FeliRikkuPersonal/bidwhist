import React, { useState, useEffect } from "react";
import { useGameState } from "../context/GameStateContext.jsx";
import { useUIDisplay } from "../context/UIDisplayContext.jsx";

export default function Scoreboard() {
    const { gameState, phase } = useGameState();
    const { bidPhase, setBidPhase } = useUIDisplay();

{/* Depricated
    useEffect(() => {
        if (gameState?.phase === 'BID') {
            setBidPhase(true);
        } else {
            setBidPhase(false);
        }
    }, [gameState?.phase]);
    */}

    return (
        <div className="scoreboard">
            <h4>Scoreboard</h4>
            <p>Team: 0</p>
            <p>Opponents: 0</p>
            
        </div>
    );
}
