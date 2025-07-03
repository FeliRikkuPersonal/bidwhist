import React, { useState, useEffect } from "react";

export default function Scoreboard({ gameState }) {
    const [bidPhase, setBidPhase] = useState(false);

    function formatBid(bid) {
        if (bid.isPassed) return `${bid.player} passes`;
        return `${bid.player} bids ${bid.value}${bid.isNo ? ' No' : ''}`;
    }

    useEffect(() => {
        if (gameState?.phase === 'BID') {
            setBidPhase(true);
        } else {
            setBidPhase(false);
        }
    }, [gameState?.phase]);

    return (
        <div className="scoreboard">
            <h4>Scoreboard</h4>
            <p>Team: 0</p>
            <p>Opponents: 0</p>
            {bidPhase && (
                <div className="bidding-history">
                    {gameState?.bids?.map((bid, index) => (
                        <div key={index}>{formatBid(bid)}</div>
                    ))}
                </div>
            )}
        </div>
    );
}
