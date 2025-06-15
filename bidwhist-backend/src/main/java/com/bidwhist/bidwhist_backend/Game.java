package com.bidwhist.bidwhist_backend;

import java.util.List;

import com.bidwhist.bidwhist_backend.model.Deck;
import com.bidwhist.bidwhist_backend.model.Player;
import com.bidwhist.bidwhist_backend.model.Table;

public class Game {
    private final Deck deck = new Deck();
    private final Table table = new Table(this, deck);
    private List<Player> players;

    public Player addPlayer(Player player) {
        players.add(player);
        return player;
    }

    public List<Player> getPlayers() {
        return players;
    }
}
