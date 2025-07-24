// src/main/java/com/bidwhist/model/GameState.java

package com.bidwhist.model;

import com.bidwhist.bidding.BidType;
import com.bidwhist.bidding.FinalBid;
import com.bidwhist.bidding.InitialBid;
import com.bidwhist.dto.Animation;
import com.bidwhist.service.DeckService;
import com.bidwhist.utils.PlayerUtils;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameState {

  private final String gameId;
  private final List<Player> players;
  private final Deck deck;
  private List<Card> kitty;
  private int currentTurnIndex;
  private GamePhase phase;
  private Suit trumpSuit;
  private final List<InitialBid> bids;
  private int bidTurnIndex;
  private InitialBid highestBid;
  private BidType trumpType;
  private Map<PlayerPos, FinalBid> finalBidCache = new HashMap<>();
  private FinalBid winningBid;
  private String winningPlayerName;
  private List<Card> shuffledDeck;
  private PlayerPos firstBidder;
  private Difficulty difficulty;
  private GameRoom room;
  private Map<PlayerPos, List<Animation>> animationList = new EnumMap<>(PlayerPos.class);
  private PlayerPos bidWinnerPos;

  private List<PlayedCard> currentTrick = new ArrayList<>();
  private List<Book> completedTricks = new ArrayList<>();
  private Suit leadSuit;
  private final List<Card> playedCards = new ArrayList<>();
  private int currentPlayerIndex;

  private int teamAScore = 0;
  private int teamBScore = 0;
  private int teamATricksWon = 0;
  private int teamBTricksWon = 0;

  private Map<Team, Integer> teamTrickCounts = new HashMap<>();
  private Map<Team, Integer> teamScores = new HashMap<>();

  public GameState(String gameId) {
    this.room = new GameRoom(gameId);
    this.gameId = gameId;
    this.players = new ArrayList<>();
    this.deck = DeckService.createNewDeck();
    this.kitty = new ArrayList<>();
    this.currentTurnIndex = 0;
    this.phase = GamePhase.DEAL;
    this.trumpSuit = null;
    this.bids = new ArrayList<>();
    this.bidTurnIndex = 0;
    this.highestBid = null;
    this.winningPlayerName = null;
    this.shuffledDeck = deck.getCards();

    for (PlayerPos pos : PlayerPos.values()) {
      animationList.put(pos, new ArrayList<>());
    }
  }

  public void addAnimation(Animation animation) {
    if (animationList == null) return;
    for (List<Animation> queue : animationList.values()) {
      queue.add(animation);
    }
  }

  public boolean removeAnimationById(PlayerPos player, String animationId) {
    List<Animation> animations = animationList.get(player);
    if (animations == null) return false;
    return animations.removeIf(
        animation ->
            animationId != null
                && animation.getId() != null
                && animationId.trim().equalsIgnoreCase(animation.getId().trim()));
  }

  public void addPlayedCard(Card card) {
    if (card != null) {
      playedCards.add(card);
    }
  }

  public Team getTeamByPlayerPos(List<Player> players, PlayerPos playerPos) {
    for (Player p : players) {
      if (p.getPosition().equals(playerPos)) return p.getTeam();
    }
    return null;
  }

  // Player and Room
  public List<Player> getPlayers() {
    return players;
  }

  public void addPlayer(Player player) {
    players.add(player);
  }

  public GameRoom getRoom() {
    return room;
  }

  // Game ID and Deck
  public String getGameId() {
    return gameId;
  }

  public Deck getDeck() {
    return deck;
  }

  public List<Card> getShuffledDeck() {
    return shuffledDeck;
  }

  public void setShuffledDeck(List<Card> shuffledDeck) {
    this.shuffledDeck = shuffledDeck;
  }

  // Game Phase and Turn
  public GamePhase getPhase() {
    return phase;
  }

  public void setPhase(GamePhase phase) {
    this.phase = phase;
  }

  public int getCurrentTurnIndex() {
    return currentTurnIndex;
  }

  public void setCurrentTurnIndex(int index) {
    this.currentTurnIndex = index;
  }

  public int getCurrentPlayerIndex() {
    return currentPlayerIndex;
  }

  public void setCurrentPlayerIndex(int currentPlayerIndex) {
    this.currentPlayerIndex = currentPlayerIndex;
  }

  // Kitty
  public List<Card> getKitty() {
    return kitty;
  }

  public void setKitty(List<Card> kitty) {
    this.kitty = kitty;
  }

  // Trump and Bid Info
  public BidType getTrumpType() {
    return trumpType;
  }

  public void setTrumpType(BidType trumpType) {
    this.trumpType = trumpType;
  }

  public Suit getTrumpSuit() {
    return trumpSuit;
  }

  public void setTrumpSuit(Suit trumpSuit) {
    this.trumpSuit = trumpSuit;
  }

  public PlayerPos getFirstBidder() {
    return firstBidder;
  }

  public void setFirstBidder(PlayerPos firstBidder) {
    this.firstBidder = firstBidder;
  }

  public List<InitialBid> getBids() {
    return bids;
  }

  public void addBid(InitialBid bid) {
    bids.add(bid);
  }

  public int getBidTurnIndex() {
    return bidTurnIndex;
  }

  public void setBidTurnIndex(int bidTurnIndex) {
    this.bidTurnIndex = bidTurnIndex;
  }

  public InitialBid getHighestBid() {
    return highestBid;
  }

  public void setHighestBid(InitialBid highestBid) {
    this.highestBid = highestBid;
  }

  public FinalBid getWinningBid() {
    return winningBid;
  }

  public void setWinningBid(FinalBid winningBid) {
    this.winningBid = winningBid;
  }

  public void setWinningBidStats(FinalBid bid) {
    this.winningBid = bid;
    this.winningPlayerName = PlayerUtils.getNameByPosition(bid.getPlayer(), players);
    this.trumpSuit = bid.getSuit();
  }

  public BidType getFinalBidType() {
    return (winningBid != null) ? winningBid.getType() : null;
  }

  public PlayerPos getBidWinnerPos() {
    return bidWinnerPos;
  }

  public void setBidWinnerPos(PlayerPos winnerPos) {
    this.bidWinnerPos = winnerPos;
  }

  public PlayerPos getWinningPlayerPos() {
    return winningBid.getPlayer();
  }

  public String getWinningPlayerName() {
    return winningPlayerName;
  }

  public void setWinningPlayerName(String winningPlayerName) {
    this.winningPlayerName = winningPlayerName;
  }

  public Map<PlayerPos, FinalBid> getFinalBidCache() {
    return finalBidCache;
  }

  // Animations
  public Map<PlayerPos, List<Animation>> getAnimationList() {
    return animationList;
  }

  public void setAnimationList(Map<PlayerPos, List<Animation>> animationList) {
    this.animationList = animationList;
  }

  // Trick and Book
  public List<PlayedCard> getCurrentTrick() {
    return currentTrick;
  }

  public void setCurrentTrick(List<PlayedCard> currentTrick) {
    this.currentTrick = currentTrick;
  }

  public Suit getLeadSuit() {
    return leadSuit;
  }

  public void setLeadSuit(Suit suit) {
    this.leadSuit = suit;
  }

  public List<Book> getCompletedTricks() {
    return completedTricks;
  }

  public void setCompletedTricks(List<Book> completedTricks) {
    this.completedTricks = completedTricks;
  }

  public List<Card> getCompletedCards() {
    List<Card> completed = new ArrayList<>();
    for (Book book : completedTricks) {
      for (PlayedCard pc : book.getPlayedCards()) {
        if (pc.getCard() != null) {
          completed.add(pc.getCard());
        }
      }
    }
    return completed;
  }

  public List<Card> getPlayedCards() {
    return playedCards;
  }

  // Difficulty
  public Difficulty getDifficulty() {
    return difficulty;
  }

  public void setDifficulty(Difficulty difficulty) {
    this.difficulty = difficulty;
  }

  // Team Scores and Tricks
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
}
