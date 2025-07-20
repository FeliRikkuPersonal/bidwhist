// src/components/BidZone.jsx

import { useEffect, useState } from 'react';
import { useGameState } from '../context/GameStateContext.jsx';
import { useUIDisplay } from '../context/UIDisplayContext.jsx';

import '../css/BidZone.css';

export default function BidZone() {
  const { phase, bids } = useGameState();
  const { bidPhase, kittyPhase, setKittyPhase } = useUIDisplay();

  const [showPlayerBids, setShowPlayerBids] = useState(false);

  /*
   * formatBid: Returns a human-readable string based on the player's bid.
   * If the player passed, shows "X passes". If they made a bid, shows the amount and optional "No".
   */
  function formatBid(bid) {
    if (bid.passed) return `${bid.player} passes`;
    return `${bid.player} bids ${bid.value}${bid.isNo ? ' No' : ''}`;
  }

  /*
   * useEffect: Controls when the bid history becomes visible.
   * Shows player bids if the game is in the bid or kitty phase.
   * Also triggers a side effect to mark the UI as in "kitty phase" if relevant.
   */
  useEffect(() => {
    setKittyPhase(phase === 'KITTY');
    setShowPlayerBids(bidPhase || kittyPhase);
  }, [phase, bidPhase, kittyPhase]);

  /*
   * JSX: Displays the bid zone when active.
   * Shows all recorded bids in a scrollable section.
   */
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
