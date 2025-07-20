// src/main/java/com/bidwhist/model/Suit.java

package com.bidwhist.model;

public enum Suit {
  SPADES("♠"),
  HEARTS("♥"),
  DIAMONDS("♦"),
  CLUBS("♣");

  private final String symbol;

  Suit(String symbol) {
    this.symbol = symbol;
  }

  public String getSymbol() {
    return symbol;
  }
}
