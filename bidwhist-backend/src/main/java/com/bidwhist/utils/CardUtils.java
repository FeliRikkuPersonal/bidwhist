// src/main/java/com/bidwhist/utils/CardUtils.java

package com.bidwhist.utils;

import com.bidwhist.bidding.BidType;
import com.bidwhist.model.Card;

public class CardUtils {

  /*
   *
   * Checks if two cards are equal by comparing rank and suit.
   * Safely handles Jokers and null suits.
   *
   */
  public static boolean cardsMatch(Card c1, Card c2) {
    if (c1 == null || c2 == null)
      return false;

    boolean ranksMatch = c1.getRank() != null && c1.getRank().equals(c2.getRank());

    boolean suitsMatch = (c1.getSuit() == null && c2.getSuit() == null)
        || (c1.getSuit() != null && c1.getSuit().equals(c2.getSuit()));

    return ranksMatch && suitsMatch;
  }

  /**
   * Compares two cards according to bid type (UPTOWN, DOWNTOWN, or NO_TRUMP).
   *
   * @param thisCard The card being evaluated as greater.
   * @param thatCard The card being compared against.
   * @param bidType  The current bid type, which affects rank comparisons.
   * @return true if thisCard is considered greater than thatCard under the
   *         bidType rules.
   */
  public static boolean isGreaterThan(Card thisCard, Card thatCard, BidType bidType) {
    if (thisCard == null || thatCard == null || thisCard.getRank() == null || thatCard.getRank() == null) {
      return false; // Or throw IllegalArgumentException
    }

    int thisVal = thisCard.getRank().getValue();
    int thatVal = thatCard.getRank().getValue();

    if (bidType == BidType.DOWNTOWN) {
      // Lower cards win, except for special handling (already handled in your game
      // rules)
      return thisVal < thatVal;
    }

    // UPTOWN and default
    return thisVal > thatVal;
  }

}
