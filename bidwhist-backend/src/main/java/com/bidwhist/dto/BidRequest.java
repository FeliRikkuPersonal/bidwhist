package com.bidwhist.dto;

import com.bidwhist.model.BidType;

public class BidRequest {
    private String player;
    private int value;
    private BidType type;

    public String getPlayer() {
        return player;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public BidType getType() {
        return type;
    }

    public void setType(BidType type) {
        this.type = type;
    }
}
