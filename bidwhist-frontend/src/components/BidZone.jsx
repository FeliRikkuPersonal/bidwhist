import { useEffect, useState } from "react";
import { useGameState } from "../context/GameStateContext.jsx";
import { useUIDisplay } from "../context/UIDisplayContext.jsx";

import '../css/BidZone.css';

export default function BidZone() {
    const { phase, bids } = useGameState();
    const { bidPhase, kittyPhase, setKittyPhase } = useUIDisplay();

    const [showPlayerBids, setShowPlayerBids] = useState(false);


    // --- formatBid Function ---
    /* Formats the bid information to display whether a player has passed or made a bid.
       If the player passed, it shows "player passes". Otherwise, it shows the bid value with or without "No". */
    function formatBid(bid) {
        if (bid.passed) return `${bid.player} passes`;
        return `${bid.player} bids ${bid.value}${bid.isNo ? ' No' : ''}`;
    }

    // --- useEffect for Updating Bid Visibility ---
    /* Listens to changes in the game phase, bid phase, and kitty phase to update the visibility of player bids. */
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