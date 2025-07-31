// src/test/java/com/bidwhist/utils/HandUtilsTest.java

package com.bidwhist.utils;

import com.bidwhist.bidding.*;
import com.bidwhist.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class HandUtilsTest {

    private Card twoSpades, aceSpades, kingSpades, jokerB, jokerS, threeHearts, queenHearts, fiveClubs, jackDiamonds;
    private List<Card> sampleHand;
    private GameState uptownGame, downtownGame, noTrumpGame;

    @BeforeEach
    void setUp() {
        twoSpades = new Card(Suit.SPADES, Rank.TWO);
        aceSpades = new Card(Suit.SPADES, Rank.ACE);
        kingSpades = new Card(Suit.SPADES, Rank.KING);
        jokerB = new Card(null, Rank.JOKER_B);
        jokerS = new Card(null, Rank.JOKER_S);

        threeHearts = new Card(Suit.HEARTS, Rank.THREE);
        queenHearts = new Card(Suit.HEARTS, Rank.QUEEN);
        fiveClubs = new Card(Suit.CLUBS, Rank.FIVE);
        jackDiamonds = new Card(Suit.DIAMONDS, Rank.JACK);

        sampleHand = new ArrayList<>(List.of(twoSpades, aceSpades, jokerB, threeHearts, fiveClubs, kingSpades));

        uptownGame = new GameState("uptown");
        uptownGame.setTrumpSuit(Suit.HEARTS);
        uptownGame.setBidType(BidType.UPTOWN);
        uptownGame.setWinningBid(new FinalBid(PlayerPos.P1, 5, false, false, BidType.UPTOWN, Suit.HEARTS));

        downtownGame = new GameState("downtown");
        downtownGame.setTrumpSuit(Suit.CLUBS);
        downtownGame.setBidType(BidType.DOWNTOWN);
        downtownGame.setWinningBid(new FinalBid(PlayerPos.P2, 5, false, false, BidType.DOWNTOWN, Suit.CLUBS));

        noTrumpGame = new GameState("notrump");
        noTrumpGame.setTrumpSuit(null);
        noTrumpGame.setBidType(BidType.DOWNTOWN);
        noTrumpGame.setWinningBid(new FinalBid(PlayerPos.P3, 5, true, false, BidType.DOWNTOWN, null));
    }

    @Test
    void testGetLeadSuit() {
        List<PlayedCard> trick = List.of(
                new PlayedCard(PlayerPos.P1, new Card(Suit.HEARTS, Rank.FOUR)),
                new PlayedCard(PlayerPos.P2, jokerB));
        assertEquals(Suit.HEARTS, HandUtils.getLeadSuit(uptownGame, trick));
    }

    @Test
    void testAllHigherCardsPlayedTrue() {
        List<Card> played = List.of(aceSpades, jokerB, jokerS);
        List<Card> hand = List.of(twoSpades);
        boolean result = HandUtils.allHigherCardsPlayed(twoSpades, played, hand, Suit.SPADES, BidType.DOWNTOWN,  false);
        assertTrue(result);
    }

    @Test
    void testAllHigherCardsPlayedFalse() {
        List<Card> played = List.of();
        List<Card> hand = List.of(twoSpades);
        boolean result = HandUtils.allHigherCardsPlayed(twoSpades, played, hand, Suit.SPADES, BidType.DOWNTOWN, false);
        assertFalse(result);
    }

    @Test
    void testPartnerIsWinning() {
        PlayedCard pc = new PlayedCard(PlayerPos.P3, aceSpades);
        assertTrue(HandUtils.partnerIsWinning(PlayerPos.P1, pc));
        assertFalse(HandUtils.partnerIsWinning(PlayerPos.P1, new PlayedCard(PlayerPos.P2, kingSpades)));
    }

    @Test
    void testHasSuit() {
        assertTrue(HandUtils.hasSuit(sampleHand, Suit.SPADES));
        assertFalse(HandUtils.hasSuit(sampleHand, Suit.DIAMONDS));
    }

    @Test
    void testGetHighestRankedCard() {
        Card result = HandUtils.getHighestRankedCard(uptownGame, sampleHand);
        assertEquals(Rank.JOKER_B, result.getRank());

        Card result2 = HandUtils.getHighestRankedCard(noTrumpGame, sampleHand);
        assertEquals(Rank.ACE, result2.getRank());
    }

    @Test
    void testGetHighestOfSuit() {
        Card result = HandUtils.getHighestOfSuit(uptownGame, sampleHand, Suit.SPADES);
        assertEquals(Rank.ACE, result.getRank());
    }

    @Test
    void testGetLowestOfSuit() {
        Card result = HandUtils.getLowestOfSuit(downtownGame, sampleHand, Suit.SPADES);
        assertEquals(BidType.DOWNTOWN, downtownGame.getBidType());
        assertFalse(downtownGame.getWinningBid().isNo());
        assertEquals(Rank.KING, result.getRank());
    }

    @Test
    void testHasDiscardSuit() {
        assertTrue(HandUtils.hasDiscardSuit(sampleHand, Suit.SPADES));
        assertFalse(HandUtils.hasDiscardSuit(List.of(threeHearts, queenHearts, new Card(Suit.HEARTS, Rank.FIVE)), Suit.HEARTS));
    }

    @Test
    void testGetDiscardCard() {
        Card result = HandUtils.getDiscardCard(uptownGame, sampleHand);
        assertNotNull(result);
        assertNotEquals(Suit.HEARTS, result.getSuit());
    }

    @Test
    void testPartnerHasPlayed() {
        List<PlayedCard> trick = List.of(new PlayedCard(PlayerPos.P3, aceSpades));
        assertTrue(HandUtils.partnerHasPlayed(PlayerPos.P1, trick));
    }

    @Test
    void testGetPartner() {
        assertEquals(PlayerPos.P3, HandUtils.getPartner(PlayerPos.P1));
        assertEquals(PlayerPos.P1, HandUtils.getPartner(PlayerPos.P3));
    }

    @Test
    void testGetNextHigherCard() {
        List<Card> hand = List.of(twoSpades, aceSpades, kingSpades);
        Card result = HandUtils.getNextHigherCard(uptownGame, kingSpades, hand);
        assertEquals(Rank.ACE, result.getRank());
    }

    @Test
    void testGetPlayableHand() {
        List<PlayedCard> trick = List.of(new PlayedCard(PlayerPos.P1, aceSpades));
        List<Card> playable = HandUtils.getPlayableHand(uptownGame, trick, sampleHand);
        assertEquals(3, playable.size());
        assertTrue(playable.contains(twoSpades));
        assertTrue(playable.contains(aceSpades));
    }

    @Test
    void testGetLowestLegalCard() {
        List<PlayedCard> trick = List.of(new PlayedCard(PlayerPos.P1, threeHearts));
        Card result = HandUtils.getLowestLegalCard(downtownGame, trick, sampleHand);
        assertNotNull(result);
    }

    @Test
    void testGetLowestLegalNonTrumpCard() {
        List<PlayedCard> trick = List.of(new PlayedCard(PlayerPos.P1, fiveClubs));
        Card result = HandUtils.getLowestLegalNonTrumpCard(downtownGame, trick, sampleHand);
        assertNotNull(result);
    }

    @Test
    void testGetHighestLegalCard() {
        List<PlayedCard> trick = List.of(new PlayedCard(PlayerPos.P1, aceSpades));
        Card result = HandUtils.getHighestLegalCard(uptownGame, trick, sampleHand);
        assertEquals(Rank.ACE, result.getRank());
    }

    @Test
    void testDetermineTrickWinnerUptown() {
        List<PlayedCard> trick = List.of(
                new PlayedCard(PlayerPos.P1, twoSpades),
                new PlayedCard(PlayerPos.P2, aceSpades));
        PlayedCard result = HandUtils.determineTrickWinner(uptownGame, trick);
        assertEquals(PlayerPos.P2, result.getPlayer());
    }

    @Test
    void testDetermineTrickWinnerNoTrump() {
        List<PlayedCard> trick = List.of(
                new PlayedCard(PlayerPos.P1, new Card(Suit.HEARTS, Rank.SIX)),
                new PlayedCard(PlayerPos.P2, new Card(Suit.HEARTS, Rank.EIGHT)),
                new PlayedCard(PlayerPos.P3, jokerB));
        PlayedCard result = HandUtils.determineTrickWinner(noTrumpGame, trick);
        assertEquals(PlayerPos.P1, result.getPlayer());
    }

    @Test
    void testAreOpponentsSuitVoid() {
        Map<PlayerPos, Map<Suit, Boolean>> voidMap = new EnumMap<>(PlayerPos.class);
        voidMap.put(PlayerPos.P2, Map.of(Suit.SPADES, true));
        voidMap.put(PlayerPos.P4, Map.of(Suit.SPADES, true));
        uptownGame.setSuitVoidMap(voidMap);

        assertTrue(HandUtils.areOpponentsSuitVoid(uptownGame, PlayerPos.P1, Suit.SPADES));
    }

    @Test
    void testIsPartnerSuitVoid() {
        Map<PlayerPos, Map<Suit, Boolean>> voidMap = new EnumMap<>(PlayerPos.class);
        voidMap.put(PlayerPos.P3, Map.of(Suit.SPADES, true));
        uptownGame.setSuitVoidMap(voidMap);

        assertTrue(HandUtils.isPartnerSuitVoid(uptownGame, PlayerPos.P1, Suit.SPADES));
    }
}
