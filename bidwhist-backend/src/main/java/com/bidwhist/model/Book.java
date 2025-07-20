// src/main/java/com/bidwhist/model/Book.java

package com.bidwhist.model;

import java.util.ArrayList;
import java.util.List;

/*
 * Represents a completed book (or trick) containing 4 played cards.
 * Once full, it tracks which team won the round.
 */
public class Book {
  public List<PlayedCard> cards = new ArrayList<>();
  private Team winningTeam;

  /* Constructs a Book with the given played cards and the winning team */
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

  /* Extracts and returns only the Card objects from the PlayedCard list */
  public List<Card> getCards() {
    List<Card> cardList = new ArrayList<>();

    for (PlayedCard pc : cards) {
      cardList.add(pc.getCard());
    }

    return cardList;
  }

  public Team getWinningTeam() {
    return winningTeam;
  }
}
