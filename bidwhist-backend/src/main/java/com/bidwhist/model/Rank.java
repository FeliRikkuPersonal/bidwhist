// src/main/java/com/bidwhist/model/Rank.java

package com.bidwhist.model;

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
}
