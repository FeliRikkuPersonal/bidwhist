// src/main/java/com/bidwhist/dto/PollRequest.java

package com.bidwhist.dto;

import com.bidwhist.model.PlayerPos;

/**
 * DTO for polling the current game state.
 *
 * Typically used by the frontend to ask the server for updates based on the player's position
 * and associated game ID. Helps keep client state synchronized during multiplayer sessions.
 */
public class PollRequest {
  private PlayerPos player;
  private String gameId;

  public PlayerPos getPlayer() {
    return player;
  }

  public String getGameId() {
    return gameId;
  }
}
