package com.bidwhist.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.Comparator;
import java.util.HashMap;

import org.springframework.stereotype.Service;

import com.bidwhist.dto.Animation;
import com.bidwhist.dto.BidRequest;
import com.bidwhist.dto.CardVisibility;
import com.bidwhist.dto.FinalBidRequest;
import com.bidwhist.dto.GameStateResponse;
import com.bidwhist.dto.JoinGameRequest;
import com.bidwhist.dto.KittyRequest;
import com.bidwhist.dto.PlayRequest;
import com.bidwhist.dto.PlayerRequest;
import com.bidwhist.dto.PlayerView;
import com.bidwhist.dto.PollRequest;
import com.bidwhist.dto.PopAnimationRequest;
import com.bidwhist.dto.StartGameRequest;
import com.bidwhist.dto.CardOwner;
import com.bidwhist.bidding.InitialBid;
import com.bidwhist.bidding.FinalBid;
import com.bidwhist.bidding.HandEvaluator;
import com.bidwhist.bidding.BidType;
import com.bidwhist.model.GameRoom;
import com.bidwhist.model.Book;
import com.bidwhist.model.Card;
import com.bidwhist.model.Difficulty;
import com.bidwhist.model.GamePhase;
import com.bidwhist.model.GameState;
import com.bidwhist.model.PlayedCard;
import com.bidwhist.model.Player;
import com.bidwhist.model.PlayerPos;
import com.bidwhist.model.RoomStatus;
import com.bidwhist.model.Suit;
import com.bidwhist.model.Team;
import com.bidwhist.utils.CardUtils;
import com.bidwhist.utils.PlayerUtils;

@Service
public class GameService {

    private final DeckService deckService;
    private final Map<String, GameState> games = new ConcurrentHashMap<>();

    public GameService(DeckService deckService) {
        this.deckService = deckService;
    }

    // Start New Game, add players and shuffled deck
    public GameStateResponse startSoloGame(StartGameRequest request) {
        String playerName = request.getPlayerName();

        GameState game = new GameState(request.getGameId());

        game.setFirstBidder(PlayerPos.P1);
        game.setDifficulty(request.getDifficulty());

        // debug log
        System.out.println("Staring new game for player: " + playerName);

        // Define all positions
        PlayerPos[] positions = PlayerPos.values();
        List<Player> players = new ArrayList<>();

        // one human, three AIs
        Player humanPlayer = new Player(playerName, false, positions[0], Team.A);
        players.add(humanPlayer);

        // Fill the rest with AI
        players.add(new Player("AI 1", true, positions[1], Team.B));
        players.add(new Player("AI 2", true, positions[2], Team.A));
        players.add(new Player("AI 3", true, positions[3], Team.B));

        for (Player p : players) {
            System.out.println("Created player: " + p.getName() + " (" + p.getPosition() + ")");
        }

        game.getDeck().shuffle();
        game.setShuffledDeck(game.getDeck().getCards());

        game.getPlayers().addAll(players);
        game.setPhase(GamePhase.SHUFFLE);

        GameStateResponse response = getGameStateForPlayer(game, PlayerPos.P1);

        response.setPlayerPosition(PlayerUtils.getPositionByName(playerName, players));
        response.setViewerName(playerName);

        // debug log
        System.out.println("Current phase: " + game.getPhase());

        games.put(game.getGameId(), game);
        System.out.println(game.getGameId());
        dealToPlayers(game);

        return response;
    }

    // Start New Game, add players and shuffled deck
    public GameStateResponse createMutliplayerGame(StartGameRequest request) {
        // debug log
        System.out.println("Staring new game for player: " + request.getPlayerName());

        GameState game = new GameState(request.getGameId());

        Player player = new Player(request.getPlayerName(), false, PlayerPos.values()[0], Team.A);
        game.getRoom().addPlayer(player);

        game.getDeck().shuffle();
        game.setShuffledDeck(game.getDeck().getCards());

        game.getRoom().setStatus(RoomStatus.WAITING_FOR_PLAYERS);
        game.setPhase(GamePhase.INITIATED);

        GameStateResponse response = getGameStateForPlayer(game, player.getPosition());
        response.setPlayerPosition(player.getPosition());
        response.setViewerName(player.getName());
        response.setLobbySize(game.getRoom().getPlayers().size());

        // debug log
        System.out.println("Current phase: " + game.getPhase());

        return response;
    }

