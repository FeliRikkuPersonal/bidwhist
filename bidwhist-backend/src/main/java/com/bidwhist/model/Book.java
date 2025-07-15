package com.bidwhist.model;

import java.util.ArrayList;
/* Books will be holders of 4 cards. These are the cards played during
 * a round. Once all cards are played, the book will be evaluated to 
 * decide who won the book.
 */
import java.util.List;

public class Book {
    public List<PlayedCard> cards = new ArrayList<>();
    private Team winningTeam;

    public Book(List<PlayedCard> cards, Team team) {
        this.cards = cards;
        this.winningTeam = team;
    }

    public void setCards(List<PlayedCard> cards) {
        this.cards = cards;
    }

    public void setWinningTeam(Team team) {
        this.winningTeam = team;
    }
    
    public List<PlayedCard> getPlayedCards() {
        return cards;
    }

    public List<Card> getCards() {
        List<Card> cardList = new ArrayList<>();
        
        for(PlayedCard pc : cards) {
            cardList.add(pc.getCard());
        }

        return cardList;
    }

    public Team getWinningTeam() {
        return winningTeam;
    }
}

