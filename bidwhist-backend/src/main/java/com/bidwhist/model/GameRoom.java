package com.bidwhist.model;

import java.util.ArrayList;
import java.util.List;

import com.bidwhist.model.Player;;

public class GameRoom {
    private String roomId; // Unique ID for the room
    private List<Player> players; // Players in this room
    private RoomStatus status; // WAITING, READY, IN_PROGRESS, etc.
    private GameState gameState; // Optional: reference to actual game logic

    public GameRoom(String id) {
        this.roomId = id;
        this.players = new ArrayList<>();
        this.status = RoomStatus.WAITING_FOR_PLAYERS;
    }

    public boolean isFull() {
        return players.size() >= 4; // or make this configurable
    }

    public void addPlayer(Player player) {
        this.players.add(player);
    }

    // For Multiplayer player creation and assignment
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

    public void setStatus(RoomStatus status) {
        this.status = status;
    }

    public String getRoomId() {
        return roomId;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public PlayerPos getPlayerPositionByName(String name) {
        return this.players.stream()
                .filter(p -> p.getName().equalsIgnoreCase(name))
                .map(Player::getPosition)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No player found with name: " + name));
    }

    public RoomStatus getStatus() {
        return status;
    }

    public GameState getGameState() {
        return gameState;
    }
}
