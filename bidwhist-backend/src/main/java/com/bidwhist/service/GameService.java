package com.bidwhist.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.bidwhist.dto.BidRequest;
import com.bidwhist.dto.GameStateResponse;
import com.bidwhist.dto.KittyRequest;
import com.bidwhist.dto.PlayRequest;
import com.bidwhist.dto.PlayerView;
import com.bidwhist.model.Bid;
import com.bidwhist.model.BidType;
import com.bidwhist.model.Card;
import com.bidwhist.model.GamePhase;
import com.bidwhist.model.GameState;
import com.bidwhist.model.PlayedCard;
import com.bidwhist.model.Player;
import com.bidwhist.model.Suit;
import com.bidwhist.model.Team;

@Service
public class GameService {

    private final GameState currentGame = new GameState();

    public GameState getCurrentGame() {
        return currentGame;
    }

    public void startGame(List<String> playerNames) {
        List<Card> deck = DeckService.createShuffledDeck();
        List<Player> players = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            List<Card> hand = new ArrayList<>(deck.subList(i * 12, (i + 1) * 12));
            Team team = (i % 2 == 0) ? Team.A : Team.B;
            players.add(new Player(playerNames.get(i), hand, team, i, i != 0));
        }

        List<Card> kitty = new ArrayList<>(deck.subList(48, 54));

