// src/main/java/com/bidwhist/utils/CardUtils.java

package com.bidwhist.utils;

import java.util.Comparator;

import com.bidwhist.bidding.BidType;
import com.bidwhist.model.Card;
import com.bidwhist.model.GameState;
import com.bidwhist.model.Rank;
import com.bidwhist.model.Suit;

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
   */
public static Comparator<Card> getCardComparator(GameState game) {
  BidType bidType = game.getBidType();
  boolean isNo = game.getWinningBid() != null && game.getWinningBid().isNo();
  Suit trumpSuit = isNo ? null : game.getTrumpSuit();

  Comparator<Rank> rankComparator = Rank.rankComparator(bidType, isNo);

  return (a, b) -> {
    // Trump suit check (ignored in No Trump)
    boolean aTrump = trumpSuit != null && trumpSuit.equals(a.getSuit());
    boolean bTrump = trumpSuit != null && trumpSuit.equals(b.getSuit());

    if (aTrump && !bTrump) return -1;
    if (!aTrump && bTrump) return 1;

    // Let rankComparator handle all card comparisons, including jokers
    return rankComparator.compare(a.getRank(), b.getRank());
  };
}


}
