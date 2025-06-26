package com.bidwhist.model;

import java.util.ArrayList;
import java.util.List;

public class Player {
    private final String name;
    private List<Card> hand;
    private Team team;
    private int seatIndex;
    private boolean isAI;

    public Player(String name, List<Card> hand, Team team, int seatIndex, boolean isAI) {
        this.name = name;
        this.hand = hand != null ? hand : new ArrayList<>();
        this.team = team;
        this.seatIndex = seatIndex;
        this.isAI = isAI;
    }

    public String getName() {
        return name;
    }

    public List<Card> getHand() {
        return hand;
    }

    public void setHand(List<Card> hand) {
        this.hand = hand;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public int getSeatIndex() {
        return seatIndex;
    }

    public void setSeatIndex(int seatIndex) {
        this.seatIndex = seatIndex;
    }

    public boolean isAI() {
        return isAI;
    }

    public void setAI(boolean isAI) {
        this.isAI = isAI;
    }

    public void addCard(Card card) {
        if (hand == null) hand = new ArrayList<>();
        hand.add(card);
    }
}
