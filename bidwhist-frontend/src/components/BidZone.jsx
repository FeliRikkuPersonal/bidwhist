import React from 'react';
import '../css/BidZone.css';

/**
 * BidZone displays all player bids if the phase is BIDDING or KITTY.
 */
export default function BidZone({ phase, bids }) {
  const showPlayerBids = phase === 'BIDDING' || phase === 'KITTY';

  function formatBid(bid) {
    if (bid.passed) return `${bid.player} passes`;
    return `${bid.player} bids ${bid.value}${bid.isNo ? ' No' : ''}`;
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
