// src/components/BidModal.jsx
import React, { useState } from 'react';
import '../css/BidModal.css';

export default function BidModal({ onSubmit, onClose }) {
  const [bidNumber, setBidNumber] = useState(null);
  const [noTrump, setNoTrump] = useState(false);

  const handleSubmit = () => {
    if (bidNumber) {
      onSubmit({ bid: bidNumber, no: noTrump });
      onClose();
    }
  };

  return (
    <div className="bid-modal-overlay">
      <div className="bid-modal">
        <h3>Select Your Bid</h3>
        <div className="bid-options">
          {[4, 5, 6, 7].map(num => (
            <button
              key={num}
              className={bidNumber === num ? 'selected' : ''}
              onClick={() => setBidNumber(num)}
            >
              {num}
            </button>
          ))}
        </div>
        <div className="no-toggle">
          <label>
            <input
              type="checkbox"
              checked={noTrump}
              onChange={(e) => setNoTrump(e.target.checked)}
            />
            No Trump
          </label>
        </div>
        <button onClick={handleSubmit} disabled={!bidNumber}>Submit Bid</button>
      </div>
    </div>
  );
}
