package com.bidwhist.dto;

public class PlayerRequest {
    private String playerName;

    public PlayerRequest() {
        // Default constructor for JSON
    }

    public PlayerRequest(String playerName) {
        this.playerName = playerName;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setplayerName(String playerName) {
        this.playerName = playerName;
    }
}
