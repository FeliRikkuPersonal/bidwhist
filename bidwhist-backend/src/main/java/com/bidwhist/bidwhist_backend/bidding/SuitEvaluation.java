package com.bidwhist.bidwhist_backend.bidding;

/* Evaluates the strength of a particular suit in one's hand */

import com.bidwhist.bidwhist_backend.model.Card.Suit;

public class SuitEvaluation {
    public Suit suit;
    public int highStrength;
    public int lowStrength;
    public int count;

    public SuitEvaluation(Suit suit, int high, int low, int count) {
        this.suit = suit;
        this.highStrength = high;
        this.lowStrength = low;
        this.count = count;
    }

    public int getBestStrength() {
        return Math.max(highStrength, lowStrength);
    }

    public String getBestMode() {
        return (highStrength >= lowStrength) ? "High" : "Low";
    }
}
