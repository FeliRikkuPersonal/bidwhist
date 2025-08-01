// src/test/java/com/bidwhist/utils/GameplayUtilsTest.java

package com.bidwhist.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.bidwhist.bidding.BidType;
import com.bidwhist.bidding.FinalBid;
import com.bidwhist.bidding.InitialBid;
import com.bidwhist.model.Card;
import com.bidwhist.model.GamePhase;
import com.bidwhist.model.GameState;
import com.bidwhist.model.PlayedCard;
import com.bidwhist.model.Player;
import com.bidwhist.model.PlayerPos;
import com.bidwhist.model.Rank;
import com.bidwhist.model.Suit;
import com.bidwhist.model.Team;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GameplayUtilsTest {

  private Player p1, p2, p3, p4;
  private GameState game;

  @BeforeEach
  void setup() {
    p1 = new Player("P1", true, PlayerPos.P1, Team.A);
    p2 = new Player("P2", false, PlayerPos.P2, Team.B);
    p3 = new Player("P3", true, PlayerPos.P3, Team.A);
    p4 = new Player("P4", false, PlayerPos.P4, Team.B);
    List<Player> players = new ArrayList<>(List.of(p1, p2, p3, p4));

    game = new GameState("gameId");
    for (Player player : players) {
      game.addPlayer(player);
    }
    game.setTrumpSuit(Suit.HEARTS);
    game.setBidType(BidType.UPTOWN);
    game.setPhase(GamePhase.SHUFFLE);
  }

  @Test
  void testDetermineTrickWinner_uptownPrefersTrump() {
    game.setBidType(BidType.UPTOWN);
    List<PlayedCard> trick =
        List.of(
            new PlayedCard(PlayerPos.P1, new Card(Suit.SPADES, Rank.FOUR)),
            new PlayedCard(PlayerPos.P2, new Card(Suit.HEARTS, Rank.THREE)),
            new PlayedCard(PlayerPos.P3, new Card(Suit.SPADES, Rank.ACE)));

    PlayedCard winner = HandUtils.determineTrickWinner(game, trick);
    assertEquals(PlayerPos.P2, winner.getPlayer()); // trump wins
  }

  @Test
  void testDetermineTrickWinner_downtownInvertsRanks() {
    game.setBidType(BidType.DOWNTOWN);
    List<PlayedCard> trick =
        List.of(
            new PlayedCard(PlayerPos.P1, new Card(Suit.SPADES, Rank.TWO)),
            new PlayedCard(PlayerPos.P2, new Card(Suit.SPADES, Rank.THREE)),
            new PlayedCard(PlayerPos.P3, new Card(Suit.SPADES, Rank.FOUR)));

    PlayedCard winner = HandUtils.determineTrickWinner(game, trick);
    assertEquals(PlayerPos.P1, winner.getPlayer()); // downtown reverses rank order
  }

  @Test
  void testDetermineTrickWinner_noTrumpLeadSuitWins() {
    game.setBidType(BidType.UPTOWN);
    game.setTrumpSuit(null);
    game.setWinningBid(new FinalBid(PlayerPos.P1, 5, true, false, BidType.UPTOWN, null));

    List<PlayedCard> trick =
        List.of(
            new PlayedCard(PlayerPos.P1, new Card(Suit.CLUBS, Rank.TEN)),
            new PlayedCard(PlayerPos.P2, new Card(Suit.CLUBS, Rank.JACK)),
            new PlayedCard(PlayerPos.P3, new Card(Suit.HEARTS, Rank.ACE)));

    PlayedCard winner = HandUtils.determineTrickWinner(game, trick);
    assertEquals(PlayerPos.P2, winner.getPlayer()); // highest of lead suit wins
  }

  @Test
  void testScoreHand_successfulBidIncreasesScore() {
    game.setTeamTrickCounts(Map.of(Team.A, 9)); // 9 tricks
    FinalBid bid = new FinalBid(PlayerPos.P1, 3, false, false, BidType.UPTOWN, Suit.HEARTS);
    game.setWinningBid(bid);
    game.setTeamScores(new HashMap<>());
    game.setTeamAScore(0);
    game.setTeamBScore(0);

    GameplayUtils.scoreHand(game);

    assertEquals(4, game.getTeamAScore()); // 9 - 5 = +4
    assertEquals(GamePhase.SHUFFLE, game.getPhase()); // still playing
  }

  @Test
  void testScoreHand_failedBidDecreasesScoreAndEndsGame() {
    game.setTeamTrickCounts(Map.of(Team.B, 3)); // not enough
    FinalBid bid = new FinalBid(new InitialBid(PlayerPos.P2, 5, true), BidType.NO_TRUMP, null);
    game.setWinningBid(bid);
    game.setTeamScores(new HashMap<>());
    game.setTeamAScore(0);
    game.setTeamBScore(0);

    GameplayUtils.scoreHand(game);

    assertEquals(-10, game.getTeamBScore());
    assertEquals(GamePhase.END, game.getPhase()); // still playing
  }
}
