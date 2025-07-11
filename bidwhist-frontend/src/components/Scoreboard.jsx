import { useEffect } from "react";
import { useGameState } from "../context/GameStateContext.jsx";
import { usePositionContext } from "../context/PositionContext.jsx";

export default function Scoreboard() {
    const { teamAScore, teamBScore } = useGameState();
    const { viewerPosition } = usePositionContext();
    let myTeam;
    let theirTeam;


    useEffect(() => {
    const myTeam = viewerPosition === 'P1' || viewerPosition === 'P3' ? teamAScore : teamBScore;
    const theirTeam = viewerPosition === 'P1' || viewerPosition === 'P3' ? teamBScore : teamAScore;

    }, [viewerPosition, teamAScore, teamBScore])

    return (
        <div className="scoreboard">
            <h4>Scoreboard</h4>
            <p>Team: {myTeam}</p>
            <p>Opponents: {theirTeam}</p>
            
        </div>
    );
}
