// src/test/java/com/bidwhist/utils/CardUtilsTest.java

package com.bidwhist.utils;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.bidwhist.model.Card;
import com.bidwhist.model.Rank;
import com.bidwhist.model.Suit;

class CardUtilsTest {

    @Test
    void testCardsMatch_exactMatch() {
        Card c1 = new Card(Suit.HEARTS, Rank.QUEEN);
        Card c2 = new Card(Suit.HEARTS, Rank.QUEEN);
        assertTrue(CardUtils.cardsMatch(c1, c2));
    }

    @Test
    void testCardsMatch_differentSuit() {
        Card c1 = new Card(Suit.HEARTS, Rank.QUEEN);
        Card c2 = new Card(Suit.DIAMONDS, Rank.QUEEN);
        assertFalse(CardUtils.cardsMatch(c1, c2));
    }

    @Test
    void testCardsMatch_differentRank() {
        Card c1 = new Card(Suit.SPADES, Rank.KING);
        Card c2 = new Card(Suit.SPADES, Rank.QUEEN);
        assertFalse(CardUtils.cardsMatch(c1, c2));
    }

    @Test
    void testCardsMatch_nullSuit_match() {
        Card c1 = new Card(null, Rank.JOKER_B);
        Card c2 = new Card(null, Rank.JOKER_B);
        assertTrue(CardUtils.cardsMatch(c1, c2));
    }

    @Test
    void testCardsMatch_nullSuit_mismatch() {
        Card c1 = new Card(null, Rank.JOKER_B);
        Card c2 = new Card(null, Rank.JOKER_S);
        assertFalse(CardUtils.cardsMatch(c1, c2));
    }

    @Test
    void testCardsMatch_nullInputs() {
        assertFalse(CardUtils.cardsMatch(null, null));
        assertFalse(CardUtils.cardsMatch(new Card(Suit.CLUBS, Rank.TEN), null));
        assertFalse(CardUtils.cardsMatch(null, new Card(Suit.CLUBS, Rank.TEN)));
    }

  }