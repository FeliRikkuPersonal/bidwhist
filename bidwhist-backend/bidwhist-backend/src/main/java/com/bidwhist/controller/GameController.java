package com.bidwhist.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bidwhist.dto.BidRequest;
import com.bidwhist.dto.GameStateResponse;
import com.bidwhist.dto.KittyRequest;
import com.bidwhist.dto.PlayRequest;
import com.bidwhist.dto.StartGameRequest;
import com.bidwhist.model.PlayedCard;
import com.bidwhist.model.Suit;
import com.bidwhist.model.Team;
import com.bidwhist.service.GameService;

@RestController
@RequestMapping("/api/game")
public class GameController {

    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @PostMapping("/start")
    public void startGame(@RequestBody StartGameRequest request) {
        gameService.startGame(request.getPlayerNames());
    }

    @PostMapping("/bid")
    public GameStateResponse submitBid(@RequestBody BidRequest request) {
        return gameService.submitBid(request);
    }

    @PostMapping("/kitty")
    public GameStateResponse applyKitty(@RequestBody KittyRequest request) {
        gameService.applyKittyAndDiscards(request);
        return gameService.getGameStateForPlayer(request.getPlayer());
    }

    @PostMapping("/play")
    public GameStateResponse playCard(@RequestBody PlayRequest request) {
        gameService.playCard(request);
        return gameService.getGameStateForPlayer(request.getPlayer());
    }

    @GetMapping("/state")
    public GameStateResponse getGameState(@RequestParam String player) {
        return gameService.getGameStateForPlayer(player);
    }

    @GetMapping("/score")
    public Map<String, Integer> getTeamScores() {
        Map<Team, Integer> teamScores = gameService.getCurrentGame().getTeamScores();

        // Convert enum keys to strings for JSON
        return teamScores.entrySet().stream()
                .collect(Collectors.toMap(e -> e.getKey().name(), Map.Entry::getValue));
    }

    @PostMapping("/startNewHand")
    public ResponseEntity<String> startNewHand() {
        gameService.startNewHand();
        return ResponseEntity.ok("New hand started.");
    }

    @GetMapping("/lead-suit")
    public ResponseEntity<String> getLeadSuit() {
        List<PlayedCard> currentTrick = gameService.getCurrentGame().getCurrentTrick();

        if (!currentTrick.isEmpty()) {
            Suit leadSuit = currentTrick.get(0).getCard().getSuit();
            return ResponseEntity.ok("Current lead suit: " + leadSuit.name());
        }

        // If current trick is empty, get last trick (if any)
        List<List<PlayedCard>> completed = gameService.getCurrentGame().getCompletedTricks();
        if (!completed.isEmpty()) {
            List<PlayedCard> lastTrick = completed.get(completed.size() - 1);
            Suit leadSuit = lastTrick.get(0).getCard().getSuit();
            return ResponseEntity.ok("Previous trick's lead suit was: " + leadSuit.name());
        }

        return ResponseEntity.ok("No cards have been played yet in any trick.");
    }

}
