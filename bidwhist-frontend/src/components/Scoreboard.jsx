import { useState, useEffect } from "react";
import { useGameState } from "../context/GameStateContext.jsx";
import { usePositionContext } from "../context/PositionContext.jsx";

export default function Scoreboard() {
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
      <h4>Scoreboard</h4>
      <p>Team: {myTeam}</p>
      <p>Opponents: {theirTeam}</p>
      {formattedBid && <p>Winning Bid: {formattedBid}</p>}
    </div>
  );
}
