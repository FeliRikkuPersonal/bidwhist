// src/main/java/com/bidwhist/service/GameService.java
package com.bidwhist.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import com.bidwhist.bidding.BidType;
import com.bidwhist.bidding.FinalBid;
import com.bidwhist.bidding.InitialBid;
import com.bidwhist.dto.Animation;
import com.bidwhist.dto.AnimationType;
import com.bidwhist.dto.BidRequest;
import com.bidwhist.dto.CardVisibility;
import com.bidwhist.dto.FinalBidRequest;
import com.bidwhist.dto.GameStateResponse;
import com.bidwhist.dto.HandRequest;
import com.bidwhist.dto.HandResponse;
import com.bidwhist.dto.JoinGameRequest;
import com.bidwhist.dto.KittyRequest;
import com.bidwhist.dto.PlayRequest;
import com.bidwhist.dto.PlayerView;
import com.bidwhist.dto.PollRequest;
import com.bidwhist.dto.PopAnimationRequest;
import com.bidwhist.dto.QuitGameRequest;
import com.bidwhist.dto.StartGameRequest;
import com.bidwhist.model.Book;
import com.bidwhist.model.Card;
import com.bidwhist.model.GamePhase;
import com.bidwhist.model.GameState;
import com.bidwhist.model.PlayedCard;
import com.bidwhist.model.Player;
import com.bidwhist.model.PlayerPos;
import com.bidwhist.model.RoomStatus;
import com.bidwhist.model.Suit;
import com.bidwhist.model.Team;
import com.bidwhist.utils.AIUtils;
import com.bidwhist.utils.CardUtils;
import com.bidwhist.utils.GameplayUtils;
import com.bidwhist.utils.PlayerUtils;

@Service
public class GameService {

    private final Map<String, GameState> games = new ConcurrentHashMap<>();

    /* Constructor for GameService (DeckService currently unused) */
    public GameService(DeckService deckService) {
    }

    /*
   * Starts a solo game with one human player and three AIs.
   * Initializes player positions, shuffles the deck, assigns cards, and updates
   * state.
     */
    public GameStateResponse startSoloGame(StartGameRequest request) {
        String playerName = request.getPlayerName();
        GameState game = new GameState(request.getGameId());

        game.setFirstBidder(PlayerPos.P1);
        game.setDifficulty(request.getDifficulty());

        System.out.println("Staring new game for player: " + playerName);

        PlayerPos[] positions = PlayerPos.values();
        List<Player> players = new ArrayList<>();

        Player humanPlayer = new Player(playerName, false, positions[0], Team.A);
        players.add(humanPlayer);

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

        System.out.println("Current phase: " + game.getPhase());

        games.put(game.getGameId(), game);
        System.out.println(game.getGameId());
        GameplayUtils.dealToPlayers(game);

        return response;
    }

    /*
   * Starts a multiplayer game and registers the first player.
   * Sets game status to waiting for additional players.
     */
    public GameStateResponse createMutliplayerGame(StartGameRequest request) {
        System.out.println("Staring new game for player: " + request.getPlayerName());

        GameState game = new GameState(request.getGameId());

        Player player = new Player(request.getPlayerName(), false, PlayerPos.values()[0], Team.A);
        game.getRoom().addPlayer(player);
        game.getPlayers().add(player);

        game.getDeck().shuffle();
        game.setShuffledDeck(game.getDeck().getCards());

        game.getRoom().setStatus(RoomStatus.WAITING_FOR_PLAYERS);
        game.setPhase(GamePhase.INITIATED);

        games.putIfAbsent(request.getGameId(), game);
        games.putIfAbsent(game.getGameId(), game);

        GameStateResponse response = getGameStateForPlayer(game, player.getPosition());
        response.setPlayerPosition(player.getPosition());
        response.setViewerName(player.getName());
        response.setLobbySize(game.getRoom().getPlayers().size());

        System.out.println("Current phase: " + game.getPhase());

        return response;
    }

