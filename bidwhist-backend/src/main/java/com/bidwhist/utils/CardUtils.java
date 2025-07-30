// src/main/java/com/bidwhist/utils/CardUtils.java

package com.bidwhist.utils;

import com.bidwhist.model.Card;

public class CardUtils {

  /*
   *
   * Checks if two cards are equal by comparing rank and suit.
   * Safely handles Jokers and null suits.
   *
   */
  public static boolean cardsMatch(Card c1, Card c2) {
    if (c1 == null || c2 == null) return false;

    boolean ranksMatch = c1.getRank() != null && c1.getRank().equals(c2.getRank());

    boolean suitsMatch =
        (c1.getSuit() == null && c2.getSuit() == null)
            || (c1.getSuit() != null && c1.getSuit().equals(c2.getSuit()));

    return ranksMatch && suitsMatch;
  }


}
