package com.bidwhist.model;

public class PlayedCard {
    private final PlayerPos player;
    private final Card card;

    public PlayedCard(PlayerPos player, Card card) {
        this.player = player;
        this.card = card;
    }

    public PlayerPos getPlayer() {
        return player;
    }

    public Card getCard() {
        return card;
    }
}