    /*
   * Allows a player to join an existing game.
   * If room is ready, triggers shuffle and deals cards.
     */
    public GameStateResponse joinGame(JoinGameRequest request) {
        GameState game = getGameById(request.getGameId());
        game.getRoom().addPlayer(request.getPlayerName());

        List<Player> roomPlayers = game.getRoom().getPlayers();
        Player thisPlayer = roomPlayers.get(roomPlayers.size() - 1);
        game.getPlayers().add(thisPlayer);

        PlayerPos position = game.getRoom().getPlayerPositionByName(request.getPlayerName());

        if (game.getRoom().getStatus() == RoomStatus.READY) {
            game.setPhase(GamePhase.SHUFFLE);
            System.out.println("Current phase: " + game.getPhase());
            GameplayUtils.dealToPlayers(game);
            game.getRoom().setStatus(RoomStatus.IN_PROGRESS);
        }

        GameStateResponse response = getGameStateForPlayer(game, position);
        response.setPlayerPosition(position);
        response.setViewerName(request.getPlayerName());
        response.setLobbySize(game.getRoom().getPlayers().size());

        return response;
    }

    /*
   * Returns a GameStateResponse customized for a single player.
   * Includes masked hands, phase info, team, and all visible public game state.
     */
    public GameStateResponse getGameStateForPlayer(GameState game, PlayerPos playerPosition) {
        List<PlayerView> playerViews = getMyPlayerViews(game, playerPosition);
        List<Card> myKittyView = getMyKittyView(game, playerPosition);
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
                game.getFirstBidder(),
                game.getBidTurnIndex());

        response.setWinningPlayerName(game.getWinningPlayerName());
        response.setHighestBid(game.getHighestBid());
        response.setPlayerPosition(playerPosition);
        response.setLobbySize(game.getRoom().getPlayers().size());
        response.setTrumpSuit(game.getTrumpSuit());
        response.setBidType(game.getFinalBidType());
        response.setBids(game.getBids());
        response.setWinningBid(game.getWinningBid());
        response.setPlayerTeam(myTeam);
        response.setBidWinnerPos(game.getBidWinnerPos());
        response.setTeamAScore(game.getTeamAScore());
        response.setTeamBScore(game.getTeamBScore());

