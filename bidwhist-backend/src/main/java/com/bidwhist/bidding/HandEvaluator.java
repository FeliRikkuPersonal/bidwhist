// src/main/java/com/bidwhist/bidding/HandEvaluator.java

package com.bidwhist.bidding;

/*
 * Evaluates hand by removing jokers and checking each suit's strength.
 * Strength is checked by listing cards in suit and checking the run.
 * If n = number of cards in suit, m = n - 2 (initial strength). Then,
 * from Ace to Jack (or Ace to 4), each time a gap is reached, the lowest card is removed,
 * resulting in adjusted strength. Final strength takes the adjusted strength and
 * subtracts 1 for each missing joker and adds 1 for each joker in hand.
 */

import com.bidwhist.model.*;
import com.bidwhist.utils.JokerUtils;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Evaluates a player's hand for possible bids. Handles per-suit evaluation
 * (Uptown/Downtown) and
 * "No" bid options using pure runs.
 */
public class HandEvaluator {

  private final List<Card> cards;
  private final int jokerCount;
  private final Map<Suit, List<Card>> suits;

  private List<SuitEvaluation> suitEvals;
  private int noUptown;
  private int noDowntown;

  // Static rank orderings for bid evaluation
  public static final List<Rank> UPTOWN_ORDER = List.of(
      Rank.ACE,
      Rank.KING,
      Rank.QUEEN,
      Rank.JACK,
      Rank.TEN,
      Rank.NINE,
      Rank.EIGHT,
      Rank.SEVEN,
      Rank.SIX,
      Rank.FIVE,
      Rank.FOUR,
      Rank.THREE,
      Rank.TWO);

  public static final List<Rank> DOWNTOWN_ORDER = List.of(
      Rank.ACE,
      Rank.TWO,
      Rank.THREE,
      Rank.FOUR,
      Rank.FIVE,
      Rank.SIX,
      Rank.SEVEN,
      Rank.EIGHT,
      Rank.NINE,
      Rank.TEN,
      Rank.JACK,
      Rank.QUEEN,
      Rank.KING);

  /** Binds this evaluator to a specific player's hand. */
  public HandEvaluator(Player player) {
    this.cards = player.getHand().getCards();
    this.jokerCount = JokerUtils.countJokers(player.getHand());

    List<Card> nonJokers = cards.stream().filter(card -> !card.getRank().name().contains("JOKER")).toList();

    this.suits = splitBySuit(nonJokers);
  }

  /** Performs all evaluations (suit strength + No Trump potential). */
  public void evaluateHand() {
    this.suitEvals = evaluateAllSuits(suits, jokerCount);
    this.noUptown = evaluateNoBidHigh(suits);
    this.noDowntown = evaluateNoBidLow(suits);
  }

  /**
   * Returns all valid bids the hand could support. Includes suit-based
   * Uptown/Downtown bids and No
   * Trump bids.
   */
  public List<FinalBid> evaluateAll(PlayerPos player) {
    List<FinalBid> bidOptions = new ArrayList<>();

    for (SuitEvaluation eval : suitEvals) {
      for (BidType type : List.of(BidType.UPTOWN, BidType.DOWNTOWN)) {
        FinalBid bid = AiBidOption.fromEvaluation(player, eval, type, false);
        if (bid != null)
          bidOptions.add(bid);
      }
    }

    for (BidType type : List.of(BidType.UPTOWN, BidType.DOWNTOWN)) {
      int strength = (type == BidType.UPTOWN) ? noUptown : noDowntown;
      if (strength >= 9 && strength <= 12) {
        int value = strength - 5;
        bidOptions.add(new FinalBid(player, value, true, false, type, null));
      }
    }

    for (FinalBid bid : bidOptions)
      System.out.println(bid.toString());

    return bidOptions;
  }

  /** Splits a hand of cards into groups by suit. */
  public static Map<Suit, List<Card>> splitBySuit(List<Card> fullHand) {
    Map<Suit, List<Card>> suits = new EnumMap<>(Suit.class);
    for (Suit suit : Suit.values())
      suits.put(suit, new ArrayList<>());
    for (Card card : fullHand) {
      if (card.getSuit() != null)
        suits.get(card.getSuit()).add(card);
    }
    return suits;
  }

