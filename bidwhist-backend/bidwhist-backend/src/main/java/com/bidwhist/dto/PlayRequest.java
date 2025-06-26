package com.bidwhist.dto;

import com.bidwhist.model.Card;

public class PlayRequest {
    private String player;
    private Card card;

    public String getPlayer() {
        return player;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    public Card getCard() {
        return card;
    }

    public void setCard(Card card) {
        this.card = card;
    }
}
