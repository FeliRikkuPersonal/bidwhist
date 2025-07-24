// src/main/java/com/bidwhist/service/GameServiceTest.java

package com.bidwhist.service;

import com.bidwhist.bidding.BidType;
import com.bidwhist.bidding.FinalBid;
import com.bidwhist.bidding.InitialBid;
import com.bidwhist.dto.BidRequest;
import com.bidwhist.dto.FinalBidRequest;
import com.bidwhist.dto.GameStateResponse;
import com.bidwhist.dto.JoinGameRequest;
import com.bidwhist.dto.KittyRequest;
import com.bidwhist.dto.StartGameRequest;
import com.bidwhist.model.Card;
import com.bidwhist.model.Difficulty;
import com.bidwhist.model.GamePhase;
import com.bidwhist.model.GameState;
import com.bidwhist.model.Hand;
import com.bidwhist.model.PlayerPos;
import com.bidwhist.model.Rank;
import com.bidwhist.model.RoomStatus;
import com.bidwhist.model.Suit;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

public class GameServiceTest {

    @Test
    void testSoloGameStart() {
        GameService game = new GameService(new DeckService());
        GameStateResponse gameState = game.startSoloGame(new StartGameRequest("player1", Difficulty.EASY, "gameId"));
        assertTrue(gameState.getPlayers().size() == 4);
        assertTrue(gameState.getFirstBidder() == PlayerPos.P1);
        assertFalse(gameState.getPlayers().get(0).isAI());
        assertTrue(gameState.getPlayers().get(1).isAI());
        assertTrue(gameState.getPlayers().get(2).isAI());
        assertTrue(gameState.getPlayers().get(3).isAI());
        assertTrue(gameState.getPhase() == GamePhase.SHUFFLE);
    }

    @Test
    void testMultiplayerGameStart() {
        GameService gameService = new GameService(new DeckService());
        GameStateResponse gameState = gameService.createMutliplayerGame(new StartGameRequest("gameId", "player1"));
        assertTrue(gameState.getPlayers().size() == 1);
        assertTrue(gameService.getGameById("gameId").getRoom().getPlayers().size() == 1);
        assertTrue(gameService.getGameById("gameId").getRoom().getStatus() == RoomStatus.WAITING_FOR_PLAYERS);
        assertTrue(gameState.getPhase() == GamePhase.INITIATED);
        assertTrue(gameState.getLobbySize() == 1);
    }

    @Test
    void testPlayerJoiningRoom() {
        GameService gameService = new GameService(new DeckService());
        gameService.createMutliplayerGame(new StartGameRequest("gameId", "player1"));
        GameStateResponse joinState = gameService.joinGame(new JoinGameRequest("player2", "gameId"));
        GameState gameData = gameService.getGameById("gameId");
        assertTrue(gameData.getRoom().getPlayers().size() == 2);
        assertTrue(joinState.getLobbySize() == 2);
    }

    @Test
    void testBiddingAndEvaluation() {
        GameService gameService = new GameService(new DeckService());
        gameService.createMutliplayerGame(new StartGameRequest("gameId", "player1"));
        GameState gameState = gameService.getGameById("gameId");
        gameService.joinGame(new JoinGameRequest("player2", "gameId"));
        gameService.joinGame(new JoinGameRequest("player3", "gameId"));
        gameService.joinGame(new JoinGameRequest("player4", "gameId"));
        gameService.submitBid(new BidRequest("gameId", PlayerPos.P1, 5, true));
        assertTrue(gameState.getBids().size() == 1);
        assertTrue(gameState.getBidTurnIndex() == 1);
        gameService.submitBid(new BidRequest("gameId", PlayerPos.P2, 4, false));
        assertTrue(gameState.getBids().size() == 2);
        assertTrue(gameState.getBidTurnIndex() == 2);
        gameService.submitBid(new BidRequest("gameId", PlayerPos.P3, 0, false));
        assertTrue(gameState.getBids().size() == 3);
        assertTrue(gameState.getBidTurnIndex() == 3);
        gameService.submitBid(new BidRequest("gameId", PlayerPos.P4, 5, false));
        assertTrue(gameState.getPhase() == GamePhase.KITTY);
    }

    @Test
    void testFinalizingWinningBid() {
        GameService gameService = new GameService(new DeckService());
        gameService.startSoloGame(new StartGameRequest("", Difficulty.EASY, "gameId"));
        GameState gameState = gameService.getGameById("gameId");
        gameState.setHighestBid(new InitialBid(PlayerPos.P1, 6, false));
        gameService.getFinalBid(new FinalBidRequest("gameId", PlayerPos.P1, BidType.UPTOWN, Suit.CLUBS));
        assertTrue(gameState.getFinalBidType() == BidType.UPTOWN);
        assertTrue(gameState.getWinningBid().getInitialBid().getValue() == 6);
        assertTrue(gameState.getWinningBid().getSuit() == Suit.CLUBS);
        assertThrows(IllegalStateException.class, () -> {
            gameService.getFinalBid(new FinalBidRequest("gameId", PlayerPos.P2, BidType.DOWNTOWN, Suit.HEARTS));
        });
    }

