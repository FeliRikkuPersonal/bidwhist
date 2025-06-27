package com.bidwhist.dto;

import com.bidwhist.bidding.InitialBid;
import com.bidwhist.bidding.BidType;
import com.bidwhist.model.Suit;
import com.bidwhist.model.PlayerPos;
import com.bidwhist.model.Player;

/**
 * DTO representing a player's bid submission, typically from client → server.
 */
public class BidRequest {
    private PlayerPos player; // Player name or identifier
    private int value; // Bid value (4–7), or 0 if pass
    private BidType type; // UPTOWN or DOWNTown
    private boolean isNo; // "No" bid flag
    private Suit suit; // Suit name (e.g., "HEARTS") or null for "No" bids

    public BidRequest() {
    }

    public BidRequest(PlayerPos player, int value, boolean isNo) {
        this.player = player;
        this.value = value;
        this.isNo = isNo;
    }

    public static InitialBid fromRequest(BidRequest req, Player bidder) {
        // Defensive checks
        if (req.getValue() != 0 && (req.getValue() < 4 || req.getValue() > 7)) {
            throw new IllegalArgumentException("Bid value must be between 4 and 7.");
        }
        if (req.getValue() == 0) {
            return InitialBid.pass(bidder.getPosition());
        }

        PlayerPos player = req.getPlayer(); // Assuming player string is like "P1"


        return new InitialBid(
                player,
                req.getValue(),
                req.isNo());
    }

    public PlayerPos getPlayer() {
        return player;
    }

    public void setPlayer(PlayerPos player) {
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

    public boolean isNo() {
        return isNo;
    }

    public void setNo(boolean isNo) {
        this.isNo = isNo;
    }

    public Suit getSuit() {
        return suit;
    }

    public void setSuit(Suit suit) {
        this.suit = suit;
    }

    public boolean isPass() {
        if (value == 0) {
            return true;
        }
        return false;
    }
}
