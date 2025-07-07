import '../css/BiddingPanel.css';
import '../css/index.css';
import '../css/GameScreen.css';
import { useState } from 'react';
import { usePositionContext } from '../context/PositionContext.jsx';
import { useGameState } from '../context/GameStateContext.jsx';
import { useUIDisplay } from '../context/UIDisplayContext.jsx';

export default function BidTypePanel({ closeBidTypePanel }) {
    const { viewerPosition } = usePositionContext();
    const { 
        gameId,
        setBids, 
        highestBid, 
        setPhase, 
        setTrumpSuit, 
        setBidType, 
        setCurrentTurnIndex,
        setWinningPlayerName,
    } = useGameState();
    const { showFinalizeBid, setAwardKitty } = useUIDisplay();

    const [direction, setDirection] = useState('UPTOWN');
    const [suit, setSuit] = useState('HEARTS');

    const isNoTrump = highestBid?.isNo;

    if (!showFinalizeBid) return null;

    const finalizeBid = async () => {
        const payload = {
            gameId: gameId,
            player: viewerPosition,
            type: direction,             // UPTOWN or DOWNTOWN
            suit: isNoTrump ? null : suit // Only send suit if not No Trump
        };

        try {
            const res = await fetch('/api/game/finalizeBid', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            });

            const data = await res.json();

            if (res.ok) {
                setTrumpSuit(data.trumpSuit);
                setBidType(data.bidType);
                setCurrentTurnIndex(data.currentTurnIndex);
                setPhase(data.phase);         // update to KITTY or next phase
                setWinningPlayerName(data.winningPlayerName);
                setBids([]);
                setAwardKitty(true);
                closeBidTypePanel?.();
            } else {
                console.error("Finalize Bid failed:", data);
            }
        } catch (error) {
            console.error("Network error:", error);
        }
    };

    return (
     
            <div className="bidding-overlay card-play-zone grid-item center">
                <div className="bidding-panel">
                    <h2>Finalize Your Bid</h2>

                    <label style={{ display: "block", marginBottom: "10px" }}>
                        Direction:
                        <select
                            value={direction}
                            onChange={(e) => setDirection(e.target.value)}
                            className="index-input-box short-box"
                        >
                            <option value="UPTOWN">Uptown</option>
                            <option value="DOWNTOWN">Downtown</option>
                        </select>
                    </label>
                    {!isNoTrump && (
                        <label style={{ display: "block", marginBottom: "10px" }}>
                            Suit:
                            <select
                                value={suit}
                                onChange={(e) => setSuit(e.target.value)}
                                className="index-input-box short-box"
                            >
                                <option value="HEARTS">Hearts</option>
                                <option value="DIAMONDS">Diamonds</option>
                                <option value="CLUBS">Clubs</option>
                                <option value="SPADES">Spades</option>
                            </select>
                        </label>
                    )}
                    <div className="settings-actions">
                        <button className="index-button settings-button" onClick={finalizeBid}>
                            Confirm
                        </button>
                        <button className="index-button settings-button" onClick={closeBidTypePanel}>
                            Cancel
                        </button>
                    </div>
                </div>
            </div>

    );
}