        return response;
    }

    /*
   * Submits a bid from a human player.
   * Also triggers AI bidding logic and resolves winner if 4 bids exist.
     */
    public GameStateResponse submitBid(BidRequest request) {
        GameState game = getGameById(request.getGameId());

        if (game == null) {
            throw new IllegalStateException("Game has not been started. Call /start first.");
        }

        Player bidder = game.getPlayers().stream()
                .filter(p -> p.getPosition().equals(request.getPlayer()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Player not found"));

        InitialBid bid = BidRequest.fromRequest(request, bidder);
        game.addBid(bid);

        if (!bid.isPassed()) {
            InitialBid currentHighest = game.getHighestBid();

            // Compares bid to select winner (if same value NO_TRUMP wins if isNO() otherwise first bidder wins)
            if (currentHighest == null || bid.compareTo(currentHighest) > 0) {
                game.setHighestBid(bid);
            }

            /*if (currentHighest == null || bid.getValue() > currentHighest.getValue()) {
                game.setHighestBid(bid);
            } else if (bid.getValue() == currentHighest.getValue()) {
                // Same-value bid — do nothing, keep earlier bid
            }*/
        }

        int nextIndex = (game.getBidTurnIndex() + 1) % game.getPlayers().size();
        game.setBidTurnIndex(nextIndex);

        AIUtils.processAllBids(game);

        if (game.getBids().size() >= 4) {
            PlayerPos winnerPos = game.getHighestBid().getPlayer();
            Player winner = game.getPlayers().stream()
                    .filter(p -> p.getPosition().equals(winnerPos))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("Winner not found"));

            game.setBidWinnerPos(winnerPos);

            //NO_TRUMP Handling
            if (game.getHighestBid().isNo()) {
                game.setBidType(BidType.NO_TRUMP);
                game.setTrumpSuit(null);

                FinalBid finalBid = new FinalBid(game.getHighestBid().getPlayer(), game.getHighestBid().getValue(), BidType.NO_TRUMP, null);
                game.setWinningBidStats(finalBid);

                if (winner.isAI()) {
                    AIUtils.applyAIAutoKitty(game, winner);
                    game.setPhase(GamePhase.PLAY);
                } else {
                    game.setPhase(GamePhase.KITTY);
                }

                int nextTurn = (game.getBidTurnIndex() + 1) % 4;
                game.setBidTurnIndex(nextTurn);
                game.setFirstBidder(PlayerPos.values()[nextTurn]);

                return getGameStateForPlayer(game, request.getPlayer());
            }

            if (winner.isAI()) {
                FinalBid finalBid = game.getFinalBidCache().get(winnerPos);
                if (finalBid == null) {
                    throw new IllegalStateException("AI Final bid missing for: " + winnerPos);
                }
                game.setWinningBidStats(finalBid);
                game.getDeck().assignTrumpSuitToJokers(finalBid.getSuit());
                AIUtils.applyAIAutoKitty(game, winner);
            }

            int nextTurn = (game.getBidTurnIndex() + 1) % 4;
            game.setBidTurnIndex(nextTurn);
            game.setFirstBidder(PlayerPos.values()[nextTurn]);

            if (winner.isAI()) {
                game.setPhase(GamePhase.PLAY);
            } else {
                game.setPhase(GamePhase.KITTY);
            }
        }

        return getGameStateForPlayer(game, request.getPlayer());
    }

    /*
   * Finalizes the winning bid by the human player.
   * Assigns trump suit and updates game state and kitty visibility.
     */
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

        FinalBid finalBid = new FinalBid(game.getHighestBid(), request.getType(), request.getSuit());

        game.getFinalBidCache().put(winnerPos, finalBid);
        game.setWinningBid(finalBid);
        game.setWinningBidStats(finalBid);
        game.getBids().clear();
        game.setBidType(request.getType());
        game.setTrumpSuit(request.getSuit());
        game.getDeck().assignTrumpSuitToJokers(request.getSuit());

        GameStateResponse response = getGameStateForPlayer(game, winnerPos);
        response.setKitty(game.getKitty());
        winner.getHand().getCards().addAll(game.getKitty());
        game.addAnimation(new Animation(AnimationType.UPDATE_CARDS));

        return response;
    }

    /*
   * Applies the kitty and discards to the winner’s hand.
   * Advances game to PLAY phase once done.
     */
    public GameStateResponse applyKittyAndDiscards(KittyRequest request) {
        GameState game = getGameById(request.getGameId());

        if (game == null || game.getPhase() != GamePhase.KITTY) {
            throw new IllegalStateException("Game is not in KITTY phase.");
        }

        if (!request.getPlayer().equals(game.getWinningPlayerPos())) {
            throw new IllegalArgumentException("Only the winning player may apply the kitty.");
        }

        Player winner = game.getPlayers().stream()
                .filter(
                        p -> p.getName()
                                .equals(
                                        PlayerUtils.getNameByPosition(request.getPlayer(), game.getPlayers())))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Winning player not found."));

        if (request.getDiscards().size() != 6) {
            throw new IllegalArgumentException("You must discard exactly 6 cards.");
        }

        for (Card discard : request.getDiscards()) {
            boolean removed = winner.getHand().getCards().removeIf(c -> CardUtils.cardsMatch(c, discard));
            if (!removed) {
                throw new IllegalArgumentException(
                        "Discard card not found in hand: " + discard.getRank() + " of " + discard.getSuit());
            }
        }

        game.addAnimation(new Animation(AnimationType.UPDATE_CARDS));
        PlayerPos winnerPos = game.getHighestBid().getPlayer();

        FinalBid winningBid = game.getWinningBid();
        if (winningBid == null) {
            throw new IllegalStateException("Winning bid is not set.");
        }

        if (!winningBid.isNo() && !game.getFinalBidCache().containsKey(winnerPos)) {
            throw new IllegalStateException("Winning bid must be finalized by human before taking the kitty.");
        }

        game.setTrumpSuit(winningBid.getSuit());
        game.getKitty().clear();
        game.getDeck().assignTrumpSuitToJokers(winningBid.getSuit());
        game.setPhase(GamePhase.PLAY);
        game.setCurrentTurnIndex(winnerPos.ordinal());

        return getGameStateForPlayer(game, request.getPlayer());
    }

    /*
   * Plays a card from a player and evaluates the trick.
   * Validates legality, triggers animations, and handles trick scoring.
     */
    public GameStateResponse playCard(PlayRequest request) {
        System.out.println("[playCard]");

        GameState game = getGameById(request.getGameId());

        if (game.getPhase() != GamePhase.PLAY) {
            throw new IllegalStateException("Not in PLAY phase");
        }

        Player currentPlayer = PlayerUtils.getPlayerByPosition(request.getPlayer(), game.getPlayers());
        System.out.println(
                "DEBUG: Current turn is player index "
                + game.getCurrentTurnIndex()
                + " ("
                + currentPlayer.getName()
                + ")");
        System.out.println(
                "DEBUG: Player "
                + request.getPlayer()
                + " playing: "
                + request.getCard().getRank()
                + " of "
                + request.getCard().getSuit());

        if (game.getCurrentTurnIndex() != request.getPlayer().ordinal()) {
            throw new IllegalStateException("It's not " + request.getPlayer() + "'s turn");
        }

        Card cardToPlay = request.getCard();
        if (!currentPlayer.getHand().getCards().contains(cardToPlay)) {
            throw new IllegalArgumentException(
                    "Player does not have the specified card "
                    + cardToPlay.getRank()
                    + " of "
                    + cardToPlay.getSuit());
        }

        List<PlayedCard> currentTrick = game.getCurrentTrick();
        if (!currentTrick.isEmpty()) {
            Suit leadSuit = currentTrick.get(0).getCard().getSuit();
            game.setLeadSuit(leadSuit);
            boolean hasLeadSuit = currentPlayer.getHand().getCards().stream().anyMatch(c -> c.getSuit() == leadSuit);
            if (hasLeadSuit && cardToPlay.getSuit() != leadSuit) {
                throw new IllegalArgumentException("Must follow suit if possible");
            }
        }

        PlayedCard validPlayedCard = new PlayedCard(request.getPlayer(), cardToPlay);

        currentPlayer.getHand().getCards().remove(cardToPlay);
        currentTrick.add(validPlayedCard);
        game.addPlayedCard(cardToPlay);

        game.addAnimation(new Animation(validPlayedCard, game.getLeadSuit()));
        game.setCurrentTurnIndex((game.getCurrentTurnIndex() + 1) % 4);

        if (currentTrick.size() == 4) {
            PlayedCard winningPlay = GameplayUtils.determineTrickWinner(game, currentTrick);
            Player winner = PlayerUtils.getPlayerByPosition(winningPlay.getPlayer(), game.getPlayers());
            Team winnerTeam = winner.getTeam();
            System.out.println("DEBUG: Trick won by " + winner.getName() + " (Team " + winnerTeam + ")");

            game.getTeamTrickCounts().putIfAbsent(winnerTeam, 0);
            game.getTeamTrickCounts().put(winnerTeam, game.getTeamTrickCounts().get(winnerTeam) + 1);
            System.out.println("DEBUG: Team trick counts: " + game.getTeamTrickCounts());

            Book currentBook = new Book(currentTrick, winnerTeam);
            game.getCompletedTricks().add(currentBook);

            game.addAnimation(new Animation(currentBook));
            game.addAnimation(new Animation(AnimationType.UPDATE_CARDS));

            currentTrick.clear();
            game.setCurrentTurnIndex(winner.getPosition().ordinal());

            if (game.getCompletedTricks().size() == 12) {
                GameplayUtils.scoreHand(game);
                if (game.getTeamAScore() >= 7 || game.getTeamBScore() <= -7) {
                    game.addAnimation(new Animation(AnimationType.SHOW_WINNER));
                    game.setPhase(GamePhase.END);
                } else {
                    game.addAnimation(new Animation(AnimationType.CLEAR));
                    GameplayUtils.startNewHand(game);
                }
                return getGameStateForPlayer(game, request.getPlayer());
            }
        }

        if (PlayerUtils.getPlayerByPosition(
                PlayerPos.values()[game.getCurrentTurnIndex()], game.getPlayers())
                .isAI()) {
            AIUtils.autoPlayAITurns(game);
        }

        return getGameStateForPlayer(game, request.getPlayer());
    }

    /*
   * Removes a single animation (by ID) from a player's animation queue.
     */
    public void popAnimation(PopAnimationRequest request) {
        GameState game = getGameById(request.getGameId());
        PlayerPos playerPosition = request.getPlayer();
        String animationId = request.getAnimationId();

        game.removeAnimationById(playerPosition, animationId);
    }

    /*
   * Removes a person from a game.
     */
    public void quitMyGame(QuitGameRequest request) {
        GameState game = getGameById(request.getGameId());
        String player = PlayerUtils.getNameByPosition(request.getPlayer(), game.getPlayers());
        if (request.getMode() == "multiplayer") {
            game.addAnimation(new Animation(player));
            games.remove(request.getGameId());
        }
    }

    /*
   * Returns the latest game state for polling updates.
     */
    public GameStateResponse updateState(PollRequest request) {
        PlayerPos playerPosition = request.getPlayer();
        GameState game = getGameById(request.getGameId());

        return getGameStateForPlayer(game, playerPosition);
    }

    /*
   * Retrieves a game instance from memory by its ID.
     */
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

    /*
   * Returns a HandResponse including player views and kitty for UI refresh.
     */
    public HandResponse provideUpdatedCards(HandRequest request) {
        GameState game = getGameById(request.getGameId());
        PlayerPos playerPosition = request.getPlayer();

        List<PlayerView> views = getMyPlayerViews(game, playerPosition);
        List<Card> kitty = getMyKittyView(game, playerPosition);

        return new HandResponse(views, kitty);
    }

    /*
   * Builds list of PlayerView objects for the requesting player.
   * Hides other hands with placeholder cards.
     */
    public List<PlayerView> getMyPlayerViews(GameState game, PlayerPos playerPosition) {
        List<PlayerView> playerViews = new ArrayList<>();

        for (Player p : game.getPlayers()) {
            List<Card> visibleHand;

            if (p.getPosition().equals(playerPosition)) {
                visibleHand = p.getHand().getCards();
            } else {
                int hiddenCount = p.getHand().getCards().size();
                visibleHand = new ArrayList<>();
                for (int i = 0; i < hiddenCount; i++) {
                    Card hiddenCard = new Card(null, null);
                    hiddenCard.setVisibility(CardVisibility.HIDDEN);
                    visibleHand.add(hiddenCard);
                }
            }

            playerViews.add(
                    new PlayerView(p.getName(), p.getPosition(), p.getTeam(), p.isAI(), visibleHand));
        }

        return playerViews;
    }

    /*
   * Returns the correct kitty view for the player.
   * Winning player sees the actual cards, others see hidden placeholders.
     */
    public List<Card> getMyKittyView(GameState game, PlayerPos playerPosition) {
        List<Card> myKittyView = new ArrayList<>(game.getKitty());

        if (!myKittyView.isEmpty()) {
            String viewerName = PlayerUtils.getNameByPosition(playerPosition, game.getPlayers());

            if (viewerName.equals(game.getWinningPlayerName())) {
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

        return myKittyView;
    }
}
