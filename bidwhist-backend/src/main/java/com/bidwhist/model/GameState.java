package com.bidwhist.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bidwhist.bidding.FinalBid;
import com.bidwhist.bidding.InitialBid;
import com.bidwhist.bidding.BidType;
import com.bidwhist.utils.PlayerUtils;
import com.bidwhist.service.DeckService;

public class GameState {

    private final List<Player> players;
    private final Deck deck;
    private List<Card> kitty;
    private int currentTurnIndex;
    private GamePhase phase;
    private Suit trumpSuit;

    private final List<InitialBid> bids;
    private int bidTurnIndex;
    private InitialBid highestBid;
    private Map<PlayerPos, FinalBid> finalBidCache = new HashMap<>();
    private FinalBid winningBid;
    private String winningPlayerName; // <-- NEW FIELD
    private List<Card> shuffledDeck;

    public GameState() {
        this.players = new ArrayList<>();
        this.deck = DeckService.createNewDeck();
        this.kitty = new ArrayList<>();
        this.currentTurnIndex = 0;
        this.phase = GamePhase.DEAL;
        this.trumpSuit = null;
        this.bids = new ArrayList<>();
        this.bidTurnIndex = 0;
        this.highestBid = null;
        this.winningPlayerName = null; // <-- Initialize
        this.shuffledDeck = deck.getCards();
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void addPlayer(Player player) {
        players.add(player);
    }

    public int getCurrentTurnIndex() {
        return currentTurnIndex;
    }

    public void setCurrentTurnIndex(int index) {
        this.currentTurnIndex = index;
    }

    public Deck getDeck() {
        return deck;
    }

    public List<Card> getShuffledDeck() {
        return shuffledDeck;
    }

    public void setShuffledDeck(List<Card> shuffledDeck) {
        this.shuffledDeck = shuffledDeck;
    }

    public List<Card> getKitty() {
        return kitty;
    }

    public void setKitty(List<Card> kitty) {
        this.kitty = kitty;
    } 

    public GamePhase getPhase() {
        return phase;
    }

    public void setPhase(GamePhase phase) {
        this.phase = phase;
    }

    public Suit getTrumpSuit() {
        return trumpSuit;
    }

    public void setTrumpSuit(Suit trumpSuit) {
        this.trumpSuit = trumpSuit;
    }

    public List<InitialBid> getBids() {
        return bids;
    }

    public void addBid(InitialBid bid) {
        bids.add(bid);
    }

    public int getBidTurnIndex() {
        return bidTurnIndex;
    }

    public void setBidTurnIndex(int bidTurnIndex) {
        this.bidTurnIndex = bidTurnIndex;
    }

    public InitialBid getHighestBid() {
        return highestBid;
    }

    public void setHighestBid(InitialBid highestBid) {
        this.highestBid = highestBid;
    }

    public void setWinningBidStats(FinalBid bid) {
        this.winningBid = bid;
        this.winningPlayerName = PlayerUtils.getNameByPosition(bid.getPlayer(), players);
        this.trumpSuit = bid.getSuit();

    }

    public String getWinningPlayerName() {
        return winningPlayerName;
    }

    public void setWinningPlayerName(String winningPlayerName) {
        this.winningPlayerName = winningPlayerName;
    }

    public PlayerPos getWinningPlayerPos() {
        return winningBid.getPlayer();
    }

    public Map<PlayerPos, FinalBid> getFinalBidCache() {
        return finalBidCache;
    }

    public BidType getFinalBidType() {
        if (winningBid != null) {
            return winningBid.getType();
        } else {
            return null;
        }
    }
}
