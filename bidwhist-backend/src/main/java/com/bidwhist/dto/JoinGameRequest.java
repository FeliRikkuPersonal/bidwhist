package com.bidwhist.dto;

public class JoinGameRequest {
    
    private String playerName;
    private String gameId;

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
