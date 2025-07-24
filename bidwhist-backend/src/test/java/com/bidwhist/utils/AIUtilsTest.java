package com.bidwhist.utils;

import com.bidwhist.bidding.InitialBid;
import com.bidwhist.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import com.bidwhist.model.PlayerPos;

import static org.junit.jupiter.api.Assertions.*;

public class AIUtilsTest {

    private Player aiPlayer;
    private GameState game;

    @BeforeEach
    void setup() {
        aiPlayer = new Player("AI Bot", true, PlayerPos.P3, Team.A);
        aiPlayer.getHand().addCards(new ArrayList<>(List.of(
                new Card(Suit.SPADES, Rank.ACE),
                new Card(Suit.SPADES, Rank.KING),
                new Card(Suit.HEARTS, Rank.TEN),
                new Card(Suit.CLUBS, Rank.TWO)
        )));

        List<Player> players = List.of(
                aiPlayer,
                new Player("Human", false,  PlayerPos.P4, Team.B),
                new Player("AI Bot 2", true, PlayerPos.P1, Team.A),
                new Player("AI Bot 3", true, PlayerPos.P2, Team.B)
        );

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

    @Test
    void testGetLowestLegalCard_selectsLowestMatchingSuit() {
        List<Card> hand = List.of(
                new Card(Suit.SPADES, Rank.ACE),
                new Card(Suit.SPADES, Rank.THREE),
                new Card(Suit.HEARTS, Rank.TEN)
        );

        List<PlayedCard> trick = List.of(
                new PlayedCard(PlayerPos.P1, new Card(Suit.SPADES, Rank.FIVE))
        );

        Card result = AIUtils.getLowestLegalCard(hand, trick);
        assertEquals(Rank.THREE, result.getRank());
    }

    @Test
    void testCanBeat_logicWorksCorrectly() {
        Card winning = new Card(Suit.HEARTS, Rank.KING);
        Card challenger = new Card(Suit.HEARTS, Rank.ACE);

        boolean result = AIUtils.canBeat(challenger, winning, null, Suit.HEARTS);
        assertTrue(result);
    }

    @Test
    void testChooseCardForAI_easy_playsLowest() {
        game.setDifficulty(Difficulty.EASY);
        Card chosen = AIUtils.chooseCardForAI(game, aiPlayer, new ArrayList<>());
        assertEquals(Rank.TWO, chosen.getRank());
    }
}