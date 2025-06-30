package com.bidwhist.model;

public class Player {
    private String name;
    private Hand hand;
    private final boolean isAI;
    private boolean isDealer;
    private final PlayerPos position; // P1, P2, P3, P4
    private final Team team;   // "A" or "B" (optional team-based scoring)

    public Player(String name, boolean isAI, PlayerPos position, Team team) {
        this.name = name;
        this.isAI = isAI;
        this.position = position;
        this.team = team;
        this.hand = new Hand();
    }

    public void addCard(Card card) {
        this.hand.addCard(card);
    }

    public void removeCard(Card card) {
        this.hand.removeCard(card);
    }

    public boolean isAI() {
        return isAI;
    }

    public boolean isDealer() {
        return isDealer;
    }

    public String getName() {
        return name;
    }

    public PlayerPos getPosition() {
        return position;
    }

    public Team getTeam() {
        return team;
    }

    public Hand getHand() {
        return hand;
    }

    public void setHand(Hand hand) {
        this.hand = hand;
    }

    public void setName(String name) {
        this.name = name;
    }
}

