import { useState, useEffect } from "react";
import { useGameState } from "../context/GameStateContext.jsx";
import { usePositionContext } from "../context/PositionContext.jsx";
import BidZone from "./BidZone.jsx";
import "../css/ScoreBoard.css";

export default function Scoreboard({ bidType }) {
  const { teamAScore, teamBScore, winningBid } = useGameState();
  const { viewerPosition } = usePositionContext();

  const [myTeam, setMyTeam] = useState(0);
  const [theirTeam, setTheirTeam] = useState(0);
  const [formattedBid, setFormattedBid] = useState("");

  // Who's on which team
  useEffect(() => {
    if (viewerPosition === "P1" || viewerPosition === "P3") {
      setMyTeam(teamAScore);
      setTheirTeam(teamBScore);
    } else {
      setMyTeam(teamBScore);
      setTheirTeam(teamAScore);
    }
  }, [viewerPosition, teamAScore, teamBScore]);

  // Format bid when it changes
  useEffect(() => {
    if (winningBid) {
      const isNo = winningBid.isNo ? "-No" : "";
      const suit = winningBid.suit ?? "";
      const type = winningBid.type ?? "";
      const value = winningBid.value ?? "";

      setFormattedBid(`${value}${isNo} ${type} ${suit}`.trim());
    } else {
      setFormattedBid("");
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
    </div>
  );
}
