package com.bidwhist.dto;

import java.util.List;

import com.bidwhist.bidding.InitialBid;
import com.bidwhist.model.Card;
import com.bidwhist.model.GamePhase;
import com.bidwhist.model.PlayerPos;
import com.bidwhist.model.Suit;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.bidwhist.bidding.BidType;
import com.bidwhist.bidding.FinalBid;

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
    private String viewerName;
    private PlayerPos firstBidder;
    private int bidTurnIndex;
    private List<InitialBid> bids;
    private FinalBid winningFinalBid;
    private int lobbySize;

public GameStateResponse(
        List<PlayerView> players,
        List<Card> kitty,
        int currentTurnIndex,
        GamePhase phase,
        List<Card> shuffledDeck,
        PlayerPos playerPosition,
        String viewerName,
        PlayerPos firstBidder,
        int bidTurnIndex
) {
    this.players = players;
    this.kitty = kitty;
    this.currentTurnIndex = currentTurnIndex;
    this.phase = phase;
    this.shuffledDeck = shuffledDeck;
    this.playerPosition = playerPosition;
    this.viewerName  = viewerName;
    this.firstBidder = firstBidder;
    this.bidTurnIndex = bidTurnIndex;
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

    public String getViewerName() {
        return viewerName;
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

    public void setBids(List<InitialBid> bids) {
        this.bids = bids;
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

    public FinalBid getWinningbid() {
        return winningFinalBid;
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

    public int getLobbySize() {
        return lobbySize;
    }

    public void setHighestBid(InitialBid highestBid) {
        this.highestBid = highestBid;
    }

    public void setPlayers(List<PlayerView> players) {
        this.players = players;
    }

    public void setPlayerPosition(PlayerPos playerPosition) {
        this.playerPosition = playerPosition;
    }

    public void setViewerName(String viewerName) {
        this.viewerName = viewerName;
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
    
    public void setWinningBid(FinalBid winningBid) {
        this.winningFinalBid = winningBid;
    }

    public void setLobbySize(int size) {
        this.lobbySize = size;
    }
}
