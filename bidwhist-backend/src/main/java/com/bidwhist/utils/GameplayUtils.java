package com.bidwhist.utils;

import java.util.Comparator;
import java.util.List;

import com.bidwhist.bidding.BidType;
import com.bidwhist.bidding.FinalBid;
import com.bidwhist.dto.Animation;
import com.bidwhist.dto.AnimationType;
import com.bidwhist.model.Card;
import com.bidwhist.model.GamePhase;
import com.bidwhist.model.GameState;
import com.bidwhist.model.PlayedCard;
import com.bidwhist.model.Player;
import com.bidwhist.model.Rank;
import com.bidwhist.model.Suit;
import com.bidwhist.model.Team;

public class GameplayUtils {

    /*
     * Deals cards to players after shuffling and sets game phase to BID.
     * Also triggers card animations and assigns the kitty.
     * If the next bidder is AI, initiates AI bid processing.
     */
    public static void dealToPlayers(GameState game) {
        System.out.println("[dealToPlayers]");

        if (game == null) {
            throw new IllegalStateException("Game not started.");
        }

        if (game.getPhase() != GamePhase.SHUFFLE) {
            throw new IllegalStateException("Can only deal after shuffle.");
        }

        game.getDeck().deal(game.getPlayers());
        game.addAnimation(new Animation(game.getShuffledDeck()));
        game.setKitty(game.getDeck().getKitty().getCards());
        game.setPhase(GamePhase.BID);
        System.out.println("Current phase: " + game.getPhase());
        game.addAnimation(new Animation(AnimationType.UPDATE_CARDS));

        Player nextBidder = game.getPlayers().get(game.getBidTurnIndex());
        if (nextBidder.isAI()) {
            AIUtils.processAllBids(game);
        }
    }

