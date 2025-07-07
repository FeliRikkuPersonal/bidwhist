import React, { useEffect, useState } from "react";
import { useGameState } from "../context/GameStateContext.jsx";
import { useUIDisplay } from "../context/UIDisplayContext.jsx";

import '../css/BidZone.css';

export default function BidZone() {
    const { phase, bids } = useGameState();
    const { bidPhase, kittyPhase, setKittyPhase } = useUIDisplay();
    
    const [showPlayerBids, setShowPlayerBids] = useState(false);


    function formatBid(bid) {
        if (bid.passed) return `${bid.player} passes`;
        return `${bid.player} bids ${bid.value}${bid.isNo ? ' No' : ''}`;
    }

    useEffect(() => {
        setKittyPhase(phase === 'KITTY');

        if (bidPhase || kittyPhase) {
            setShowPlayerBids(true);
        } else {
            setShowPlayerBids(false);
        }

    }, [phase, bidPhase, kittyPhase]);


    return (
        <div className="bid-zone">
            {showPlayerBids && (
                <div className="player-bids">
                    <h4>Player Bids</h4>
                    <div className="bidding-history">
                        {bids?.map((bid, index) => (
                            <div key={index}>{formatBid(bid)}</div>
                        ))}
                    </div>
                </div>
            )}
        </div>
    );
}