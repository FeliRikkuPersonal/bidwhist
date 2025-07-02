package com.bidwhist.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;

import org.springframework.stereotype.Service;

import com.bidwhist.dto.BidRequest;
import com.bidwhist.dto.CardVisibility;
import com.bidwhist.dto.GameStateResponse;
import com.bidwhist.dto.KittyRequest;
import com.bidwhist.dto.PlayerView;
import com.bidwhist.dto.CardOwner;
import com.bidwhist.bidding.InitialBid;
import com.bidwhist.bidding.FinalBid;
import com.bidwhist.bidding.HandEvaluator;
import com.bidwhist.model.Card;
import com.bidwhist.model.GamePhase;
import com.bidwhist.model.GameState;
import com.bidwhist.model.Player;
import com.bidwhist.model.PlayerPos;
import com.bidwhist.model.Team;
import com.bidwhist.utils.CardUtils;
import com.bidwhist.utils.PlayerUtils;

@Service
public class GameService {

    private final DeckService deckService;
    private GameState currentGame;

    public GameService(DeckService deckService) {
        this.deckService = deckService;
    }

    // Start New Game, add players and shuffled deck
    public GameState startNewGame(String playerName) {
        // debug log
        System.out.println("Staring new game for player: " + playerName);

        GameState gameState = new GameState();

        List<Player> players = new ArrayList<>();
        players.add(new Player(playerName, false, PlayerPos.P1, Team.A));
        players.add(new Player("AI 1", true, PlayerPos.P2, Team.B));
        players.add(new Player("AI 2", true, PlayerPos.P3, Team.A));
        players.add(new Player("AI 3", true, PlayerPos.P4, Team.B));

        gameState.getDeck().shuffle();
        gameState.setShuffledDeck(gameState.getDeck().getCards());

        gameState.getPlayers().addAll(players);
        gameState.setPhase(GamePhase.START);
        this.currentGame = gameState;

        // debug log
        System.out.println("Initial phase: " + gameState.getPhase());

        return currentGame;
    }

    public GameState shuffleDeck() {
        GameState gameState = currentGame;

        gameState.getDeck().shuffle();
        gameState.setShuffledDeck(gameState.getDeck().getCards());
        gameState.setPhase(GamePhase.SHUFFLE);

        return gameState;
    }

    // Return GameState for specific player with dummy cards for other players
    public GameStateResponse getGameStateForPlayer(PlayerPos playerPos) {
        List<PlayerView> playerViews = new ArrayList<>();

        for (Player p : currentGame.getPlayers()) {
            List<Card> visibleHand;

            if (p.getPosition().equals(playerPos)) {
                visibleHand = p.getHand().getCards();
            } else {
                int hiddenCount = p.getHand().getCards().size();
                visibleHand = new ArrayList<>();
                for (int i = 0; i < hiddenCount; i++) {
                    Card hiddenCard = new Card(null, null);
                    hiddenCard.setCardOwner(CardUtils.fromPlayerPos(p.getPosition()));
                    hiddenCard.setVisibility(CardVisibility.HIDDEN); // optional enum
                    visibleHand.add(hiddenCard);
                }
            }

            playerViews.add(new PlayerView(
                    p.getName(),
                    p.getPosition(),
                    p.getTeam(),
                    p.isAI(),
                    visibleHand));
        }

        GameStateResponse response = new GameStateResponse(
                playerViews,
                currentGame.getKitty(),
                currentGame.getCurrentTurnIndex(),
                currentGame.getPhase(),
                currentGame.getTrumpSuit(),
                currentGame.getFinalBidType(),
                currentGame.getWinningPlayerName(),
                currentGame.getHighestBid(),
                currentGame.getShuffledDeck(),
                playerPos,
                currentGame.getFirstBidder(),
                currentGame.getBidTurnIndex(),
                currentGame.getBids()
                );
        response.setWinningPlayerName(currentGame.getWinningPlayerName());
        response.setHighestBid(currentGame.getHighestBid());
        response.setPlayerPostion(playerPos);
        return response;
    }

    // Helper function for getting GameState for specific player using name string
    public GameStateResponse getGameStateForPlayer(String playerName) {
        PlayerPos pos = currentGame.getPlayers().stream()
                .filter(p -> p.getName().equals(playerName))
                .map(Player::getPosition)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Player not found: " + playerName));

        GameStateResponse response = getGameStateForPlayer(pos);

        response.setShuffledDeck(currentGame.getDeck().getCards());

        return response;

    }

