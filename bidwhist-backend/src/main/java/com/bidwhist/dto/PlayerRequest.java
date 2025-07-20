// src/main/java/com/bidwhist/dto/PlayerRequest.java

package com.bidwhist.dto;

import com.bidwhist.model.PlayerPos;

/**
 * Represents a request payload for identifying or initializing a player in a game.
 *
 * Used in client-to-server communication when joining a game, assigning a position,
 * or identifying the player by name. Also carries the gameId for routing context.
 */
public class PlayerRequest {
  private String playerName;
  private PlayerPos playerPosition;
  private String gameId;

  public PlayerRequest() {
    // Default constructor for JSON
  }

  public PlayerRequest(String playerName) {
    this.playerName = playerName;
  }

  public String getPlayerName() {
    return playerName;
  }

  public PlayerPos getPlayerPosition() {
    return playerPosition;
  }

  public String getGameId() {
    return gameId;
  }

  public void setPlayerName(String playerName) {
    this.playerName = playerName;
  }

  public void setPlayerPosition(PlayerPos playerPos) {
    this.playerPosition = playerPos;
  }
}
