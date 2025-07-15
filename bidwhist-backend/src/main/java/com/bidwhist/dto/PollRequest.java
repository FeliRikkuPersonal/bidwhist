package com.bidwhist.dto;

import com.bidwhist.model.PlayerPos;

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
