package com.bidwhist.bidwhist_backend.model;

import java.util.List;

import com.bidwhist.bidwhist_backend.Game;

public class Table {
    private Game game;
    private Deck deck;
    private List<Player> turnOrder;
    public Book currentPlay;

    
    public Table(Game game, Deck deck) {
        this.game = game;
        this.deck = deck;
        this.turnOrder = game.getPlayers();
    }

    public void rotateTurnOrderToStartWith(Player firstPlayer) {
        while (turnOrder.get(0) != firstPlayer) {
            Player p = turnOrder.remove(0);
            turnOrder.add(p);
    }
}
    
}
