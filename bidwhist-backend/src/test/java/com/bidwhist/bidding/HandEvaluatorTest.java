// src/test/java/com/bidwhist/bidding/HandEvaluatorTest.java

package com.bidwhist.bidding;

import static org.junit.jupiter.api.Assertions.*;

import com.bidwhist.model.*;
import java.util.*;
import org.junit.jupiter.api.Test;

class HandEvaluatorTest {

  @Test
  void testSplitBySuitSeparatesCorrectly() {
    List<Card> hand =
        List.of(
            new Card(Suit.HEARTS, Rank.ACE),
            new Card(Suit.SPADES, Rank.TEN),
            new Card(Suit.HEARTS, Rank.KING),
            new Card(Suit.CLUBS, Rank.THREE));

    Map<Suit, List<Card>> split = HandEvaluator.splitBySuit(hand);
    assertEquals(2, split.get(Suit.HEARTS).size());
    assertEquals(1, split.get(Suit.SPADES).size());
    assertEquals(1, split.get(Suit.CLUBS).size());
    assertEquals(0, split.get(Suit.DIAMONDS).size());
  }

  @Test
  void testEvaluateRunReturnsCorrectCountWithoutJokers() {
    List<Rank> ranks = List.of(Rank.ACE, Rank.KING, Rank.QUEEN, Rank.JACK);
    int score = HandEvaluator.evaluateRun(ranks, 0, HandEvaluator.UPTOWN_ORDER);
    assertTrue(score >= 3, "Run score should include at least 3 cards without jokers");
  }

  @Test
  void testEvaluateRunHandlesJokersToBridgeGaps() {
    List<Rank> ranks = List.of(Rank.ACE, Rank.QUEEN); // gap between ACE–QUEEN
    int score = HandEvaluator.evaluateRun(ranks, 2, HandEvaluator.UPTOWN_ORDER);
    assertTrue(score >= 3, "Jokers should help form longer runs");
  }

  @Test
  void testEvaluatePureRunStopsAtFirstGap() {
    List<Card> cards =
        List.of(
            new Card(Suit.SPADES, Rank.ACE),
            new Card(Suit.SPADES, Rank.KING),
            new Card(Suit.SPADES, Rank.JACK) // gap at QUEEN
            );
    int run = HandEvaluator.evaluatePureRun(cards, HandEvaluator.UPTOWN_ORDER);
    assertEquals(2, run); // Stops at KING (ACE, KING)
  }

  @Test
  void testEvaluateNoBidHighAndLow() {
    List<Card> cards =
        List.of(
            new Card(Suit.HEARTS, Rank.ACE),
            new Card(Suit.HEARTS, Rank.KING),
            new Card(Suit.CLUBS, Rank.ACE),
            new Card(Suit.CLUBS, Rank.TWO));
    Map<Suit, List<Card>> suits = HandEvaluator.splitBySuit(cards);
    int high = HandEvaluator.evaluateNoBidHigh(suits);
    int low = HandEvaluator.evaluateNoBidLow(suits);
    assertTrue(high > 0);
    assertTrue(low > 0);
  }

  @Test
  void testGetForcedMinimumBidReturnsValidBid() {
    GameState game = new GameState("test");
    Player player = new Player("myName", false, PlayerPos.P1, Team.A);
    Hand hand = new Hand();
    hand.addCard(new Card(Suit.HEARTS, Rank.ACE));
    hand.addCard(new Card(Suit.HEARTS, Rank.KING));
    hand.addCard(new Card(Suit.HEARTS, Rank.QUEEN));
    hand.addCard(new Card(Suit.HEARTS, Rank.JACK));
    player.setHand(hand);
    game.addPlayer(player);

    HandEvaluator evaluator = new HandEvaluator(player);
    FinalBid forced = evaluator.getForcedMinimumBid(game, PlayerPos.P1);
    assertNotNull(forced);
    assertEquals(4, forced.getValue());
    assertNotNull(forced.getSuit());
  }
}
