// src/components/BidTypePanel.jsx

import '../css/BiddingPanel.css';
import '../css/index.css';
import '../css/GameScreen.css';
import React, { useState } from 'react';
import { usePositionContext } from '../context/PositionContext.jsx';
import { useGameState } from '../context/GameStateContext.jsx';
import { useUIDisplay } from '../context/UIDisplayContext.jsx';
import { useThrowAlert } from '../hooks/useThrowAlert.js';
import handleQuit from '../utils/handleQuit.js';

/*
 *
 * BidTypePanel handles the final bid confirmation UI.
 * It allows the winning player to select bid direction and suit (if applicable),
 * and sends the finalized bid to the backend to transition the game phase.
 *
 */
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

  const { showFinalizeBid, setShowFinalizeBid, setAwardKitty } = useUIDisplay();
  const throwAlert = useThrowAlert();

  const [direction, setDirection] = useState('UPTOWN');
  const [suit, setSuit] = useState('HEARTS');

  /* check if No Trump */
  const isNoTrump = highestBid?.no;

  /* return null if panel should not show */
  if (!showFinalizeBid) return null;

  /*
   *
   * finalizeBid sends the selected direction and suit (if not No Trump)
   * to the server, updates global game state, and closes the panel.
   *
   */
  const finalizeBid = async () => {
    const payload = {
      gameId: gameId,
      player: viewerPosition,
      type: direction,
      suit: isNoTrump ? null : suit,
    };

    const API = import.meta.env.VITE_API_URL;

    try {
      const res = await fetch(`${API}/game/finalizeBid`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload),
      });

      const data = await res.json();

      if (res.ok) {
        setTrumpSuit(data.trumpSuit);
        setBidType(data.bidType);
        setCurrentTurnIndex(data.currentTurnIndex);
        setPhase(data.phase);
        setWinningPlayerName(data.winningPlayerName);
        setBids([]);
        setAwardKitty(true);
        closeBidTypePanel?.();
        setShowFinalizeBid(false);
      } else {
        throwAlert(data, 'warning');
        console.error('Finalize Bid failed:', data);
      }
    } catch (error) {
      console.error('Network error:', error);
    }
  };

  /*
   *
   * Render the bid direction and (conditionally) suit selector.
   * Shows Confirm and Cancel buttons to finalize or abort.
   *
   */
  return (
    <div className="bidding-overlay">
      <div className="bidding-panel">
        <h2>Finalize Your Bid</h2>

        <label>
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
          <label style={{ display: 'block', marginBottom: '10px' }}>
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
          <button className="index-button settings-button" onClick={handleQuit}>
            Cancel
          </button>
        </div>
      </div>
    </div>
  );
}
