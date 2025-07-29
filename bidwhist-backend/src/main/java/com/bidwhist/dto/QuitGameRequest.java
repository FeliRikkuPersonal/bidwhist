// src/main/java/com/bidwhist/dto/QuitGameRequest.java

package com.bidwhist.dto;

import com.bidwhist.model.PlayerPos;

/**
 * DTO for quiting the current game state.
 */
public class QuitGameRequest {
  private PlayerPos player;
  private String gameId;
  private String mode;
  private int sessionKey;

  public PlayerPos getPlayer() {
    return player;
  }

  public String getGameId() {
    return gameId;
  }

  public String getMode() {
    return mode;
  }

  public int getSessionKey() {
    return sessionKey;
  }
}