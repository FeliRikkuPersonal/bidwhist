package com.bidwhist.bidding;

/*
 * Evaluates hand by removing jokers and checking each suit's strength.
 * Strength is checked by listing cards in suit and checking the run, 
 * If n = number of cards in suit, m = n - 2 (initial strength). Then
 * from Ace to Jack (or Ace to 4), each time a gap is reached, the lowest card is removed.
 * resulting in adjusted strength. Final strength takes the adjusted strength and
 * subtracts 1 for each missing joker and adds 1 for each joker in hand.
 */

import java.util.*;
import java.util.stream.Collectors;

import com.bidwhist.model.*;
import com.bidwhist.utils.JokerUtils;

/**
 * Evaluates a player's hand for possible bids.
 * Handles per-suit evaluation and "No" bid evaluation (based on pure runs).
 */
public class HandEvaluator {

    private final List<Card> cards;
    private final int jokerCount;
    private final Map<Suit, List<Card>> suits;

    private List<SuitEvaluation> suitEvals;
    private int noUptown;
    private int noDowntown;

    // --- Static rank orderings ---
    public static final List<Rank> UPTOWN_ORDER = List.of(
            Rank.ACE, Rank.KING, Rank.QUEEN, Rank.JACK,
            Rank.TEN, Rank.NINE, Rank.EIGHT, Rank.SEVEN,
            Rank.SIX, Rank.FIVE, Rank.FOUR, Rank.THREE, Rank.TWO);

    public static final List<Rank> DOWNTOWN_ORDER = List.of(
            Rank.ACE, Rank.TWO, Rank.THREE, Rank.FOUR, Rank.FIVE,
            Rank.SIX, Rank.SEVEN, Rank.EIGHT, Rank.NINE, Rank.TEN,
            Rank.JACK, Rank.QUEEN, Rank.KING);

    /**
     * Binds this evaluator to a specific player's hand.
     */
    public HandEvaluator(Player player) {
        this.cards = player.getHand().getCards();
        this.jokerCount = JokerUtils.countJokers(player.getHand());

        List<Card> nonJokers = cards.stream()
                .filter(card -> !card.getRank().name().contains("JOKER"))
                .toList();

        this.suits = splitBySuit(nonJokers);
    }

    /**
     * Performs all suit and no-bid evaluations.
     * Must be called before using evaluateAll().
     */
    public void evaluateHand() {
        this.suitEvals = evaluateAllSuits(suits, jokerCount);
        this.noUptown = evaluateNoBidHigh(suits);
        this.noDowntown = evaluateNoBidLow(suits);
    }

    /**
     * Returns all valid Bid objects for this hand.
     * Includes Uptown/Downtown with or without "No", plus evaluated "No" bids.
     */
    public List<FinalBid> evaluateAll(PlayerPos player) {
        List<FinalBid> bidOptions = new ArrayList<>();

        // Suit-based bids
        for (SuitEvaluation eval : suitEvals) {
            for (BidType type : List.of(BidType.UPTOWN, BidType.DOWNTOWN)) {

                FinalBid bid = AiBidOption.fromEvaluation(player, eval, type, false);
                if (bid != null) {
                    bidOptions.add(bid);
                }

            }
        }

        // "No" bids (not based on suit)
        for (BidType type : List.of(BidType.UPTOWN, BidType.DOWNTOWN)) {
            int strength = (type == BidType.UPTOWN) ? noUptown : noDowntown;

            if (strength >= 9 && strength <= 12) {
                int value = strength - 5;
                bidOptions.add(new FinalBid(player, value, true, false, type, null));
            }
        }

        for (FinalBid bid : bidOptions) {
            System.out.println(bid.toString());
        }

        return bidOptions;
    }

    /**
     * Splits a list of cards into a suit-based map.
     */
    public static Map<Suit, List<Card>> splitBySuit(List<Card> fullHand) {
        Map<Suit, List<Card>> suits = new EnumMap<>(Suit.class);
        for (Suit suit : Suit.values()) {
            suits.put(suit, new ArrayList<>());
        }
        for (Card card : fullHand) {
            if (card.getSuit() != null) {
                suits.get(card.getSuit()).add(card);
            }
        }
        return suits;
    }

    /**
     * Evaluates all suits and returns their respective strength values.
     */
    public static List<SuitEvaluation> evaluateAllSuits(Map<Suit, List<Card>> suits, int jokerCount) {
        List<SuitEvaluation> results = new ArrayList<>();

        for (Suit suit : suits.keySet()) {
            List<Card> cards = suits.get(suit);
            List<Rank> ranks = cards.stream().map(Card::getRank).toList();

            int high = evaluateRun(ranks, jokerCount, UPTOWN_ORDER);
            int low = evaluateRun(ranks, jokerCount, DOWNTOWN_ORDER);

            results.add(new SuitEvaluation(suit, high, low, cards.size()));
        }

        for (SuitEvaluation result : results) {
            System.out.println(result.toString());
        }

        return results;
    }

    /**
     * Measures a run in a given order and adjusts length using available jokers.
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
                    i--; // Re-check after shortening
                    j = 0;
                }
            } else {
                break;
            }
        }

        return kept.size() + jokers;
    }

    /**
     * Evaluates a "pure" uninterrupted run from the ACE using a given rank order.
     */
    public static int evaluatePureRun(List<Card> cards, List<Rank> order) {
        Set<Rank> ranksInHand = cards.stream()
                .map(Card::getRank)
                .collect(Collectors.toSet());

        int run = 0;
        for (Rank rank : order) {
            if (ranksInHand.contains(rank)) {
                run++;
            } else {
                break;
            }
        }
        return run;
    }

    /**
     * Calculates the total high-direction run score for a No bid.
     */
    public static int evaluateNoBidHigh(Map<Suit, List<Card>> suits) {
        int run = suits.values().stream()
                .mapToInt(cards -> evaluatePureRun(cards, UPTOWN_ORDER))
                .sum();

        System.out.println("No-Up: " + run);
        return run;
    }

    /**
     * Calculates the total low-direction run score for a No bid.
     */
    public static int evaluateNoBidLow(Map<Suit, List<Card>> suits) {
        int run = suits.values().stream()
                .mapToInt(cards -> evaluatePureRun(cards, DOWNTOWN_ORDER))
                .sum();

        System.out.println("No-Down: " + run);
        return run;
    }

    public FinalBid getForcedMinimumBid(PlayerPos player) {
        evaluateHand(); // Ensure hand is evaluated

        FinalBid bestBid = null;
        int bestStrength = -1;

        // Look through all suits for the best strength (Uptown or Downtown), no
        // No-Trumps
        for (SuitEvaluation eval : suitEvals) {
            for (BidType type : List.of(BidType.UPTOWN, BidType.DOWNTOWN)) {
                int strength = (type == BidType.UPTOWN) ? eval.getUptownStrength() : eval.getDowntownStrength();

                if (strength > bestStrength) {
                    bestStrength = strength;
                    bestBid = new FinalBid(
                            player,
                            4, // Forced minimum bid value
                            false, // not No Trump
                            false, // not passed
                            type,
                            eval.getSuit() // best suit for this bid
                    );
                }
            }
        }

        // Fallback (shouldn't happen, but defensively)
        if (bestBid == null) {
            bestBid = new FinalBid(player, 4, false, false, BidType.UPTOWN, Suit.SPADES);
        }

        return bestBid;
    }

}
