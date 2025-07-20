// src/main/java/com/bidwhist/service/DeckService.java

package com.bidwhist.service;

import com.bidwhist.model.Deck;
import org.springframework.stereotype.Service;

@Service
public class DeckService {

  /*
   * Creates a new shuffled deck and returns it.
   */
  public static Deck createNewDeck() {
    Deck deck = new Deck();
    deck.shuffle();
    return deck;
  }
}
