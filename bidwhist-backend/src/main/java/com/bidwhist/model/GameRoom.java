// src/main/java/com/bidwhist/model/GameRoom.java

package com.bidwhist.model;

import java.util.ArrayList;
import java.util.List;

public class GameRoom {
  private String roomId; // Unique ID for the room
  private List<Player> players; // Players in this room
  private RoomStatus status; // WAITING, READY, IN_PROGRESS, etc.
  private GameState gameState; // Optional: reference to actual game logic

  /** Constructs a new GameRoom with a given ID. */
  public GameRoom(String id) {
    this.roomId = id;
    this.players = new ArrayList<>();
    this.status = RoomStatus.WAITING_FOR_PLAYERS;
  }

  /** Checks whether the room has reached the player limit. */
  public boolean isFull() {
    return players.size() >= 4; // or make this configurable
  }

  /** Adds an already constructed player to the room. */
  public void addPlayer(Player player) {
    this.players.add(player);
  }

  /**
   * Constructs and adds a player by name, assigning position and team. Automatically switches
   * status to READY if fourth player joins.
   */
  public void addPlayer(String name) {
    if (this.players.size() >= 4) {
      throw new IllegalStateException("Cannot add more than 4 players.");
    }

    PlayerPos[] positions = PlayerPos.values();
    int index = this.players.size();

    PlayerPos position = positions[index];
    Team team = (index % 2 == 0) ? Team.A : Team.B;
    boolean isAI = name.startsWith("AI"); // or your own logic

    Player newPlayer = new Player(name, isAI, position, team);
    this.players.add(newPlayer);

    System.out.println("✅ Added player: " + name + " as " + position + " (" + team + ")");

    if (players.size() == 4) {
      this.status = RoomStatus.READY;
    }
  }

  /** Looks up a player’s position by name. */
  public PlayerPos getPlayerPositionByName(String name) {
    return this.players.stream()
        .filter(p -> p.getName().equalsIgnoreCase(name))
        .map(Player::getPosition)
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("No player found with name: " + name));
  }

  /* Getters and setters */

  public String getRoomId() {
    return roomId;
  }

  public List<Player> getPlayers() {
    return players;
  }

  public RoomStatus getStatus() {
    return status;
  }

  public void setStatus(RoomStatus status) {
    this.status = status;
  }

  public GameState getGameState() {
    return gameState;
  }
}
