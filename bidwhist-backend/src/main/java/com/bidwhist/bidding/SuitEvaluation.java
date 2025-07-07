package com.bidwhist.bidding;

import com.bidwhist.model.Suit;

/**
 * Stores strength information for a single suit in a player's hand.
 * Evaluated in both Uptown (high) and Downtown (low) directions.
 */
public class SuitEvaluation {
    private final Suit suit;
    private final int uptownStrength;
    private final int downtownStrength;
    private final int count;

    public SuitEvaluation(Suit suit, int uptownStrength, int downtownStrength, int count) {
        this.suit = suit;
        this.uptownStrength = uptownStrength;
        this.downtownStrength = downtownStrength;
        this.count = count;
    }

    /** The suit being evaluated (e.g., HEARTS, CLUBS). */
    public Suit getSuit() {
        return suit;
    }

    /** Strength when evaluating in Uptown (high-card) direction. */
    public int getUptownStrength() {
        return uptownStrength;
    }

    /** Strength when evaluating in Downtown (low-card) direction. */
    public int getDowntownStrength() {
        return downtownStrength;
    }

    /** Total number of cards in the suit (excluding jokers). */
    public int getCardCount() {
        return count;
    }

    /** Returns the higher of high vs low strength. */
    public int getBestStrength() {
        return Math.max(uptownStrength, downtownStrength);
    }

    /** Returns which mode (Uptown or Downtown) gives the best strength. */
    public BidType getBestMode() {
        return (uptownStrength >= downtownStrength) ? BidType.UPTOWN : BidType.DOWNTOWN;
    }

    @Override
    public String toString() {
        return suit.toString() +  " Uptown: " + uptownStrength + ", Downtown: "
        + downtownStrength + " count..." + count;
    }
}
