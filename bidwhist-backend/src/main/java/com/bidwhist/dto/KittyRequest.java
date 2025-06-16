package com.bidwhist.dto;

import java.util.List;

import com.bidwhist.model.Card;
import com.bidwhist.model.PlayerPos;

public class KittyRequest {
    private PlayerPos player;
    private List<Card> discards;

    public KittyRequest() {}

    public KittyRequest(PlayerPos player, List<Card> discards) {
        this.player = player;
        this.discards = discards;
    }

    public PlayerPos getPlayer() {
        return player;
    }

    public void setPlayer(PlayerPos player) {
        this.player = player;
    }

    public List<Card> getDiscards() {
        return discards;
    }

    public void setDiscards(List<Card> discards) {
        this.discards = discards;
    }
}