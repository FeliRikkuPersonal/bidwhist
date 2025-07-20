// src/main/java/com/bidwhist/bidding/AiBidOption.java

package com.bidwhist.bidding;

import com.bidwhist.model.PlayerPos;

/**
 * AiBidOption is a utility class that helps convert evaluated suit data into FinalBid objects,
 * based on the given direction (Uptown or Downtown) and "No" bid status.
 */
public class AiBidOption {

  /**
   * Converts a SuitEvaluation into a FinalBid if the strength is valid. Returns null for values
   * below 4 or unsupported directions.
   *
   * @param player The player making the bid (typically an AI).
   * @param eval The evaluated strength and suit breakdown.
   * @param type The bidding direction (UPTOWN or DOWNTOWN).
   * @param isNo True if this should be a No-Trump bid (rare with suit eval).
   * @return FinalBid instance or null if strength < 4.
   */
  public static FinalBid fromEvaluation(
      PlayerPos player, SuitEvaluation eval, BidType type, boolean isNo) {
    int strength;

    if (type == BidType.UPTOWN) {
      strength = eval.getUptownStrength();
    } else if (type == BidType.DOWNTOWN) {
      strength = eval.getDowntownStrength();
    } else {
      return null; // Invalid bid direction
    }

    if (strength < 4) {
      return null; // Too weak to bid
    }
    if (strength > 7) {
      strength = 7; // Cap max legal bid at 7
    }

    return new FinalBid(player, strength, isNo, false, type, eval.getSuit());
  }
}