        currentGame.setPlayers(players);
        currentGame.setKitty(kitty);
        currentGame.setPhase(GamePhase.BID);
        currentGame.setBidTurnIndex(0);
        currentGame.setBids(new ArrayList<>());
        currentGame.setCompletedTricks(new ArrayList<>());
        currentGame.setCurrentTrick(new ArrayList<>());
        currentGame.setCurrentPlayerIndex(0);
        currentGame.setTeamTrickCounts(new HashMap<>());
        currentGame.setTeamScores(new HashMap<>());
    }

    public void startNewHand() {
        List<Card> deck = DeckService.createShuffledDeck();

        for (int i = 0; i < 4; i++) {
            List<Card> hand = new ArrayList<>(deck.subList(i * 12, (i + 1) * 12));
            currentGame.getPlayers().get(i).setHand(hand);
        }

        List<Card> kitty = new ArrayList<>(deck.subList(48, 54));

        currentGame.setKitty(kitty);
        currentGame.setPhase(GamePhase.BID);
        currentGame.setBidTurnIndex(0);
        currentGame.setBids(new ArrayList<>());
        currentGame.setCompletedTricks(new ArrayList<>());
        currentGame.setCurrentTrick(new ArrayList<>());
        currentGame.setCurrentPlayerIndex(0);
        currentGame.setHighestBid(null);
        currentGame.setWinningPlayerName(null);
        currentGame.setTrumpType(null);
        currentGame.setTeamTrickCounts(new HashMap<>());
    }

    public GameStateResponse getGameStateForPlayer(String playerName) {
        List<PlayerView> playerViews = new ArrayList<>();

        for (Player p : currentGame.getPlayers()) {
            List<Card> visibleHand = p.getName().equals(playerName) ? p.getHand() : null;
            playerViews.add(new PlayerView(
                    p.getName(),
                    p.getSeatIndex(),
                    p.getTeam(),
                    p.isAI(),
                    visibleHand
            ));
        }

        GameStateResponse response = new GameStateResponse(
                playerViews,
                currentGame.getKitty(),
                currentGame.getCurrentTurnIndex(),
                currentGame.getPhase(),
                currentGame.getTrumpType()
        );
        response.setWinningPlayerName(currentGame.getWinningPlayerName());
        response.setHighestBid(currentGame.getHighestBid());
        response.setTeamTrickCounts(currentGame.getTeamTrickCounts());
        response.setTeamScores(currentGame.getTeamScores());
        return response;
    }

    public GameStateResponse submitBid(BidRequest request) {
        if (currentGame == null) {
            throw new IllegalStateException("Game has not been started. Call /start first.");
        }

        currentGame.getPlayers().stream()
                .filter(p -> p.getName().equals(request.getPlayer()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Player not found"));

        Bid bid = new Bid(request.getPlayer(), request.getValue(), request.getType());
        currentGame.addBid(bid);

        if (request.getType() != BidType.PASS
                && (currentGame.getHighestBid() == null
                || request.getValue() > currentGame.getHighestBid().getValue())) {
            currentGame.setHighestBid(bid);
        }

        int nextIndex = (currentGame.getBidTurnIndex() + 1) % currentGame.getPlayers().size();
        currentGame.setBidTurnIndex(nextIndex);

        while (currentGame.getBids().size() < 4) {
            Player currentBidder = currentGame.getPlayers().get(currentGame.getBidTurnIndex());

            if (!currentBidder.isAI()) {
                break;
            }

            Bid aiBid = generateAIBid(currentBidder);
            currentGame.addBid(aiBid);

            if (aiBid.getType() != BidType.PASS
                    && (currentGame.getHighestBid() == null
                    || aiBid.getValue() > currentGame.getHighestBid().getValue())) {
                currentGame.setHighestBid(aiBid);
            }

            currentGame.setBidTurnIndex((currentGame.getBidTurnIndex() + 1) % currentGame.getPlayers().size());
        }

        if (currentGame.getBids().size() >= 4) {
            currentGame.setWinningPlayerName(currentGame.getHighestBid().getPlayerName());
            currentGame.setTrumpType(currentGame.getHighestBid().getType());
            currentGame.setPhase(GamePhase.KITTY);
            Player winner = currentGame.getPlayers().stream()
                    .filter(p -> p.getName().equals(currentGame.getWinningPlayerName()))
                    .findFirst()
                    .orElseThrow();

            if (winner.isAI()) {
                applyAIAutoKitty(winner);
            }

        }

        return getGameStateForPlayer(request.getPlayer());
    }

    private Bid generateAIBid(Player ai) {
        Random rand = new Random();
        Bid currentHigh = currentGame.getHighestBid();
        int baseBid = (currentHigh != null) ? currentHigh.getValue() + 1 : 3;

        if (baseBid > 6 || rand.nextInt(100) < 40) {
            return new Bid(ai.getName(), 0, BidType.PASS);
        }

        int bidValue = baseBid + rand.nextInt(7 - baseBid + 1);

        BidType[] types = {BidType.UPTOWN, BidType.DOWNTOWN, BidType.NO_TRUMP};
        BidType chosenType = types[rand.nextInt(types.length)];

        return new Bid(ai.getName(), bidValue, chosenType);
    }

    private void autoPlayAITurns() {
        while (true) {
            Player current = currentGame.getPlayers().get(currentGame.getCurrentPlayerIndex());
            if (!current.isAI()) {
                System.out.println("DEBUG: Human player's turn: " + current.getName());
                break;
            }

            System.out.println("DEBUG: AI Player " + current.getName() + " is taking a turn.");
            Card chosenCard = chooseCardForAI(current, currentGame.getCurrentTrick());
            System.out.println("DEBUG: AI played card: " + chosenCard);

            current.getHand().remove(chosenCard);
            currentGame.getCurrentTrick().add(new PlayedCard(current.getName(), chosenCard));

            currentGame.setCurrentPlayerIndex((currentGame.getCurrentPlayerIndex() + 1) % 4);

            if (currentGame.getCurrentTrick().size() == 4) {
                System.out.println("DEBUG: Trick complete. Evaluating winner...");
                PlayedCard winner = determineTrickWinner(currentGame.getCurrentTrick(), currentGame.getTrumpType());

                int winnerIndex = findPlayerIndexByName(winner.getPlayerName());
                Player winnerPlayer = currentGame.getPlayers().get(winnerIndex);
                Team team = winnerPlayer.getTeam();
                System.out.println("DEBUG: Trick won by " + winnerPlayer.getName() + " (Team " + team + ")");

                currentGame.getTeamTrickCounts().putIfAbsent(team, 0);
                currentGame.getTeamTrickCounts().put(team, currentGame.getTeamTrickCounts().get(team) + 1);
                System.out.println("DEBUG: Team trick counts: " + currentGame.getTeamTrickCounts());

                currentGame.getCompletedTricks().add(new ArrayList<>(currentGame.getCurrentTrick()));
                currentGame.getCurrentTrick().clear();
                currentGame.setCurrentPlayerIndex(winnerIndex);

                if (currentGame.getCompletedTricks().size() == 13) {
                    scoreHand();
                    currentGame.setPhase(GamePhase.END);
                    return;
                }

                // ✅ Recursively call autoPlayAITurns if winner is also AI
                if (winnerPlayer.isAI()) {
                    autoPlayAITurns();
                }

                return; // End loop for current trick
            }
        }
    }

    private Card chooseCardForAI(Player aiPlayer, List<PlayedCard> currentTrick) {
        List<Card> hand = aiPlayer.getHand();
        BidType trumpType = currentGame.getTrumpType();
        Suit trumpSuit = getTrumpSuitFromBidType(trumpType); // Convert BidType to Suit

        // If AI is leading the trick, play the highest non-trump card
        if (currentTrick.isEmpty()) {
            return hand.stream()
                    .filter(c -> trumpSuit == null || !c.getSuit().equals(trumpSuit))
                    .max(Comparator.comparingInt(c -> c.getRank().getValue()))
                    .orElse(hand.get(0)); // fallback
        }

        // If AI is following suit
        Suit leadSuit = currentTrick.get(0).getCard().getSuit();
        List<Card> sameSuit = hand.stream()
                .filter(c -> c.getSuit().equals(leadSuit))
                .collect(Collectors.toList());

        if (!sameSuit.isEmpty()) {
            return sameSuit.stream()
                    .min(Comparator.comparingInt(c -> c.getRank().getValue()))
                    .orElse(sameSuit.get(0));
        }

        // No cards of the lead suit, play the lowest card
        return hand.stream()
                .min(Comparator.comparingInt(c -> c.getRank().getValue()))
                .orElse(hand.get(0));
    }

    private void applyAIAutoKitty(Player winner) {
        // Add kitty to hand
        winner.getHand().addAll(currentGame.getKitty());

        // Auto-discard the lowest 6 cards (simple logic)
        List<Card> sorted = new ArrayList<>(winner.getHand());
        sorted.sort(Comparator.comparingInt(c -> c.getRank().getValue()));
        List<Card> toDiscard = sorted.subList(0, 6);

        for (Card card : toDiscard) {
            winner.getHand().remove(card);
        }

        currentGame.setKitty(new ArrayList<>());

        // Set trump from the bid
        Bid winningBid = currentGame.getHighestBid();
        currentGame.setTrumpType(winningBid.getType());
        currentGame.setPhase(GamePhase.PLAY);
    }

    public void applyKittyAndDiscards(KittyRequest request) {
        Player winner = currentGame.getPlayers().stream()
                .filter(p -> p.getName().equals(currentGame.getWinningPlayerName()))
                .findFirst()
                .orElseThrow();

        winner.getHand().addAll(currentGame.getKitty());

        for (Card card : request.getDiscards()) {
            winner.getHand().removeIf(c -> c.equals(card));
        }

        currentGame.setKitty(new ArrayList<>());
        currentGame.setPhase(GamePhase.PLAY);
    }

    public void playCard(PlayRequest request) {
        if (currentGame.getPhase() != GamePhase.PLAY) {
            throw new IllegalStateException("Not in PLAY phase");
        }

        Player currentPlayer = currentGame.getPlayers().get(currentGame.getCurrentPlayerIndex());
        System.out.println("DEBUG: Current turn is player index " + currentGame.getCurrentPlayerIndex()
                + " (" + currentPlayer.getName() + ")");
        System.out.println("DEBUG: Player trying to play: " + request.getPlayer());

        if (!currentPlayer.getName().equals(request.getPlayer())) {
            throw new IllegalStateException("It's not " + request.getPlayer() + "'s turn");
        }

        Card cardToPlay = request.getCard();
        if (!currentPlayer.getHand().contains(cardToPlay)) {
            throw new IllegalArgumentException("Player does not have the specified card");
        }

        List<PlayedCard> currentTrick = currentGame.getCurrentTrick();
        if (!currentTrick.isEmpty()) {
            Suit leadSuit = currentTrick.get(0).getCard().getSuit();
            boolean hasLeadSuit = currentPlayer.getHand().stream()
                    .anyMatch(c -> c.getSuit() == leadSuit);
            if (hasLeadSuit && cardToPlay.getSuit() != leadSuit) {
                throw new IllegalArgumentException("Must follow suit if possible");
            }
        }

        currentPlayer.getHand().remove(cardToPlay);
        currentTrick.add(new PlayedCard(currentPlayer.getName(), cardToPlay));

        // Advance turn
        currentGame.setCurrentPlayerIndex((currentGame.getCurrentPlayerIndex() + 1) % 4);

        if (currentTrick.size() == 4) {
            PlayedCard winningPlay = determineTrickWinner(currentTrick, currentGame.getTrumpType());
            int winnerIndex = findPlayerIndexByName(winningPlay.getPlayerName());
            Player winner = currentGame.getPlayers().get(winnerIndex);
            Team winnerTeam = winner.getTeam();

            currentGame.getTeamTrickCounts().putIfAbsent(winnerTeam, 0);
            currentGame.getTeamTrickCounts().put(
                    winnerTeam,
                    currentGame.getTeamTrickCounts().get(winnerTeam) + 1
            );

            currentGame.getCompletedTricks().add(new ArrayList<>(currentTrick));
            currentTrick.clear();
            currentGame.setCurrentPlayerIndex(winnerIndex);

            if (currentGame.getCompletedTricks().size() == 13) {
                scoreHand();
                currentGame.setPhase(GamePhase.END);
                return;
            }
        }

        // Always let AI play if it's their turn now
        autoPlayAITurns();
    }

    private PlayedCard determineTrickWinner(List<PlayedCard> trick, BidType trumpType) {
        Suit leadSuit = trick.get(0).getCard().getSuit();

        return trick.stream().max(Comparator.comparing(pc -> {
            Card c = pc.getCard();
            boolean isTrump = isTrumpSuit(c, trumpType);
            boolean isLead = c.getSuit() == leadSuit;
            int rankValue = c.getRank().getValue();
            return (isTrump ? 1000 : (isLead ? 100 : 0)) + rankValue;
        })).orElseThrow();
    }

    private boolean isTrumpSuit(Card card, BidType bidType) {
        Suit suit = card.getSuit();
        if (bidType == BidType.NO_TRUMP) {
            return false;
        }
        if (bidType == BidType.UPTOWN) {
            return suit == Suit.SPADES || suit == Suit.CLUBS;
        }
        if (bidType == BidType.DOWNTOWN) {
            return suit == Suit.HEARTS || suit == Suit.DIAMONDS;
        }
        return false;
    }

    private int findPlayerIndexByName(String name) {
        List<Player> players = currentGame.getPlayers();
        for (int i = 0; i < players.size(); i++) {
            if (players.get(i).getName().equals(name)) {
                return i;
            }
        }
        throw new IllegalArgumentException("No player with name: " + name);
    }

    private void scoreHand() {
        Bid winningBid = currentGame.getHighestBid();
        Team winningTeam = currentGame.getPlayers().stream()
                .filter(p -> p.getName().equals(winningBid.getPlayerName()))
                .findFirst()
                .map(Player::getTeam)
                .orElseThrow();

        int tricksWon = currentGame.getTeamTrickCounts().getOrDefault(winningTeam, 0);
        int bidValue = winningBid.getValue();

        boolean success = tricksWon >= bidValue;
        int points = success ? bidValue : -bidValue;

        currentGame.getTeamScores().putIfAbsent(winningTeam, 0);
        currentGame.getTeamScores().put(winningTeam,
                currentGame.getTeamScores().get(winningTeam) + points);
    }
    // Place this just before the final closing brace of GameService

    private Suit getTrumpSuitFromBidType(BidType bidType) {
        return switch (bidType) {
            case UPTOWN ->
                Suit.SPADES;
            case DOWNTOWN ->
                Suit.CLUBS;
            case NO_TRUMP ->
                null;
            default ->
                null; // handles any future or unexpected BidType values
        };
    }

}
