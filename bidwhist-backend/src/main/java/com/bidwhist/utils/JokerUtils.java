// src/main/java/com/bidwhist/utils/JokerUtils.java

package com.bidwhist.utils;

import com.bidwhist.model.Card;
import com.bidwhist.model.Hand;
import com.bidwhist.model.Rank;

public class JokerUtils {

  /*
  *
  * Checks if the given rank is a joker (small or big).
  *
  */
  public static boolean isJokerRank(Rank rank) {
    return rank == Rank.JOKER_S || rank == Rank.JOKER_B;
  }

  /*
  *
  * Counts the number of jokers in a given hand.
  *
  */
  public static int countJokers(Hand hand) {
    return (int)
        hand.getCards().stream().map(Card::getRank).filter(JokerUtils::isJokerRank).count();
  }
}
