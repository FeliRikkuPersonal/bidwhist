// src/main/java/com/bidwhist/dto/Animation.java

package com.bidwhist.dto;

import com.bidwhist.model.Book;
import com.bidwhist.model.Card;
import com.bidwhist.model.PlayedCard;
import com.bidwhist.model.PlayerPos;
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

  @JsonProperty private AnimationType type;
  @JsonProperty private PlayerPos player;
  @JsonProperty private Card card;
  @JsonProperty private List<Card> cardList;
  @JsonProperty private Team winningTeam;
  @JsonProperty private List<PlayerView> playerViews;
  @JsonProperty private List<Card> kittyView;

  public Animation() {}

  /*
   * Constructs a PLAY animation from a PlayedCard object.
   */
  public Animation(PlayedCard playedCard) {
    this.type = AnimationType.PLAY;
    this.card = playedCard.getCard();
    this.player = playedCard.getPlayer();
  }

  /*
   * Constructs a DEAL animation with the full deck of cards.
   */
  public Animation(List<Card> deck) {
    this.type = AnimationType.DEAL;
    this.cardList = deck;
  }

  /*
   * Constructs a COLLECT animation from a completed trick (Book).
   */
  public Animation(Book trick) {
    this.type = AnimationType.COLLECT;
    this.cardList = trick.getCards();
    this.winningTeam = trick.getWinningTeam();
  }

  /*
   * Constructs an UPDATE_CARDS animation for UI sync of all hands and kitty.
   */
  public Animation(List<PlayerView> playerViews, List<Card> kittyView) {
    this.type = AnimationType.UPDATE_CARDS;
    this.playerViews = playerViews;
    this.kittyView = kittyView;
  }

  /*
   * Constructs a generic animation with the specified type only.
   */
  public Animation(AnimationType type) {
    this.type = type;
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
}
