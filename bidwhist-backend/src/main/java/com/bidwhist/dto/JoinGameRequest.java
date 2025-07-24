// src/main/java/com/bidwhist/dto/JoinGameRequest.java

package com.bidwhist.dto;

/*
 * Represents a request to join an existing game room.
 * Contains the player's name and the target game ID.
 */
public class JoinGameRequest {

  private String playerName;
  private String gameId;

  public JoinGameRequest(String playerName, String gameId) {
    this.playerName = playerName;
    this.gameId = gameId;
  }

  public String getPlayerName() {
    return playerName;
  }

  public void setPlayerName(String playerName) {
    this.playerName = playerName;
  }

  public String getGameId() {
    return gameId;
  }
}
