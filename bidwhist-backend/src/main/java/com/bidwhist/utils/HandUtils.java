// src/main/java/com/bidwhist/utils/HandUtils.java

package com.bidwhist.utils;

import com.bidwhist.bidding.BidType;
import com.bidwhist.model.*;

import java.util.*;

public class HandUtils {

    /*
     * Returns the lead suit for a given trick.
     * Skips jokers if NO_TRUMP is active.
     */
    public static Suit getLeadSuit(GameState game, List<PlayedCard> trick) {
        if (trick == null || trick.isEmpty()) {
            return null;
        }

        // Step 1: Detect lead suit for lambda-safe usage
        final Suit initialLeadSuit = trick.stream()
                .map(PlayedCard::getCard)
                .filter(c -> !c.isJoker())
                .map(Card::getSuit)
                .findFirst()
                .orElse(null);

        // Step 2: Declare mutable version separately (don’t reuse the lambda one!)
        Suit leadSuitForComparison;

        // Optional fallback for No Trump where joker leads
        if (initialLeadSuit == null && game.getWinningBid() != null && game.getWinningBid().isNo()) {
            leadSuitForComparison = trick.stream()
                    .map(PlayedCard::getCard)
                    .filter(c -> !c.isJoker())
                    .map(Card::getSuit)
                    .filter(Objects::nonNull)
                    .findFirst()
                    .orElse(null);
        } else {
            leadSuitForComparison = initialLeadSuit;
        }

        return leadSuitForComparison;
    }

    /**
     * Checks whether all higher-ranked cards in the same suit have already been
     * played. This helps AI
     * determine if a card is now the highest in play.
     * 
     * @param candidate   the card being considered
     * @param playedCards list of cards that have already been played
     * @param trumpSuit
     * @param isNo
     * @return true if no higher-ranked card in the same suit remains unplayed
     */
    public static boolean allHigherCardsPlayed(
            Card card, List<Card> playedCards, Suit targetSuit, boolean isNo) {
        if (card == null || card.getSuit() == null || card.getRank() == null || targetSuit == null) {
            return false;
        }

        Suit suitToCheck = isNo ? card.getSuit() : targetSuit;

        if (suitToCheck == null || (!isNo && !card.getSuit().equals(targetSuit))) {
            return false;
        }

        int candidateValue = card.getRank().getValue();
        return Rank.getOrderedRanks().stream()
                .map(Rank::getValue)
                .filter(v -> v > candidateValue)
                .allMatch(
                        v -> playedCards.stream()
                                .anyMatch(c -> targetSuit.equals(c.getSuit()) && c.getRank().getValue() == v));
    }

    /*
     * Checks if the AI’s partner is currently winning the trick.
     */
    public static boolean partnerIsWinning(PlayerPos player, PlayedCard winningCard) {
        if (player == null || winningCard == null || winningCard.getPlayer() == null) {
            return false;
        }

        int current = player.ordinal(); // 0 to 3
        int played = winningCard.getPlayer().ordinal();

        // P1 (0) & P3 (2), P2 (1) & P4 (3) are partners — difference of 2
        return Math.abs(current - played) == 2;
    }

    /**
     * Returns the best card from the hand that can beat the current trick’s winning
     * card.
     * If no card can beat it, returns null.
     */
    public static Card canBeat(GameState game, List<PlayedCard> currentTrick, List<Card> myHand) {
        if (game == null || currentTrick == null || currentTrick.isEmpty() || myHand == null || myHand.isEmpty()) {
            return null;
        }

        BidType bidType = game.getBidType();
        boolean isDowntown = bidType == BidType.DOWNTOWN;
        Suit trumpSuit = game.getTrumpSuit();
        Card winningCard = HandUtils.determineTrickWinner(game, currentTrick).getCard();
        Suit winningSuit = winningCard.getSuit();

        // First: Try to follow suit and beat the winning card
        Optional<Card> bestInSuit = myHand.stream()
                .filter(c -> c.getSuit() == winningSuit)
                .filter(c -> {
                    int cVal = c.getRank().getValue();
                    int wVal = winningCard.getRank().getValue();
                    return isDowntown ? cVal < wVal : cVal > wVal;
                })
                .max(Comparator.comparingInt(c -> {
                    int val = c.getRank().getValue();
                    return isDowntown ? (15 - val) : val;
                }));

        if (bestInSuit.isPresent()) {
            return bestInSuit.get();
        }

        // Second: Try to cut with lowest trump
        return myHand.stream()
                .filter(c -> c.getSuit() == trumpSuit)
                .min(Comparator.comparingInt(c -> c.getRank().getValue()))
                .orElse(null);
    }

