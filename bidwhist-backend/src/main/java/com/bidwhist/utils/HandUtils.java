// src/main/java/com/bidwhist/utils/HandUtils.java

package com.bidwhist.utils;

import com.bidwhist.bidding.BidType;
import com.bidwhist.bidding.FinalBid;
import com.bidwhist.model.*;

import java.util.*;
import java.util.stream.Collectors;

public class HandUtils {

    /**
     * Returns the lead suit of the current trick.
     * 
     * Skips cards with null suit (e.g. unassigned jokers during NO_TRUMP).
     * Assumes jokers have suit set appropriately during suited bids.
     */
    public static Suit getLeadSuit(GameState game, List<PlayedCard> trick) {
        if (trick == null || trick.isEmpty()) {
            return null;
        }

        return trick.stream()
                .map(PlayedCard::getCard)
                .map(Card::getSuit)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
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
            Card card, List<Card> playedCards, List<Card> hand, Suit targetSuit, BidType bidType, boolean isNo) {

        if (card == null || card.getSuit() == null || card.getRank() == null || targetSuit == null) {
            return false;
        }

        Suit suitToCheck = isNo ? card.getSuit() : targetSuit;

        if (suitToCheck == null || (!isNo && !card.getSuit().equals(targetSuit))) {
            return false;
        }

        Comparator<Rank> comparator = Rank.rankComparator(bidType, isNo);

        List<Card> knownCards = new ArrayList<>(playedCards);
        knownCards.addAll(hand);

        return Rank.getOrderedRanks().stream()
                .filter(r -> comparator.compare(r, card.getRank()) < 0) // means r would beat our card
                .allMatch(r -> knownCards.stream().anyMatch(c -> {
                    if (c.isJoker())
                        return true;
                    return suitToCheck.equals(c.getSuit()) && c.getRank() == r;
                }));

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
     * Returns the best legal card from the hand that can beat the current winning
     * card.
     * If no legal card can beat it, returns null.
     *
     * Rules:
     * - If player has lead suit, they must follow it.
     * - Otherwise, they may cut with trump suit.
     */
    public static Card canBeat(GameState game, List<PlayedCard> currentTrick, List<Card> myHand) {
        if (game == null || currentTrick == null || currentTrick.isEmpty() || myHand == null || myHand.isEmpty()) {
            return null;
        }

        BidType bidType = game.getBidType();
        boolean isNo = game.getWinningBid().isNo();
        Suit trumpSuit = game.getTrumpSuit();
        Suit leadSuit = HandUtils.getLeadSuit(game, currentTrick);
        Card winningCard = HandUtils.determineTrickWinner(game, currentTrick).getCard();

        boolean hasLeadSuit = HandUtils.hasSuit(myHand, leadSuit);

        Comparator<Card> cardComparator = CardUtils.getCardComparator(game); // 💡 You may need to add this helper

        return myHand.stream()
                .filter(card -> {
                    if (hasLeadSuit && card.getSuit() != leadSuit)
                        return false;
                    return cardComparator.compare(card, winningCard) > 0;
                })
                .min(cardComparator) // lowest card that beats the winner
                .orElse(null);
    }

    /*
     * Determines whether player can win trick.
     */
    public static boolean canWinTrick(GameState game, List<PlayedCard> trick, List<Card> hand) {
        return canBeat(game, trick, hand) != null;
    }

    /**
     * Checks whether both opponents of the given player are void in the specified
     * suit.
     *
     * @param thisPlayer  The player asking the question.
     * @param targetSuit  The suit to check.
     * @param suitVoidMap The full suitVoidMap from GameState.
     * @return true if both opponents are void in the target suit, false otherwise.
     */
    public static boolean areOpponentsSuitVoid(GameState game, PlayerPos thisPlayer, Suit targetSuit) {
        Map<PlayerPos, Map<Suit, Boolean>> suitVoidMap = game.getSuitVoidMap();

        if (thisPlayer == null || targetSuit == null || suitVoidMap == null) {
            return false;
        }

        // Determine opponents
        Set<PlayerPos> opponents = new HashSet<>();
        switch (thisPlayer) {
            case P1 -> {
                opponents.add(PlayerPos.P2);
                opponents.add(PlayerPos.P4);
            }
            case P2 -> {
                opponents.add(PlayerPos.P1);
                opponents.add(PlayerPos.P3);
            }
            case P3 -> {
                opponents.add(PlayerPos.P2);
                opponents.add(PlayerPos.P4);
            }
            case P4 -> {
                opponents.add(PlayerPos.P1);
                opponents.add(PlayerPos.P3);
            }
        }

        // Check if both opponents are marked as void in the given suit
        for (PlayerPos opponent : opponents) {
            Map<Suit, Boolean> playerVoidMap = suitVoidMap.get(opponent);
            if (playerVoidMap == null || !Boolean.TRUE.equals(playerVoidMap.get(targetSuit))) {
                return false; // At least one opponent is not void
            }
        }

        return true; // Both opponents are void in this suit
    }

    /**
     * Checks if the AI's partner is void (out) of the given suit.
     *
     * @param game       The current game state.
     * @param thisPlayer The current player position.
     * @param targetSuit The suit to check for void status.
     * @return true if the partner is void of the suit; false otherwise.
     */
    public static boolean isPartnerSuitVoid(GameState game, PlayerPos thisPlayer, Suit targetSuit) {
        if (game == null || targetSuit == null)
            return false;

        Map<PlayerPos, Map<Suit, Boolean>> voidMap = game.getSuitVoidMap();
        if (voidMap == null || voidMap.isEmpty())
            return false;

        // Determine partner position
        PlayerPos partner = (thisPlayer.ordinal() + 2) % 4 == 0
                ? PlayerPos.values()[0]
                : PlayerPos.values()[(thisPlayer.ordinal() + 2) % 4];

        // Check if partner's void map has this suit marked true
        return voidMap.getOrDefault(partner, Collections.emptyMap())
                .getOrDefault(targetSuit, false);
    }

    /**
     * Checks if the player’s hand contains at least one card of the specified suit.
     *
     * @param hand The list of cards in the player's hand.
     * @param suit The suit to check for.
     * @return true if the hand contains at least one card of the given suit; false
     *         otherwise.
     */
    public static boolean hasSuit(List<Card> hand, Suit suit) {
        if (hand == null || suit == null) {
            return false;
        }

        return hand.stream()
                .anyMatch(card -> suit.equals(card.getSuit()));
    }

    /*
     * Determines the winner of the current trick.
     * Applies different logic for NO_TRUMP, DOWNTOWN, and UPTOWN bids.
     * Track players who play out of suit for AI advanced logic.
     */
    public static PlayedCard determineTrickWinner(GameState game, List<PlayedCard> trick) {
        if (trick == null || trick.isEmpty()) {
            throw new IllegalArgumentException("Cannot determine trick winner: trick is empty.");
        }

        Suit leadSuitForComparison = getLeadSuit(game, trick);
        FinalBid winningBid = game.getWinningBid();
        BidType bidType = game.getBidType();
        boolean isNoBid = winningBid != null && winningBid.isNo();
        Suit trumpSuit = isNoBid ? null : game.getTrumpSuit();

        // Track suit voids
        for (PlayedCard played : trick) {
            Card card = played.getCard();
            PlayerPos player = played.getPlayer();

            if (card.isJoker())
                continue;

            if (card.getSuit() != null && !card.getSuit().equals(leadSuitForComparison)) {
                game.getSuitVoidMap()
                        .computeIfAbsent(player, p -> new EnumMap<>(Suit.class))
                        .put(leadSuitForComparison, true);
            }
        }

        // Set up rank comparator
        Comparator<Rank> rankComparator = Rank.rankComparator(bidType, isNoBid);

        // For No-Trump: only cards of lead suit, jokers excluded
        if (isNoBid) {
            Comparator<Card> cardComparator = CardUtils.getCardComparator(game);

            return trick.stream()
                    .filter(pc -> !pc.getCard().isJoker())
                    .filter(pc -> pc.getCard().getSuit() == leadSuitForComparison)
                    .max(Comparator.comparing(PlayedCard::getCard, cardComparator))
                    .orElseThrow(() -> new IllegalStateException(
                            "BUG: No eligible cards found in NO_TRUMP bid. Check card legality."));
        }

        // Otherwise: trump > lead > others, compare by rank
        return trick.stream()
                .max(Comparator.<PlayedCard>comparingInt(pc -> {
                    Card c = pc.getCard();
                    if (c.isJoker())
                        return 3000; // Jokers always win
                    if (trumpSuit != null && c.getSuit() == trumpSuit)
                        return 2000;
                    if (c.getSuit() == leadSuitForComparison)
                        return 1000;
                    return 0;
                })
                        .thenComparing(pc -> pc.getCard().getRank(), rankComparator))
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
        if (hand == null || hand.isEmpty())
            return null;

        Suit leadSuit = getLeadSuit(game, trick);
        boolean isNo = game.getWinningBid() != null && game.getWinningBid().isNo();

        Comparator<Card> cardComparator = CardUtils.getCardComparator(game);

        boolean hasLeadSuit = hand.stream().anyMatch(c -> leadSuit != null && leadSuit.equals(c.getSuit()));

        if (hasLeadSuit) {
            return hand.stream()
                    .filter(c -> leadSuit.equals(c.getSuit()))
                    .min(cardComparator)
                    .orElse(null);
        }

        // In No Trump bids, avoid wasting jokers
        if (isNo) {
            return hand.stream()
                    .filter(c -> !c.isJoker())
                    .min(cardComparator)
                    .orElseGet(() -> hand.stream().findFirst().orElse(null)); // all jokers? return any
        }

        // Suited bid fallback: just lowest ranked by game-aware rules
        return hand.stream()
                .min(cardComparator)
                .orElse(null);
    }

    /**
     * Returns the lowest-ranked non-trump card that follows the lead suit.
     * If none exists, returns the lowest-ranked non-trump card in hand.
     * If still none exists, returns the lowest trump card.
     * Respects Downtown bid logic for rank comparison.
     */
    public static Card getLowestLegalNonTrumpCard(GameState game, List<PlayedCard> trick, List<Card> hand) {
        if (hand == null || hand.isEmpty()) {
            return null;
        }

        Suit leadSuit = getLeadSuit(game, trick);
        Suit trumpSuit = game.getTrumpSuit();
        BidType bidType = game.getBidType();
        boolean isNo = game.getWinningBid() != null && game.getWinningBid().isNo();

        Comparator<Rank> rankComparator = Rank.rankComparator(bidType, isNo);

        // Step 1: Try to follow the lead suit but avoid trump
        Optional<Card> legalNonTrumpLeadSuit = hand.stream()
                .filter(c -> c.getSuit() != null && c.getSuit().equals(leadSuit) && !c.getSuit().equals(trumpSuit))
                .min(Comparator.comparing(Card::getRank, rankComparator));

        if (legalNonTrumpLeadSuit.isPresent()) {
            return legalNonTrumpLeadSuit.get();
        }

        // Step 2: Any non-trump card
        Optional<Card> anyNonTrump = hand.stream()
                .filter(c -> c.getSuit() != null && !c.getSuit().equals(trumpSuit))
                .min(Comparator.comparing(Card::getRank, rankComparator));

        if (anyNonTrump.isPresent()) {
            return anyNonTrump.get();
        }

        // Step 3: In a No-Trump bid, return any joker (if all else fails)
        if (isNo) {
            return hand.stream().filter(Card::isJoker).findFirst().orElse(null);
        }

        // Step 4: Default to lowest trump card
        return hand.stream()
                .filter(c -> c.getSuit() != null && c.getSuit().equals(trumpSuit))
                .min(Comparator.comparing(Card::getRank, rankComparator))
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
        if (hand == null || hand.isEmpty())
            return null;

        Suit trumpSuit = game.getTrumpSuit();
        Suit leadSuit = getLeadSuit(game, trick);
        BidType bidType = game.getBidType();
        boolean isNo = game.getWinningBid() != null && game.getWinningBid().isNo();

        Comparator<Rank> rankComparator = Rank.rankComparator(bidType, isNo);

        // First: Try to follow lead suit and play highest of those
        Optional<Card> leadCandidate = hand.stream()
                .filter(c -> c.getSuit() != null && c.getSuit().equals(leadSuit))
                .max(Comparator.comparing(Card::getRank, rankComparator));

        if (leadCandidate.isPresent()) {
            return leadCandidate.get();
        }

        // Second: Try to cut with highest trump
        Optional<Card> trumpCandidate = hand.stream()
                .filter(c -> c.getSuit() != null && c.getSuit().equals(trumpSuit))
                .max(Comparator.comparing(Card::getRank, rankComparator));

        if (trumpCandidate.isPresent()) {
            return trumpCandidate.get();
        }

        // Last: No legal suit—return highest-ranked card from full hand
        return hand.stream()
                .max(Comparator.comparing(Card::getRank, rankComparator))
                .orElse(null);
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

        Comparator<Card> cardComparator = CardUtils.getCardComparator(game);

        return hand.stream()
                .max(cardComparator)
                .orElse(hand.get(0)); // Fallback
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

        BidType bidType = game.getBidType();
        boolean isNo = game.getWinningBid() != null && game.getWinningBid().isNo();

        Comparator<Rank> rankComparator = Rank.rankComparator(bidType, isNo);

        return hand.stream()
                .filter(c -> suit.equals(c.getSuit()))
                .max(Comparator.comparing(Card::getRank, rankComparator))
                .orElse(null);
    }

    /**
     * Returns the lowest-ranked card of the specified suit from the given hand.
     * If no cards of the suit are found, returns null.
     *
     * @param game The current GameState (used to check for Downtown rules).
     * @param hand The list of cards in the player's hand.
     * @param suit The target suit to search for.
     * @return The lowest-ranked card of the given suit, or null if none exists.
     */
    public static Card getLowestOfSuit(GameState game, List<Card> hand, Suit suit) {
        if (game == null || hand == null || suit == null) {
            return null;
        }

        BidType bidType = game.getBidType();
        boolean isNo = game.getWinningBid() != null && game.getWinningBid().isNo();

        Comparator<Rank> rankComparator = Rank.rankComparator(bidType, isNo);

        return hand.stream()
                .filter(c -> suit.equals(c.getSuit()))
                .min((a, b) -> rankComparator.compare(a.getRank(), b.getRank()))
                .orElse(null);
    }

    /**
     * Checks if the hand contains a non-trump suit with 2 or fewer cards.
     * This is useful for finding a discard suit to potentially go void in.
     *
     * @param hand      The list of cards in hand.
     * @param trumpSuit The current trump suit (to exclude).
     * @return true if a discardable suit is found, false otherwise.
     */
    public static boolean hasDiscardSuit(List<Card> hand, Suit trumpSuit) {
        if (hand == null || hand.isEmpty()) {
            return false;
        }

        // Count cards per non-trump suit
        Map<Suit, Long> suitCounts = hand.stream()
                .filter(c -> c.getSuit() != null && !c.getSuit().equals(trumpSuit))
                .collect(Collectors.groupingBy(Card::getSuit, Collectors.counting()));

        // Look for any non-trump suit with 2 or fewer cards
        return suitCounts.values().stream().anyMatch(count -> count <= 2);
    }

    /**
     * Returns the best discard card from hand:
     * Prioritizes suits where the player holds 2 or fewer cards and avoids trump
     * suit.
     * Within that suit, selects the lowest-ranked card.
     *
     * @param game The current game state.
     * @param hand The AI player's hand.
     * @return The selected discard card, or any lowest-ranked card if no
     *         discardable suit found.
     */
    public static Card getDiscardCard(GameState game, List<Card> hand) {
        if (hand == null || hand.isEmpty() || game == null) {
            return null;
        }

        Suit trumpSuit = game.getTrumpSuit();
        BidType bidType = game.getBidType();
        boolean isNo = game.getWinningBid() != null && game.getWinningBid().isNo();

        Comparator<Rank> rankComparator = Rank.rankComparator(bidType, isNo);

        // Group cards by suit (excluding trump)
        Map<Suit, List<Card>> nonTrumpSuitGroups = hand.stream()
                .filter(c -> c.getSuit() != null && !c.getSuit().equals(trumpSuit))
                .collect(Collectors.groupingBy(Card::getSuit));

        // Prefer to discard from suits with ≤ 2 cards
        for (Map.Entry<Suit, List<Card>> entry : nonTrumpSuitGroups.entrySet()) {
            List<Card> cards = entry.getValue();
            if (cards.size() <= 2) {
                return cards.stream()
                        .min(Comparator.comparing(Card::getRank, rankComparator))
                        .orElse(null);
            }
        }

        // Fallback: discard lowest-ranked non-trump card
        return hand.stream()
                .filter(c -> c.getSuit() != null && !c.getSuit().equals(trumpSuit))
                .min(Comparator.comparing(Card::getRank, rankComparator))
                .orElse(hand.get(0)); // last fallback
    }

    /**
     * Checks whether the AI's partner has already played in the current trick.
     *
     * @param aiPlayer The AI's position (e.g., P1, P2).
     * @param trick    The current list of played cards.
     * @return true if the partner has played in this trick; false otherwise.
     */
    public static boolean partnerHasPlayed(PlayerPos aiPlayer, List<PlayedCard> trick) {
        if (aiPlayer == null || trick == null || trick.isEmpty()) {
            return false;
        }

        PlayerPos partner = getPartner(aiPlayer);
        return trick.stream()
                .anyMatch(pc -> pc.getPlayer() == partner);
    }

    /**
     * Returns the partner PlayerPos for the given player.
     * Assumes P1–P3 and P2–P4 are always partnered.
     */
    public static PlayerPos getPartner(PlayerPos player) {
        switch (player) {
            case P1:
                return PlayerPos.P3;
            case P2:
                return PlayerPos.P4;
            case P3:
                return PlayerPos.P1;
            case P4:
                return PlayerPos.P2;
            default:
                throw new IllegalArgumentException("Unknown player position: " + player);
        }
    }

    /**
     * Finds the next higher card in the same suit as the target card from a given
     * list of cards.
     * Useful when trying to overtake the current winning card.
     *
     * @param winningCard The card currently winning the trick.
     * @param hand        The list of cards to search (e.g., player's hand).
     * @return The next higher card in the same suit, or null if none found.
     */
    public static Card getNextHigherCard(GameState game, Card winningCard, List<Card> hand) {
        if (game == null || winningCard == null || winningCard.getSuit() == null ||
                winningCard.getRank() == null || hand == null) {
            return null;
        }

        BidType bidType = game.getBidType();
        boolean isNo = game.getWinningBid() != null && game.getWinningBid().isNo();

        Comparator<Rank> comparator = Rank.rankComparator(bidType, isNo);
        Suit targetSuit = winningCard.getSuit();
        Rank winningRank = winningCard.getRank();

        return hand.stream()
                .filter(c -> c.getSuit() == targetSuit)
                .filter(c -> comparator.compare(c.getRank(), winningRank) > 0)
                .min(Comparator.comparing(Card::getRank, comparator))
                .orElse(null);
    }

    /**
     * Returns the list of cards from the hand that are legally playable
     * according to standard trick-taking rules.
     * 
     * If the lead suit is present in the player's hand, only cards of that suit
     * are considered legal. Otherwise, all cards are playable.
     * 
     * @param game  the current game state
     * @param trick the current trick in progress
     * @param hand  the player's full hand
     * @return a list of legally playable cards from the hand
     */
    public static List<Card> getPlayableHand(GameState game, List<PlayedCard> trick, List<Card> hand) {
        if (hand == null || hand.isEmpty())
            return Collections.emptyList();

        Suit leadSuit = getLeadSuit(game, trick);
        if (leadSuit == null || !hasSuit(hand, leadSuit)) {
            return hand;
        }

        return hand.stream()
                .filter(c -> c.getSuit() == leadSuit)
                .collect(Collectors.toList());
    }

}
