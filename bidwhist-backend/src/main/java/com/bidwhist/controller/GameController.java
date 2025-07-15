package com.bidwhist.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bidwhist.dto.BidRequest;
import com.bidwhist.dto.FinalBidRequest;
import com.bidwhist.dto.JoinGameRequest;
import com.bidwhist.dto.GameStateResponse;
import com.bidwhist.dto.StartGameRequest;
import com.bidwhist.dto.KittyRequest;
import com.bidwhist.dto.PlayRequest;
import com.bidwhist.dto.PollRequest;
import com.bidwhist.dto.PopAnimationRequest;
import com.bidwhist.service.GameService;


@CrossOrigin(origins = {"https://bidwhist.onrender.com","http://localhost:5173"})
@RestController
@RequestMapping("/api/game")
public class GameController {

    @Autowired
    private GameService gameService;

    @PostMapping("/start")
    public GameStateResponse startGame(@RequestBody StartGameRequest request) {
        return gameService.startSoloGame(request);
    }

    @PostMapping("/create-multiplayer")
    public GameStateResponse createMuliplayerGame(@RequestBody StartGameRequest request) {
        return gameService.createMutliplayerGame(request);
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

    @PostMapping("/pop-animation")
    public void popCompletedAnimation(@RequestBody PopAnimationRequest request) {
        gameService.popAnimation(request);
    }

    @PostMapping("/state")
    public GameStateResponse getGameStateForPlayer(@RequestBody PollRequest request) {
        return gameService.updateState(request);
    }
}
