import React from 'react';
import { useGameState } from '../context/GameStateContext';
import { usePositionContext } from '../context/PositionContext';
import '../css/BidZone.css';

/**
 * BidZone displays all player bids if the phase is BIDDING or KITTY.
 */
export default function BidZone() {
  const { phase, bids } = useGameState(); // use context directly
  const showPlayerBids = phase === 'BID' || phase === 'KITTY';
  const { getNameFromPosition } = usePositionContext();

  function formatBid(bid) {
    const name = getNameFromPosition(bid.player);
    if (bid.passed) return `${bid.player} passes`;
    return `${name} bids ${bid.value}${bid.isNo ? ' No' : ''}`;
  }

  return (
    <div className="bid-zone">
      {showPlayerBids && (
        <div className="player-bids">
          <p className="player-bid-header">Player Bids</p>
          <div className="bidding-history">
            {bids && bids.length > 0 ? (
              bids.map((bid, index) => <div key={index}>{formatBid(bid)}</div>)
            ) : (
              <p>No bids yet.</p>
            )}
          </div>
        </div>
      )}
    </div>
  );
}
