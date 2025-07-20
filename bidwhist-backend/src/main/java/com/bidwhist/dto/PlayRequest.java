// src/main/java/com/bidwhist/dto/PlayRequest.java

package com.bidwhist.dto;

import com.bidwhist.model.Card;
import com.bidwhist.model.PlayerPos;

/**
 * DTO for submitting a card play action during the game.
 *
 * <p>This request is sent from the frontend when a player attempts to play a card during their
 * turn. It includes the game ID, the player’s position, and the card they wish to play.
 */
public class PlayRequest {
  private String gameId;
  private PlayerPos player;
  private Card card;

  public PlayerPos getPlayer() {
    return player;
  }

  public void setPlayer(PlayerPos player) {
    this.player = player;
  }

  public Card getCard() {
    return card;
  }

  public void setCard(Card card) {
    this.card = card;
  }

  public String getGameId() {
    return gameId;
  }
}
