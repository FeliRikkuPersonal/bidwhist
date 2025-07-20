// src/main/java/com/bidwhist/model/PlayerPos.java

package com.bidwhist.model;

public enum PlayerPos {
  P1,
  P2,
  P3,
  P4;

  public PlayerPos getPartner() {
    switch (this) {
      case P1:
        return P3;
      case P3:
        return P1;
      case P2:
        return P4;
      case P4:
        return P2;
      default:
        throw new IllegalStateException("Unknown PlayerPos: " + this);
    }
  }
}
