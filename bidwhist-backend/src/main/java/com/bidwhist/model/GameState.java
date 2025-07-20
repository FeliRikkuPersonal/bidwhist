package com.bidwhist.model;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bidwhist.bidding.BidType;
import com.bidwhist.bidding.FinalBid;
import com.bidwhist.bidding.InitialBid;
import com.bidwhist.dto.Animation;
import com.bidwhist.service.DeckService;
import com.bidwhist.utils.PlayerUtils;

public class GameState {

    private final String gameId;
    private final List<Player> players;
    private final Deck deck;
    private List<Card> kitty;
    private int currentTurnIndex;
    private GamePhase phase;
    private Suit trumpSuit;
    private final List<InitialBid> bids;
    private int bidTurnIndex;
    private InitialBid highestBid;
    private BidType trumpType;
    private final Map<PlayerPos, FinalBid> finalBidCache = new HashMap<>();
    private FinalBid winningBid;
    private String winningPlayerName; // <-- NEW FIELD
    private List<Card> shuffledDeck;
    private PlayerPos firstBidder;
    private Difficulty difficulty;
    private final GameRoom room;
    private Map<PlayerPos, List<Animation>> animationList = new EnumMap<>(PlayerPos.class);
    private PlayerPos bidWinnderPos;
    private final List<Card> playedCards = new ArrayList<>();

    private List<PlayedCard> currentTrick = new ArrayList<>();
    private List<Book> completedTricks = new ArrayList<>();
    private int currentPlayerIndex;

    private int teamAScore = 0;
    private int teamBScore = 0;
    private int teamATricksWon = 0;
    private int teamBTricksWon = 0;

    private Map<Team, Integer> teamTrickCounts = new HashMap<>();
    private Map<Team, Integer> teamScores = new HashMap<>();

    public GameState(String gameId) {
        this.room = new GameRoom(gameId);
        this.gameId = gameId;
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

        for (PlayerPos pos : PlayerPos.values()) {
            animationList.put(pos, new ArrayList<>());
        }
    }

    public void addAnimation(Animation animation) {
        if (animationList == null) {
            return;
        }

        for (List<Animation> queue : animationList.values()) {
            queue.add(animation);
        }
    }

    public boolean removeAnimationById(PlayerPos player, String animationId) {
        List<Animation> animations = animationList.get(player);

        if (animations == null) {
            System.out.println("⚠️ No animations found for player: " + player);
            return false;
        }

        System.out.println("🔍 Attempting to remove animationId: " + animationId + " for player: " + player);

        boolean removed = animations.removeIf(animation -> {
            String currentId = animation.getId();
            System.out.println("   → Comparing against animation ID: " + currentId);
            return animationId != null && currentId != null
                    && animationId.trim().equalsIgnoreCase(currentId.trim());
        });

        if (removed) {
            System.out.println("✅ Animation removed successfully.");
        } else {
            System.out.println("❌ No matching animation found for ID: " + animationId);
        }

        return removed;
    }

    public Team getTeamByPlayerPos(List<Player> players, PlayerPos playerPos) {
        for (Player p : players) {
            if (p.getPosition().equals(playerPos)) {
                return p.getTeam();
            }
        }
        return null; // or "UNKNOWN", or throw new IllegalArgumentException(...)
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void addPlayer(Player player) {
        players.add(player);
    }

    public GameRoom getRoom() {
        return room;
    }

    public int getCurrentTurnIndex() {
        return currentTurnIndex;
    }

    public void setCurrentTurnIndex(int index) {
        this.currentTurnIndex = index;
        System.out.println("DEBUG: Current Turn Index is " + currentTurnIndex);
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

    public PlayerPos getFirstBidder() {
        return firstBidder;
    }

    public BidType getTrumpType() {
        return trumpType;
    }

    public Map<PlayerPos, List<Animation>> getAnimationList() {
        return animationList;
    }

    public void setTrumpType(BidType trumpType) {
        this.trumpType = trumpType;
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

    public PlayerPos getBidWinnerPos() {
        return bidWinnderPos;
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

    public void setFirstBidder(PlayerPos firstBidder) {
        this.firstBidder = firstBidder;
    }

    public FinalBid getWinningBid() {
        return winningBid;
    }

    public void setWinningBid(FinalBid winningBid) {
        this.winningBid = winningBid;
    }

    public String getGameId() {
        return gameId;
    }

    public List<PlayedCard> getCurrentTrick() {
        return currentTrick;
    }

    public void setCurrentTrick(List<PlayedCard> currentTrick) {
        this.currentTrick = currentTrick;
    }

    public List<Book> getCompletedTricks() {
        return completedTricks;
    }

    public void setCompletedTricks(List<Book> completedTricks) {
        this.completedTricks = completedTricks;
    }

    public List<Card> getCompletedCards() {
        List<Card> completed = new ArrayList<>();
        for (Book book : completedTricks) {
            for (PlayedCard pc : book.getPlayedCards()) {
                if (pc.getCard() != null) {
                    completed.add(pc.getCard());
                }
            }
        }
        return completed;
    }

    public void addPlayedCard(Card card) {
        if (card != null) {
            playedCards.add(card);
        }
    }

    public int getCurrentPlayerIndex() {
        return currentPlayerIndex;
    }

    public void setCurrentPlayerIndex(int currentPlayerIndex) {
        this.currentPlayerIndex = currentPlayerIndex;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    public int getTeamAScore() {
        return teamAScore;
    }

    public void setTeamAScore(int teamAScore) {
        this.teamAScore = teamAScore;
    }

    public int getTeamBScore() {
        return teamBScore;
    }

    public void setTeamBScore(int teamBScore) {
        this.teamBScore = teamBScore;
    }

    public int getTeamATricksWon() {
        return teamATricksWon;
    }

    public void setTeamATricksWon(int teamATricksWon) {
        this.teamATricksWon = teamATricksWon;
    }

    public int getTeamBTricksWon() {
        return teamBTricksWon;
    }

    public void setTeamBTricksWon(int teamBTricksWon) {
        this.teamBTricksWon = teamBTricksWon;
    }

    public Map<Team, Integer> getTeamTrickCounts() {
        return teamTrickCounts;
    }

    public void setTeamTrickCounts(Map<Team, Integer> teamTrickCounts) {
        this.teamTrickCounts = teamTrickCounts;
    }

    public Map<Team, Integer> getTeamScores() {
        return teamScores;
    }

    public void setTeamScores(Map<Team, Integer> teamScores) {
        this.teamScores = teamScores;
    }

    public void setAnimationList(Map<PlayerPos, List<Animation>> animationList) {
        this.animationList = animationList;
    }

    public void setBidWinnerPos(PlayerPos winnerPos) {
        this.bidWinnderPos = winnerPos;
    }

    public List<Card> getPlayedCards() {
        return playedCards;
    }

}
