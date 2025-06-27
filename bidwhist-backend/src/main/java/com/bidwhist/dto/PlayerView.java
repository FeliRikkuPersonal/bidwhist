package com.bidwhist.dto;

import java.util.List;

import com.bidwhist.model.Team;
import com.bidwhist.model.PlayerPos;
import com.bidwhist.model.Card;

public class PlayerView {
    private final String name;
    private final PlayerPos position;
    private final Team team;
    private final boolean isAI;
    private final List<Card> hand;

    public PlayerView(String name, PlayerPos position, Team team, boolean isAI, List<Card> hand) {
        this.name = name;
        this.position = position;
        this.team = team;
        this.isAI = isAI;
        this.hand = hand;
    }

    // Getters
    public String getName() {
        return name;
    }

    public PlayerPos getPosition() {
        return position;
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
