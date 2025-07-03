package com.bidwhist.dto;

import com.bidwhist.model.PlayerPos;

public class PlayerRequest {
    private String playerName;
    private PlayerPos playerPosition;

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

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }
 
    public void setPlayerPosition(PlayerPos playerPos) {
        this.playerPosition = playerPos;
    }
}
