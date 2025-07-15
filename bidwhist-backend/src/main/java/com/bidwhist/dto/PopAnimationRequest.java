package com.bidwhist.dto;

import com.bidwhist.model.PlayerPos;

public class PopAnimationRequest {
    private String gameId;
    private String animationId;
    private PlayerPos player;

    public String getGameId() {
        return gameId;
    }

    public String getAnimationId() {
        return animationId;
    }

    public PlayerPos getPlayer() {
        return player;
    }

    public void setGameId(String id) {
        this.gameId = id;
    }

    public void setAnimationId(String id) {
        this.animationId = id;
    }

    public void setPlayer(PlayerPos player) {
        this.player = player;
    }
}
