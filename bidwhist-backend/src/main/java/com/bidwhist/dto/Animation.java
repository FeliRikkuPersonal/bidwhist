// src/main/java/com/bidwhist/dto/Animation.java

package com.bidwhist.dto;

import com.bidwhist.model.Book;
import com.bidwhist.model.Card;
import com.bidwhist.model.GamePhase;
import com.bidwhist.model.GameState;
import com.bidwhist.model.PlayedCard;
import com.bidwhist.model.Player;
import com.bidwhist.model.PlayerPos;
import com.bidwhist.model.Suit;
import com.bidwhist.model.Team;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.UUID;

/*
 * Represents a game animation event to be queued for players' frontends.
 * Supports animations like play, deal, collect, and card updates.
 * Each instance has a unique ID and a defined AnimationType.
 */
public class Animation {

  private final String id = UUID.randomUUID().toString();

  @JsonProperty
  private AnimationType type;
  @JsonProperty
  private PlayerPos player;
  @JsonProperty
  private Card card;
  @JsonProperty
  private List<Card> cardList;
  @JsonProperty
  private Team winningTeam;
  @JsonProperty
  private List<PlayerView> playerViews;
  @JsonProperty
  private List<Card> kittyView;
  @JsonProperty
  private Suit leadSuit;
  @JsonProperty
  private String exitingPlayerName;
  @JsonProperty
  private String message;
  @JsonProperty
  private int currentTurnIndex;
  @JsonProperty
  private int trickSize;
  @JsonProperty
  private GamePhase currentPhase;
  @JsonProperty
  private int sessionKey;
  @JsonProperty
  private List<Player> playerList;
  @JsonProperty
  private String hide;

  public Animation() {
  }

  /*
   * Constructs a PLAY animation from a PlayedCard object.
   */
  public Animation(PlayedCard playedCard, Suit leadSuit, int currentTurnIndex, int trickSize, int sessionKey, GameState game) {
    this.type = AnimationType.PLAY;
    this.card = playedCard.getCard();
    this.player = playedCard.getPlayer();
    this.leadSuit = leadSuit;
    this.currentTurnIndex = currentTurnIndex;
    this.trickSize = trickSize;
    this.sessionKey = sessionKey;
    if (game.getCompletedTricks().size() == 11 && game.getCurrentTrick().size() == 4) {
      this.hide = "hide";
    }
  }

  /*
   * Constructs a DEAL animation with the full deck of cards.
   */
  public Animation(List<Card> deck, int currentTurnIndex, int sessionKey) {
    this.type = AnimationType.DEAL;
    this.cardList = deck;
    this.currentTurnIndex = currentTurnIndex;
    this.sessionKey = sessionKey;
  }

  /*
   * Constructs a COLLECT animation from a completed trick (Book).
   */
  public Animation(Book trick, int currentTurnIndex, GamePhase currentPhase, int sessionKey) {
    this.type = AnimationType.COLLECT;
    this.cardList = trick.getCards();
    this.winningTeam = trick.getWinningTeam();
    this.currentTurnIndex = currentTurnIndex;
    this.currentPhase = currentPhase;
    this.sessionKey = sessionKey;
  }

  /*
   * Constructs an UPDATE_CARDS animation for UI sync of all hands and kitty.
   */
  public Animation(List<PlayerView> playerViews, List<Card> kittyView, int sessionKey) {
    this.type = AnimationType.UPDATE_CARDS;
    this.playerViews = playerViews;
    this.kittyView = kittyView;
    this.sessionKey = sessionKey;
  }

  /*
   * Constructs a QUIT_GAME animation.
   */
  public Animation(String playerName, int sessionKey) {
    this.type = AnimationType.QUIT_GAME;
    this.exitingPlayerName = playerName;
    this.message = playerName + " has left the game. Game Over.";
    this.sessionKey = sessionKey;
  }

  /*
   * Constructs a generic animation with the specified type only.
   */
  public Animation(AnimationType type, int sessionKey) {
    this.type = type;
    this.sessionKey = sessionKey;
  }

  public String getId() {
    return id;
  }

  public AnimationType getType() {
    return type;
  }

  public PlayerPos getPlayer() {
    return player;
  }

  public Card getCard() {
    return card;
  }

  public List<Card> getCardList() {
    return cardList;
  }

  public Team getWinningTeam() {
    return winningTeam;
  }

  public List<PlayerView> getPlayerViews() {
    return playerViews;
  }

  public List<Card> getKittyView() {
    return kittyView;
  }

  public int getSessionKey() {
    return sessionKey;
  }

  public void setType(AnimationType type) {
    this.type = type;
  }

  public void setPlayer(PlayerPos player) {
    this.player = player;
  }

  public void setCard(Card card) {
    this.card = card;
  }

  public void setCardList(List<Card> cardList) {
    this.cardList = cardList;
  }

  public void setWinningTeam(Team winningTeam) {
    this.winningTeam = winningTeam;
  }

  public void setPlayerViews(List<PlayerView> playerViews) {
    this.playerViews = playerViews;
  }

  public void setKittyView(List<Card> kittyView) {
    this.kittyView = kittyView;
  }

  public void setSessionKey(int key) {
    this.sessionKey = key;
  }
}
