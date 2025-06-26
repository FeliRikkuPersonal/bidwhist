package com.bidwhist.dto;

import java.util.List;

import com.bidwhist.model.Card;
import com.bidwhist.model.Team;

public class PlayerView {
    private final String name;
    private final int seatIndex;
    private final Team team;
    private final boolean isAI;
    private final List<Card> hand;

    public PlayerView(String name, int seatIndex, Team team, boolean isAI, List<Card> hand) {
        this.name = name;
        this.seatIndex = seatIndex;
        this.team = team;
        this.isAI = isAI;
        this.hand = hand;
    }

    public String getName() {
        return name;
    }

    public int getSeatIndex() {
        return seatIndex;
    }

    public Team getTeam() {
        return team;
    }

    public boolean isAI() {
        return isAI;
    }

    public List<Card> getHand() {
        return hand;
    }
}
