// src/main/java/com/bidwhist/model/PlayedCard.java

package com.bidwhist.model;

/*
 * Represents a card played in a trick,
 * including which player played it and what card was played.
 */
public class PlayedCard {
  private final PlayerPos player;
  private final Card card;

  public PlayedCard(PlayerPos player, Card card) {
    this.player = player;
    this.card = card;
  }

  public PlayerPos getPlayer() {
    return player;
  }

  public Card getCard() {
    return card;
  }
}
