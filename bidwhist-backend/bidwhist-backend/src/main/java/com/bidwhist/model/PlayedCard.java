package com.bidwhist.model;

public class PlayedCard {
    private final String playerName;
    private final Card card;

    public PlayedCard(String playerName, Card card) {
        this.playerName = playerName;
        this.card = card;
    }

    public String getPlayerName() {
        return playerName;
    }

    public Card getCard() {
        return card;
    }
}