    /**
     * Determines which card is currently winning a trick.
     *
     * <p>
     * It checks each card in the trick. Trump cards beat non-trump cards. If
     * both are trump or both are the lead suit, the higher rank wins.
     */
    public static PlayedCard getWinningCard(List<PlayedCard> trick, Suit trumpSuit) {
        if (trick.isEmpty()) {
            return null;
        }

        PlayedCard winning = trick.get(0);
        Suit leadSuit = winning.getCard().getSuit();

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
     * Scores the current hand based on bid success and trick count.
     * Updates team scores accordingly.
     */
    public static void scoreHand(GameState game) {
        FinalBid winningBid = game.getWinningBid();
        Team biddingTeam = game.getPlayers().stream()
                .filter(p -> p.getPosition().equals(winningBid.getPlayer()))
                .findFirst()
                .map(Player::getTeam)
                .orElseThrow();

        int tricksWon = game.getTeamTrickCounts().getOrDefault(biddingTeam, 0);
        int requiredTricks = winningBid.getValue() + 5;

        int deltaScore;
        if (tricksWon >= requiredTricks) {
            // Team succeeded: total tricks - 5 base tricks
            deltaScore = tricksWon - 5;
        } else {
            // Team failed: lose bid value
            deltaScore = -winningBid.getValue();
        }

        // Double points gained or lost if NO_TRUMP bid
        if (winningBid.isNo()) {
            deltaScore *= 2;
        }

        int newScore = game.getTeamScores().getOrDefault(biddingTeam, 0) + deltaScore;
        game.getTeamScores().put(biddingTeam, newScore);

        if (biddingTeam == Team.A) {
            game.setTeamAScore(newScore);
        } else {
            game.setTeamBScore(newScore);
        }

        // Game end condition: either team reaches ≥ 7 or ≤ -7
        int teamAScore = game.getTeamAScore();
        int teamBScore = game.getTeamBScore();

        // Win condition logic
        if (teamAScore >= 7 || teamBScore <= -7) {
            System.out.println(
                    "DEBUG: Team A WON (Score A: " + teamAScore + ", Score B: " + teamBScore + ")");
            game.setPhase(GamePhase.END);
        } else if (teamBScore >= 7 || teamAScore <= -7) {
            System.out.println(
                    "DEBUG: Team B WON (Score B: " + teamBScore + ", Score A: " + teamAScore + ")");
            game.setPhase(GamePhase.END);
        }
    }

    /*
     * Determines the winner of the current trick.
     * Applies different logic for NO_TRUMP, DOWNTOWN, and UPTOWN bids.
     */
    public static PlayedCard determineTrickWinner(GameState game, List<PlayedCard> trick) {
        if (trick == null || trick.isEmpty()) {
            throw new IllegalArgumentException("Cannot determine trick winner: trick is empty.");
        }

        Suit leadSuit = trick.stream()
                .map(PlayedCard::getCard)
                .filter(c -> !c.isJoker())
                .map(Card::getSuit)
                .findFirst()
                .orElse(null);
        Suit trumpSuit = game.getTrumpSuit();
        BidType bidType = game.getBidType();
        boolean isNoBid = game.getWinningBid() != null && game.getWinningBid().isNo();

        // Excludes jokers from being eligible to win if bidType == NO_TRUMP
        if (isNoBid) {
            return trick.stream()
                    .filter(pc -> {
                        Card c = pc.getCard();
                        return !c.isJoker() && c.getSuit() == leadSuit;
                    })
                    .max(Comparator.comparing(pc -> pc.getCard().getRank().getValue()))
                    .orElseThrow(() -> new IllegalStateException("No non-joker cards of lead suit found in NO_TRUMP bid"));
        }

        /* DOWNTOWN: inverted rank values except ACE and JOKERs */
        if (bidType == BidType.DOWNTOWN) {
            return trick.stream()
                    .max(
                            Comparator.comparing(
                                    pc -> {
                                        Card c = pc.getCard();
                                        boolean isTrump = trumpSuit != null && c.getSuit() == trumpSuit;
                                        boolean isLead = c.getSuit() == leadSuit;
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
                                    boolean isLead = c.getSuit() == leadSuit;
                                    int rankValue = c.getRank().getValue();
                                    return (isTrump ? 1000 : (isLead ? 100 : 0)) + rankValue;
                                }))
                .orElseThrow();
    }

    /*
     * Resets all game state to prepare for a new hand.
     * Clears trick counts, kitty, and all tracked state.
     * Shuffles and re-deals cards for new round.
     */
    public static void startNewHand(GameState game) {
        System.out.println("[startNewHand]");

        game.getKitty().clear();
        game.getDeck().clearKitty();
        game.setWinningBid(null);
        game.setBidWinnerPos(null);
        game.setTeamATricksWon(0);
        game.setTeamBTricksWon(0);
        game.setPhase(GamePhase.SHUFFLE);
        game.getBids().clear();
        game.getCompletedTricks().clear();
        game.getCurrentTrick().clear();
        game.setHighestBid(null);
        game.setWinningPlayerName(null);
        game.setBidType(null);
        game.getTeamTrickCounts().clear();
        game.getDeck().resetJokerSuits();
        game.getFinalBidCache().clear();
        game.setBidTurnIndex(game.getFirstBidder().ordinal());

        game.getDeck().shuffle();
        game.setShuffledDeck(game.getDeck().getCards());

        dealToPlayers(game);
    }

    public static void startNewGame(GameState game) {
        game.setPhase(GamePhase.START);
        List<Player> players = game.getPlayers();

        for (Player player : players) {
            player.getHand().getCards().clear();
        }
        game.getDeck().clearKitty();
        game.getKitty().clear();
        game.setPhase(GamePhase.SHUFFLE);
        game.setTrumpSuit(null);
        game.getBids().clear();
        game.setHighestBid(null);
        game.setBidType(null);
        game.getFinalBidCache().clear();
        game.setWinningBid(null);
        game.setBidWinnerPos(null);
        game.getCurrentTrick().clear();
        game.getCompletedTricks().clear();
        game.setLeadSuit(null);
        game.getPlayedCards().clear();
        game.setTeamAScore(0);
        game.setTeamBScore(0);
        game.setTeamATricksWon(0);
        game.setTeamBTricksWon(0);
        game.getTeamTrickCounts().clear();
        game.getTeamScores().clear();
        game.setFinalScore(-1);

        game.getDeck().shuffle();
        game.setShuffledDeck(game.getDeck().getCards());
        GameplayUtils.dealToPlayers(game);
    }

}
