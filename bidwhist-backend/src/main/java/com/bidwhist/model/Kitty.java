// src/main/java/com/bidwhist/model/Kitty.java

package com.bidwhist.model;

import java.util.ArrayList;
import java.util.List;

/*
 * Represents the kitty—a separate set of 6 cards used in bidding games.
 * Cards are added here during deal and transferred to the winning bidder,
 * who then discards back into it.
 */
public class Kitty {
  public enum State {
    UNCLAIMED,
    CLAIMED,
    DISCARDED
  }

  public List<Card> cards;
  public State state;

  public Kitty() {
    this.cards = new ArrayList<>();
    this.state = State.UNCLAIMED;
  }

  public void addCard(Card card) {
    if (cards.size() >= 6) {
      throw new IllegalStateException("Kitty already has 6 cards.");
    }
    cards.add(card);
  }

  public List<Card> getCards() {
    return new ArrayList<>(cards);
  }

  public void clear() {
    System.out.println("[Kitty: clear]");
    cards.clear();
  }

  /*
   * Transfers all kitty cards to a player's hand and marks the kitty as claimed.
   */
  public List<Card> transferToHand(Hand hand) {
    hand.addCards(cards);
    List<Card> transferred = new ArrayList<>(cards);
    clear();
    setState(State.CLAIMED);
    return transferred;
  }

  /*
   * Accepts a full discard of 6 cards from a player's hand and marks the kitty as discarded.
   */
  public List<Card> receiveCardsFromHand(List<Card> newCards) {
    if (newCards.size() != 6) {
      throw new IllegalArgumentException("You must discard exactly 6 cards to the kitty.");
    }
    for (Card card : newCards) {
      cards.add(card);
    }

    setState(State.DISCARDED);
    return cards;
  }

  public int size() {
    return cards.size();
  }

  public boolean isFull() {
    return cards.size() == 6;
  }

  public void setState(State state) {
    this.state = state;
  }
}
