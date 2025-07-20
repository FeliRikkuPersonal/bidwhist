// src/components/BiddingPanel.jsx

import '../css/BiddingPanel.css';
import '../css/index.css';
import '../css/GameScreen.css';

import { useState, useEffect } from 'react';
import { usePositionContext } from '../context/PositionContext.jsx';
import { useGameState } from '../context/GameStateContext.jsx';
import { useUIDisplay } from '../context/UIDisplayContext.jsx';

/*
 * BiddingPanel manages the UI for players to place or pass a bid during the bidding phase.
 * It handles bid submission, phase control, and visibility based on turn order.
 */
export default function BiddingPanel({ closeBidding, onBidPlaced }) {
  const API = import.meta.env.VITE_API_URL;

  const { debugLog: logPosition, viewerPosition } = usePositionContext();
  const {
    debugLog: logGameState,
    gameId,
    bids,
    setBids,
    bidTurnIndex,
    currentTurnIndex,
    setCurrentTurnIndex,
    setFirstBidder,
    setPhase,
  } = useGameState();

  const {
    debugLog: logUI,
    bidPhase,
    setBidPhase,
    showBidding,
    setShowBidding,
    setShowFinalizeBid,
  } = useUIDisplay();

  const [bidValue, setBidValue] = useState('');
  const [isNo, setIsNo] = useState(false);

  /*
   * Updates UI visibility based on whether it's the player's turn to bid
   */
  useEffect(() => {
    if (!bidPhase || !viewerPosition || bidTurnIndex == null) return;
    if (!bidPhase || !viewerPosition || bidTurnIndex == null) return;

    const turnPlayerPos = ['P1', 'P2', 'P3', 'P4'][bidTurnIndex];
    const isMyTurn = viewerPosition === turnPlayerPos;

    setShowBidding(bidPhase && isMyTurn);
  }, [bids, bidPhase, bidTurnIndex, viewerPosition]);

  /* do not render if bidding is not visible */
  if (!showBidding) return null;

  /*
   * sendBidRequest handles the POST request to submit a bid to the backend
   */
  const sendBidRequest = async (bidBody) => {
    const res = await fetch(`${API}/game/bid`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(bidBody),
    });

    const bidData = await res.json();

    if (res.ok) {
      onBidPlaced?.(bidData);
      setBidPhase(false);
      setBids(bidData.bids);
      setPhase(bidData.phase);
      setFirstBidder(bidData.firstBidder);
      setCurrentTurnIndex(bidData.currentTurnIndex);
      closeBidding();
    } else {
      console.error('Bid failed:', bidData);
    }
  };

  /*
   * placeBid sends the current numeric bid value along with No Trump flag
   */
  const placeBid = () => {
    sendBidRequest({
      gameId,
      player: viewerPosition,
      value: parseInt(bidValue),
      isNo,
      isPassed: false,
    });
  };

  /*
   * passBid submits a pass bid (no value, not a No Trump)
   */
  const passBid = () => {
    sendBidRequest({
      gameId,
      player: viewerPosition,
      value: 0,
      no: false,
      isPassed: true,
    });
  };

  /*
   * Render the bid input panel and actions for Set, Pass, and Close
   */
  return (
    <div className="bidding-overlay  grid-item center">
      <div className="bidding-panel">
        <h2>Place Your Bid</h2>

        <input
          type="number"
          min="4"
          max="7"
          placeholder="Enter bid (4-7)"
          value={bidValue}
          onChange={(e) => setBidValue(e.target.value)}
          className="index-input-box short-box enter-bid-input"
        />

        <label>
          <input type="checkbox" checked={isNo} onChange={(e) => setIsNo(e.target.checked)} />
          No Trump
        </label>

        <div className="settings-actions">
          <button className="index-button settings-button" onClick={placeBid}>
            Set Bid
          </button>
          <button className="index-button settings-button" onClick={passBid}>
            Pass
          </button>
          <button className="index-button settings-button" onClick={closeBidding}>
            Close
          </button>
        </div>
      </div>
    </div>
  );
}
