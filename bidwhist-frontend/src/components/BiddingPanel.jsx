import React, { useState, useContext } from 'react';
import '../css/BiddingPanel.css';
import { PlayerContext } from '../context/PlayerContext';

export default function BiddingPanel({ closeBidding, onBidPlaced }) {
    const { viewerPosition } = useContext(PlayerContext);
    const [bidValue, setBidValue] = useState('');
    const [isNo, setIsNo] = useState(false);

    const placeBid = async () => {
        const res = await fetch('/api/game/bid', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                player: viewerPosition,
                value: parseInt(bidValue),
                isNo: isNo
            })
        });

        const bidData = await res.json();

        if (res.ok) {
            onBidPlaced(bidData); // optional: trigger game state update
            closeBidding();
        } else {
            console.error("Bid failed:", bidData);
        }
    };

    return (
        <div className="bidding-overlay">
            <div className="bidding-panel">
                <h2>Place Your Bid</h2>

                <input
                    type="number"
                    min="4"
                    max="7"
                    placeholder="Enter bid (4–7)"
                    value={bidValue}
                    onChange={(e) => setBidValue(e.target.value)}
                    className="index-input-box short-box"
                />

                <label style={{ display: "block", marginTop: "10px" }}>
                    <input
                        type="checkbox"
                        checked={isNo}
                        onChange={(e) => setIsNo(e.target.checked)}
                    />{' '}
                    No Trump
                </label>

                <div className="settings-actions">
                    <button className="index-button settings-button" onClick={placeBid}>
                        Set Bid
                    </button>
                    <button className="index-button settings-button" onClick={closeBidding}>
                        Close
                    </button>
                </div>
            </div>
        </div>
    );
}
