package com.bidwhist.model;

import com.bidwhist.utils.JokerUtils;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "rank", "suit" })
public class Card {

    /* Track card location across game */
    public enum CardLocation {
        DECK, HAND, KITTY, PLAY, BOOK, DISCARDED
    }

    private Rank rank;
    private Suit suit;
    
    public Card(Suit suit, Rank rank) {
        this.rank = rank;
        this.suit = suit;
    }

    /* Suite will be assigned across the deck, but only effect the Jokers.
     * This ensure that Jokers count towards the winning bid's suit.
     */
    public void assignSuit(Suit suit) {
        if (JokerUtils.isJokerRank(rank)) {this.suit = suit;}
    }

    // Suit must be cleared after match and reset on winning bid.
    public void clearSuit() {
        if (JokerUtils.isJokerRank(rank)) this.suit = null;
    }

    public Suit getSuit() {
        return suit;
    }

    public Rank getRank() {
        return rank;
    }

    @Override
    public String toString() {
        return rank + "of" + suit;
    }
}
