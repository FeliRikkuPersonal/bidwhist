package com.bidwhist.model;

import java.util.Objects;

public class Card {
    private final Suit suit;
    private final Rank rank;

    public Card(Suit suit, Rank rank) {
        // ✅ Enforce Joker validation
        if (rank == Rank.JOKER && !suit.isJokerSuit()) {
            throw new IllegalArgumentException("JOKER rank must have RED_JOKER or BLACK_JOKER suit.");
        }
        if (suit.isJokerSuit() && rank != Rank.JOKER) {
            throw new IllegalArgumentException("RED_JOKER and BLACK_JOKER suits must have JOKER rank.");
        }

        this.suit = suit;
        this.rank = rank;
    }

    public Suit getSuit() {
        return suit;
    }

    public Rank getRank() {
        return rank;
    }

    @Override
    public String toString() {
        return rank + " of " + suit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Card)) return false;
        Card card = (Card) o;
        return suit == card.suit && rank == card.rank;
    }

    @Override
    public int hashCode() {
        return Objects.hash(suit, rank);
    }
}
