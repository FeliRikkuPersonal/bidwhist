package com.bidwhist.dto;

import java.util.List;
import java.util.UUID;

import com.bidwhist.model.PlayerPos;
import com.bidwhist.model.Team;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.bidwhist.model.Book;
import com.bidwhist.model.Card;
import com.bidwhist.model.PlayedCard;

public class Animation {

    private final String id = UUID.randomUUID().toString();

    @JsonProperty
    private AnimationType type;

    @JsonProperty
    private PlayerPos player;

    @JsonProperty
    private Card card;

    @JsonProperty
    private List<Card> cardList;

    @JsonProperty
    private Team winningTeam;

    public Animation() {}

    public Animation(PlayedCard playedCard) {
        this.type = AnimationType.PLAY;
        this.card = playedCard.getCard();
        this.player = playedCard.getPlayer();
    }

    public Animation(List<Card> deck) {
        this.type = AnimationType.DEAL;
        this.cardList = deck;
    }

    public Animation(Book trick) {
        this.type = AnimationType.COLLECT;
        this.cardList = trick.getCards();
        this.winningTeam = trick.getWinningTeam(); // fixed typo
    }

    // Getters
    public String getId() {
        return id;
    }

    public AnimationType getType() {
        return type;
    }

    public PlayerPos getPlayer() {
        return player;
    }

    public Card getCard() {
        return card;
    }

    public List<Card> getCardList() {
        return cardList;
    }

    public Team getWinningTeam() {
        return winningTeam;
    }

    // Setters
    public void setType(AnimationType type) {
        this.type = type;
    }

    public void setPlayer(PlayerPos player) {
        this.player = player;
    }

    public void setCard(Card card) {
        this.card = card;
    }

    public void setCardList(List<Card> cardList) {
        this.cardList = cardList;
    }

    public void setWinningTeam(Team winningTeam) {
        this.winningTeam = winningTeam;
    }
}
