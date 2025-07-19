package com.bidwhist.dto;

import java.util.List;
import com.bidwhist.model.Card;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class HandResponse {
    private List<PlayerView> players;
    private List<Card> kitty;

    public HandResponse(
            List<PlayerView> players, List<Card> kitty) {
        this.players = players;
        this.kitty = kitty;
    }

    public List<PlayerView> getPlayers() {
        return players;
    }

    public List<Card> getKitty() {
        return kitty;
    }

    public void setPlayers(List<PlayerView> players) {
        this.players = players;
    }

    public void setKitty(List<Card> kitty) {
        this.kitty = kitty;
    }
}