    /*
     * Determines whether player can win trick.
     */
    public static boolean canWinTrick(GameState game, List<PlayedCard> trick, List<Card> hand) {
        return canBeat(game, trick, hand) != null;
    }

    /**
     * Determines which card is currently winning a trick.
     *
     * It checks each card in the trick. Trump cards beat non-trump cards. If
     * both are trump or both are the lead suit, the higher rank wins.
     */
    public static PlayedCard getWinningCard(List<PlayedCard> trick, Suit trumpSuit) {
        if (trick.isEmpty()) {
            return null;
        }

        // Determine lead suit from first non-null-suit card (skip jokers or undefined)
        Suit leadSuit = trick.stream()
                .map(PlayedCard::getCard)
                .map(Card::getSuit)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);

        // Start with first card as placeholder winner
        PlayedCard winning = trick.get(0);

        for (PlayedCard pc : trick) {
            Card card = pc.getCard();
            Suit suit = card.getSuit();

            boolean isTrump = trumpSuit != null && trumpSuit.equals(suit);
            boolean winningIsTrump = trumpSuit != null && trumpSuit.equals(winning.getCard().getSuit());

            if (isTrump && !winningIsTrump) {
                winning = pc;
            } else if (isTrump == winningIsTrump && suit == leadSuit) {
                int currentVal = card.getRank().getValue();
                int winningVal = winning.getCard().getRank().getValue();
                if (currentVal > winningVal) {
                    winning = pc;
                }
            }
        }

