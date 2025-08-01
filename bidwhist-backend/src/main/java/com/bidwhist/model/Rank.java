// src/main/java/com/bidwhist/model/Rank.java

package com.bidwhist.model;

import com.bidwhist.bidding.BidType;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public enum Rank {
  TWO(2),
  THREE(3),
  FOUR(4),
  FIVE(5),
  SIX(6),
  SEVEN(7),
  EIGHT(8),
  NINE(9),
  TEN(10),
  JACK(11),
  QUEEN(12),
  KING(13),
  ACE(14),
  JOKER_S(15),
  JOKER_B(16);

  private final int value;

  Rank(int value) {
    this.value = value;
  }

  public int getValue() {
    return value;
  }

  public static List<Rank> getOrderedRanks() {
    return Arrays.stream(Rank.values())
        .sorted(Comparator.comparingInt(Rank::getValue))
        .collect(Collectors.toList());
  }

  public static Comparator<Rank> rankComparator(BidType type, boolean isNo) {
    return (r1, r2) -> {
      // Define weights per rank based on bid type
      int w1 = getRankWeight(r1, type, isNo);
      int w2 = getRankWeight(r2, type, isNo);
      return Integer.compare(w1, w2);
    };
  }

  private static int getRankWeight(Rank r, BidType type, boolean isNo) {
    if (isNo) {
      // Jokers are lowest in No bids
      if (r == Rank.JOKER_B) return -2;
      if (r == Rank.JOKER_S) return -1;

      if (type == BidType.UPTOWN) {
        // Standard uptown: ACE > KING > ... > TWO
        return r.getValue(); // ACE = 14
      } else { // DOWNTOWN
        // Downtown: ACE > TWO > THREE > ... > KING (lowest)
        if (r == Rank.ACE) return 100; // ACE highest
        return 100 - r.getValue(); // KING = 87, TWO = 98
      }
    } else {
      // Suited bids — jokers are highest
      if (r == Rank.JOKER_B) return 200;
      if (r == Rank.JOKER_S) return 199;

      if (type == BidType.UPTOWN) {
        return r.getValue(); // ACE = 14 highest
      } else { // DOWNTOWN
        if (r == Rank.ACE) return 200; // ACE highest
        return 100 - r.getValue(); // Inverted for rest
      }
    }
  }
}
