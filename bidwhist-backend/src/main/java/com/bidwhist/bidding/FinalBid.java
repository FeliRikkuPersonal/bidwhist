// src/main/java/com/bidwhist/bidding/FinalBid.java

package com.bidwhist.bidding;

import com.bidwhist.model.PlayerPos;
import com.bidwhist.model.Suit;

/**
 * Represents a finalized bid in the game.
 * Includes bid value (4–7), type (Uptown or Downtown), optional suit, and No-Trump status.
 */
public class FinalBid {

  private final PlayerPos player;
  private final int value; // 4 to 7
  private final boolean isNo;
  private final BidType type;
  private final Suit suit;

  /**
   * Constructs a finalized bid using an InitialBid as a base.
   * Throws error if a non-No bid is missing a suit.
   */
  public FinalBid(InitialBid baseBid, BidType type, Suit suit) {
    if (!baseBid.isNo() && suit == null) {
      throw new IllegalArgumentException("Suit required for non-No bids.");
    }
    this.player = baseBid.getPlayer();
    this.value = baseBid.getValue();
    this.isNo = baseBid.isNo();
    this.type = type;
    this.suit = suit;
  }

  /**
   * Full constructor used for AI or fallback logic to directly define a bid.
   * Suit must be non-null unless this is a No-Trump bid.
   */
  public FinalBid(
    PlayerPos player,
    int value,
    boolean isNo,
    boolean isPassed,
    BidType type,
    Suit suit
  ) {
    if (!isNo && suit == null) {
      throw new IllegalArgumentException("Suit required for non-No bids.");
    }
    this.player = player;
    this.value = value;
    this.isNo = isNo;
    this.type = type;
    this.suit = suit;
  }

  /** Returns string summary of the finalized bid. */
  @Override
  public String toString() {
    return player + " bids " + value + (isNo ? " No" : "") + " " + type + " in " + suit;
  }

  // Getters
  public PlayerPos getPlayer() {
    return player;
  }

  public int getValue() {
    return value;
  }

  public boolean isNo() {
    return isNo;
  }

  public BidType getType() {
    return type;
  }

  public Suit getSuit() {
    return suit;
  }

  /** Converts this finalized bid back to a simplified InitialBid. */
  public InitialBid getInitialBid() {
    return new InitialBid(player, value, isNo);
  }
}
