package com.bidwhist.utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.bidwhist.bidding.BidType;
import com.bidwhist.bidding.FinalBid;
import com.bidwhist.bidding.HandEvaluator;
import com.bidwhist.bidding.InitialBid;
import com.bidwhist.dto.Animation;
import com.bidwhist.dto.AnimationType;
import com.bidwhist.model.Book;
import com.bidwhist.model.Card;
import com.bidwhist.model.Difficulty;
import com.bidwhist.model.GamePhase;
import com.bidwhist.model.GameState;
import com.bidwhist.model.PlayedCard;
import com.bidwhist.model.Player;
import com.bidwhist.model.PlayerPos;
import com.bidwhist.model.Rank;
import com.bidwhist.model.Suit;
import com.bidwhist.model.Team;
import com.bidwhist.utils.HandUtils;

public class AIUtils {

    /*
     * Evaluates a given AI player's hand and returns the strongest valid bid.
     * Saves final bid to cache for later reference.
     */
    public static InitialBid generateAIBid(GameState game, Player ai) {

        HandEvaluator evaluator = new HandEvaluator(ai);
        evaluator.evaluateHand();

        List<FinalBid> bidOptions = evaluator.evaluateAll(ai.getPosition());
        InitialBid currentHigh = game.getHighestBid();

        List<FinalBid> strongerBids = bidOptions.stream()
                .filter(bid -> currentHigh == null || bid.getInitialBid().compareTo(currentHigh) > 0)
                .toList();

        if (strongerBids.isEmpty()) {
            return InitialBid.pass(ai.getPosition());
        }

        FinalBid bestBid = strongerBids.stream().max(Comparator.comparingInt(FinalBid::getValue)).orElse(null);

        game.getFinalBidCache().put(ai.getPosition(), bestBid);

        return bestBid.getInitialBid();
    }

    /*
     * Executes AI bids until it’s the human player’s turn or all bids are
     * completed.
     */
    public static void processAllBids(GameState game) {
        while (game.getBids().size() < 4) {
            Player nextBidder = game.getPlayers().get(game.getBidTurnIndex());

            if (!nextBidder.isAI()) {
                break;
            }

            List<InitialBid> currentBids = game.getBids();
            boolean isFinalBidder = currentBids.size() == 3;
            long passedCount = currentBids.stream().filter(InitialBid::isPassed).count();

            InitialBid aiBid;
            FinalBid aiFinalBid;

            if (isFinalBidder && passedCount == 3) {
                HandEvaluator aiHandEval = new HandEvaluator(nextBidder);
                aiFinalBid = aiHandEval.getForcedMinimumBid(nextBidder.getPosition());
                aiBid = aiFinalBid.getInitialBid();
                game.getFinalBidCache().put(nextBidder.getPosition(), aiFinalBid);
            } else {
                aiBid = AIUtils.generateAIBid(game, nextBidder);
                System.out.println(
                        "DEBUG: "
                                + nextBidder.getName()
                                + " (AI) bids "
                                + aiBid.getValue()
                                + " is No?: "
                                + aiBid.isNo());
            }

            game.addBid(aiBid);

            if (!aiBid.isPassed()
                    && (game.getHighestBid() == null || aiBid.compareTo(game.getHighestBid()) > 0)) {
                game.setHighestBid(aiBid);
            }

            game.setBidTurnIndex((game.getBidTurnIndex() + 1) % game.getPlayers().size());
        }
    }

