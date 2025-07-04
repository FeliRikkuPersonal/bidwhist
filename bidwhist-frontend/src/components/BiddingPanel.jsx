import '../css/BiddingPanel.css';
import '../css/index.css';
import '../css/GameScreen.css';
import { useState, useEffect } from 'react';
import { usePositionContext } from '../context/PositionContext.jsx';
import { useGameState } from '../context/GameStateContext.jsx';
import { useUIDisplay } from '../context/UIDisplayContext.jsx';

export default function BiddingPanel({ closeBidding, onBidPlaced }) {
    const { viewerPosition, positionToDirection } = usePositionContext();
    const { currentTurnIndex } = useGameState();
    const { bidPhase, showBidding, setShowBidding } = useUIDisplay();

    // ✅ HOOKS MUST BE DECLARED FIRST
    const [bidValue, setBidValue] = useState('');
    const [isNo, setIsNo] = useState(false);

    useEffect(() => {
        if (!bidPhase || !viewerPosition || currentTurnIndex == null) return;

        const turnPlayerPos = ['P1', 'P2', 'P3', 'P4'][currentTurnIndex];
        const biddingTime = bidPhase;
        const isMyTurn = viewerPosition === turnPlayerPos;

        setShowBidding(biddingTime && isMyTurn);
    }, [bidPhase, currentTurnIndex, viewerPosition]);

    // ✅ Only conditionally render JSX, not the hooks
    if (!showBidding) return null;

    const placeBid = async () => {
        const res = await fetch('/api/game/bid', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                player: viewerPosition,
                value: parseInt(bidValue),
                isNo
            })
        });

        const bidData = await res.json();

        if (res.ok) {
            onBidPlaced?.(bidData);
            closeBidding?.();
        } else {
            console.error("Bid failed:", bidData);
        }
    };

    return (
        <div className="bidding-overlay card-play-zone grid-item center">
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
