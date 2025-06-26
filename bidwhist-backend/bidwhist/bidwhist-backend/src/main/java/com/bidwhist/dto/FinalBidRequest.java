package com.bidwhist.dto;

import com.bidwhist.bidding.BidType;
import com.bidwhist.model.PlayerPos;
import com.bidwhist.model.Suit;

public class FinalBidRequest {
    private PlayerPos player;
    private BidType type;
    private Suit suit;

    public FinalBidRequest() {}

    public PlayerPos getPlayer() {
        return player;
    }

    public void setPlayer(PlayerPos player) {
        this.player = player;
    }

    public BidType getType() {
        return type;
    }

    public void setType(BidType type) {
        this.type = type;
    }

    public Suit getSuit() {
        return suit;
    }

    public void setSuit(Suit suit) {
        this.suit = suit;
    }
}