    /*
     * Main AI play loop. AI players will continue playing until a human's turn.
     * Handles animations, trick resolution, and transitions to scoring or new hand.
     */
    public static void autoPlayAITurns(GameState game) {
        while (true) {
            Player current = game.getPlayers().get(game.getCurrentTurnIndex());
            if (!current.isAI()) {
                break;
            }

            Card chosenCard = chooseCardForAI(game, current, game.getCurrentTrick());
            System.out.println(
                    "DEBUG: "
                            + current.getName()
                            + " played card: "
                            + chosenCard
                            + " "
                            + game.getCurrentTurnIndex());

            current.getHand().getCards().remove(chosenCard);

            PlayedCard validPlayedCard = new PlayedCard(current.getPosition(), chosenCard);

            game.getCurrentTrick().add(validPlayedCard);

            game.addAnimation(new Animation(validPlayedCard, game.getLeadSuit(), game.getCurrentTurnIndex(),
                    game.getCurrentTrick().size(), game.getSessionKey()));
            game.setCurrentTurnIndex((game.getCurrentTurnIndex() + 1) % 4);

            if (game.getCurrentTrick().size() == 4) {
                System.out.println("DEBUG: Trick complete. Evaluating winner...");
                PlayedCard winner = HandUtils.determineTrickWinner(game, game.getCurrentTrick());

                Player winnerPlayer = PlayerUtils.getPlayerByPosition(winner.getPlayer(), game.getPlayers());
                Team winnerTeam = winnerPlayer.getTeam();
                System.out.println(
                        "DEBUG: Trick won by " + winnerPlayer.getName() + " (Team " + winnerTeam + ")");

                Book currentBook = new Book(game.getCurrentTrick(), winnerTeam);
                game.setCurrentTurnIndex(winnerPlayer.getPosition().ordinal());

                game.addAnimation(
                        new Animation(currentBook, game.getCurrentTurnIndex(), game.getPhase(), game.getSessionKey()));
                game.addAnimation(new Animation(AnimationType.UPDATE_CARDS, game.getSessionKey()));

                game.getTeamTrickCounts().putIfAbsent(winnerTeam, 0);
                game.getTeamTrickCounts().put(winnerTeam, game.getTeamTrickCounts().get(winnerTeam) + 1);
                System.out.println("DEBUG: Team trick counts: " + game.getTeamTrickCounts());

                game.getCompletedTricks().add(currentBook);
                game.getCurrentTrick().clear();

                if (game.getCompletedTricks().size() == 12) {
                    GameplayUtils.scoreHand(game);

                    if (game.getPhase() == GamePhase.END) {
                        game.addAnimation(new Animation(AnimationType.SHOW_WINNER, game.getSessionKey()));
                    } else {
                        game.addAnimation(new Animation(AnimationType.CLEAR, game.getSessionKey()));
                        GameplayUtils.startNewHand(game);
                    }
                    game.setBidWinnerPos(null);
                    return;
                }

                if (winnerPlayer.isAI()) {
                    autoPlayAITurns(game);
                }
            }
        }
    }

    /**
     * Selects the best card for the AI to play based on difficulty level.
     *
     * - EASY: Plays the lowest legal card to keep logic simple. - MEDIUM: Leads
     * with the highest non-trump card or follows suit with the lowest. - HARD:
     * Evaluates trump, lead suit, partner position, and past tricks to choose:
     * - A safe high card if leading - A defensive low card if partner is
     * winning - The weakest card that can beat the current winner - A fallback
     * lowest card when no better play is found
     *
     * Decision logic adjusts dynamically based on trick position, trump usage,
     * and potential to preserve strong cards for later rounds.
     */
    public static Card chooseCardForAI(GameState game, Player aiPlayer, List<PlayedCard> currentTrick) {
        List<Card> hand = aiPlayer.getHand().getCards();
        Difficulty difficulty = game.getDifficulty();
        Suit trumpSuit = game.getTrumpSuit();
        PlayerPos aiPlayerPosition = aiPlayer.getPosition();
        boolean isNoTrump = game.getWinningBid().isNo();

        if (difficulty == Difficulty.EASY) {
            Card easyCard = getEasyAIMove(hand, currentTrick);
            game.addPlayedCard(easyCard);
            return easyCard;
        } else

        if (difficulty == Difficulty.MEDIUM) {
            Card mediumCard = getMediumAIMove(game, aiPlayerPosition, hand, currentTrick);
            game.addPlayedCard(mediumCard);
            return mediumCard;
        } else

        if (difficulty == Difficulty.HARD) {
            Card hardCard = getHardAIMove(game, aiPlayer, hand, currentTrick);
            game.addPlayedCard(hardCard);
            return hardCard;
        }
    }

