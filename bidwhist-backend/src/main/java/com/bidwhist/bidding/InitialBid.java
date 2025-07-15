package com.bidwhist.bidding;

import com.bidwhist.dto.BidRequest;
import com.bidwhist.model.PlayerPos;

/*
 * Allows for dynami bidding. Initial bid with only value and boolean for "no bid"
 * new Bid(P1, 4, true)
 * Final full bid
 * new Bid(P1, 4, true, DOWNTOWN, null)
 * Passing
 * Bid.pass(P1)
 */
public class InitialBid {
    private final PlayerPos player;
    private final int value; // 4 to 7
    private final boolean isNo;
    private final boolean isPassed;

    // Constructor: initial, unfinalized bid
    public InitialBid(PlayerPos player, int value, boolean isNo) {
        this.player = player;
        this.value = value;
        this.isNo = isNo;
        this.isPassed = false;

    }

    public InitialBid(PlayerPos player) {
        this.player = player;
        this.value = 0;
        this.isNo = false;
        this.isPassed = true;
    }

    // Static constructor: pass bid
    public static InitialBid pass(PlayerPos player) {
        return new InitialBid(player);
    }


    // Show initial bid state only (for bidding phase)
    public String showInitialBid() {
        if (isPassed)
            return player + " passes";
        return player + " bids " + value + (isNo ? " (No)" : "");
    }

    @Override
    public String toString() {
        if (isPassed)
            return player + " passes";
        return player + " bids " + value + (isNo ? " No" : "");
    }

    // Getters
    public PlayerPos getPlayer() {
        return player;
    }

    public int getValue() {
        return value;
    }

    public boolean isNo() {
        return isNo;
    }

    public boolean isPassed() {
        return isPassed;
    }
}