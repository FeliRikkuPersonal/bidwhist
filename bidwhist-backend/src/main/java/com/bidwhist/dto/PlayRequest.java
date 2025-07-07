package com.bidwhist.dto;

import com.bidwhist.model.Card;
import com.bidwhist.model.PlayerPos;

public class PlayRequest {
    private String gameId;
    private PlayerPos player;
    private Card card;

    public PlayerPos getPlayer() {
        return player;
    }

    public void setPlayer(PlayerPos player) {
        this.player = player;
    }

    public Card getCard() {
        return card;
    }

    public void setCard(Card card) {
        this.card = card;
    }

    public String getGameId() {
        return gameId;
    }
}
