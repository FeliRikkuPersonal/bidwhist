// src/main/java/com/bidwhist/bidding/InitialBid.java

package com.bidwhist.bidding;

import com.bidwhist.model.PlayerPos;

/*
 * Represents an initial bid during the bidding phase.
 * Supports regular bids, "no" bids (e.g., No Trump), and pass bids.
 *
 * Examples:
 * - new InitialBid(P1, 4, true) → P1 bids 4 (No)
 * - InitialBid.pass(P1) → P1 passes
 */
public class InitialBid {
  private final PlayerPos player;
  private final int value;       // Valid values: 4 to 7 (0 for pass)
  private final boolean isNo;    // Whether this is a "No" bid
  private final boolean isPassed;

  /* Constructs a regular or "No" bid */
  public InitialBid(PlayerPos player, int value, boolean isNo) {
    this.player = player;
    this.value = value;
    this.isNo = isNo;
    this.isPassed = false;
  }

  /* Constructs a pass bid */
  public InitialBid(PlayerPos player) {
    this.player = player;
    this.value = 0;
    this.isNo = false;
    this.isPassed = true;
  }

  /* Static factory method for creating a pass bid */
  public static InitialBid pass(PlayerPos player) {
    return new InitialBid(player);
  }

  /* Returns human-readable summary of the initial bid (for display in UI) */
  public String showInitialBid() {
    if (isPassed) return player + " passes";
    return player + " bids " + value + (isNo ? " (No)" : "");
  }

  @Override
  public String toString() {
    if (isPassed) return player + " passes";
    return player + " bids " + value + (isNo ? " No" : "");
  }

  public PlayerPos getPlayer() {
    return player;
  }

  public int getValue() {
    return value;
  }

  public boolean isNo() {
    return isNo;
  }

  public boolean isPassed() {
    return isPassed;
  }
}
