// src/main/java/com/bidwhist/model/Deck.java

package com.bidwhist.model;

import com.bidwhist.utils.JokerUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Deck {

  private final List<Card> cards;
  private Kitty kitty;

  /* 
   * Constructs a new standard Bid Whist deck.
   * Includes 52 standard cards and 2 Jokers. Initializes an empty kitty.
   */
  public Deck() {
    this.cards = new ArrayList<>();
    buildStandardDeck();
    this.kitty = new Kitty();
  }

  /* 
   * Populates the deck with standard cards (excluding Jokers), 
   * then adds the small and big Jokers.
   */
  private void buildStandardDeck() {
    for (Suit suit : Suit.values()) {
      for (Rank rank : Rank.values()) {
        if (!JokerUtils.isJokerRank(rank)) {
          cards.add(new Card(suit, rank));
        }
      }
    }

    cards.add(new Card(null, Rank.JOKER_S));
    cards.add(new Card(null, Rank.JOKER_B));
  }

  /* 
   * Randomly shuffles the order of cards in the deck.
   */
  public void shuffle() {
    Collections.shuffle(cards);
  }

  /* 
   * Deals the first 48 cards evenly to 4 players, 
   * then places the remaining 6 cards in the kitty.
   */
  public void deal(List<Player> players) {
    for (int i = 0; i < 48; i++) {
      int index = i % 4;
      players.get(index).addCard(cards.get(i));
    }

    for (int i = 48; i < 54; i++) {
      kitty.addCard(cards.get(i));
    }
  }

  /* 
   * Clears the assigned suit of all Jokers in the deck.
   */
  public void resetJokerSuits() {
    for (Card card : cards) {
      card.clearSuit();
    }
  }

  /* 
   * Assigns the trump suit to all Jokers in the deck.
   */
  public void assignTrumpSuitToJokers(Suit trump) {
    for (Card card : cards) {
      card.assignSuit(trump);
    }
  }

  /* 
   * Empties the kitty and logs the operation to console.
   */
  public void clearKitty() {
    System.out.println("[clearKitty]");
    kitty.clear();
  }

  public List<Card> getCards() {
    return cards;
  }

  public Kitty getKitty() {
    return kitty;
  }
}