    /**
     * Returns a "dumb" AI move for Easy difficulty.
     * - If leading: play the highest card (no strategy)
     * - If following: play the lowest legal card (follow suit if possible)
     */
    public static Card getEasyAIMove(List<Card> hand, List<PlayedCard> trick) {
        if (trick == null || trick.isEmpty()) {
            // AI is leading — play highest card
            return hand.stream()
                    .max(Comparator.comparingInt(c -> c.getRank().getValue()))
                    .orElse(hand.get(0));
        }

        // Determine lead suit (first non-joker with a suit)
        Suit leadSuit = trick.stream()
                .map(PlayedCard::getCard)
                .filter(c -> !c.isJoker() && c.getSuit() != null)
                .map(Card::getSuit)
                .findFirst()
                .orElse(null);

        // Try to follow suit
        List<Card> matchingSuit = hand.stream()
                .filter(c -> c.getSuit() != null && c.getSuit().equals(leadSuit))
                .collect(Collectors.toList());

        if (!matchingSuit.isEmpty()) {
            // Play lowest card in lead suit
            return matchingSuit.stream()
                    .min(Comparator.comparingInt(c -> c.getRank().getValue()))
                    .orElse(matchingSuit.get(0));
        }

        // No matching suit — discard lowest card
        return hand.stream()
                .min(Comparator.comparingInt(c -> c.getRank().getValue()))
                .orElse(hand.get(0));
    }

/**
 * Determines the Medium AI's move based on the current game state.
 *
 * - If leading: plays the highest-ranked card in hand.
 * - If following:
 *    - Plays the lowest legal non-trump card if partner is winning or cannot win the trick.
 *    - Otherwise, attempts to win with the highest legal card.
 *
 * Prioritizes safe play and avoids wasting strong cards when unnecessary.
 */
    public static Card getMediumAIMove(GameState game, PlayerPos player, List<Card> hand, List<PlayedCard> trick) {
        if (trick.isEmpty()) {
            return HandUtils.getHighestRankedCard(game, hand);
        }

        Suit trumpSuit = game.getTrumpSuit();
        PlayedCard winningCard = HandUtils.getWinningCard(trick, trumpSuit);

        if (HandUtils.partnerIsWinning(player, winningCard) || !HandUtils.canWinTrick(game, trick, hand)) {
            return HandUtils.getLowestLegalNonTrumpCard(game, trick, hand);
        }
        
        return HandUtils.getHighestLegalCard(game, trick, hand);
    }
    
    /**
     * 
     * @param hand
     * @param trick
     * @return
     */
    public static Card getHardAIMove(GameState game, PlayerPos player, List<Card> hand, List<PlayedCard> trick) {
        Difficulty difficulty = game.getDifficulty();
        Suit trumpSuit = game.getTrumpSuit();
        boolean isNoTrump = game.getWinningBid().isNo();
    
        
        if (trick.isEmpty()) {
                Card highestTrump = HandUtils.getHighestOfSuit(game, hand, trumpSuit);
                if (highestTrump != null && 
                        HandUtils.allHigherCardsPlayed(highestTrump, game.getPlayedCards(), trumpSuit, isNoTrump)){
                    
                }
            }
    }

    /*
     * Auto-applies kitty and discards for AI winners.
     * Chooses 6 lowest cards to discard and starts the AI's first play.
     */
    public static void applyAIAutoKitty(GameState game, Player winner) {
        winner.getHand().getCards().addAll(game.getKitty());

        List<Card> sorted = new ArrayList<>(winner.getHand().getCards());
        sorted.sort(Comparator.comparingInt(c -> c.getRank().getValue()));
        List<Card> toDiscard = sorted.subList(0, 6);

        for (Card card : toDiscard) {
            winner.getHand().getCards().remove(card);
        }

        game.setKitty(new ArrayList<>());

        PlayerPos winnerPos = game.getHighestBid().getPlayer();
        FinalBid winningBid = game.getFinalBidCache().get(winnerPos);
        game.setBidType(winningBid.getType());
        game.setPhase(GamePhase.PLAY);
        game.setCurrentTurnIndex(winnerPos.ordinal());

        System.out.println("DEBUG: First trick will be led by " + winner.getName());

        autoPlayAITurns(game);
    }
}
