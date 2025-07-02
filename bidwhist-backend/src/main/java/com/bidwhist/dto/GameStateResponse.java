package com.bidwhist.dto;

import java.util.List;

import com.bidwhist.bidding.InitialBid;
import com.bidwhist.model.Card;
import com.bidwhist.model.GamePhase;
import com.bidwhist.model.PlayerPos;
import com.bidwhist.model.Suit;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.bidwhist.bidding.BidType;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class GameStateResponse {
    private List<PlayerView> players;
    private List<Card> kitty;
    private int currentTurnIndex;
    private GamePhase phase;
    private Suit trumpSuit;
    private BidType bidType;
    private String winningPlayerName; // NEW FIELD
    private InitialBid highestBid; // NEW FIELD
    private List<Card> shuffledDeck;
    private PlayerPos playerPosition;
    private PlayerPos firstBidder;
    private int bidTurnIndex;
    private List<InitialBid> bids;

public GameStateResponse(
        List<PlayerView> players,
        List<Card> kitty,
        int currentTurnIndex,
        GamePhase phase,
        Suit trumpSuit,
        BidType bidType,
        String winningPlayerName,
        InitialBid highestBid,
        List<Card> shuffledDeck,
        PlayerPos playerPosition,
        PlayerPos firstBidder,
        int bidTurnIndex,
        List<InitialBid> bids
) {
    this.players = players;
    this.kitty = kitty;
    this.currentTurnIndex = currentTurnIndex;
    this.phase = phase;
    this.trumpSuit = trumpSuit;
    this.bidType = bidType;
    this.winningPlayerName = winningPlayerName;
    this.highestBid = highestBid;
    this.shuffledDeck = shuffledDeck;
    this.playerPosition = playerPosition;
    this.firstBidder = firstBidder;
    this.bidTurnIndex = bidTurnIndex;
    this.bids = bids;
}


    public void setShuffledDeck(List<Card> shuffledDeck) {
        this.shuffledDeck = shuffledDeck;
    }

    public List<PlayerView> getPlayers() {
        return players;
    }

    public PlayerPos getPlayerPosition() {
        return playerPosition;
    }

    public List<Card> getKitty() {
        return kitty;
    }

    public List<Card> getShuffledDeck() {
        return shuffledDeck;
    }

    public int getCurrentTurnIndex() {
        return currentTurnIndex;
    }

    public GamePhase getPhase() {
        return phase;
    }

    public Suit getTrumpSuit() {
        return trumpSuit;
    }

    public PlayerPos getFirstBidder() {
        return firstBidder;
    }

    public int getBidTurnIndex() {
        return bidTurnIndex;
    }

    public List<InitialBid> getBids() {
        return bids;
    }

    public String getWinningPlayerName() {
        return winningPlayerName;
    }

    public void setWinningPlayerName(String winningPlayerName) {
        this.winningPlayerName = winningPlayerName;
    }

    public InitialBid getHighestBid() {
        return highestBid;
    }

    public BidType getBidType() {
        return bidType;
    }

    public void setHighestBid(InitialBid highestBid) {
        this.highestBid = highestBid;
    }

    public void setPlayers(List<PlayerView> players) {
        this.players = players;
    }

    public void setPlayerPostion(PlayerPos playerPosition) {
        this.playerPosition = playerPosition;
    }

    public void setKitty(List<Card> kitty) {
        this.kitty = kitty;
    }

    public void setCurrentTurnIndex(int currentTurnIndex) {
        this.currentTurnIndex = currentTurnIndex;
    }

    public void setPhase(GamePhase phase) {
        this.phase = phase;
    }

    public void setTrumpSuit(Suit trumpSuit) {
        this.trumpSuit = trumpSuit;
    }

    public void setBidType(BidType newBidType) {
        bidType = newBidType;
    }
}
