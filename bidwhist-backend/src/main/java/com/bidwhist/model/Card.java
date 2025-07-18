package com.bidwhist.model;

import com.bidwhist.dto.CardOwner;
import com.bidwhist.dto.CardVisibility;
import com.bidwhist.utils.JokerUtils;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "rank", "suit" })
public class Card implements Comparable<Card> {

    /* Track card location across game */
    public enum CardLocation {
        DECK, HAND, KITTY, PLAY, BOOK, DISCARDED
    }

    private Rank rank;
    private Suit suit;
    private String cardImage;
    private CardVisibility visibility;

    public Card(Suit suit, Rank rank) {
        this.rank = rank;
        this.suit = suit;
        this.cardImage = this.getCardImage();

        this.visibility = CardVisibility.HIDDEN;
    }

    /*
     * Suite will be assigned across the deck, but only effect the Jokers.
     * This ensure that Jokers count towards the winning bid's suit.
     */
    public void assignSuit(Suit suit) {
        if (JokerUtils.isJokerRank(rank)) {
            this.suit = suit;
        }
    }

    // Suit must be cleared after match and reset on winning bid.
    public void clearSuit() {
        if (JokerUtils.isJokerRank(rank)) {
            this.suit = null;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Card card = (Card) o;
        return rank == card.rank && suit == card.suit;
    }

    @Override
    public int compareTo(Card other) {
        // Handle suit nulls (Jokers)
        if (this.suit == null && other.suit != null)
            return 1; // Jokers go after normal cards
        if (this.suit != null && other.suit == null)
            return -1;
        if (this.suit == null && other.suit == null) {
            // Compare by rank only (optional: treat JokerLow < JokerHigh)
            return this.rank.compareTo(other.rank);
        }

        int suitCompare = this.suit.compareTo(other.suit);
        return (suitCompare != 0)
                ? suitCompare
                : this.rank.compareTo(other.rank);
    }

    public Suit getSuit() {
        return suit;
    }

    public Rank getRank() {
        return rank;
    }

    public CardVisibility getVisibility() {
        return visibility;
    }

    public String getCardImage() {
        if (this.rank == null) {
            return "Deck_Back.png";
        } else if (this.rank == Rank.JOKER_B) {
            return rank.toString().toLowerCase() + ".png";
        } else if (this.rank == Rank.JOKER_S) {
            return rank.toString().toLowerCase() + ".png";
        } else if (this.rank.getValue() == 11) {
            return suit.toString().toLowerCase() + "_jack.png";
        } else if (this.rank.getValue() == 12) {
            return suit.toString().toLowerCase() + "_queen.png";
        } else if (this.rank.getValue() == 13) {
            return suit.toString().toLowerCase() + "_king.png";
        } else if (this.rank.getValue() == 14) {
            return suit.toString().toLowerCase() + "_ace.png";
        } else {
            return suit.toString().toLowerCase() + "_" + rank.getValue() + ".png";
        }
    }

    public void setVisibility(CardVisibility newVisibility) {
        this.visibility = newVisibility;
    }

    @Override
    public String toString() {
        return rank + "of" + suit;
    }

}