    public GameStateResponse joinGame(JoinGameRequest request) {
        GameState game = getGameById(request.getGameId());
        game.getRoom().addPlayer(request.getPlayerName());
        PlayerPos position = game.getRoom().getPlayerPositionByName(request.getPlayerName());
        if (game.getRoom().getStatus() == RoomStatus.READY) {
            game.setPhase(GamePhase.SHUFFLE);
            System.out.println("Current phase: " + game.getPhase());
            dealToPlayers(game);
            game.getRoom().setStatus(RoomStatus.IN_PROGRESS);
        }
        GameStateResponse response = getGameStateForPlayer(game, position);
        response.setPlayerPosition(position);
        response.setViewerName(request.getPlayerName());
        response.setLobbySize(game.getRoom().getPlayers().size());
        return response;

    }

    // Return GameState for specific player with dummy cards for other players
    public GameStateResponse getGameStateForPlayer(GameState game, PlayerPos playerPosition) {
        List<PlayerView> playerViews = new ArrayList<>();
        String viewerName = PlayerUtils.getNameByPosition(playerPosition, game.getPlayers());

        for (Player p : game.getPlayers()) {
            List<Card> visibleHand;

            if (p.getPosition().equals(playerPosition)) {
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

        List<Card> myKittyView = new ArrayList<>(game.getKitty());

        if (myKittyView.size() > 0) {
            if (PlayerUtils.getNameByPosition(playerPosition, game.getPlayers())
                    .equals(game.getWinningPlayerName())) {
                for (Card card : myKittyView) {
                    card.setVisibility(CardVisibility.VISIBLE_TO_SELF);
                }
            } else {

                for (int i = 0; i < myKittyView.size(); i++) {
                    myKittyView.set(i, new Card(null, null));
                    myKittyView.get(i).setVisibility(CardVisibility.HIDDEN);
                }
            }
        }
        
        List<Player> players = game.getPlayers();
        Team myTeam = game.getTeamByPlayerPos(players, playerPosition);

        GameStateResponse response = new GameStateResponse(
                game.getAnimationList().getOrDefault(playerPosition, new ArrayList<>()),
                playerViews,
                myKittyView,
                game.getCurrentTurnIndex(),
                game.getPhase(),
                game.getShuffledDeck(),
                playerPosition,
                viewerName,
                game.getFirstBidder(),
                game.getBidTurnIndex());

        response.setWinningPlayerName(game.getWinningPlayerName());
        response.setHighestBid(game.getHighestBid());
        response.setPlayerPosition(playerPosition);
        response.setLobbySize(game.getRoom().getPlayers().size());
        response.setTrumpSuit(game.getTrumpSuit());
        response.setBidType(game.getFinalBidType());
        response.setWinningPlayerName(game.getWinningPlayerName());
        response.setHighestBid(game.getHighestBid());
        response.setBids(game.getBids());
        response.setWinningBid(game.getWinningBid());
        response.setPlayerTeam(myTeam);
        return response;
    }

    {
        /*
         * NOT NEEDED???
         * public GameStateResponse getGameResultForPlayer(GameState game, PlayerPos
         * playerPosition) {
         * GameStateResponse response = getGameStateForPlayer(game, playerPosition);
         * 
         * Bid highestBid = game.getHighestBid();
         * Team biddingTeam = game.getPlayers().stream()
         * .filter(p -> p.getName().equals(highestBid.getPlayerName()))
         * .findFirst()
         * .map(Player::getTeam)
         * .orElse(null);
         * 
         * int tricksWon = currentGame.getTeamTrickCounts().getOrDefault(biddingTeam,
         * 0);
         * boolean success = tricksWon >= highestBid.getValue();
         * 
         * response.setBidSuccessful(success);
         * response.setWinningTeam(success ? biddingTeam : (biddingTeam == Team.A ?
         * Team.B : Team.A));
         * 
         * return response;
         * }
         */}

    public void dealToPlayers(GameState game) {

        if (game == null) {
            throw new IllegalStateException("Game not started.");
        }

        if (game.getRound() != 0) {
            startNewHand(game);
            game.getDeck().shuffle();
        }

        if (game.getPhase() != GamePhase.SHUFFLE) {
            throw new IllegalStateException("Can only deal after shuffle.");
        }

        game.getDeck().deal(game.getPlayers()); // Perform actual dealing

        // Animation signal : DEAL
        game.addAnimation(new Animation(game.getShuffledDeck()));

        game.setKitty(game.getDeck().getKitty().getCards()); // Move kitty over
        game.setPhase(GamePhase.BID); // Advance phase
        System.out.println("Current phase: " + game.getPhase());

    }

    public void startNewHand(GameState game) {

        game.getKitty().clear();
        game.setPhase(GamePhase.SHUFFLE);
        game.getBids().clear();
        game.getCompletedTricks().clear();
        game.getCurrentTrick().clear();
        game.setHighestBid(null);
        game.setWinningPlayerName(null);
        game.setTrumpType(null);
        game.getTeamTrickCounts().clear();
        game.getFinalBidCache().clear();
        game.getShuffledDeck().clear();
        game.setRound(game.getRound() + 1);
    }

    // Collects bid from players and generates AI Bids
    public GameStateResponse submitBid(BidRequest request) {
        GameState game = getGameById(request.getGameId());

        if (game == null) {
            throw new IllegalStateException("Game has not been started. Call /start first.");
        }

        // Find current human bidder and add their bid
        Player bidder = game.getPlayers().stream()
                .filter(p -> p.getPosition().equals(request.getPlayer()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Player not found"));

        InitialBid bid = BidRequest.fromRequest(request, bidder);
        game.addBid(bid);

        if (!bid.isPassed() &&
                (game.getHighestBid() == null ||
                        bid.getValue() > game.getHighestBid().getValue())) {
            game.setHighestBid(bid);
        }

        // Advance to next bidder
        int nextIndex = (game.getBidTurnIndex() + 1) % game.getPlayers().size();
        game.setBidTurnIndex(nextIndex);

        // If no bids and final bidder is AI, evaluate force bid.
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
                // Force minimum legal bid (e.g., 4, not a No Trump)
                HandEvaluator aiHandEval = new HandEvaluator(nextBidder);

                aiFinalBid = aiHandEval.getForcedMinimumBid(nextBidder.getPosition());
                aiBid = aiFinalBid.getInitialBid();

                game.getFinalBidCache().put(nextBidder.getPosition(), aiFinalBid);

            } else {
                aiBid = generateAIBid(game, nextBidder);
                System.out.println("DEBUG: " + nextBidder.getName() + " (AI) bids "
                        + aiBid.getValue() + " is No?: " + aiBid.isNo());
            }

            game.addBid(aiBid);

            if (!aiBid.isPassed() &&
                    (game.getHighestBid() == null ||
                            aiBid.getValue() > game.getHighestBid().getValue())) {
                game.setHighestBid(aiBid);
            }

            game.setBidTurnIndex((game.getBidTurnIndex() + 1) % game.getPlayers().size());
        }

        // Once all 4 bids are in, set phase and finalize AI if needed
        if (game.getBids().size() >= 4) {
            PlayerPos winnerPos = game.getHighestBid().getPlayer();
            Player winner = game.getPlayers().stream()
                    .filter(p -> p.getPosition().equals(winnerPos))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("Winner not found"));

            if (winner.isAI()) {
                FinalBid finalBid = game.getFinalBidCache().get(winnerPos);
                if (finalBid == null) {
                    throw new IllegalStateException("AI Final bid missing for: " + winnerPos);
                }
                game.setWinningBidStats(finalBid);
                applyAIAutoKitty(game, winner);
            }
            int nextTurn = (game.getBidTurnIndex() + 1) % 4;
            game.setBidTurnIndex(nextTurn);
            game.setFirstBidder(PlayerPos.values()[nextTurn]);
            game.setCurrentTurnIndex(winnerPos.ordinal());
            game.setWinningPlayerName(winner.getName());
            if (winner.isAI()) {
                game.setPhase(GamePhase.PLAY);
            } else {
                game.setPhase(GamePhase.KITTY);
            }
        }

        return getGameStateForPlayer(game, request.getPlayer());
    }

    // Evaluates AI player's hand to provide an appropriate bid
    private InitialBid generateAIBid(GameState game, Player ai) {
        HandEvaluator evaluator = new HandEvaluator(ai);
        evaluator.evaluateHand();

        List<FinalBid> bidOptions = evaluator.evaluateAll(ai.getPosition());
        InitialBid currentHigh = game.getHighestBid();

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

        game.getFinalBidCache().put(ai.getPosition(), bestBid);

        // Return the strongest bid (highest value)
        return bestBid.getInitialBid();
    }

    // Get full winning bid
    public GameStateResponse getFinalBid(FinalBidRequest request) {
        GameState game = getGameById(request.getGameId());

        PlayerPos winnerPos = request.getPlayer();
        Player winner = game.getPlayers().stream()
                .filter(p -> p.getPosition().equals(winnerPos))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Player not found: " + winnerPos));

        if (winner.isAI()) {
            throw new IllegalStateException("AI players do not need to finalize a bid.");
        }

        FinalBid finalBid = new FinalBid(
                game.getHighestBid(),
                request.getType(),
                request.getSuit());

        game.getFinalBidCache().put(winnerPos, finalBid);
        game.setWinningBid(finalBid);
        game.setWinningBidStats(finalBid);
        game.getBids().clear();
        game.setTrumpType(request.getType());
        game.setTrumpSuit(request.getSuit());

        GameStateResponse response = getGameStateForPlayer(game, winnerPos);
        response.setKitty(game.getKitty());

        return response;
    }

    // Provides Kitty to winner and discards cards - May need to divide function
    // into 2
    public GameStateResponse applyKittyAndDiscards(KittyRequest request) {
        GameState game = getGameById(request.getGameId());

        if (game == null || game.getPhase() != GamePhase.KITTY) {
            throw new IllegalStateException("Game is not in KITTY phase.");
        }

        if (!request.getPlayer().equals(game.getWinningPlayerPos())) {
            throw new IllegalArgumentException("Only the winning player may apply the kitty.");
        }

        Player winner = game.getPlayers().stream()
                .filter(p -> p.getName()
                        .equals(PlayerUtils.getNameByPosition(request.getPlayer(), game.getPlayers())))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Winning player not found."));

        winner.getHand().getCards().addAll(game.getKitty());

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

        PlayerPos winnerPos = game.getHighestBid().getPlayer();

        FinalBid winningBid;
        if (game.getFinalBidCache().containsKey(winnerPos)) {
            winningBid = game.getFinalBidCache().get(winnerPos);
        } else {
            throw new IllegalStateException("Winning bid must be finalized by human before taking the kitty.");
        }

        game.setTrumpSuit(winningBid.getSuit());
        game.getKitty().clear();
        game.getDeck().assignTrumpSuitToJokers(winningBid.getSuit());
        game.setPhase(GamePhase.PLAY);

        return getGameStateForPlayer(game, request.getPlayer());

    }

    public GameStateResponse playCard(PlayRequest request) {
        GameState game = getGameById(request.getGameId());

        if (game.getPhase() != GamePhase.PLAY) {
            throw new IllegalStateException("Not in PLAY phase");
        }

        Player currentPlayer = PlayerUtils.getPlayerByPosition(request.getPlayer(), game.getPlayers());
        System.out.println("DEBUG: Current turn is player index " + game.getCurrentTurnIndex()
                + " (" + currentPlayer.getName() + ")");
        System.out.println("DEBUG: Player " + request.getPlayer() + " playing: " +
                request.getCard().getRank() + " of " + request.getCard().getSuit());

        if (game.getCurrentTurnIndex() != request.getPlayer().ordinal()) {
            throw new IllegalStateException("It's not " + request.getPlayer() + "'s turn");
        }

        Card cardToPlay = request.getCard();
        if (!currentPlayer.getHand().getCards().contains(cardToPlay)) {
            throw new IllegalArgumentException("Player does not have the specified card " +
                    cardToPlay.getRank() + " of " + cardToPlay.getSuit());
        }

        List<PlayedCard> currentTrick = game.getCurrentTrick();
        if (!currentTrick.isEmpty()) {
            Suit leadSuit = currentTrick.get(0).getCard().getSuit();
            boolean hasLeadSuit = currentPlayer.getHand().getCards().stream()
                    .anyMatch(c -> c.getSuit() == leadSuit);
            if (hasLeadSuit && cardToPlay.getSuit() != leadSuit) {
                throw new IllegalArgumentException("Must follow suit if possible");
            }
        }

        PlayedCard validPlayedCard = new PlayedCard(request.getPlayer(), cardToPlay);

        currentPlayer.getHand().getCards().remove(cardToPlay);
        currentTrick.add(validPlayedCard);

        // Animation Signal : PLAY
        game.addAnimation(new Animation(validPlayedCard));

        // Advance turn
        game.setCurrentTurnIndex((game.getCurrentTurnIndex() + 1) % 4);

        if (currentTrick.size() == 4) {
            PlayedCard winningPlay = determineTrickWinner(currentTrick, game.getTrumpType());
            Player winner = PlayerUtils.getPlayerByPosition(winningPlay.getPlayer(), game.getPlayers());
            Team winnerTeam = winner.getTeam();
            System.out.println("DEBUG: Trick won by " + winner.getName() + " (Team " + winnerTeam + ")");

            game.getTeamTrickCounts().putIfAbsent(winnerTeam, 0);
            game.getTeamTrickCounts().put(
                    winnerTeam,
                    game.getTeamTrickCounts().get(winnerTeam) + 1);

            Book currentBook = new Book(currentTrick, winnerTeam);

            game.getCompletedTricks().add(currentBook);

            // Animation signal - COLLECT
            game.addAnimation(new Animation(currentBook));

            currentTrick.clear();
            game.setCurrentTurnIndex(winner.getPosition().ordinal());

            if (game.getCompletedTricks().size() == 12) {
                scoreHand(game);
                game.setPhase(GamePhase.END);
                return getGameStateForPlayer(game, request.getPlayer());
            }
        }
        if (PlayerUtils.getPlayerByPosition(PlayerPos.values()[game.getCurrentTurnIndex()],
                game.getPlayers()).isAI()) {

            // Always let AI play if it's their turn now
            autoPlayAITurns(game);
        }

        return getGameStateForPlayer(game, request.getPlayer());
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

    private void autoPlayAITurns(GameState game) {
        while (true) {
            Player current = game.getPlayers().get(game.getCurrentTurnIndex());
            if (!current.isAI()) {
                break;
            }

            Card chosenCard = chooseCardForAI(game, current, game.getCurrentTrick());
            System.out.println(
                    "DEBUG: " + current.getName() + " played card: " + chosenCard + " " + game.getCurrentTurnIndex());

            current.getHand().getCards().remove(chosenCard);

            PlayedCard validPlayedCard = new PlayedCard(current.getPosition(), chosenCard);

            game.getCurrentTrick().add(validPlayedCard);

            // Animation Signal : PLAY
            game.addAnimation(new Animation(validPlayedCard));

            game.setCurrentTurnIndex((game.getCurrentTurnIndex() + 1) % 4);

            if (game.getCurrentTrick().size() == 4) {
                System.out.println("DEBUG: Trick complete. Evaluating winner...");
                PlayedCard winner = determineTrickWinner(game.getCurrentTrick(), game.getTrumpType());

                Player winnerPlayer = PlayerUtils.getPlayerByPosition(winner.getPlayer(), game.getPlayers());
                Team winnerTeam = winnerPlayer.getTeam();
                System.out.println("DEBUG: Trick won by " + winnerPlayer.getName() + " (Team " + winnerTeam + ")");
                List<PlayedCard> currentTrick = game.getCurrentTrick();
                Book currentBook = new Book(currentTrick, winnerTeam);

                // Animation signal - COLLECT
                game.addAnimation(new Animation(currentBook));

                game.getTeamTrickCounts().putIfAbsent(winnerTeam, 0);
                game.getTeamTrickCounts().put(winnerTeam, game.getTeamTrickCounts().get(winnerTeam) + 1);
                System.out.println("DEBUG: Team trick counts: " + game.getTeamTrickCounts());

                game.getCompletedTricks().add(currentBook);

                game.getCurrentTrick().clear();
                game.setCurrentTurnIndex(winnerPlayer.getPosition().ordinal());

                if (game.getCompletedTricks().size() == 13) {
                    scoreHand(game);
                    game.setPhase(GamePhase.END);
                    return;
                }

                // Recursively call autoPlayAITurns if winner is also AI
                if (winnerPlayer.isAI()) {
                    autoPlayAITurns(game);
                }

                return; // End loop for current trick
            }
        }
    }

    private Card chooseCardForAI(GameState game, Player aiPlayer, List<PlayedCard> currentTrick) {
        List<Card> hand = aiPlayer.getHand().getCards();
        Difficulty difficulty = game.getDifficulty();
        Suit trumpSuit = getTrumpSuitFromBidType(game);

        if (difficulty == Difficulty.EASY) {
            // Always play lowest legal card
            return getLowestLegalCard(hand, currentTrick);
        }

        if (difficulty == Difficulty.MEDIUM) {
            // Same logic as before
            if (currentTrick.isEmpty()) {
                return hand.stream()
                        .filter(c -> trumpSuit == null || !c.getSuit().equals(trumpSuit))
                        .max(Comparator.comparingInt(c -> c.getRank().getValue()))
                        .orElse(hand.get(0));
            }

            Suit leadSuit = currentTrick.get(0).getCard().getSuit();
            List<Card> sameSuit = hand.stream().filter(c -> c.getSuit() == leadSuit).collect(Collectors.toList());
            if (!sameSuit.isEmpty()) {
                return sameSuit.stream()
                        .min(Comparator.comparingInt(c -> c.getRank().getValue()))
                        .orElse(sameSuit.get(0));
            }

            return hand.stream().min(Comparator.comparingInt(c -> c.getRank().getValue())).orElse(hand.get(0));
        }

        if (difficulty == Difficulty.HARD) {
            // Try to win trick or save good cards
            if (currentTrick.isEmpty()) {
                return hand.stream()
                        .filter(c -> trumpSuit == null || !c.getSuit().equals(trumpSuit))
                        .max(Comparator.comparingInt(c -> c.getRank().getValue()))
                        .orElse(hand.get(0));
            }

            Suit leadSuit = currentTrick.get(0).getCard().getSuit();
            List<Card> followSuitCards = hand.stream().filter(c -> c.getSuit() == leadSuit)
                    .collect(Collectors.toList());

            if (!followSuitCards.isEmpty()) {
                return followSuitCards.stream()
                        .max(Comparator.comparingInt(c -> c.getRank().getValue()))
                        .orElse(followSuitCards.get(0));
            }

            // Try to throw off lowest non-trump
            return hand.stream()
                    .filter(c -> trumpSuit == null || !c.getSuit().equals(trumpSuit))
                    .min(Comparator.comparingInt(c -> c.getRank().getValue()))
                    .orElse(hand.get(0));
        }

        return hand.get(0); // fallback
    }

    private Card getLowestLegalCard(List<Card> hand, List<PlayedCard> trick) {
        if (trick.isEmpty()) {
            return hand.stream().min(Comparator.comparingInt(c -> c.getRank().getValue())).orElse(hand.get(0));
        }

        Suit leadSuit = trick.get(0).getCard().getSuit();
        List<Card> sameSuit = hand.stream().filter(c -> c.getSuit() == leadSuit).collect(Collectors.toList());
        if (!sameSuit.isEmpty()) {
            return sameSuit.stream().min(Comparator.comparingInt(c -> c.getRank().getValue())).orElse(sameSuit.get(0));
        }

        return hand.stream().min(Comparator.comparingInt(c -> c.getRank().getValue())).orElse(hand.get(0));
    }

    private void applyAIAutoKitty(GameState game, Player winner) {
        // Add kitty to hand
        winner.getHand().getCards().addAll(game.getKitty());

        // Auto-discard the lowest 6 cards (simple logic)
        List<Card> sorted = new ArrayList<>(winner.getHand().getCards());
        sorted.sort(Comparator.comparingInt(c -> c.getRank().getValue()));
        List<Card> toDiscard = sorted.subList(0, 6);

        for (Card card : toDiscard) {
            winner.getHand().getCards().remove(card);
        }

        game.setKitty(new ArrayList<>());

        // Set trump from the bid
        PlayerPos winnerPos = game.getHighestBid().getPlayer();
        FinalBid winningBid = game.getFinalBidCache().get(winnerPos);
        game.setTrumpType(winningBid.getType());
        game.setPhase(GamePhase.PLAY);
        game.setCurrentTurnIndex(winnerPos.ordinal());
        System.out.println("DEBUG: First trick will be led by " + winner.getName());
        autoPlayAITurns(game);
    }

    private void scoreHand(GameState game) {

        FinalBid winningBid = game.getWinningBid();
        Team winningTeam = game.getPlayers().stream()
                .filter(p -> p.getPosition().equals(winningBid.getPlayer()))
                .findFirst()
                .map(Player::getTeam)
                .orElseThrow();

        int tricksWon = game.getTeamTrickCounts().getOrDefault(winningTeam, 0);
        int bidValue = winningBid.getValue();

        boolean success = tricksWon >= bidValue;
        int points = success ? bidValue : -bidValue;

        game.getTeamScores().putIfAbsent(winningTeam, 0);
        game.getTeamScores().put(winningTeam,
                game.getTeamScores().get(winningTeam) + points);
    }
    // Place this just before the final closing brace of GameService

    private Suit getTrumpSuitFromBidType(GameState game) {
        return switch (game.getTrumpType()) {
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

    public void popAnimation(PopAnimationRequest request) {
        GameState game = getGameById(request.getGameId());
        PlayerPos playerPosition = request.getPlayer();
        String animationId = request.getAnimationId();

        game.removeAnimationById(playerPosition, animationId);
    }

    public GameStateResponse updateState(PollRequest request) {
        PlayerPos playerPosition = request.getPlayer();
        GameState game = getGameById(request.getGameId());
        autoPlayAITurns(game);

        return getGameStateForPlayer(game, playerPosition);
    }

    public GameState getGameById(String gameId) {
        if (gameId == null) {
            throw new IllegalArgumentException("Game ID cannot be null");
        }

        GameState game = games.get(gameId);
        if (game == null) {
            throw new IllegalStateException("No game found with ID: " + gameId);
        }
        return game;
    }

}
