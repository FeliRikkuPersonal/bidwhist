package com.bidwhist.dto;

import java.util.List;

public class StartGameRequest {
    private List<String> playerNames;

    public List<String> getPlayerNames() {
        return playerNames;
    }

    public void setPlayerNames(List<String> playerNames) {
        this.playerNames = playerNames;
    }
}
