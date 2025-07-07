package com.bidwhist.dto;

import com.bidwhist.model.Difficulty;

public class StartGameRequest {

    private String gameId;
    private String playerName;
    private Difficulty difficulty;

    // Required for JSON deserialization
    public StartGameRequest() {

    }

    // Multiplayer
    public StartGameRequest(String gameId, String playerName) {
        this.gameId = gameId;
        this.playerName = playerName;
    }

    // Single Player
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