// src/test/java/com/bidwhist/bidding/InitialBidTest.java

package com.bidwhist.bidding;

import static org.junit.jupiter.api.Assertions.*;

import com.bidwhist.model.PlayerPos;
import org.junit.jupiter.api.Test;

class InitialBidTest {

  @Test
  void constructsRegularBidCorrectly() {
    InitialBid bid = new InitialBid(PlayerPos.P1, 5, false);
    assertEquals(PlayerPos.P1, bid.getPlayer());
    assertEquals(5, bid.getValue());
    assertFalse(bid.isNo());
    assertFalse(bid.isPassed());
  }

  @Test
  void constructsNoBidCorrectly() {
    InitialBid bid = new InitialBid(PlayerPos.P2, 6, true);
    assertEquals(6, bid.getValue());
    assertTrue(bid.isNo());
    assertFalse(bid.isPassed());
  }

  @Test
  void constructsPassBidCorrectly() {
    InitialBid bid = new InitialBid(PlayerPos.P3);
    assertEquals(0, bid.getValue());
    assertFalse(bid.isNo());
    assertTrue(bid.isPassed());
  }

  @Test
  void allowsBidValueOfSeven() {
    InitialBid bid = new InitialBid(PlayerPos.P4, 7, false);
    assertEquals(7, bid.getValue());
  }

  @Test
  void allowsBidValueOfOne() {
    InitialBid bid = new InitialBid(PlayerPos.P2, 1, true);
    assertEquals(1, bid.getValue());
    assertTrue(bid.isNo());
  }
}