  /** Evaluates all suits for strength in both Uptown and Downtown directions. */
  public static List<SuitEvaluation> evaluateAllSuits(Map<Suit, List<Card>> suits, int jokerCount) {
    List<SuitEvaluation> results = new ArrayList<>();
    for (Suit suit : suits.keySet()) {
      List<Card> cards = suits.get(suit);
      List<Rank> ranks = cards.stream().map(Card::getRank).toList();

      int high = evaluateRun(ranks, jokerCount, UPTOWN_ORDER);
      int low = evaluateRun(ranks, jokerCount, DOWNTOWN_ORDER);

      results.add(new SuitEvaluation(suit, high, low, cards.size()));
    }

    for (SuitEvaluation result : results)
      System.out.println(result.toString());

    return results;
  }

  /**
   * Measures a suit-based run (sequence) in either uptown/downtown direction.
   * Jokers can be used to
   * bridge gaps.
   */
  public static int evaluateRun(List<Rank> ranksInSuit, int jokers, List<Rank> order) {
    List<Rank> workingList = new ArrayList<>(order);
    List<Rank> kept = new ArrayList<>();

    int jokersMissing = 2 - jokers;
    for (int i = 0; i < jokersMissing && !workingList.isEmpty(); i++) {
      workingList.remove(workingList.size() - 1);
    }

    for (int i = 0, j = 0; i < workingList.size(); i++) {
      Rank rank = workingList.get(i);
      if (ranksInSuit.contains(rank)) {
        kept.add(rank);
      } else if (i < workingList.size() - 1) {
        j++;
        if (j == 2) {
          workingList.remove(workingList.size() - 1);
          i--; // retry shortened list
          j = 0;
        }
      } else
        break;
    }
    if (kept.size() < 4) {
      return 0;
    } else {
      return kept.size() + jokers;
    }
  }

  /** Calculates continuous high-card run across all suits (used for No bids). */
  public static int evaluatePureRun(List<Card> cards, List<Rank> order) {
    Set<Rank> ranksInHand = cards.stream().map(Card::getRank).collect(Collectors.toSet());

    int run = 0;
    for (Rank rank : order) {
      if (ranksInHand.contains(rank))
        run++;
      else
        break;
    }
    return run;
  }

  /** Calculates the high-direction total run score across suits for No-Trump. */
  public static int evaluateNoBidHigh(Map<Suit, List<Card>> suits) {
    int run = suits.values().stream().mapToInt(cards -> evaluatePureRun(cards, UPTOWN_ORDER)).sum();

    System.out.println("No-Up: " + run);
    return run;
  }

  /** Calculates the low-direction total run score across suits for No-Trump. */
  public static int evaluateNoBidLow(Map<Suit, List<Card>> suits) {
    int run = suits.values().stream().mapToInt(cards -> evaluatePureRun(cards, DOWNTOWN_ORDER)).sum();

    System.out.println("No-Down: " + run);
    return run;
  }

  /**
   * Fallback method for forcing a valid 4-value minimum bid. Picks the best
   * suit/direction
   * available.
   */
  public FinalBid getForcedMinimumBid(PlayerPos player) {
    evaluateHand(); // Ensure hand is evaluated

    FinalBid bestBid = null;
    int bestStrength = -1;

    for (SuitEvaluation eval : suitEvals) {
      for (BidType type : List.of(BidType.UPTOWN, BidType.DOWNTOWN)) {
        int strength = (type == BidType.UPTOWN) ? eval.getUptownStrength() : eval.getDowntownStrength();

        if (strength > bestStrength) {
          bestStrength = strength;
          bestBid = new FinalBid(player, 4, false, false, type, eval.getSuit());
        }
      }
    }

    if (bestBid == null) {
      bestBid = new FinalBid(player, 4, false, false, BidType.UPTOWN, Suit.SPADES);
    }

    return bestBid;
  }
}
