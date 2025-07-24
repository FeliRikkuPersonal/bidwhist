package com.bidwhist.utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
                .filter(bid -> currentHigh == null || bid.getValue() > currentHigh.getValue())
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
                    && (game.getHighestBid() == null || aiBid.getValue() > game.getHighestBid().getValue())) {
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

            game.addAnimation(new Animation(validPlayedCard, game.getLeadSuit()));
            game.setCurrentTurnIndex((game.getCurrentTurnIndex() + 1) % 4);

            if (game.getCurrentTrick().size() == 4) {
                System.out.println("DEBUG: Trick complete. Evaluating winner...");
                PlayedCard winner = GameplayUtils.determineTrickWinner(game, game.getCurrentTrick());

                Player winnerPlayer = PlayerUtils.getPlayerByPosition(winner.getPlayer(), game.getPlayers());
                Team winnerTeam = winnerPlayer.getTeam();
                System.out.println(
                        "DEBUG: Trick won by " + winnerPlayer.getName() + " (Team " + winnerTeam + ")");

                Book currentBook = new Book(game.getCurrentTrick(), winnerTeam);

                game.addAnimation(new Animation(currentBook));
                game.addAnimation(new Animation(AnimationType.UPDATE_CARDS));

                game.getTeamTrickCounts().putIfAbsent(winnerTeam, 0);
                game.getTeamTrickCounts().put(winnerTeam, game.getTeamTrickCounts().get(winnerTeam) + 1);
                System.out.println("DEBUG: Team trick counts: " + game.getTeamTrickCounts());

                game.getCompletedTricks().add(currentBook);
                game.getCurrentTrick().clear();
                game.setCurrentTurnIndex(winnerPlayer.getPosition().ordinal());

                if (game.getCompletedTricks().size() == 12) {
                    GameplayUtils.scoreHand(game);
                    if (game.getTeamAScore() >= 7 || game.getTeamBScore() >= 7) {
                        game.addAnimation(new Animation(AnimationType.SHOW_WINNER));
                        game.setPhase(GamePhase.END);
                    } else {
                        game.addAnimation(new Animation(AnimationType.CLEAR));
                        GameplayUtils.startNewHand(game);
                    }
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
     * <p>
     * - EASY: Plays the lowest legal card to keep logic simple. - MEDIUM: Leads
     * with the highest
     * non-trump card or follows suit with the lowest. - HARD: Evaluates trump, lead
     * suit, partner
     * position, and past tricks to choose: - A safe high card if leading - A
     * defensive low card if
     * partner is winning - The weakest card that can beat the current winner - A
     * fallback lowest card
     * when no better play is found
     *
     * <p>
     * Decision logic adjusts dynamically based on trick position, trump usage, and
     * potential to
     * preserve strong cards for later rounds.
     */
    public static Card chooseCardForAI(GameState game, Player aiPlayer, List<PlayedCard> currentTrick) {
        List<Card> hand = aiPlayer.getHand().getCards();
        Difficulty difficulty = game.getDifficulty();
        Suit trumpSuit = game.getTrumpSuit();

        if (difficulty == Difficulty.EASY) {
            return getLowestLegalCard(hand, currentTrick);
        }

        if (difficulty == Difficulty.MEDIUM) {
            if (currentTrick.isEmpty()) {
                return hand.stream()
                        .filter(
                                c -> c.getSuit() != null && (trumpSuit == null || !c.getSuit().equals(trumpSuit)))
                        .max(Comparator.comparingInt(c -> c.getRank().getValue()))
                        .orElse(hand.get(0));
            }

            Suit leadSuit = currentTrick.get(0).getCard() != null ? currentTrick.get(0).getCard().getSuit() : null;
            game.setLeadSuit(leadSuit);
            List<Card> sameSuit = hand.stream()
                    .filter(c -> c.getSuit() != null && c.getSuit().equals(leadSuit))
                    .collect(Collectors.toList());

            if (!sameSuit.isEmpty()) {
                return sameSuit.stream()
                        .min(Comparator.comparingInt(c -> c.getRank().getValue()))
                        .orElse(sameSuit.get(0));
            }

            return hand.stream()
                    .min(Comparator.comparingInt(c -> c.getRank().getValue()))
                    .orElse(hand.get(0));
        }

        if (difficulty == Difficulty.HARD) {
            int trickIndex = currentTrick.size();
            Suit leadSuit = currentTrick.isEmpty()
                    ? null
                    : (currentTrick.get(0).getCard() != null
                            ? currentTrick.get(0).getCard().getSuit()
                            : null);
            game.setLeadSuit(leadSuit);
            PlayedCard winningCard = trickIndex > 0 ? GameplayUtils.getWinningCard(currentTrick, trumpSuit) : null;
            Card currentWinning = winningCard != null ? winningCard.getCard() : null;

            if (leadSuit != null
                    && hand.stream().anyMatch(c -> c.getSuit() != null && c.getSuit().equals(leadSuit))) {
                // Ensure at least one card of leadSuit is played
                List<Card> legalFollowSuit = hand.stream()
                        .filter(c -> c.getSuit() != null && c.getSuit().equals(leadSuit))
                        .collect(Collectors.toList());

                return legalFollowSuit.stream()
                        .min(Comparator.comparingInt(c -> c.getRank().getValue()))
                        .orElse(hand.get(0));
            }

            if (trickIndex == 0) {
                Optional<Card> safeLead = hand.stream()
                        .filter(c -> !JokerUtils.isJokerRank(c.getRank())) // Avoid jokers
                        .filter(
                                c -> c.getSuit() == null
                                        || !c.getSuit().equals(trumpSuit)
                                        || CardUtils.allHigherTrumpCardsPlayed(
                                                c, game.getCompletedCards(), trumpSuit))
                        .max(Comparator.comparingInt(c -> c.getRank().getValue()));

                if (safeLead.isPresent()) {
                    return safeLead.get();
                }

                Map<Suit, List<Card>> bySuit = hand.stream()
                        .filter(c -> c.getSuit() != null)
                        .collect(Collectors.groupingBy(Card::getSuit));

                Optional<Card> lead = bySuit.entrySet().stream()
                        .filter(e -> e.getValue().size() >= 2)
                        .flatMap(e -> e.getValue().stream())
                        .max(Comparator.comparingInt(c -> c.getRank().getValue()));
                if (lead.isPresent()) {
                    return lead.get();
                }

                return hand.stream()
                        .filter(
                                c -> c.getSuit() != null && (trumpSuit == null || !c.getSuit().equals(trumpSuit)))
                        .max(Comparator.comparingInt(c -> c.getRank().getValue()))
                        .orElse(hand.get(0));
            }

            if (trickIndex == 3 && winningCard != null && currentWinning != null) {
                PlayerPos partner = aiPlayer.getPosition().getPartner();
                boolean partnerWinning = winningCard.getPlayer() != null && winningCard.getPlayer().equals(partner);

                if (partnerWinning) {
                    List<Card> follow = hand.stream()
                            .filter(c -> c.getSuit() != null && c.getSuit().equals(leadSuit))
                            .collect(Collectors.toList());
                    if (!follow.isEmpty()) {
                        return follow.stream()
                                .min(Comparator.comparingInt(c -> c.getRank().getValue()))
                                .orElse(follow.get(0));
                    }

                    List<Card> trumps = hand.stream()
                            .filter(c -> c.getSuit() != null && c.getSuit().equals(trumpSuit))
                            .collect(Collectors.toList());
                    if (!trumps.isEmpty()) {
                        return trumps.stream()
                                .min(Comparator.comparingInt(c -> c.getRank().getValue()))
                                .orElse(trumps.get(0));
                    }

                    Optional<Card> safeHigh = hand.stream()
                            .filter(c -> c.getRank().getValue() >= Rank.QUEEN.getValue())
                            .filter(
                                    c -> c.getSuit() == null
                                            || !c.getSuit().equals(trumpSuit)
                                            || CardUtils.allHigherTrumpCardsPlayed(
                                                    c, game.getCompletedCards(), trumpSuit))
                            .findFirst();
                    if (safeHigh.isPresent()) {
                        return safeHigh.get();
                    }

                    return hand.stream()
                            .min(Comparator.comparingInt(c -> c.getRank().getValue()))
                            .orElse(hand.get(0));
                }

                List<Card> winningCards = hand.stream()
                        .filter(c -> c != null && canBeat(c, currentWinning, trumpSuit, leadSuit))
                        .sorted(Comparator.comparingInt(c -> c.getRank().getValue()))
                        .collect(Collectors.toList());

                if (!winningCards.isEmpty()) {
                    return winningCards.get(0);
                }

                Optional<Card> safeHigh = hand.stream()
                        .filter(c -> c.getRank().getValue() >= Rank.QUEEN.getValue())
                        .filter(
                                c -> c.getSuit() == null
                                        || !c.getSuit().equals(trumpSuit)
                                        || CardUtils.allHigherTrumpCardsPlayed(
                                                c, game.getCompletedCards(), trumpSuit))
                        .findFirst();
                if (safeHigh.isPresent()) {
                    return safeHigh.get();
                }

                return hand.stream()
                        .min(Comparator.comparingInt(c -> c.getRank().getValue()))
                        .orElse(hand.get(0));
            }

            List<Card> followSuit = hand.stream()
                    .filter(c -> c.getSuit() != null)
                    .filter(c -> leadSuit != null && c.getSuit().equals(leadSuit))
                    .collect(Collectors.toList());

            if (!followSuit.isEmpty()) {
                if (currentWinning != null) {
                    Optional<Card> beatCard = followSuit.stream()
                            .filter(c -> canBeat(c, currentWinning, trumpSuit, leadSuit))
                            .min(Comparator.comparingInt(c -> c.getRank().getValue()));
                    if (beatCard.isPresent()) {
                        return beatCard.get();
                    }

                    Optional<Card> safeBurn = followSuit.stream()
                            .filter(
                                    c -> c.getSuit() == null
                                            || !c.getSuit().equals(trumpSuit)
                                            || CardUtils.allHigherTrumpCardsPlayed(
                                                    c, game.getCompletedCards(), trumpSuit))
                            .max(Comparator.comparingInt(c -> c.getRank().getValue()));
                    if (safeBurn.isPresent()) {
                        return safeBurn.get();
                    }
                }

                // Fallback if can't beat or burn: play lowest legal card in lead suit
                return followSuit.stream()
                        .min(Comparator.comparingInt(c -> c.getRank().getValue()))
                        .orElse(followSuit.get(0));
            }

            List<Card> trumpCards = hand.stream()
                    .filter(c -> c.getSuit() != null && c.getSuit().equals(trumpSuit))
                    .collect(Collectors.toList());

            if (!trumpCards.isEmpty() && currentWinning != null) {
                Optional<Card> winningTrump = trumpCards.stream()
                        .filter(c -> canBeat(c, currentWinning, trumpSuit, leadSuit))
                        .min(Comparator.comparingInt(c -> c.getRank().getValue()));
                if (winningTrump.isPresent()) {
                    return winningTrump.get();
                }
            }

            Optional<Card> safeHigh = hand.stream()
                    .filter(c -> c.getRank().getValue() >= Rank.QUEEN.getValue())
                    .filter(
                            c -> c.getSuit() == null
                                    || !c.getSuit().equals(trumpSuit)
                                    || CardUtils.allHigherTrumpCardsPlayed(
                                            c, game.getCompletedCards(), trumpSuit))
                    .findFirst();
            if (safeHigh.isPresent()) {
                return safeHigh.get();
            }

            return hand.stream()
                    .min(Comparator.comparingInt(c -> c.getRank().getValue()))
                    .orElse(hand.get(0));
        }

        return hand.get(0); // fallback
    }

    /**
     * Checks if the AI's partner is currently winning the trick.
     *
     * <p>
     * Finds the current winning card and compares its player to the AI’s partner.
     */
    @SuppressWarnings("unused")
    public static boolean isPartnerWinning(List<PlayedCard> trick, PlayerPos aiPos, Suit trumpSuit) {
        if (trick.size() < 2) {
            return false; // Partner hasn't played yet
        }
        PlayerPos partner = aiPos.getPartner();

        // Find the winning card in the current trick
        PlayedCard winningCard = GameplayUtils.getWinningCard(trick, trumpSuit);
        if (winningCard == null) {
            return false;
        }

        return winningCard.getPlayer().equals(partner);
    }

    /**
     * Checks if the challenger card can beat the current winning card.
     *
     * <p>
     * Trump beats non-trump. Otherwise, the higher card of the same suit wins. If
     * suits differ and
     * neither is trump, the lead suit may still win if higher.
     */
    public static boolean canBeat(Card challenger, Card currentWinning, Suit trumpSuit, Suit leadSuit) {
        Suit challengerSuit = challenger.getSuit();
        Suit winningSuit = currentWinning.getSuit();

        boolean challengerIsTrump = trumpSuit != null && trumpSuit.equals(challengerSuit);
        boolean winningIsTrump = trumpSuit != null && trumpSuit.equals(winningSuit);

        // Trump beats non-trump
        if (challengerIsTrump && !winningIsTrump) {
            return true;
        }
        if (!challengerIsTrump && winningIsTrump) {
            return false;
        }

        // Same suit: compare values
        if (challengerSuit == winningSuit) {
            return challenger.getRank().getValue() > currentWinning.getRank().getValue();
        }

        // Lead suit but different from winning (no trump involved)
        if (!challengerIsTrump
                && !winningIsTrump
                && challengerSuit == leadSuit
                && winningSuit == leadSuit) {
            return challenger.getRank().getValue() > currentWinning.getRank().getValue();
        }

        return false;
    }

    /*
     * Selects the lowest legal card to play.
     * Prefers matching lead suit if available.
     */
    public static Card getLowestLegalCard(List<Card> hand, List<PlayedCard> trick) {
        if (trick.isEmpty()) {
            return hand.stream()
                    .min(Comparator.comparingInt(c -> c.getRank().getValue()))
                    .orElse(hand.get(0));
        }

        Suit leadSuit = trick.get(0).getCard().getSuit();
        List<Card> sameSuit = hand.stream().filter(c -> c.getSuit() == leadSuit).collect(Collectors.toList());

        if (!sameSuit.isEmpty()) {
            return sameSuit.stream()
                    .min(Comparator.comparingInt(c -> c.getRank().getValue()))
                    .orElse(sameSuit.get(0));
        }

        return hand.stream()
                .min(Comparator.comparingInt(c -> c.getRank().getValue()))
                .orElse(hand.get(0));
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
        game.setTrumpType(winningBid.getType());
        game.setPhase(GamePhase.PLAY);
        game.setCurrentTurnIndex(winnerPos.ordinal());

        System.out.println("DEBUG: First trick will be led by " + winner.getName());

        autoPlayAITurns(game);
    }

}
