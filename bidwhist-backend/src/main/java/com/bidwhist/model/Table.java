 /* package com.bidwhist.model;

import java.util.List;

import com.bidwhist.service.GameService;

public class Table {
    private GameService game;
    private Deck deck;
    private List<Player> turnOrder;
    public Book currentPlay;

    
    public Table(GameService game, Deck deck) {
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
    
} */
