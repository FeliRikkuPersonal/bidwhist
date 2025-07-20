// src/main/java/com/bidwhist/dto/HandRequest.java

package com.bidwhist.dto;

import com.bidwhist.model.PlayerPos;

/*
 * Represents a request for retrieving a player's hand from the game state.
 * This is typically used after cards have been dealt, played, or reassigned.
 */
public class HandRequest {
  private String gameId;
  private PlayerPos player;

  public HandRequest() {}

  public HandRequest(String gameId, PlayerPos player) {
    this.player = player;
    this.gameId = gameId;
  }

  public PlayerPos getPlayer() {
    return player;
  }

  public void setPlayer(PlayerPos player) {
    this.player = player;
  }

  public void setGameId(String gameId) {
    this.gameId = gameId;
  }

  public String getGameId() {
    return gameId;
  }
}
