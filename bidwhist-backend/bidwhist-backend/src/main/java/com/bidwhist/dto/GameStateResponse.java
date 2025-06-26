package com.bidwhist.dto;

import java.util.List;
import java.util.Map;

import com.bidwhist.model.Bid;
import com.bidwhist.model.BidType;
import com.bidwhist.model.Card;
import com.bidwhist.model.GamePhase;
import com.bidwhist.model.Team;

public class GameStateResponse {

    private List<PlayerView> players;
    private List<Card> kitty;
    private int currentTurnIndex;
    private GamePhase phase;
    private BidType trumpType;
    private String winningPlayerName;
    private Bid highestBid;
    private Map<Team, Integer> teamTrickCounts;
    private Map<Team, Integer> teamScores;

    public GameStateResponse(
            List<PlayerView> players,
            List<Card> kitty,
            int currentTurnIndex,
            GamePhase phase,
            BidType trumpType
    ) {
        this.players = players;
        this.kitty = kitty;
        this.currentTurnIndex = currentTurnIndex;
        this.phase = phase;
        this.trumpType = trumpType;
    }

    // Getters and Setters
    public List<PlayerView> getPlayers() {
        return players;
    }

    public void setPlayers(List<PlayerView> players) {
        this.players = players;
    }

    public List<Card> getKitty() {
        return kitty;
    }

    public void setKitty(List<Card> kitty) {
        this.kitty = kitty;
    }

    public int getCurrentTurnIndex() {
        return currentTurnIndex;
    }

    public void setCurrentTurnIndex(int currentTurnIndex) {
        this.currentTurnIndex = currentTurnIndex;
    }

    public GamePhase getPhase() {
        return phase;
    }

    public void setPhase(GamePhase phase) {
        this.phase = phase;
    }

    public BidType getTrumpType() {
        return trumpType;
    }

    public void setTrumpType(BidType trumpType) {
        this.trumpType = trumpType;
    }

    public String getWinningPlayerName() {
        return winningPlayerName;
    }

    public void setWinningPlayerName(String winningPlayerName) {
        this.winningPlayerName = winningPlayerName;
    }

    public Bid getHighestBid() {
        return highestBid;
    }

    public void setHighestBid(Bid highestBid) {
        this.highestBid = highestBid;
    }

    public Map<Team, Integer> getTeamTrickCounts() {
        return teamTrickCounts;
    }

    public void setTeamTrickCounts(Map<Team, Integer> teamTrickCounts) {
        this.teamTrickCounts = teamTrickCounts;
    }

    public Map<Team, Integer> getTeamScores() {
        return teamScores;
    }

    public void setTeamScores(Map<Team, Integer> teamScores) {
        this.teamScores = teamScores;
    }
}
