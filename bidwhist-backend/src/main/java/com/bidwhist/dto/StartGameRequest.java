// src/main/java/com/bidwhist/dto/StartGameRequest.java

package com.bidwhist.dto;

import com.bidwhist.model.Difficulty;

/**
 * DTO used to initiate a new game session.
 *
 * This object is passed from the client to the server to start a game with specified player
 * information, game difficulty (optional), and a game ID. It supports both multiplayer and
 * single-player initialization formats.
 */
public class StartGameRequest {

  private String gameId;
  private String playerName;
  private Difficulty difficulty;

  /* Required for JSON deserialization (used by frameworks like Jackson) */
  public StartGameRequest() {}

  /* Constructor for multiplayer games (player joins with gameId and name) */
  public StartGameRequest(String gameId, String playerName) {
    this.gameId = gameId;
    this.playerName = playerName;
  }

  /* Constructor for single-player games (includes difficulty) */
  public StartGameRequest(String playerName, Difficulty difficulty, String gameId) {
    this.playerName = playerName;
    this.difficulty = difficulty;
    this.gameId = gameId;
  }

  public String getPlayerName() {
    return playerName;
  }

  public String getGameId() {
    return gameId;
  }

  public void setPlayerName(String playerName) {
    this.playerName = playerName;
  }

  public Difficulty getDifficulty() {
    return difficulty;
  }

  public void setDifficulty(Difficulty difficulty) {
    this.difficulty = difficulty;
  }
}
