// src/main/java/com/bidwhist/controller/GameController.java

package com.bidwhist.controller;

import com.bidwhist.dto.BidRequest;
import com.bidwhist.dto.FinalBidRequest;
import com.bidwhist.dto.GameStateResponse;
import com.bidwhist.dto.HandRequest;
import com.bidwhist.dto.HandResponse;
import com.bidwhist.dto.JoinGameRequest;
import com.bidwhist.dto.KittyRequest;
import com.bidwhist.dto.PlayRequest;
import com.bidwhist.dto.PollRequest;
import com.bidwhist.dto.PopAnimationRequest;
import com.bidwhist.dto.StartGameRequest;
import com.bidwhist.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/*
* REST Controller for all game-related endpoints.
* Acts as the main entry point for starting, joining,
* and interacting with game sessions.
*/
@RestController
@RequestMapping("/game")
public class GameController {

  @Autowired private GameService gameService;

  /* Starts a single-player game session */
  @PostMapping("/start")
  public GameStateResponse startGame(@RequestBody StartGameRequest request) {
    return gameService.startSoloGame(request);
  }

  /* Creates a new multiplayer game room */
  @PostMapping("/create-multiplayer")
  public GameStateResponse createMuliplayerGame(@RequestBody StartGameRequest request) {
    return gameService.createMutliplayerGame(request);
  }

  /* Joins an existing game room using code */
  @PostMapping("/join")
  public GameStateResponse joinGameByCode(@RequestBody JoinGameRequest request) {
    return gameService.joinGame(request);
  }

  /* Submits an initial bid during the bidding phase */
  @PostMapping("/bid")
  public GameStateResponse submitBid(@RequestBody BidRequest request) {
    return gameService.submitBid(request);
  }

  /* Finalizes the winning bid with bid type and suit */
  @PostMapping("/finalizeBid")
  public GameStateResponse finalizeBid(@RequestBody FinalBidRequest request) {
    return gameService.getFinalBid(request);
  }

  /* Applies kitty cards and discards after bid win */
  @PostMapping("/kitty")
  public GameStateResponse applyKitty(@RequestBody KittyRequest request) {
    return gameService.applyKittyAndDiscards(request);
  }

  /* Plays a card from a player's hand */
  @PostMapping("/play")
  public GameStateResponse playTurn(@RequestBody PlayRequest request) {
    return gameService.playCard(request);
  }

  /* Removes the completed animation from the queue */
  @PostMapping("/pop-animation")
  public void popCompletedAnimation(@RequestBody PopAnimationRequest request) {
    gameService.popAnimation(request);
  }

  /* Sends updated hand and kitty views to clients */
  @PostMapping("/update-cards")
  public HandResponse postMethodName(@RequestBody HandRequest request) {
    return gameService.provideUpdatedCards(request);
  }

  /* Fetches the latest game state for polling clients */
  @PostMapping("/state")
  public GameStateResponse getGameStateForPlayer(@RequestBody PollRequest request) {
    return gameService.updateState(request);
  }
}
