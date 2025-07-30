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

    @Test
    void testAllHigherTrumpCardsPlayed_true_whenAllHigherArePlayed() {
        Card candidate = new Card(Suit.SPADES, Rank.JACK);
        List<Card> played = List.of(
                new Card(Suit.SPADES, Rank.QUEEN),
                new Card(Suit.SPADES, Rank.KING),
                new Card(Suit.SPADES, Rank.ACE),
                new Card(Suit.SPADES, Rank.JOKER_S),
                new Card(Suit.SPADES, Rank.JOKER_B));
        List<Card> hand = List.of(
                new Card(Suit.SPADES, Rank.ACE),
                new Card(Suit.SPADES, Rank.THREE),
                new Card(Suit.HEARTS, Rank.TEN));

        boolean result = HandUtils.allHigherCardsPlayed(candidate, played, hand, Suit.SPADES, false);
        assertTrue(result);
    }

    @Test
    void testAllHigherTrumpCardsPlayed_false_whenSomeHigherNotPlayed() {
        Card candidate = new Card(Suit.SPADES, Rank.JACK);
        List<Card> played = List.of(
                new Card(Suit.SPADES, Rank.QUEEN),
                new Card(Suit.SPADES, Rank.KING) // missing ACE, JOKER_S, JOKER_B
        );
        List<Card> hand = List.of(
                new Card(Suit.SPADES, Rank.ACE),
                new Card(Suit.SPADES, Rank.THREE),
                new Card(Suit.HEARTS, Rank.TEN));

        boolean result = HandUtils.allHigherCardsPlayed(candidate, played, hand, Suit.SPADES, false);
        assertFalse(result);
    }

    @Test
    void testAllHigherTrumpCardsPlayed_false_whenNotTrumpSuit() {
        Card candidate = new Card(Suit.HEARTS, Rank.JACK);
        List<Card> played = List.of(
                new Card(Suit.HEARTS, Rank.QUEEN),
                new Card(Suit.HEARTS, Rank.KING),
                new Card(Suit.HEARTS, Rank.ACE));
        List<Card> hand = List.of(
                new Card(Suit.SPADES, Rank.ACE),
                new Card(Suit.SPADES, Rank.THREE),
                new Card(Suit.HEARTS, Rank.TEN));

        boolean result = HandUtils.allHigherCardsPlayed(candidate, played, hand, Suit.SPADES, false);
        assertFalse(result);
    }

    @Test
    void testAllHigherTrumpCardsPlayed_false_onNullInput() {
                List<Card> hand = List.of(
                new Card(Suit.SPADES, Rank.ACE),
                new Card(Suit.SPADES, Rank.THREE),
                new Card(Suit.HEARTS, Rank.TEN));
        List<Card> played = new ArrayList<>();
        assertFalse(HandUtils.allHigherCardsPlayed(null, List.of(), hand, Suit.SPADES, false));
        assertFalse(HandUtils.allHigherCardsPlayed(new Card(Suit.SPADES, Rank.JACK), played, hand, Suit.SPADES, false));
        assertFalse(HandUtils.allHigherCardsPlayed(new Card(Suit.SPADES, Rank.TEN), List.of(), hand, null, false));
    }
}
