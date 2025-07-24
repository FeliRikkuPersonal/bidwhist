// src/components/Scoreboard.jsx

import { useState, useEffect } from 'react';
import { useGameState } from '../context/GameStateContext.jsx';
import { usePositionContext } from '../context/PositionContext.jsx';
import BidZone from './BidZone.jsx';
import '../css/ScoreBoard.css';

/**
 * Displays the score for both teams and the current winning bid.
 * Also conditionally renders the BidZone if a bidType is active.
 *
 * @param {string} bidType - The type of bidding phase (used to show BidZone)
 * @returns {JSX.Element} Scoreboard UI
 */
export default function Scoreboard({ bidType,phase }) {
  const { teamAScore, teamBScore, winningBid } = useGameState();
  const { viewerPosition, playerTeam } = usePositionContext();

  const [myTeam, setMyTeam] = useState(0);
  const [theirTeam, setTheirTeam] = useState(0);
  const [formattedBid, setFormattedBid] = useState('');

  /**
   * Determine which team the viewer is on based on their position.
   * P1/P3 are always Team A; P2/P4 are Team B.
   */
  useEffect(() => {
    if (viewerPosition === 'P1' || viewerPosition === 'P3') {
      setMyTeam(teamAScore);
      setTheirTeam(teamBScore);
    } else {
      setMyTeam(teamBScore);
      setTheirTeam(teamAScore);
    }
  }, [viewerPosition, teamAScore, teamBScore]);

  /**
   * Builds a readable version of the winning bid for display.
   */
  useEffect(() => {
    if (winningBid) {
      const isNo = winningBid.isNo ? '-No' : '';
      const suit = winningBid.suit ?? '';
      const type = winningBid.type ?? '';
      const value = winningBid.value ?? '';
      const team = playerTeam[winningBid.player];

      setFormattedBid(`Team ${team} / ${value}${isNo} ${type} ${suit}`.trim());
    } else {
      setFormattedBid('');
    }
  }, [winningBid]);

  return (
    <div className="scoreboard">
      <div className="scoreboard-content">
        <p className="score-headers">Team</p>
        <p className="score-value">{myTeam}</p>
        <p className="score-headers">Opponents</p>
        <p className="score-value">{theirTeam}</p>
        {formattedBid && (
          <>
            <p className="score-headers">Winning Bid</p>
            <p className="score-value">{formattedBid}</p>
          </>
        )}
      </div>
      {bidType && <BidZone />}
      {String(phase[0]).toUpperCase() + String(phase).slice(1).toLowerCase() + " " + "Phase"}
    </div>
  );
}
