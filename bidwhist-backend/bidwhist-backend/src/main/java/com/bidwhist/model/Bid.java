package com.bidwhist.model;

public class Bid {
    private final String playerName;
    private final int value;
    private final BidType type;

    public Bid(String playerName, int value, BidType type) {
        this.playerName = playerName;
        this.value = value;
        this.type = type;
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getValue() {
        return value;
    }

    public BidType getType() {
        return type;
    }
}
