package com.bidwhist.bidwhist_backend.bidding;

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

import com.bidwhist.bidwhist_backend.model.Card.Rank;
import com.bidwhist.bidwhist_backend.model.Card.Suit;
import com.bidwhist.bidwhist_backend.model.Card;

public class HandEvaluator {

    private List<Card> hand;
    private int jokerCount;
    private Map<Suit, List<Card>> suits;
    private List<SuitEvaluation> suitEvals;
    private int noHigh, noLow;

    // for High bids
    public static final List<Rank> HIGH_ORDER = List.of(
        Rank.ACE, Rank.KING, Rank.QUEEN, Rank.JACK,
        Rank.TEN, Rank.NINE, Rank.EIGHT, Rank.SEVEN,
        Rank.SIX, Rank.FIVE, Rank.FOUR, Rank.THREE, Rank.TWO
    );

    // for Low bids
    public static final List<Rank> LOW_ORDER = List.of(
        Rank.ACE, Rank.TWO, Rank.THREE, Rank.FOUR, Rank.FIVE,
        Rank.SIX, Rank.SEVEN, Rank.EIGHT, Rank.NINE, Rank.TEN,
        Rank.JACK, Rank.QUEEN, Rank.KING
    );

    public HandEvaluator(List<Card> hand) {
        this.hand = hand;
        this.jokerCount = (int) hand.stream().filter(card -> card.getRank().name().contains("JOKER")).count();
        List<Card> nonJokers = hand.stream()
            .filter(card -> !card.getRank().name().contains("JOKER"))
            .toList();
        this.suits = splitBySuit(nonJokers);
    }

    public void evaluateHand() {
        suitEvals = evaluateAllSuits(suits, jokerCount);
        noHigh = evaluateNoBidHigh(suits);
        noLow  = evaluateNoBidLow(suits);
    }

    // Returns a list of possible bids (actual bid affected by other bids)
    public List<String> getRankedBids() {
        List<BidOption> options = new ArrayList<>();

        for (SuitEvaluation eval : suitEvals) {
            options.add(new BidOption(eval.getBestStrength(), eval.suit.name(), eval.getBestMode(), false));
        }

        options.add(new BidOption(noHigh, "No", "High", true));
        options.add(new BidOption(noLow, "No", "Low", true));

        return options.stream()
                .map(BidOption::toLabel)
                .filter(Objects::nonNull)
                .distinct()
                .sorted(Comparator.comparingInt(label -> Integer.parseInt(label.replace("No", ""))))
                .toList();
    }

    public static Map<Suit, List<Card>> splitBySuit(List<Card> fullHand) {
        Map<Suit, List<Card>> suits = new HashMap<>();
        for (Suit suit : Suit.values()) {
            suits.put(suit, new ArrayList<>());
        }

        for (Card card : fullHand) {
            if (card.getSuit() != null) { // avoid jokers or nulls
                suits.get(card.getSuit()).add(card);
            }
        }

        return suits;
    }

    public static List<SuitEvaluation> evaluateAllSuits(Map<Suit, List<Card>> suits, int jokerCount) {
        List<SuitEvaluation> results = new ArrayList<>();

        for (Suit suit : suits.keySet()) {
            List<Card> cards = suits.get(suit);
            List<Rank> ranks = cards.stream().map(Card::getRank).toList();

            int count = cards.size();
            int high = evaluateRun(ranks, jokerCount, HIGH_ORDER);
            int low  = evaluateRun(ranks, jokerCount, LOW_ORDER);

            results.add(new SuitEvaluation(suit, high, low, count));
        }

        return results;
    }

    public static int evaluateRun(List<Rank> ranksInSuit, int jokers, List<Rank> order) {
        List<Rank> workingList = new ArrayList<>(order);

        int jokersMissing = 2 - jokers;
        for (int i = 0; i < jokersMissing && !workingList.isEmpty(); i++) {
            workingList.remove(workingList.size() - 1);
        }

        List<Rank> kept = new ArrayList<>();
        for (int i = 0; i < workingList.size(); i++) {
            Rank rank = workingList.get(i);
            if (ranksInSuit.contains(rank)) {
                kept.add(rank);
            } else if (i < workingList.size() - 1) {
                workingList.remove(workingList.size() - 1);
                i--; // adjust index to stay in sync after removal
            } else {
                break;
            }
        }

        return kept.size() + jokers;
    }

    // Counts consecutive cards until gap depending on rank order
    public static int evaluatePureRun(List<Card> cards, List<Rank> order) {
        Set<Rank> ranksInHand = cards.stream().map(Card::getRank).collect(Collectors.toSet());
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

    /* 
     * "No" bids are evaluated by counting runs from Ace (up or down). The total
     * stength is the sum of cards in the consecutive run for each suit. 
     */
    public static int evaluateNoBidHigh(Map<Suit, List<Card>> suits) {
        return suits.values().stream().mapToInt(cards -> evaluatePureRun(cards, HIGH_ORDER)).sum();
    }

    public static int evaluateNoBidLow(Map<Suit, List<Card>> suits) {
        return suits.values().stream().mapToInt(cards -> evaluatePureRun(cards, LOW_ORDER)).sum();
    }
}