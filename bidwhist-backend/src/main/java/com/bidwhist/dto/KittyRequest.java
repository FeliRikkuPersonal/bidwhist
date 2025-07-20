// src/main/java/com/bidwhist/dto/KittyRequest.java

package com.bidwhist.dto;

import com.bidwhist.model.Card;
import com.bidwhist.model.PlayerPos;
import java.util.List;

/**
 * Represents a request to submit discarded cards to the kitty.
 *
 * <p>Used after a player has claimed the kitty and must return 6 cards back to it. Carries the
 * player identity, discarded cards, and associated game context.
 */
public class KittyRequest {
  private String gameId;
  private PlayerPos player;
  private List<Card> discards;

  public KittyRequest() {}

  public KittyRequest(PlayerPos player, List<Card> discards) {
    this.player = player;
    this.discards = discards;
  }

  public PlayerPos getPlayer() {
    return player;
  }

  public void setPlayer(PlayerPos player) {
    this.player = player;
  }

  public List<Card> getDiscards() {
    return discards;
  }

  public void setDiscards(List<Card> discards) {
    this.discards = discards;
  }

  public String getGameId() {
    return gameId;
  }
}
