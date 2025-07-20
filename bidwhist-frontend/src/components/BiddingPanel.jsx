import "../css/BiddingPanel.css";
import "../css/index.css";
import "../css/GameScreen.css";
import { useState, useEffect } from "react";
import { usePositionContext } from "../context/PositionContext.jsx";
import { useGameState } from "../context/GameStateContext.jsx";
import { useUIDisplay } from "../context/UIDisplayContext.jsx";

export default function BiddingPanel({ closeBidding, onBidPlaced }) {
  const API = import.meta.env.VITE_API_URL;
  const { debugLog: logPosition, viewerPosition } = usePositionContext();
  const {
    debugLog: logGameState,
    gameId,
    bids,
    setBids,
    bidTurnIndex, currentTurnIndex,
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

  const [bidValue, setBidValue] = useState("");
  const [isNo, setIsNo] = useState(false);

  useEffect(() => {
    if (!bidPhase || !viewerPosition || bidTurnIndex == null) return;

    const turnPlayerPos = ["P1", "P2", "P3", "P4"][bidTurnIndex];
    const isMyTurn = viewerPosition === turnPlayerPos;

        setShowBidding(bidPhase && isMyTurn);
    }, [bids, bidPhase, bidTurnIndex, viewerPosition]);

  if (!showBidding) return null;

  const sendBidRequest = async (bidBody) => {
    const res = await fetch(`${API}/game/bid`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
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
      console.error("Bid failed:", bidData);
    }
  };

  const placeBid = () => {
    sendBidRequest({
      gameId,
      player: viewerPosition,
      value: parseInt(bidValue),
      isNo,
      isPassed: false,
    });
  };

  const passBid = () => {
    sendBidRequest({
      gameId,
      player: viewerPosition,
      value: 0,
      no: false,
      isPassed: true,
    });
  };

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
          <input
            type="checkbox"
            checked={isNo}
            onChange={(e) => setIsNo(e.target.checked)}
          />
          No Trump
        </label>

        <div className="settings-actions">
          <button className="index-button settings-button" onClick={placeBid}>
            Set Bid
          </button>
          <button className="index-button settings-button" onClick={passBid}>
            Pass
          </button>
          <button
            className="index-button settings-button"
            onClick={closeBidding}
          >
            Close
          </button>
        </div>
      </div>
    </div>
  );
}
