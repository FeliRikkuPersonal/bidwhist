import React, { useState, useEffect } from 'react';
import { useGameState } from '../context/GameStateContext.jsx';
import { usePositionContext } from '../context/PositionContext.jsx';
import BidZone from './BidZone.jsx';
import '../css/ScoreBoard.css';

/**
 * Displays the score for both teams and the current winning bid.
 */
export default function Scoreboard({ phase, bids }) {
  const { teamAScore, teamBScore, winningBid } = useGameState();
  const { viewerPosition, playerTeam } = usePositionContext();

  const [myTeam, setMyTeam] = useState(0);
  const [theirTeam, setTheirTeam] = useState(0);
  const [formattedBid, setFormattedBid] = useState('');

  useEffect(() => {
    if (viewerPosition === 'P1' || viewerPosition === 'P3') {
      setMyTeam(teamAScore);
      setTheirTeam(teamBScore);
    } else {
      setMyTeam(teamBScore);
      setTheirTeam(teamAScore);
    }
  }, [viewerPosition, teamAScore, teamBScore]);

  useEffect(() => {
    if (winningBid) {
      const isNo = winningBid.isNo ? 'No' : '';
      const bidString = `${winningBid.value} ${isNo} ${winningBid.type} ${winningBid.suit}`.trim();
      const team = playerTeam[winningBid.player];
      setFormattedBid({ bidString, team });
    } else {
      setFormattedBid('');
    }
  }, [winningBid, playerTeam]);

  return (
    <div className="scoreboard">
      <>
        <div className="scoreboard-content left-content">
          <p className="team-name">Team A</p>
          <p className="score-value">{myTeam}</p>
        </div>

        <div className="center-content">
          {formattedBid ? (
            <div className="winning-bid">
              <p className="score-headers">Winning Bid</p>
              <p className="winning-team-name">Team {formattedBid.team}</p>
              <p className="wining-bid-value">{formattedBid.bidString}</p>
            </div>
          ) : (
            <>
              <BidZone phase={phase} bids={bids} />
            </>
          )}
          <div className="phase-indicator">
            <p className="phase-text">{phase[0] + phase.slice(1).toLowerCase()} Phase</p>
          </div>
        </div>

        <div className="scoreboard-content right-content">
          <p className="team-name">Team B</p>
          <p className="score-value">{theirTeam}</p>
        </div>
      </>
    </div>
  );
}