    // Collects bid from players and generates AI Bids
    public GameStateResponse submitBid(BidRequest request) {
        if (currentGame == null) {
            throw new IllegalStateException("Game has not been started. Call /start first.");
        }

        Player bidder = currentGame.getPlayers().stream()
                .filter(p -> p.getPosition().equals(request.getPlayer()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Player not found"));

        InitialBid bid = BidRequest.fromRequest(request, bidder);
        currentGame.addBid(bid);

        if (!request.isPass() &&
                (currentGame.getHighestBid() == null ||
                        request.getValue() > currentGame.getHighestBid().getValue())) {

            currentGame.setHighestBid(bid);
        }

        int nextIndex = (currentGame.getBidTurnIndex() + 1) % currentGame.getPlayers().size();
        currentGame.setBidTurnIndex(nextIndex);

        // Automatically process AI bids
        while (currentGame.getBids().size() < 4) {
            Player currentBidder = currentGame.getPlayers().get(currentGame.getBidTurnIndex());

            if (!currentBidder.isAI())
                break;

            InitialBid aiBid = generateAIBid(currentBidder);
            currentGame.addBid(aiBid);

            if (!aiBid.isPassed() &&
                    (currentGame.getHighestBid() == null ||
                            aiBid.getValue() > currentGame.getHighestBid().getValue())) {
                currentGame.setHighestBid(aiBid);
            }

            currentGame.setBidTurnIndex((currentGame.getBidTurnIndex() + 1) % currentGame.getPlayers().size());
        }

        if (currentGame.getBids().size() >= 4) {
            PlayerPos winnerPos = currentGame.getHighestBid().getPlayer();
            Player winner = currentGame.getPlayers().stream()
                    .filter(p -> p.getPosition().equals(winnerPos))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("Winner not found"));

            // AI auto-finalizes
            if (winner.isAI()) {
                FinalBid finalBid = currentGame.getFinalBidCache().get(winnerPos);
                if (finalBid == null) {
                    throw new IllegalStateException("AI Final bid missing for: " + winnerPos);
                }
                currentGame.setWinningBidStats(finalBid);
            }

            // Rotate first bidder for next round
            PlayerPos[] order = PlayerPos.values();
            PlayerPos currentFirst = currentGame.getFirstBidder();
            int nextOrdinal = (currentFirst.ordinal() + 1) % order.length;

            currentGame.setFirstBidder(order[nextOrdinal]);
            currentGame.setBidTurnIndex(nextOrdinal);

            // clear information
            currentGame.getBids().clear();
            currentGame.setHighestBid(null);
            currentGame.getFinalBidCache().clear();

            // Human will finalize later via /finalizeBid
            currentGame.setPhase(GamePhase.KITTY);
        }

        return getGameStateForPlayer(request.getPlayer());
    }

    // Evaluates AI player's hand to provide an appropriate bid
    private InitialBid generateAIBid(Player ai) {
        HandEvaluator evaluator = new HandEvaluator(ai);
        evaluator.evaluateHand();

        List<FinalBid> bidOptions = evaluator.evaluateAll(ai.getPosition());
        InitialBid currentHigh = currentGame.getHighestBid();

        // Filter for stronger bids only
        List<FinalBid> strongerBids = bidOptions.stream()
                .filter(bid -> currentHigh == null || bid.getValue() > currentHigh.getValue())
                .toList();

        if (strongerBids.isEmpty()) {
            // No valid bids better than current — pass
            return InitialBid.pass(ai.getPosition());
        }

        FinalBid bestBid = (strongerBids.stream()
                .max(Comparator.comparingInt(FinalBid::getValue))
                .orElse(null));

        currentGame.getFinalBidCache().put(ai.getPosition(), bestBid);

        // Return the strongest bid (highest value)
        return bestBid.getInitialBid();
    }

    // Provides Kitty to winner and discards cards - May need to divide function
    // into 2
    public GameStateResponse applyKittyAndDiscards(KittyRequest request) {
        if (currentGame == null || currentGame.getPhase() != GamePhase.KITTY) {
            throw new IllegalStateException("Game is not in KITTY phase.");
        }

        if (!request.getPlayer().equals(currentGame.getWinningPlayerPos())) {
            throw new IllegalArgumentException("Only the winning player may apply the kitty.");
        }

        Player winner = currentGame.getPlayers().stream()
                .filter(p -> p.getName()
                        .equals(PlayerUtils.getNameByPosition(request.getPlayer(), currentGame.getPlayers())))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Winning player not found."));

        winner.getHand().getCards().addAll(currentGame.getKitty());

        if (request.getDiscards().size() != 6) {
            throw new IllegalArgumentException("You must discard exactly 6 cards.");
        }

        for (Card discard : request.getDiscards()) {
            boolean removed = winner.getHand().getCards()
                    .removeIf(c -> CardUtils.cardsMatch(c, discard));
            if (!removed) {
                throw new IllegalArgumentException(
                        "Discard card not found in hand: " + discard.getRank() + " of " + discard.getSuit());
            }
        }

        PlayerPos winnerPos = currentGame.getHighestBid().getPlayer();

        FinalBid winningBid;
        if (currentGame.getFinalBidCache().containsKey(winnerPos)) {
            winningBid = currentGame.getFinalBidCache().get(winnerPos);
        } else {
            throw new IllegalStateException("Winning bid must be finalized by human before taking the kitty.");
        }

        currentGame.setTrumpSuit(winningBid.getSuit());
        currentGame.getDeck().assignTrumpSuitToJokers(winningBid.getSuit());
        currentGame.setPhase(GamePhase.PLAY);

        return getGameStateForPlayer(request.getPlayer());

    }

    // getters
    public GameState getCurrentGame() {
        return currentGame;
    }

}
