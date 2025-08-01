package com.bidwhist.testUtils;

import com.bidwhist.model.Card;
import com.bidwhist.model.Hand;
import java.util.ArrayList;
import java.util.List;

public class TestHands {
  public Hand noTrumpHighHand = new Hand();
  List<Card> noTrumpHighList = new ArrayList<>();
  public Hand noTrumpLowHand = new Hand();
  List<Card> noTrumpLowList = new ArrayList<>();
  public Hand strongUptownHand = new Hand();
  List<Card> strongUptownList = new ArrayList<>();
  public Hand strongDowntownHand = new Hand();
  List<Card> strongDowntownList = new ArrayList<>();
  public Hand strongDowntownWithJokerGapHand = new Hand();
  List<Card> strongDowntownWithJokerGapList = new ArrayList<>();
  public Hand ambiguousUptownDowntownHand = new Hand();
  List<Card> ambiguousUptownDowntownList = new ArrayList<>();
  public Hand maxUptownStrengthHand = new Hand();
  List<Card> maxUptownStrengthList = new ArrayList<>();
  public Hand unbridgeableGapsHand = new Hand();
  List<Card> unbridgeableGapsList = new ArrayList<>();

  public TestHands() {
    noTrumpHighList.addAll(
        List.of(
            // Spades
            TestCardUtil.getCard("ACE_of_SPADES"),
            TestCardUtil.getCard("KING_of_SPADES"),
            TestCardUtil.getCard("QUEEN_of_SPADES"),

            // Hearts
            TestCardUtil.getCard("ACE_of_HEARTS"),
            TestCardUtil.getCard("KING_of_HEARTS"),
            TestCardUtil.getCard("QUEEN_of_HEARTS"),

            // Diamonds
            TestCardUtil.getCard("ACE_of_DIAMONDS"),
            TestCardUtil.getCard("KING_of_DIAMONDS"),

            // Clubs
            TestCardUtil.getCard("ACE_of_CLUBS"),
            TestCardUtil.getCard("KING_of_CLUBS"),

            // Fillers
            TestCardUtil.getCard("TWO_of_SPADES"),
            TestCardUtil.getCard("THREE_of_CLUBS")));

    noTrumpLowList.addAll(
        List.of(
            // Spades: A-2-3-4
            TestCardUtil.getCard("ACE_of_SPADES"),
            TestCardUtil.getCard("TWO_of_SPADES"),
            TestCardUtil.getCard("THREE_of_SPADES"),
            TestCardUtil.getCard("FOUR_of_SPADES"),

            // Hearts: A-2-3
            TestCardUtil.getCard("ACE_of_HEARTS"),
            TestCardUtil.getCard("TWO_of_HEARTS"),
            TestCardUtil.getCard("THREE_of_HEARTS"),

            // Clubs: A-2-3
            TestCardUtil.getCard("ACE_of_CLUBS"),
            TestCardUtil.getCard("TWO_of_CLUBS"),
            TestCardUtil.getCard("THREE_of_CLUBS"),

            // Diamonds: A-2
            TestCardUtil.getCard("ACE_of_DIAMONDS"),
            TestCardUtil.getCard("TWO_of_DIAMONDS")));

    strongUptownList.addAll(
        List.of(
            // Spades: Ace through 7
            TestCardUtil.getCard("ACE_of_SPADES"),
            TestCardUtil.getCard("KING_of_SPADES"),
            TestCardUtil.getCard("QUEEN_of_SPADES"),
            TestCardUtil.getCard("JACK_of_SPADES"),
            TestCardUtil.getCard("TEN_of_SPADES"),
            TestCardUtil.getCard("NINE_of_SPADES"),
            TestCardUtil.getCard("EIGHT_of_SPADES"),
            TestCardUtil.getCard("SEVEN_of_SPADES"),

            // Filler cards (random off-suit, unrelated)
            TestCardUtil.getCard("TWO_of_HEARTS"),
            TestCardUtil.getCard("FIVE_of_DIAMONDS"),
            TestCardUtil.getCard("FOUR_of_CLUBS"),
            TestCardUtil.getCard("SIX_of_HEARTS")));

    strongDowntownList.addAll(
        List.of(
            // Spades: Ace through Eight (low run)
            TestCardUtil.getCard("ACE_of_SPADES"),
            TestCardUtil.getCard("TWO_of_SPADES"),
            TestCardUtil.getCard("THREE_of_SPADES"),
            TestCardUtil.getCard("FOUR_of_SPADES"),
            TestCardUtil.getCard("FIVE_of_SPADES"),
            TestCardUtil.getCard("SIX_of_SPADES"),
            TestCardUtil.getCard("SEVEN_of_SPADES"),
            TestCardUtil.getCard("EIGHT_of_SPADES"),

            // 5 unrelated filler cards
            TestCardUtil.getCard("KING_of_HEARTS"),
            TestCardUtil.getCard("NINE_of_DIAMONDS"),
            TestCardUtil.getCard("TEN_of_CLUBS"),
            TestCardUtil.getCard("JACK_of_HEARTS")));

    strongDowntownWithJokerGapList.addAll(
        List.of(
            // Spades (low run with one gap at 5)
            TestCardUtil.getCard("ACE_of_SPADES"),
            TestCardUtil.getCard("TWO_of_SPADES"),
            TestCardUtil.getCard("THREE_of_SPADES"),
            TestCardUtil.getCard("FOUR_of_SPADES"),
            // MISSING: FIVE_of_SPADES
            TestCardUtil.getCard("SIX_of_SPADES"),
            TestCardUtil.getCard("SEVEN_of_SPADES"),
            TestCardUtil.getCard("EIGHT_of_SPADES"),

            // Fillers: 1 Joker + 4 random cards
            TestCardUtil.getCard("JOKER_S"), // Joker bridges the gap
            TestCardUtil.getCard("KING_of_HEARTS"),
            TestCardUtil.getCard("NINE_of_DIAMONDS"),
            TestCardUtil.getCard("TEN_of_CLUBS"),
            TestCardUtil.getCard("QUEEN_of_CLUBS")));

    ambiguousUptownDowntownList.addAll(
        List.of(
            // Spades: Uptown run
            TestCardUtil.getCard("ACE_of_SPADES"),
            TestCardUtil.getCard("KING_of_SPADES"),
            TestCardUtil.getCard("QUEEN_of_SPADES"),
            TestCardUtil.getCard("JACK_of_SPADES"),
            TestCardUtil.getCard("TEN_of_SPADES"),

            // Hearts: Downtown run
            TestCardUtil.getCard("ACE_of_HEARTS"),
            TestCardUtil.getCard("TWO_of_HEARTS"),
            TestCardUtil.getCard("THREE_of_HEARTS"),
            TestCardUtil.getCard("FOUR_of_HEARTS"),
            TestCardUtil.getCard("FIVE_of_HEARTS"),

            // Two Jokers
            TestCardUtil.getCard("JOKER_B"),
            TestCardUtil.getCard("JOKER_S")));

    maxUptownStrengthList.addAll(
        List.of(
            // 10-card Uptown run in Spades (A → 4)
            TestCardUtil.getCard("ACE_of_SPADES"),
            TestCardUtil.getCard("KING_of_SPADES"),
            TestCardUtil.getCard("QUEEN_of_SPADES"),
            TestCardUtil.getCard("JACK_of_SPADES"),
            TestCardUtil.getCard("TEN_of_SPADES"),
            TestCardUtil.getCard("NINE_of_SPADES"),
            TestCardUtil.getCard("EIGHT_of_SPADES"),
            TestCardUtil.getCard("SEVEN_of_SPADES"),
            TestCardUtil.getCard("SIX_of_SPADES"),
            TestCardUtil.getCard("FIVE_of_SPADES"),

            // 2 Jokers
            TestCardUtil.getCard("JOKER_B"),
            TestCardUtil.getCard("JOKER_S")));

    unbridgeableGapsList.addAll(
        List.of(
            // Spades with gaps: A, K, 10, 8, 5 — missing Q, J, 9, 7, 6, 4, 3, 2
            TestCardUtil.getCard("TEN_of_SPADES"),
            TestCardUtil.getCard("SEVEN_of_SPADES"),
            TestCardUtil.getCard("FIVE_of_SPADES"),
            TestCardUtil.getCard("TWO_of_SPADES"),

            // One Joker (not enough to bridge)
            TestCardUtil.getCard("JOKER_S"),

            // Filler cards
            TestCardUtil.getCard("TWO_of_HEARTS"),
            TestCardUtil.getCard("FOUR_of_CLUBS"),
            TestCardUtil.getCard("NINE_of_DIAMONDS"),
            TestCardUtil.getCard("SIX_of_HEARTS"),
            TestCardUtil.getCard("EIGHT_of_CLUBS"),
            TestCardUtil.getCard("TEN_of_CLUBS"),
            TestCardUtil.getCard("QUEEN_of_DIAMONDS")));

    noTrumpHighHand.addCards(noTrumpHighList);
    noTrumpLowHand.addCards(noTrumpLowList);
    strongUptownHand.addCards(strongUptownList);
    strongDowntownHand.addCards(strongDowntownList);
    strongDowntownWithJokerGapHand.addCards(strongDowntownWithJokerGapList);
    ambiguousUptownDowntownHand.addCards(ambiguousUptownDowntownList);
    maxUptownStrengthHand.addCards(maxUptownStrengthList);
    unbridgeableGapsHand.addCards(unbridgeableGapsList);
  }

  public Hand getNoTrumpHighHand() {
    return noTrumpHighHand;
  }

  public Hand getNoTrumpLowHand() {
    return noTrumpLowHand;
  }

  public Hand getStrongUptownHand() {
    return strongUptownHand;
  }

  public Hand getStrongDowntownHand() {
    return strongDowntownHand;
  }

  public Hand getStrongDowntownWithJokerGapHand() {
    return strongDowntownWithJokerGapHand;
  }

  public Hand getAmbiguousUptownDowntownHand() {
    return ambiguousUptownDowntownHand;
  }

  public Hand getMaxUptownStrengthHand() {
    return maxUptownStrengthHand;
  }

  public Hand getUnbridgeableGapsHand() {
    return unbridgeableGapsHand;
  }
}
