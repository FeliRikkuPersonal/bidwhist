package com.bidwhist.bidwhist_backend.model;

public class Card {
    public enum Suit {
        SPADES("♠"),
        HEARTS("♥"),
        DIAMONDS("♦"),
        CLUBS("♣");

        private final String symbol;

        Suit(String symbol) {
            this.symbol = symbol;
        }

        public String getSymbol() {
            return symbol;
        }
}

    public enum Rank {
        TWO(2), THREE(3), FOUR(4), FIVE(5), SIX(6),
        SEVEN(7), EIGHT(8), NINE(9), TEN(10),
        JACK(11), QUEEN(12), KING(13), ACE(14), 
        JOKER_S(15), JOKER_L(16);

        private final int value;

        Rank(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }  
    }

    private Suit suit;
    private Rank rank;

    public Card(Suit suit, Rank rank) {
        this.suit = suit;
        this.rank = rank;
    }

    public boolean isJoker() {
        return rank == Rank.JOKER_S || rank == Rank.JOKER_L;
    }

        public Suit getSuit() {
        return suit;
    }

    public Rank getRank() {
        return rank;
    }

    public void assignSuit(Suit suit) {
        if (isJoker()) {this.suit = suit;}
    }

        public void clearSuit() {
        if (isJoker()) this.suit = null;
    }
}
