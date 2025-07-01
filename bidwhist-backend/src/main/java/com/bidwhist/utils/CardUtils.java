package com.bidwhist.utils;

import com.bidwhist.dto.CardOwner;
import com.bidwhist.model.Card;
import com.bidwhist.model.PlayerPos;

public class CardUtils {

    /**
     * Checks if two cards are equal by comparing rank and suit.
     * Safely handles Jokers and null suits.
     */
    public static boolean cardsMatch(Card c1, Card c2) {
        if (c1 == null || c2 == null)
            return false;

        boolean ranksMatch = c1.getRank() != null && c1.getRank().equals(c2.getRank());

        boolean suitsMatch = (c1.getSuit() == null && c2.getSuit() == null)
                || (c1.getSuit() != null && c1.getSuit().equals(c2.getSuit()));

        return ranksMatch && suitsMatch;
    }

    public static CardOwner fromPlayerPos(PlayerPos pos) {
        if (pos == null)
            return CardOwner.TABLE; // fallback
        switch (pos) {
            case P1:
                return CardOwner.P1;
            case P2:
                return CardOwner.P2;
            case P3:
                return CardOwner.P3;
            case P4:
                return CardOwner.P4;
            default:
                return CardOwner.TABLE;
        }
    }

    public static PlayerPos toPlayerPos(CardOwner owner) {
        if (owner == null)
            return null;
        switch (owner) {
            case P1:
                return PlayerPos.P1;
            case P2:
                return PlayerPos.P2;
            case P3:
                return PlayerPos.P3;
            case P4:
                return PlayerPos.P4;
            default:
                return null; // TABLE has no player position
        }
    }
}
