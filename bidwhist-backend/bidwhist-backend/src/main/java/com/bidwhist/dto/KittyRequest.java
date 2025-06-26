package com.bidwhist.dto;

import java.util.List;

import com.bidwhist.model.Card;

public class KittyRequest {
    private String player;
    private List<Card> discards;
    private String trumpSuit;

    public String getPlayer() {
        return player;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    public List<Card> getDiscards() {
        return discards;
    }

    public void setDiscards(List<Card> discards) {
        this.discards = discards;
    }

    public String getTrumpSuit() {
        return trumpSuit;
    }

    public void setTrumpSuit(String trumpSuit) {
        this.trumpSuit = trumpSuit;
    }
}
