// src/main/java/com/bidwhist/service/GameService.java

package com.bidwhist.service;

import com.bidwhist.bidding.BidType;
import com.bidwhist.bidding.FinalBid;
import com.bidwhist.bidding.HandEvaluator;
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
import com.bidwhist.dto.StartGameRequest;
import com.bidwhist.model.Book;
import com.bidwhist.model.Card;
import com.bidwhist.model.Difficulty;
import com.bidwhist.model.GamePhase;
import com.bidwhist.model.GameState;
import com.bidwhist.model.PlayedCard;
import com.bidwhist.model.Player;
import com.bidwhist.model.PlayerPos;
import com.bidwhist.model.Rank;
import com.bidwhist.model.RoomStatus;
import com.bidwhist.model.Suit;
import com.bidwhist.model.Team;
import com.bidwhist.utils.CardUtils;
import com.bidwhist.utils.JokerUtils;
import com.bidwhist.utils.PlayerUtils;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class GameService {

  private final Map<String, GameState> games = new ConcurrentHashMap<>();

  /* Constructor for GameService (DeckService currently unused) */
  public GameService(DeckService deckService) {}

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
    dealToPlayers(game);

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
      dealToPlayers(game);
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

    GameStateResponse response =
        new GameStateResponse(
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
   * Deals cards to players after shuffling and sets game phase to BID.
   * Also triggers card animations and assigns the kitty.
   * If the next bidder is AI, initiates AI bid processing.
   */
  public void dealToPlayers(GameState game) {
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
      processAllBids(game);
    }
  }

  /*
   * Resets all game state to prepare for a new hand.
   * Clears trick counts, kitty, and all tracked state.
   * Shuffles and re-deals cards for new round.
   */
  public void startNewHand(GameState game) {
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
    game.setTrumpType(null);
    game.getTeamTrickCounts().clear();
    game.getDeck().resetJokerSuits();
    game.getFinalBidCache().clear();
    game.setBidTurnIndex(game.getFirstBidder().ordinal());

    game.getDeck().shuffle();
    game.setShuffledDeck(game.getDeck().getCards());

    dealToPlayers(game);
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

    Player bidder =
        game.getPlayers().stream()
            .filter(p -> p.getPosition().equals(request.getPlayer()))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Player not found"));

    InitialBid bid = BidRequest.fromRequest(request, bidder);
    game.addBid(bid);

    if (!bid.isPassed()
        && (game.getHighestBid() == null || bid.getValue() > game.getHighestBid().getValue())) {
      game.setHighestBid(bid);
    }

    int nextIndex = (game.getBidTurnIndex() + 1) % game.getPlayers().size();
    game.setBidTurnIndex(nextIndex);

    processAllBids(game);

    if (game.getBids().size() >= 4) {
      PlayerPos winnerPos = game.getHighestBid().getPlayer();
      Player winner =
          game.getPlayers().stream()
              .filter(p -> p.getPosition().equals(winnerPos))
              .findFirst()
              .orElseThrow(() -> new IllegalStateException("Winner not found"));

      game.setBidWinnerPos(winnerPos);

      if (winner.isAI()) {
        FinalBid finalBid = game.getFinalBidCache().get(winnerPos);
        if (finalBid == null) {
          throw new IllegalStateException("AI Final bid missing for: " + winnerPos);
        }
        game.setWinningBidStats(finalBid);
        game.getDeck().assignTrumpSuitToJokers(finalBid.getSuit());
        applyAIAutoKitty(game, winner);
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
   * Executes AI bids until it’s the human player’s turn or all bids are
   * completed.
   */
  private void processAllBids(GameState game) {
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
        aiBid = generateAIBid(game, nextBidder);
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
   * Evaluates a given AI player's hand and returns the strongest valid bid.
   * Saves final bid to cache for later reference.
   */
  private InitialBid generateAIBid(GameState game, Player ai) {
    HandEvaluator evaluator = new HandEvaluator(ai);
    evaluator.evaluateHand();

    List<FinalBid> bidOptions = evaluator.evaluateAll(ai.getPosition());
    InitialBid currentHigh = game.getHighestBid();

    List<FinalBid> strongerBids =
        bidOptions.stream()
            .filter(bid -> currentHigh == null || bid.getValue() > currentHigh.getValue())
            .toList();

    if (strongerBids.isEmpty()) {
      return InitialBid.pass(ai.getPosition());
    }

    FinalBid bestBid =
        strongerBids.stream().max(Comparator.comparingInt(FinalBid::getValue)).orElse(null);

    game.getFinalBidCache().put(ai.getPosition(), bestBid);

    return bestBid.getInitialBid();
  }

  /*
   * Finalizes the winning bid by the human player.
   * Assigns trump suit and updates game state and kitty visibility.
   */
  public GameStateResponse getFinalBid(FinalBidRequest request) {
    GameState game = getGameById(request.getGameId());

    PlayerPos winnerPos = request.getPlayer();
    Player winner =
        game.getPlayers().stream()
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
    game.setTrumpType(request.getType());
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

    Player winner =
        game.getPlayers().stream()
            .filter(
                p ->
                    p.getName()
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

    FinalBid winningBid;
    if (game.getFinalBidCache().containsKey(winnerPos)) {
      winningBid = game.getFinalBidCache().get(winnerPos);
    } else {
      throw new IllegalStateException(
          "Winning bid must be finalized by human before taking the kitty.");
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
      boolean hasLeadSuit =
          currentPlayer.getHand().getCards().stream().anyMatch(c -> c.getSuit() == leadSuit);
      if (hasLeadSuit && cardToPlay.getSuit() != leadSuit) {
        throw new IllegalArgumentException("Must follow suit if possible");
      }
    }

    PlayedCard validPlayedCard = new PlayedCard(request.getPlayer(), cardToPlay);

    currentPlayer.getHand().getCards().remove(cardToPlay);
    currentTrick.add(validPlayedCard);
    game.addPlayedCard(cardToPlay);

    game.addAnimation(new Animation(validPlayedCard));
    game.setCurrentTurnIndex((game.getCurrentTurnIndex() + 1) % 4);

    if (currentTrick.size() == 4) {
      PlayedCard winningPlay = determineTrickWinner(game, currentTrick);
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
        scoreHand(game);
        if (game.getTeamAScore() >= 7 || game.getTeamBScore() <= -7) {
          game.addAnimation(new Animation(AnimationType.SHOW_WINNER));
          game.setPhase(GamePhase.END);
        } else {
          game.addAnimation(new Animation(AnimationType.CLEAR));
          startNewHand(game);
        }
        return getGameStateForPlayer(game, request.getPlayer());
      }
    }

    if (PlayerUtils.getPlayerByPosition(
            PlayerPos.values()[game.getCurrentTurnIndex()], game.getPlayers())
        .isAI()) {
      autoPlayAITurns(game);
    }

    return getGameStateForPlayer(game, request.getPlayer());
  }

  /*
   * Determines the winner of the current trick.
   * Applies different logic for NO_TRUMP, DOWNTOWN, and UPTOWN bids.
   */
  private PlayedCard determineTrickWinner(GameState game, List<PlayedCard> trick) {
    if (trick == null || trick.isEmpty()) {
      throw new IllegalArgumentException("Cannot determine trick winner: trick is empty.");
    }

    Suit leadSuit = trick.get(0).getCard().getSuit();
    Suit trumpSuit = game.getTrumpSuit();
    BidType bidType = game.getTrumpType();

    /* NO_TRUMP: highest lead suit wins */
    if (bidType == BidType.NO_TRUMP) {
      return trick.stream()
          .filter(pc -> pc.getCard().getSuit() == leadSuit)
          .max(Comparator.comparing(pc -> pc.getCard().getRank().getValue()))
          .orElseThrow(
              () -> new IllegalStateException("No cards of lead suit found in NO_TRUMP bid"));
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
   * Main AI play loop. AI players will continue playing until a human's turn.
   * Handles animations, trick resolution, and transitions to scoring or new hand.
   */
  private void autoPlayAITurns(GameState game) {
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

      game.addAnimation(new Animation(validPlayedCard));
      game.setCurrentTurnIndex((game.getCurrentTurnIndex() + 1) % 4);

      if (game.getCurrentTrick().size() == 4) {
        System.out.println("DEBUG: Trick complete. Evaluating winner...");
        PlayedCard winner = determineTrickWinner(game, game.getCurrentTrick());

        Player winnerPlayer =
            PlayerUtils.getPlayerByPosition(winner.getPlayer(), game.getPlayers());
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
          scoreHand(game);
          if (game.getTeamAScore() >= 7 || game.getTeamBScore() >= 7) {
            game.addAnimation(new Animation(AnimationType.SHOW_WINNER));
            game.setPhase(GamePhase.END);
          } else {
            game.addAnimation(new Animation(AnimationType.CLEAR));
            startNewHand(game);
          }
          return;
        }

        if (winnerPlayer.isAI()) {
          autoPlayAITurns(game);
        }

        return;
      }
    }
  }

  /**
   * Selects the best card for the AI to play based on difficulty level.
   *
   * <p>- EASY: Plays the lowest legal card to keep logic simple. - MEDIUM: Leads with the highest
   * non-trump card or follows suit with the lowest. - HARD: Evaluates trump, lead suit, partner
   * position, and past tricks to choose: - A safe high card if leading - A defensive low card if
   * partner is winning - The weakest card that can beat the current winner - A fallback lowest card
   * when no better play is found
   *
   * <p>Decision logic adjusts dynamically based on trick position, trump usage, and potential to
   * preserve strong cards for later rounds.
   */
  private Card chooseCardForAI(GameState game, Player aiPlayer, List<PlayedCard> currentTrick) {
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

      Suit leadSuit =
          currentTrick.get(0).getCard() != null ? currentTrick.get(0).getCard().getSuit() : null;
      List<Card> sameSuit =
          hand.stream()
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
      Suit leadSuit =
          currentTrick.isEmpty()
              ? null
              : (currentTrick.get(0).getCard() != null
                  ? currentTrick.get(0).getCard().getSuit()
                  : null);
      PlayedCard winningCard = trickIndex > 0 ? getWinningCard(currentTrick, trumpSuit) : null;
      Card currentWinning = winningCard != null ? winningCard.getCard() : null;

      if (leadSuit != null
          && hand.stream().anyMatch(c -> c.getSuit() != null && c.getSuit().equals(leadSuit))) {
        // Ensure at least one card of leadSuit is played
        List<Card> legalFollowSuit =
            hand.stream()
                .filter(c -> c.getSuit() != null && c.getSuit().equals(leadSuit))
                .collect(Collectors.toList());

        return legalFollowSuit.stream()
            .min(Comparator.comparingInt(c -> c.getRank().getValue()))
            .orElse(hand.get(0));
      }

      if (trickIndex == 0) {
        Optional<Card> safeLead =
            hand.stream()
                .filter(c -> !JokerUtils.isJokerRank(c.getRank())) // Avoid jokers
                .filter(
                    c ->
                        c.getSuit() == null
                            || !c.getSuit().equals(trumpSuit)
                            || CardUtils.allHigherTrumpCardsPlayed(
                                c, game.getCompletedCards(), trumpSuit))
                .max(Comparator.comparingInt(c -> c.getRank().getValue()));

        if (safeLead.isPresent()) {
          return safeLead.get();
        }

        Map<Suit, List<Card>> bySuit =
            hand.stream()
                .filter(c -> c.getSuit() != null)
                .collect(Collectors.groupingBy(Card::getSuit));

        Optional<Card> lead =
            bySuit.entrySet().stream()
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
        boolean partnerWinning =
            winningCard.getPlayer() != null && winningCard.getPlayer().equals(partner);

        if (partnerWinning) {
          List<Card> follow =
              hand.stream()
                  .filter(c -> c.getSuit() != null && c.getSuit().equals(leadSuit))
                  .collect(Collectors.toList());
          if (!follow.isEmpty()) {
            return follow.stream()
                .min(Comparator.comparingInt(c -> c.getRank().getValue()))
                .orElse(follow.get(0));
          }

          List<Card> trumps =
              hand.stream()
                  .filter(c -> c.getSuit() != null && c.getSuit().equals(trumpSuit))
                  .collect(Collectors.toList());
          if (!trumps.isEmpty()) {
            return trumps.stream()
                .min(Comparator.comparingInt(c -> c.getRank().getValue()))
                .orElse(trumps.get(0));
          }

          Optional<Card> safeHigh =
              hand.stream()
                  .filter(c -> c.getRank().getValue() >= Rank.QUEEN.getValue())
                  .filter(
                      c ->
                          c.getSuit() == null
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

        List<Card> winningCards =
            hand.stream()
                .filter(c -> c != null && canBeat(c, currentWinning, trumpSuit, leadSuit))
                .sorted(Comparator.comparingInt(c -> c.getRank().getValue()))
                .collect(Collectors.toList());

        if (!winningCards.isEmpty()) {
          return winningCards.get(0);
        }

        Optional<Card> safeHigh =
            hand.stream()
                .filter(c -> c.getRank().getValue() >= Rank.QUEEN.getValue())
                .filter(
                    c ->
                        c.getSuit() == null
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

      List<Card> followSuit =
          hand.stream()
              .filter(c -> c.getSuit() != null)
              .filter(c -> leadSuit != null && c.getSuit().equals(leadSuit))
              .collect(Collectors.toList());

      if (!followSuit.isEmpty()) {
        if (currentWinning != null) {
          Optional<Card> beatCard =
              followSuit.stream()
                  .filter(c -> canBeat(c, currentWinning, trumpSuit, leadSuit))
                  .min(Comparator.comparingInt(c -> c.getRank().getValue()));
          if (beatCard.isPresent()) {
            return beatCard.get();
          }

          Optional<Card> safeBurn =
              followSuit.stream()
                  .filter(
                      c ->
                          c.getSuit() == null
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

      List<Card> trumpCards =
          hand.stream()
              .filter(c -> c.getSuit() != null && c.getSuit().equals(trumpSuit))
              .collect(Collectors.toList());

      if (!trumpCards.isEmpty() && currentWinning != null) {
        Optional<Card> winningTrump =
            trumpCards.stream()
                .filter(c -> canBeat(c, currentWinning, trumpSuit, leadSuit))
                .min(Comparator.comparingInt(c -> c.getRank().getValue()));
        if (winningTrump.isPresent()) {
          return winningTrump.get();
        }
      }

      Optional<Card> safeHigh =
          hand.stream()
              .filter(c -> c.getRank().getValue() >= Rank.QUEEN.getValue())
              .filter(
                  c ->
                      c.getSuit() == null
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
   * Determines which card is currently winning a trick.
   *
   * <p>It checks each card in the trick. Trump cards beat non-trump cards. If both are trump or
   * both are the lead suit, the higher rank wins.
   */
  private PlayedCard getWinningCard(List<PlayedCard> trick, Suit trumpSuit) {
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

  /**
   * Checks if the AI's partner is currently winning the trick.
   *
   * <p>Finds the current winning card and compares its player to the AI’s partner.
   */
  @SuppressWarnings("unused")
  private boolean isPartnerWinning(List<PlayedCard> trick, PlayerPos aiPos, Suit trumpSuit) {
    if (trick.size() < 2) {
      return false; // Partner hasn't played yet
    }
    PlayerPos partner = aiPos.getPartner();

    // Find the winning card in the current trick
    PlayedCard winningCard = getWinningCard(trick, trumpSuit);
    if (winningCard == null) {
      return false;
    }

    return winningCard.getPlayer().equals(partner);
  }

  /**
   * Checks if the challenger card can beat the current winning card.
   *
   * <p>Trump beats non-trump. Otherwise, the higher card of the same suit wins. If suits differ and
   * neither is trump, the lead suit may still win if higher.
   */
  private boolean canBeat(Card challenger, Card currentWinning, Suit trumpSuit, Suit leadSuit) {
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
  private Card getLowestLegalCard(List<Card> hand, List<PlayedCard> trick) {
    if (trick.isEmpty()) {
      return hand.stream()
          .min(Comparator.comparingInt(c -> c.getRank().getValue()))
          .orElse(hand.get(0));
    }

    Suit leadSuit = trick.get(0).getCard().getSuit();
    List<Card> sameSuit =
        hand.stream().filter(c -> c.getSuit() == leadSuit).collect(Collectors.toList());

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
  private void applyAIAutoKitty(GameState game, Player winner) {
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

  /*
   * Scores the current hand based on bid success and trick count.
   * Updates team scores accordingly.
   */
  private void scoreHand(GameState game) {
    FinalBid winningBid = game.getWinningBid();
    Team biddingTeam =
        game.getPlayers().stream()
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
   * Removes a single animation (by ID) from a player's animation queue.
   */
  public void popAnimation(PopAnimationRequest request) {
    GameState game = getGameById(request.getGameId());
    PlayerPos playerPosition = request.getPlayer();
    String animationId = request.getAnimationId();

    game.removeAnimationById(playerPosition, animationId);
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
