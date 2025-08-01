// src/test/java/com/bidwhist/bidding/FinalBidTest.java

package com.bidwhist.bidding;

import static org.junit.jupiter.api.Assertions.*;

import com.bidwhist.model.PlayerPos;
import com.bidwhist.model.Suit;
import org.junit.jupiter.api.Test;

class FinalBidTest {

  private final PlayerPos player = PlayerPos.P1;

  @Test
  void constructorThrowsExceptionIfNonNoBidHasNullSuit() {
    InitialBid baseBid = new InitialBid(player, 5, false); // not a No bid
    assertThrows(IllegalArgumentException.class, () -> new FinalBid(baseBid, BidType.UPTOWN, null));
  }

  @Test
  void constructorAcceptsNoBidWithNullSuit() {
    InitialBid baseBid = new InitialBid(player, 6, true); // No bid
    FinalBid bid = new FinalBid(baseBid, BidType.DOWNTOWN, null);
    assertNotNull(bid);
    assertTrue(bid.isNo());
    assertNull(bid.getSuit());
  }

  @Test
  void directConstructorThrowsIfSuitIsMissingForNonNo() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new FinalBid(player, 4, false, false, BidType.UPTOWN, null));
  }

  @Test
  void directConstructorAllowsNoBidWithNullSuit() {
    FinalBid bid = new FinalBid(player, 4, true, false, BidType.DOWNTOWN, null);
    assertNotNull(bid);
    assertTrue(bid.isNo());
    assertNull(bid.getSuit());
  }

  @Test
  void toStringOutputsCorrectly() {
    FinalBid bid = new FinalBid(player, 6, false, false, BidType.UPTOWN, Suit.HEARTS);
    String result = bid.toString();
    assertTrue(result.contains("bids 6"));
    assertTrue(result.contains("UPTOWN"));
    assertTrue(result.contains("HEARTS"));
  }

  @Test
  void getInitialBidMatchesOriginalValues() {
    FinalBid bid = new FinalBid(player, 5, true, false, BidType.DOWNTOWN, null);
    InitialBid initial = bid.getInitialBid();
    assertEquals(player, initial.getPlayer());
    assertEquals(5, initial.getValue());
    assertTrue(initial.isNo());
  }
}
