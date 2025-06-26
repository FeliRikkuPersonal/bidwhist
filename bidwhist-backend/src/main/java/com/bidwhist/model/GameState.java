package com.bidwhist.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameState {

    private List<Player> players = new ArrayList<>();
    private List<Card> kitty = new ArrayList<>();
    private GamePhase phase;
    private int bidTurnIndex;
    private List<Bid> bids = new ArrayList<>();
    private Bid highestBid;
    private String winningPlayerName;
    private BidType trumpType;
    private int currentTurnIndex;

    private List<PlayedCard> currentTrick = new ArrayList<>();
    private List<List<PlayedCard>> completedTricks = new ArrayList<>();
    private int currentPlayerIndex;

    private int teamAScore = 0;
    private int teamBScore = 0;
    private int teamATricksWon = 0;
    private int teamBTricksWon = 0;

    private Map<Team, Integer> teamTrickCounts = new HashMap<>();
    private Map<Team, Integer> teamScores = new HashMap<>();

    // Getters and setters
    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public List<Card> getKitty() {
        return kitty;
    }

    public void setKitty(List<Card> kitty) {
        this.kitty = kitty;
    }

    public GamePhase getPhase() {
        return phase;
    }

    public void setPhase(GamePhase phase) {
        this.phase = phase;
    }

    public int getBidTurnIndex() {
        return bidTurnIndex;
    }

    public void setBidTurnIndex(int bidTurnIndex) {
        this.bidTurnIndex = bidTurnIndex;
    }

    public List<Bid> getBids() {
        return bids;
    }

    public void setBids(List<Bid> bids) {
        this.bids = bids;
    }

    public void addBid(Bid bid) {
        this.bids.add(bid);
    }

    public Bid getHighestBid() {
        return highestBid;
    }

    public void setHighestBid(Bid highestBid) {
        this.highestBid = highestBid;
    }

    public String getWinningPlayerName() {
        return winningPlayerName;
    }

    public void setWinningPlayerName(String winningPlayerName) {
        this.winningPlayerName = winningPlayerName;
    }

    public BidType getTrumpType() {
        return trumpType;
    }

    public void setTrumpType(BidType trumpType) {
        this.trumpType = trumpType;
    }

    public List<PlayedCard> getCurrentTrick() {
        return currentTrick;
    }

    public void setCurrentTrick(List<PlayedCard> currentTrick) {
        this.currentTrick = currentTrick;
    }

    public List<List<PlayedCard>> getCompletedTricks() {
        return completedTricks;
    }

    public void setCompletedTricks(List<List<PlayedCard>> completedTricks) {
        this.completedTricks = completedTricks;
    }

    public int getCurrentPlayerIndex() {
        return currentPlayerIndex;
    }

    public void setCurrentPlayerIndex(int currentPlayerIndex) {
        this.currentPlayerIndex = currentPlayerIndex;
    }

    public int getTeamAScore() {
        return teamAScore;
    }

    public void setTeamAScore(int teamAScore) {
        this.teamAScore = teamAScore;
    }

    public int getTeamBScore() {
        return teamBScore;
    }

    public void setTeamBScore(int teamBScore) {
        this.teamBScore = teamBScore;
    }

    public int getTeamATricksWon() {
        return teamATricksWon;
    }

    public void setTeamATricksWon(int teamATricksWon) {
        this.teamATricksWon = teamATricksWon;
    }

    public int getTeamBTricksWon() {
        return teamBTricksWon;
    }

    public void setTeamBTricksWon(int teamBTricksWon) {
        this.teamBTricksWon = teamBTricksWon;
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

    public int getCurrentTurnIndex() {
        return currentTurnIndex;
    }

    public void setCurrentTurnIndex(int currentTurnIndex) {
        this.currentTurnIndex = currentTurnIndex;
    }

}
