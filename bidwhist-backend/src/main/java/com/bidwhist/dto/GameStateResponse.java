// src/main/java/com/bidwhist/dto/GameStateResponse.java

package com.bidwhist.dto;

import com.bidwhist.bidding.BidType;
import com.bidwhist.bidding.FinalBid;
import com.bidwhist.bidding.InitialBid;
import com.bidwhist.model.Card;
import com.bidwhist.model.GamePhase;
import com.bidwhist.model.PlayerPos;
import com.bidwhist.model.Suit;
import com.bidwhist.model.Team;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

/*
 * DTO representing the full game state sent to a player after polling or initialization.
 * This includes cards, current phase, bids, trick scores, animations, and metadata
 * like viewer position, team, and player identity.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GameStateResponse {
  private String gameId;
  private List<PlayerView> players;
  private List<Card> kitty;
  private int currentTurnIndex;
  private GamePhase phase;
  private Suit trumpSuit;
  private BidType bidType;
  private String winningPlayerName;
  private InitialBid highestBid;
  private List<Card> shuffledDeck;
  private PlayerPos playerPosition;
  private Team playerTeam;
  private String viewerName;
  private PlayerPos firstBidder;
  private int bidTurnIndex;
  private List<InitialBid> bids;
  private FinalBid winningFinalBid;
  private int lobbySize;
  List<Animation> animationQueue;
  private PlayerPos bidWinnerPos;
  private int teamAScore = 0;
  private int teamBScore = 0;
  private int teamATricksWon = 0;
  private int teamBTricksWon = 0;

  public GameStateResponse(
      List<Animation> animationQueue,
      List<PlayerView> players,
      List<Card> kitty,
      int currentTurnIndex,
      GamePhase phase,
      List<Card> shuffledDeck,
      PlayerPos playerPosition,
      PlayerPos firstBidder,
      int bidTurnIndex) {
    this.animationQueue = animationQueue;
    this.players = players;
    this.kitty = kitty;
    this.currentTurnIndex = currentTurnIndex;
    this.phase = phase;
    this.shuffledDeck = shuffledDeck;
    this.playerPosition = playerPosition;
    this.firstBidder = firstBidder;
    this.bidTurnIndex = bidTurnIndex;
  }

  public void setShuffledDeck(List<Card> shuffledDeck) {
    this.shuffledDeck = shuffledDeck;
  }

  public List<PlayerView> getPlayers() {
    return players;
  }

  public PlayerPos getPlayerPosition() {
    return playerPosition;
  }

  public Team getPlayerTeam() {
    return playerTeam;
  }

  public String getViewerName() {
    return viewerName;
  }

  public List<Card> getKitty() {
    return kitty;
  }

  public List<Card> getShuffledDeck() {
    return shuffledDeck;
  }

  public int getCurrentTurnIndex() {
    return currentTurnIndex;
  }

  public GamePhase getPhase() {
    return phase;
  }

  public int getTeamAScore() {
    return teamAScore;
  }

  public int getTeamBScore() {
    return teamBScore;
  }

  public int getTeamATricksWon() {
    return teamATricksWon;
  }

  public int getTeamBTricksWon() {
    return teamBTricksWon;
  }

  public void setTeamAScore(int score) {
    this.teamAScore = score;
  }

  public void setTeamBScore(int score) {
    this.teamBScore = score;
  }

  public void setTeamATricksWon(int tricks) {
    this.teamATricksWon = tricks;
  }

  public void setTeamBTricksWon(int tricks) {
    this.teamBTricksWon = tricks;
  }

  public void setBids(List<InitialBid> bids) {
    this.bids = bids;
  }

  public Suit getTrumpSuit() {
    return trumpSuit;
  }

  public PlayerPos getFirstBidder() {
    return firstBidder;
  }

  public int getBidTurnIndex() {
    return bidTurnIndex;
  }

  public List<InitialBid> getBids() {
    return bids;
  }

  public String getWinningPlayerName() {
    return winningPlayerName;
  }

  public FinalBid getWinningBid() {
    return winningFinalBid;
  }

  public void setWinningPlayerName(String winningPlayerName) {
    this.winningPlayerName = winningPlayerName;
  }

  public InitialBid getHighestBid() {
    return highestBid;
  }

  public BidType getBidType() {
    return bidType;
  }

  public int getLobbySize() {
    return lobbySize;
  }

  public List<Animation> getAnimationQueue() {
    return animationQueue;
  }

  public String getGameId() {
    return gameId;
  }

  public void setHighestBid(InitialBid highestBid) {
    this.highestBid = highestBid;
  }

  public void setPlayers(List<PlayerView> players) {
    this.players = players;
  }

  public void setPlayerName(String name) {
    this.viewerName = name;
  }

  public void setPlayerPosition(PlayerPos playerPosition) {
    this.playerPosition = playerPosition;
  }

  public void setPlayerTeam(Team team) {
    this.playerTeam = team;
  }

  public void setViewerName(String viewerName) {
    this.viewerName = viewerName;
  }

  public void setKitty(List<Card> kitty) {
    this.kitty = kitty;
  }

  public void setCurrentTurnIndex(int currentTurnIndex) {
    this.currentTurnIndex = currentTurnIndex;
  }

  public void setPhase(GamePhase phase) {
    this.phase = phase;
  }

  public void setTrumpSuit(Suit trumpSuit) {
    this.trumpSuit = trumpSuit;
  }

  public void setBidType(BidType newBidType) {
    bidType = newBidType;
  }

  public void setWinningBid(FinalBid winningBid) {
    this.winningFinalBid = winningBid;
  }

  public void setLobbySize(int size) {
    this.lobbySize = size;
  }

  public void setAnimationQueue(List<Animation> queue) {
    this.animationQueue = queue;
  }

  public PlayerPos getBidWinnerPos() {
    return bidWinnerPos;
  }

  public void setBidWinnerPos(PlayerPos winnerPos) {
    this.bidWinnerPos = winnerPos;
  }

  public void setGameId(String id) {
    this.gameId = id;
  }

}
