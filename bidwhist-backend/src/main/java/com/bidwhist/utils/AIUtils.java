package com.bidwhist.utils;

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
import com.bidwhist.model.Suit;
import com.bidwhist.model.Team;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

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

    List<FinalBid> strongerBids =
        bidOptions.stream()
            .filter(bid -> currentHigh == null || bid.getInitialBid().compareTo(currentHigh) > 0)
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
        aiFinalBid = aiHandEval.getForcedMinimumBid(game, nextBidder.getPosition());
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

      game.addAnimation(
          new Animation(
              validPlayedCard,
              game.getLeadSuit(),
              game.getCurrentTurnIndex(),
              game.getCurrentTrick().size(),
              game.getSessionKey(),
              game));
      game.setCurrentTurnIndex((game.getCurrentTurnIndex() + 1) % 4);

      if (game.getCurrentTrick().size() == 4) {
        System.out.println("DEBUG: Trick complete. Evaluating winner...");
        PlayedCard winner = HandUtils.determineTrickWinner(game, game.getCurrentTrick());

        Player winnerPlayer =
            PlayerUtils.getPlayerByPosition(winner.getPlayer(), game.getPlayers());
        Team winnerTeam = winnerPlayer.getTeam();
        System.out.println(
            "DEBUG: Trick won by " + winnerPlayer.getName() + " (Team " + winnerTeam + ")");

        Book currentBook = new Book(game.getCurrentTrick(), winnerTeam);
        game.setCurrentTurnIndex(winnerPlayer.getPosition().ordinal());

        game.addAnimation(
            new Animation(
                currentBook,
                winnerPlayer.getPosition().ordinal(),
                game.getPhase(),
                game.getSessionKey()));
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
   * <p>- EASY: Plays the lowest legal card to keep logic simple. - MEDIUM: Leads with the highest
   * non-trump card or follows suit with the lowest. - HARD: Evaluates trump, lead suit, partner
   * position, and past tricks to choose: - A safe high card if leading - A defensive low card if
   * partner is winning - The weakest card that can beat the current winner - A fallback lowest card
   * when no better play is found
   *
   * <p>Decision logic adjusts dynamically based on trick position, trump usage, and potential to
   * preserve strong cards for later rounds.
   */
  public static Card chooseCardForAI(
      GameState game, Player aiPlayer, List<PlayedCard> currentTrick) {
    List<Card> hand = aiPlayer.getHand().getCards();
    Difficulty difficulty = game.getDifficulty();
    PlayerPos aiPlayerPosition = aiPlayer.getPosition();

    if (difficulty == Difficulty.EASY) {
      Card easyCard = getEasyAIMove(game, hand, currentTrick);
      game.addPlayedCard(easyCard);
      return easyCard;
    } else if (difficulty == Difficulty.MEDIUM) {
      Card mediumCard = getMediumAIMove(game, aiPlayerPosition, hand, currentTrick);
      game.addPlayedCard(mediumCard);
      return mediumCard;
    } else {
      Card hardCard = getHardAIMove(game, aiPlayer.getPosition(), hand, currentTrick);
      game.addPlayedCard(hardCard);
      return hardCard;
    }
  }

  /**
   * Returns a "dumb" AI move for Easy difficulty. - If leading: play the highest card (no strategy)
   * - If following: play the lowest legal card (follow suit if possible)
   */
  public static Card getEasyAIMove(GameState game, List<Card> hand, List<PlayedCard> trick) {
    if (trick == null || trick.isEmpty()) {
      // AI is leading — play highest card
      return HandUtils.getHighestRankedCard(game, hand);
    }

    List<Card> playableHand = HandUtils.getPlayableHand(game, trick, hand);

    if (HandUtils.canWinTrick(game, trick, playableHand)) {
      return HandUtils.canBeat(game, trick, playableHand);
    } else {
      return HandUtils.getLowestLegalCard(game, trick, playableHand);
    }
  }

  /**
   * Determines the Medium AI's move based on the current game state.
   *
   * <p>- If leading: plays the highest-ranked card in hand. - If following: - Plays the lowest
   * legal non-trump card if partner is winning or cannot win the trick. - Otherwise, attempts to
   * win with the highest legal card.
   *
   * <p>Prioritizes safe play and avoids wasting strong cards when unnecessary.
   */
  public static Card getMediumAIMove(
      GameState game, PlayerPos player, List<Card> hand, List<PlayedCard> trick) {
    if (trick.isEmpty()) {
      return HandUtils.getHighestRankedCard(game, hand);
    }

    List<Card> playableHand = HandUtils.getPlayableHand(game, trick, hand);
    Suit trumpSuit = game.getTrumpSuit();
    PlayedCard winningCard = HandUtils.determineTrickWinner(game, trick);

    if (HandUtils.partnerIsWinning(player, winningCard)
        || !HandUtils.canWinTrick(game, trick, playableHand)) {
      return HandUtils.getLowestLegalNonTrumpCard(game, trick, playableHand);
    }

    return HandUtils.getHighestLegalCard(game, trick, playableHand);
  }

  /**
   * @param hand
   * @param trick
   * @return
   */
  public static Card getHardAIMove(
      GameState game, PlayerPos player, List<Card> hand, List<PlayedCard> trick) {
    Suit trumpSuit = game.getTrumpSuit();
    boolean isNoTrump = game.getWinningBid().isNo();
    List<Card> playedCardList = game.getPlayedCards();

    // Leading
    if (trick.isEmpty()) {
      Card highestTrump = HandUtils.getHighestOfSuit(game, hand, trumpSuit);
      if (highestTrump != null
          && HandUtils.allHigherCardsPlayed(
              highestTrump, playedCardList, hand, trumpSuit, game.getBidType(), isNoTrump)
          && !HandUtils.areOpponentsSuitVoid(game, player, trumpSuit)) {
        return highestTrump; // Drain opponent's trump cards
      } else {
        return HandUtils.getHighestRankedCard(game, hand);
      }

      // Following
    } else {
      List<Card> playableHand = HandUtils.getPlayableHand(game, trick, hand);
      Suit leadSuit = HandUtils.getLeadSuit(game, trick);
      PlayedCard winningCard = HandUtils.determineTrickWinner(game, trick);
      Suit winningCardSuit = winningCard.getCard().getSuit();
      boolean wasCut = !winningCard.getCard().getSuit().equals(leadSuit);
      Card canBeatCard = null;
      boolean trickIsWinnable = HandUtils.canWinTrick(game, trick, playableHand);

      if (trickIsWinnable) {
        canBeatCard = HandUtils.canBeat(game, trick, playableHand);
      }

      // Lead suit in hand
      if (HandUtils.hasSuit(hand, leadSuit)) {
        // Partner is winning
        if (HandUtils.partnerIsWinning(player, winningCard)) {
          if (wasCut
              || HandUtils.allHigherCardsPlayed(
                  winningCard.getCard(),
                  playedCardList,
                  hand,
                  winningCardSuit,
                  game.getBidType(),
                  isNoTrump)) {
            return HandUtils.getLowestLegalNonTrumpCard(game, trick, playableHand);
          } else if (trickIsWinnable) {
            return HandUtils.getHighestLegalCard(game, trick, playableHand);
          } else {
            // fallback, player cannot beat winning card
            return HandUtils.getLowestLegalNonTrumpCard(game, trick, playableHand);
          }
        } else {
          // Partner is not winning
          if (trickIsWinnable) {
            // Opponents have lead suit, partner does not & has not played
            if (!HandUtils.partnerHasPlayed(player, trick)) {
              if (!HandUtils.areOpponentsSuitVoid(game, player, leadSuit)
                  && HandUtils.isPartnerSuitVoid(game, player, leadSuit)) {
                if (HandUtils.hasDiscardSuit(playableHand, trumpSuit)) {
                  return HandUtils.getDiscardCard(game, playableHand);
                } else {
                  return HandUtils.getLowestLegalNonTrumpCard(game, trick, playableHand);
                }
              } else {
                // Go for win if no card is higher
                if (HandUtils.allHigherCardsPlayed(
                    canBeatCard, playedCardList, hand, leadSuit, game.getBidType(), isNoTrump)) {
                  return canBeatCard;
                } else {
                  // Play lowest winner
                  return HandUtils.getNextHigherCard(game, winningCard.getCard(), playableHand);
                }
              }
            }
          } else {
            // Trick is not winnable
            // find throw away card
            if (HandUtils.hasDiscardSuit(playableHand, trumpSuit)) {
              return HandUtils.getDiscardCard(game, playableHand);
            } else {
              return HandUtils.getLowestLegalNonTrumpCard(game, trick, playableHand);
            }
          }
        }
      } else {
        // Lead suit not in hand
        // Can cut
        if (trickIsWinnable) {
          // Partner is Winning
          if (HandUtils.partnerIsWinning(player, winningCard)) {
            // Higher cards are in play
            if (!HandUtils.allHigherCardsPlayed(
                winningCard.getCard(),
                playedCardList,
                hand,
                winningCardSuit,
                game.getBidType(),
                isNoTrump)) {
              return canBeatCard;
            } else {
              // find throw away card
              if (HandUtils.hasDiscardSuit(playableHand, trumpSuit)) {
                return HandUtils.getDiscardCard(game, playableHand);
              } else {
                return HandUtils.getLowestLegalNonTrumpCard(game, trick, playableHand);
              }
            }
          } else {
            // Partner is not winning
            // Partner hasn't played
            if (!HandUtils.partnerHasPlayed(player, trick)) {
              // Not cut possible
              if (leadSuit.equals(trumpSuit)) {
                if (HandUtils.allHigherCardsPlayed(
                    canBeatCard,
                    playedCardList,
                    hand,
                    winningCardSuit,
                    game.getBidType(),
                    isNoTrump)) {
                  return canBeatCard;
                } else {
                  return HandUtils.getNextHigherCard(game, winningCard.getCard(), hand);
                }
                // Lead and trump don't match suits
              } else {
                // Opponents have lead suit, partner does not
                if (!HandUtils.areOpponentsSuitVoid(game, player, leadSuit)
                    && HandUtils.isPartnerSuitVoid(game, player, leadSuit)
                    && !HandUtils.isPartnerSuitVoid(game, player, trumpSuit)) {
                  if (HandUtils.hasDiscardSuit(hand, trumpSuit)) {
                    return HandUtils.getDiscardCard(game, playableHand);
                  } else {
                    return HandUtils.getLowestOfSuit(game, playableHand, trumpSuit);
                  }
                } else {
                  return canBeatCard;
                }
              }
            } else {
              return canBeatCard;
            }
          }
        } else {
          // Trick is not winnable
          // find throw away card
          if (HandUtils.hasDiscardSuit(playableHand, trumpSuit)) {
            return HandUtils.getDiscardCard(game, playableHand);
          }
        }
      }
    }
    return HandUtils.getLowestLegalNonTrumpCard(
        game, trick, HandUtils.getPlayableHand(game, trick, hand));
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