    @Test
    void testKittyDiscards() {
        GameService gameService = new GameService(new DeckService());
        gameService.startSoloGame(new StartGameRequest("player1", Difficulty.EASY, "gameId"));
        GameState game = gameService.getGameById("gameId");
        Card twoOfHearts = new Card(Suit.HEARTS, Rank.TWO);
        Card threeOfHearts = new Card(Suit.HEARTS, Rank.THREE);
        Card fourOfHearts = new Card(Suit.HEARTS, Rank.FOUR);
        Card fiveOfHearts = new Card(Suit.HEARTS, Rank.FIVE);
        Card sixOfHearts = new Card(Suit.HEARTS, Rank.SIX);
        Card sevenOfHearts = new Card(Suit.HEARTS, Rank.SEVEN);
        Card eightOfHearts = new Card(Suit.HEARTS, Rank.EIGHT);
        Card nineOfHearts = new Card(Suit.HEARTS, Rank.NINE);
        Card tenOfHearts = new Card(Suit.HEARTS, Rank.TEN);
        Card jackOfHearts = new Card(Suit.HEARTS, Rank.JACK);
        Card twoOfSpades = new Card(Suit.SPADES, Rank.TWO);
        Card threeOfSpades = new Card(Suit.SPADES, Rank.THREE);
        Card fourOfSpades = new Card(Suit.SPADES, Rank.FOUR);
        Card fiveOfSpades = new Card(Suit.SPADES, Rank.FIVE);
        Card sixOfSpades = new Card(Suit.SPADES, Rank.SIX);
        Card sevenOfSpades = new Card(Suit.SPADES, Rank.SEVEN);
        Card eightOfSpades = new Card(Suit.SPADES, Rank.EIGHT);
        Card nineOfSpades = new Card(Suit.SPADES, Rank.NINE);
        Card tenOfSpades = new Card(Suit.SPADES, Rank.TEN);
        Card jackOfSpades = new Card(Suit.SPADES, Rank.JACK);

        Card[] heartCards = {
            twoOfHearts, threeOfHearts, fourOfHearts, fiveOfHearts, sixOfHearts, sevenOfHearts,
            eightOfHearts, nineOfHearts, tenOfHearts, jackOfHearts
        };
        List<Card> playerCardsHearts = new ArrayList<>(Arrays.asList(heartCards));
        Hand playerHandHearts = new Hand();
        playerHandHearts.addCards(playerCardsHearts);

        Card[] spadeCards = {
            twoOfSpades, threeOfSpades, fourOfSpades, fiveOfSpades, sixOfSpades,
            sevenOfSpades, eightOfSpades, nineOfSpades, tenOfSpades, jackOfSpades
        };
        List<Card> playerCardsSpades = new ArrayList<>(Arrays.asList(spadeCards));
        Hand playerHandSpades = new Hand();
        playerHandSpades.addCards(playerCardsSpades);

        Card[] bigList = {twoOfHearts, threeOfHearts, fourOfHearts, fiveOfHearts, 
            sixOfHearts, sevenOfHearts, eightOfHearts};
        Card[] smallList = {twoOfHearts, threeOfHearts, fourOfHearts, fiveOfHearts, sixOfHearts};
        Card[] correctList = {twoOfHearts, threeOfHearts, fourOfHearts, fiveOfHearts, 
            sixOfHearts, sevenOfHearts};
        List<Card> bigDiscard = new ArrayList<>(Arrays.asList(bigList));
        List<Card> smallDiscard = new ArrayList<>(Arrays.asList(smallList));
        List<Card> correctDiscard = new ArrayList<>(Arrays.asList(correctList));
        game.getPlayers().get(0).setHand(playerHandHearts);
        game.getPlayers().get(1).setHand(playerHandHearts);
        game.getPlayers().get(2).setHand(playerHandHearts);
        game.getPlayers().get(3).setHand(playerHandSpades);
        FinalBid player1 = new FinalBid(PlayerPos.P1, 4, false, false, BidType.UPTOWN, Suit.DIAMONDS);
        FinalBid player2 = new FinalBid(PlayerPos.P2, 4, false, false, BidType.UPTOWN, Suit.DIAMONDS);
        FinalBid player3 = new FinalBid(PlayerPos.P3, 4, false, false, BidType.UPTOWN, Suit.DIAMONDS);
        FinalBid player4 = new FinalBid(PlayerPos.P4, 4, false, false, BidType.UPTOWN, Suit.DIAMONDS);
        game.getFinalBidCache().put(PlayerPos.P1, player1);
        game.getFinalBidCache().put(PlayerPos.P2, player2);
        game.getFinalBidCache().put(PlayerPos.P3, player3);
        game.getFinalBidCache().put(PlayerPos.P4, player4);

        game.setPhase(GamePhase.KITTY);
        game.setHighestBid(new InitialBid(PlayerPos.P1, 4, false));
        game.setWinningBid(player1);
        assertThrows(IllegalArgumentException.class, () -> {
            gameService.applyKittyAndDiscards(new KittyRequest("gameId", PlayerPos.P1, smallDiscard));
        });
        game.setPhase(GamePhase.KITTY);
        game.setHighestBid(new InitialBid(PlayerPos.P2, 4, false));
        game.setWinningBid(player2);
        assertThrows(IllegalArgumentException.class, () -> {
            gameService.applyKittyAndDiscards(new KittyRequest("gameId", PlayerPos.P2, bigDiscard));
        });
        game.setPhase(GamePhase.KITTY);
        game.setHighestBid(new InitialBid(PlayerPos.P3, 4, false));
        game.setWinningBid(player3);
        assertDoesNotThrow(() -> {
            gameService.applyKittyAndDiscards(new KittyRequest("gameId", PlayerPos.P3, correctDiscard));
        });
        game.setPhase(GamePhase.KITTY);
        assertThrows(IllegalArgumentException.class, () -> {
            gameService.applyKittyAndDiscards(new KittyRequest("gameId", PlayerPos.P2, correctDiscard));
        });
        game.setPhase(GamePhase.KITTY);
        game.setHighestBid(new InitialBid(PlayerPos.P4, 4, false));
        game.setWinningBid(player4);
        assertThrows(IllegalArgumentException.class, () -> {
            gameService.applyKittyAndDiscards(new KittyRequest("gameId", PlayerPos.P4, correctDiscard));
        });

    };
}