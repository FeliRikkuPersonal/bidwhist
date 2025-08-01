package com.bidwhist.utils;

import static org.junit.jupiter.api.Assertions.*;

import com.bidwhist.bidding.InitialBid;
import com.bidwhist.model.*;
import com.bidwhist.model.PlayerPos;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AIUtilsTest {

  private Player aiPlayer;
  private GameState game;

  @BeforeEach
  void setup() {
    aiPlayer = new Player("AI Bot", true, PlayerPos.P3, Team.A);
    aiPlayer
        .getHand()
        .addCards(
            new ArrayList<>(
                List.of(
                    new Card(Suit.SPADES, Rank.ACE),
                    new Card(Suit.SPADES, Rank.KING),
                    new Card(Suit.HEARTS, Rank.TEN),
                    new Card(Suit.CLUBS, Rank.TWO))));

    List<Player> players =
        List.of(
            aiPlayer,
            new Player("Human", false, PlayerPos.P4, Team.B),
            new Player("AI Bot 2", true, PlayerPos.P1, Team.A),
            new Player("AI Bot 3", true, PlayerPos.P2, Team.B));

    game = new GameState("gameId");
    for (Player player : players) {
      game.addPlayer(player);
    }
  }

  @Test
  void testGenerateAIBid_returnsValidBidOrPass() {
    InitialBid bid = AIUtils.generateAIBid(game, aiPlayer);
    assertNotNull(bid);
    assertEquals(aiPlayer.getPosition(), bid.getPlayer());
  }

  @Test
  void testProcessAllBids_runsUntilHumanTurn() {
    game.setBidTurnIndex(0);
    AIUtils.processAllBids(game);
    assertTrue(game.getBids().size() >= 1);
    assertEquals(PlayerPos.P4, game.getPlayers().get(game.getBidTurnIndex()).getPosition());
  }
}
