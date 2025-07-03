package com.bidwhist.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bidwhist.bidding.FinalBid;
import com.bidwhist.dto.BidRequest;
import com.bidwhist.dto.FinalBidRequest;
import com.bidwhist.dto.GameStateResponse;
import com.bidwhist.dto.KittyRequest;
import com.bidwhist.dto.PlayerRequest;
import com.bidwhist.model.GamePhase;
import com.bidwhist.model.GameState;
import com.bidwhist.model.Player;
import com.bidwhist.model.PlayerPos;
import com.bidwhist.service.GameService;

@RestController
@RequestMapping("/api/game")
public class GameController {

    @Autowired
    private GameService gameService;

    @PostMapping("/start")
    public GameStateResponse startGame(@RequestBody PlayerRequest request) {
        String playerName = request.getPlayerName();
        gameService.startNewGame(playerName);
        gameService.getCurrentGame().setFirstBidder(PlayerPos.P1);

        GameStateResponse response = gameService.getGameStateForPlayer(playerName);

        // Temp until solution for multiplayer
        response.setPlayerPosition(PlayerPos.P1);
        return response;
    }

@PostMapping("/shuffle")
public GameStateResponse getShuffledDeck(@RequestBody PlayerRequest request) {
    PlayerPos playerPosition = request.getPlayerPosition();

    System.out.println("Requested playerPosition for shuffle: " + playerPosition);

    gameService.shuffleDeck(); // actual shuffle logic, if separate

    GameStateResponse response = gameService.getGameStateForPlayer(playerPosition);

    return response;
}


    @PostMapping("/deal")
    public GameStateResponse dealCards(@RequestBody PlayerRequest request) {
        GameState game = gameService.getCurrentGame();

        if (game == null) {
            throw new IllegalStateException("Game not started.");
        }

        if (game.getPhase() != GamePhase.SHUFFLE) {
            throw new IllegalStateException("Can only deal after shuffle.");
        }

        game.getDeck().deal(game.getPlayers()); // Perform actual dealing
        game.setKitty(game.getDeck().getKitty().getCards()); // Move kitty over
        game.setPhase(GamePhase.DEAL); // Advance phase

        PlayerPos viewer = request.getPlayerPosition();

        GameStateResponse response = gameService.getGameStateForPlayer(viewer);
        response.setPlayerPosition(viewer);
        
        return response;
    }

    @PostMapping("/pre-bid")
    public GameStateResponse showHands(@RequestBody PlayerRequest request) {
        GameState game = gameService.getCurrentGame();
        game.setPhase(GamePhase.PRE_BID);

        PlayerPos viewer = request.getPlayerPosition();
        return gameService.getGameStateForPlayer(viewer);
    }

    @PostMapping("/bid")
    public GameStateResponse submitBid(@RequestBody BidRequest request) {
        return gameService.submitBid(request);
    }

    @PostMapping("/finalizeBid")
    public GameStateResponse finalizeBid(@RequestBody FinalBidRequest request) {
        GameState game = gameService.getCurrentGame();
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
        game.setWinningBidStats(finalBid);

        return gameService.getGameStateForPlayer(winnerPos);
    }

    @PostMapping("/kitty")
    public GameStateResponse applyKitty(@RequestBody KittyRequest request) {
        return gameService.applyKittyAndDiscards(request);
    }

    @GetMapping("/state")
    public GameStateResponse getGameStateForPlayer(@RequestParam String player) {
        PlayerPos playerPos = gameService.getCurrentGame().getPlayers().stream()
                .filter(p -> p.getName().equals(player))
                .map(Player::getPosition)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Player not found: " + player));

        return gameService.getGameStateForPlayer(playerPos);
    }
}
