package com.bidwhist.dto;

import java.util.List;

import com.bidwhist.bidding.InitialBid;
import com.bidwhist.model.Card;
import com.bidwhist.model.GamePhase;
import com.bidwhist.model.Suit;
import com.bidwhist.bidding.BidType;

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

    public GameStateResponse(List<PlayerView> players, List<Card> kitty, int currentTurnIndex,
                              GamePhase phase, Suit trumpSuit, BidType bidType) {
        this.players = players;
        this.kitty = kitty;
        this.currentTurnIndex = currentTurnIndex;
        this.phase = phase;
        this.trumpSuit = trumpSuit;
        this.bidType = bidType;

    }

    public void setShuffledDeck(List<Card> shuffledDeck) {
        this.shuffledDeck = shuffledDeck;
    }

    public List<PlayerView> getPlayers() {
        return players;
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
