package com.bidwhist.model;

public enum Suit {
    SPADES,
    HEARTS,
    CLUBS,
    DIAMONDS,
    RED_JOKER,
    BLACK_JOKER;

    public boolean isJokerSuit() {
        return this == RED_JOKER || this == BLACK_JOKER;
    }

    public boolean isStandardSuit() {
        return this == SPADES || this == HEARTS || this == CLUBS || this == DIAMONDS;
    }
}
