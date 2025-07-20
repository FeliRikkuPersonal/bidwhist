// src/main/java/com/bidwhist/model/Hand.java

package com.bidwhist.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/*
 * Represents a player's hand of cards. Supports adding/removing cards,
 * sorting, and discarding to the kitty.
 */
public class Hand {
  public List<Card> cards = new ArrayList<>();

  // Not currently used
  public Card addCard(Card card) {
    cards.add(card);
    Collections.sort(cards);
    return card;
  }

  // Used for initial population of hand and adding kitty
  public List<Card> addCards(List<Card> newCards) {
    cards.addAll(newCards);
    Collections.sort(newCards);
    return newCards;
  }

  public Card removeCard(Card card) {
    cards.remove(card);
    return card;
  }

  public List<Card> transferToKitty(Kitty kitty, List<Card> discardedCards) {
    if (discardedCards.size() != 6) {
      throw new IllegalStateException("Must discard 6 cards.");
    }
    kitty.receiveCardsFromHand(discardedCards);
    cards.removeAll(discardedCards);
    return discardedCards;
  }

  public int size() {
    return cards.size();
  }

  public List<Card> getCards() {
    return cards;
  }
}
