package com.bidwhist.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bidwhist.bidding.FinalBid;
import com.bidwhist.bidding.InitialBid;
import com.bidwhist.dto.BidRequest;
import com.bidwhist.dto.FinalBidRequest;
import com.bidwhist.dto.JoinGameRequest;
import com.bidwhist.dto.GameStateResponse;
import com.bidwhist.dto.StartGameRequest;
import com.bidwhist.dto.KittyRequest;
import com.bidwhist.dto.PlayerRequest;
import com.bidwhist.dto.PlayRequest;
import com.bidwhist.dto.PollRequest;
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
    public GameStateResponse startGame(@RequestBody StartGameRequest request) {
        return gameService.startSoloGame(request);
    }

    @PostMapping("/join")
    public GameStateResponse joinGameByCode(@RequestBody JoinGameRequest request) {
        return gameService.joinGame(request);
    }

    @PostMapping("/bid")
    public GameStateResponse submitBid(@RequestBody BidRequest request) {
        return gameService.submitBid(request);
    }

    @PostMapping("/finalizeBid")
    public GameStateResponse finalizeBid(@RequestBody FinalBidRequest request) {
        return gameService.getFinalBid(request);
    }

    @PostMapping("/kitty")
    public GameStateResponse applyKitty(@RequestBody KittyRequest request) {
        return gameService.applyKittyAndDiscards(request);
    }

    @PostMapping("/play")
    public GameStateResponse playTurn(@RequestBody PlayRequest request) {
        return gameService.playCard(request);
    }

    @PostMapping("/state")
    public GameStateResponse getGameStateForPlayer(@RequestBody PollRequest request) {
        return gameService.updateState(request);
    }
}