        return winning;
    }

    /*
     * Determines the winner of the current trick.
     * Applies different logic for NO_TRUMP, DOWNTOWN, and UPTOWN bids.
     */
    public static PlayedCard determineTrickWinner(GameState game, List<PlayedCard> trick) {
        if (trick == null || trick.isEmpty()) {
            throw new IllegalArgumentException("Cannot determine trick winner: trick is empty.");
        }

        // Step 1: Detect lead suit for lambda-safe usage
        final Suit initialLeadSuit = trick.stream()
                .map(PlayedCard::getCard)
                .filter(c -> !c.isJoker())
                .map(Card::getSuit)
                .findFirst()
                .orElse(null);

        // Step 2: Declare mutable version separately (don’t reuse the lambda one!)
        Suit leadSuitForComparison;

        // Optional fallback for No Trump where joker leads
        if (initialLeadSuit == null && game.getWinningBid() != null && game.getWinningBid().isNo()) {
            leadSuitForComparison = trick.stream()
                    .map(PlayedCard::getCard)
                    .filter(c -> !c.isJoker())
                    .map(Card::getSuit)
                    .filter(Objects::nonNull)
                    .findFirst()
                    .orElse(null);
        } else {
            leadSuitForComparison = initialLeadSuit;
        }

        BidType bidType = game.getBidType();
        boolean isNoBid = game.getWinningBid() != null && game.getWinningBid().isNo();
        Suit trumpSuit = isNoBid ? null : game.getTrumpSuit();

        // Excludes jokers from being eligible to win if bidType == NO_TRUMP
        if (isNoBid) {
            return trick.stream()
                    .filter(pc -> !pc.getCard().isJoker()) // ❌ Exclude jokers (they can’t win)
                    .filter(pc -> pc.getCard().getSuit() == leadSuitForComparison) // ✅ Only cards that match lead suit
                    .max(Comparator.comparingInt(pc -> {
                        int value = pc.getCard().getRank().getValue(); // get rank number (e.g., 2 → 2, King → 13)
                        return bidType == BidType.DOWNTOWN ? (15 - value) : value;
                        // ⬆️ If Downtown, invert the score (low wins). If Uptown, use normal rank.
                    }))
                    .orElseThrow(
                            () -> new IllegalStateException(
                                    "BUG: determineTrickWinner - No eligible non-joker cards of lead suit found in NO_TRUMP bid. "
                                            +
                                            "This should never happen in a legal game. Check suit-following enforcement and card assignments."));
        }

        /* DOWNTOWN: inverted rank values except ACE and JOKERs */
        if (bidType == BidType.DOWNTOWN) {
            return trick.stream()
                    .max(
                            Comparator.comparing(
                                    pc -> {
                                        Card c = pc.getCard();
                                        boolean isTrump = trumpSuit != null && c.getSuit() == trumpSuit;
                                        boolean isLead = c.getSuit() == leadSuitForComparison;
                                        Rank rank = c.getRank();
                                        int rankValue;

                                        if (rank == Rank.JOKER_B || rank == Rank.JOKER_S || rank == Rank.ACE) {
                                            rankValue = rank.getValue();
                                        } else {
                                            rankValue = 15 - rank.getValue();
                                        }

                                        return (isTrump ? 1000 : (isLead ? 100 : 0)) + rankValue;
                                    }))
                    .orElseThrow();
        }

        /* UPTOWN or standard: normal rank values with trump > lead > others */
        return trick.stream()
                .max(
                        Comparator.comparing(
                                pc -> {
                                    Card c = pc.getCard();
                                    boolean isTrump = trumpSuit != null && c.getSuit() == trumpSuit;
                                    boolean isLead = c.getSuit() == leadSuitForComparison;
                                    int rankValue = c.getRank().getValue();
                                    return (isTrump ? 1000 : (isLead ? 100 : 0)) + rankValue;
                                }))
                .orElseThrow();
    }

    /**
     * Selects the lowest-value legal card the AI can play for the current trick.
     *
     * Priority is as follows:
     * 1. Return the lowest card that follows the lead suit (if any).
     * 2. If unable to follow suit, return the lowest trump card.
     * 3. If no trump or lead suit cards are available, return the lowest card
     * overall.
     *
     * Rank comparison respects UPTOWN (high wins) and DOWNTOWN (low wins) bid
     * types.
     *
     * @param game  The current game state, including trump and bid type.
     * @param trick The cards played so far in the current trick.
     * @param hand  The list of cards in the AI's hand.
     * @return The lowest legal card to play based on current trick and rules.
     */
    public static Card getLowestLegalCard(GameState game, List<PlayedCard> trick, List<Card> hand) {
        Suit trumpSuit = game.getTrumpSuit();
        Suit leadSuit = getLeadSuit(game, trick);
        boolean isDowntown = game.getBidType() == BidType.DOWNTOWN;

        Optional<Card> candidate = hand.stream()
                .filter(c -> c.getSuit() != null && c.getSuit().equals(leadSuit))
                .min(Comparator.comparingInt(c -> {
                    int val = c.getRank().getValue();
                    return isDowntown ? (15 - val) : val;
                }));

        if (candidate.isPresent()) {
            return candidate.get();
        }

        Optional<Card> trumpCandidate = hand.stream()
                .filter(c -> c.getSuit() == trumpSuit)
                .min(Comparator.comparingInt(c -> {
                    int val = c.getRank().getValue();
                    return isDowntown ? (15 - val) : val;
                }));

        if (trumpCandidate.isPresent()) {
            return trumpCandidate.get();
        }

        return hand.stream()
                .min(Comparator.comparingInt(c -> {
                    int val = c.getRank().getValue();
                    return isDowntown ? (15 - val) : val;
                }))
                .orElse(null); // null-safe fallback
    }

    /**
     * Returns the lowest-ranked legal card from the hand that is NOT a trump card,
     * while still following the lead suit if possible.
     *
     * If the player has cards in the lead suit that are not trump, return the
     * lowest of those.
     * If all lead-suit cards are trump, or player has no lead-suit cards, return
     * the lowest non-trump card.
     * If none qualify, return null.
     *
     * @param game  The current game state (used to determine trump suit and bid
     *              type).
     * @param trick The current trick to extract lead suit.
     * @param hand  The player's hand.
     * @return The lowest legal non-trump card, or null if none available.
     */
    public static Card getLowestLegalNonTrumpCard(GameState game, List<PlayedCard> trick, List<Card> hand) {
        if (hand == null || hand.isEmpty()) {
            return null;
        }

        Suit leadSuit = getLeadSuit(game, trick);
        Suit trumpSuit = game.getTrumpSuit();
        boolean isDowntown = game.getBidType() == BidType.DOWNTOWN;

        // First: Find lowest card that follows lead suit and is NOT trump
        Optional<Card> legalNonTrumpLeadSuit = hand.stream()
                .filter(c -> c.getSuit() != null && c.getSuit().equals(leadSuit) && !c.getSuit().equals(trumpSuit))
                .min(Comparator.comparingInt(c -> {
                    int val = c.getRank().getValue();
                    return isDowntown ? (15 - val) : val;
                }));

        if (legalNonTrumpLeadSuit.isPresent()) {
            return legalNonTrumpLeadSuit.get();
        }

        // Second: Try any other non-trump card (if no lead-suit legal non-trump
        // available)
        return hand.stream()
                .filter(c -> c.getSuit() != null && !c.getSuit().equals(trumpSuit))
                .min(Comparator.comparingInt(c -> {
                    int val = c.getRank().getValue();
                    return isDowntown ? (15 - val) : val;
                }))
                .orElse(null);
    }

    /**
     * Returns the highest-ranked legal card the player can play in the current
     * trick.
     *
     * If the player has cards in the lead suit, chooses the highest of those.
     * Otherwise, tries to play the highest trump suit card (if any).
     * If neither lead nor trump suits are available, returns the highest overall
     * card.
     *
     * Ranking is based on the game’s bid type:
     * - Uptown: higher rank values are better (Ace high)
     * - Downtown: lower rank values are better (2 low)
     *
     * @param game  The current game state
     * @param trick The current trick (to determine lead suit)
     * @param hand  The player's hand
     * @return The highest legal card to play
     */
    public static Card getHighestLegalCard(GameState game, List<PlayedCard> trick, List<Card> hand) {
        Suit trumpSuit = game.getTrumpSuit();
        Suit leadSuit = getLeadSuit(game, trick);
        boolean isDowntown = game.getBidType() == BidType.DOWNTOWN;

        // First: Try to follow lead suit and play highest of those
        Optional<Card> leadCandidate = hand.stream()
                .filter(c -> c.getSuit() != null && c.getSuit().equals(leadSuit))
                .max(Comparator.comparingInt(c -> {
                    int val = c.getRank().getValue();
                    return isDowntown ? (15 - val) : val;
                }));

        if (leadCandidate.isPresent()) {
            return leadCandidate.get();
        }

        // Second: Try to cut with highest trump
        Optional<Card> trumpCandidate = hand.stream()
                .filter(c -> c.getSuit() != null && c.getSuit().equals(trumpSuit))
                .max(Comparator.comparingInt(c -> {
                    int val = c.getRank().getValue();
                    return isDowntown ? (15 - val) : val;
                }));

        if (trumpCandidate.isPresent()) {
            return trumpCandidate.get();
        }

        // Last: No legal suit—return highest-ranked card from full hand
        return getHighestRankedCard(game, hand);
    }

    /**
     * Returns the highest-ranked card in the player’s hand.
     * 
     * Ranking is determined by the game’s bid type (Uptown or Downtown).
     * In Uptown, higher rank values are better (Ace high).
     * In Downtown, lower rank values are better (2 low).
     *
     * No filtering is done by suit or legality—this purely picks the
     * strongest-ranked card.
     *
     * @param game The current game state (used to determine bid type)
     * @param hand The player's hand
     * @return The card with the highest effective rank, or the first card if hand
     *         is empty
     */
    public static Card getHighestRankedCard(GameState game, List<Card> hand) {
        if (hand == null || hand.isEmpty()) {
            return null;
        }

        boolean isDowntown = game.getBidType() == BidType.DOWNTOWN;

        return hand.stream()
                .max(Comparator.comparingInt(c -> {
                    int val = c.getRank().getValue();
                    return isDowntown ? (15 - val) : val;
                }))
                .orElse(hand.get(0)); // Fallback (though shouldn't be needed if hand is non-empty)
    }

    /**
     * Returns the highest-ranked card of a given suit from the player's hand.
     *
     * @param game The current game state (used to check for Downtown rules).
     * @param hand The list of cards in the player's hand.
     * @param suit The suit to search for.
     * @return The highest-ranked card of the specified suit, or null if none exist.
     */
    public static Card getHighestOfSuit(GameState game, List<Card> hand, Suit suit) {
        if (suit == null || hand == null || hand.isEmpty()) {
            return null;
        }

        boolean isDowntown = game.getBidType() == BidType.DOWNTOWN;

        return hand.stream()
                .filter(c -> suit.equals(c.getSuit()))
                .max(Comparator.comparingInt(c -> {
                    int val = c.getRank().getValue();
                    return isDowntown ? (15 - val) : val;
                }))
                .orElse(null);
    }

}
