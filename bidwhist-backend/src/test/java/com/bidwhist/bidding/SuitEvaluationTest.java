// src/test/java/com/bidwhist/bidding/SuitEvaluationTest.java

package com.bidwhist.bidding;

import static org.junit.jupiter.api.Assertions.*;

import com.bidwhist.model.Suit;
import org.junit.jupiter.api.Test;

class SuitEvaluationTest {

  @Test
  void constructsProperlyWithAllValues() {
    SuitEvaluation eval = new SuitEvaluation(Suit.DIAMONDS, 6, 4, 8);

    assertEquals(Suit.DIAMONDS, eval.getSuit());
    assertEquals(6, eval.getUptownStrength());
    assertEquals(4, eval.getDowntownStrength());
    assertEquals(8, eval.getCardCount());
  }

  @Test
  void allowsZeroValues() {
    SuitEvaluation eval = new SuitEvaluation(Suit.CLUBS, 0, 0, 0);

    assertEquals(0, eval.getUptownStrength());
    assertEquals(0, eval.getDowntownStrength());
    assertEquals(0, eval.getCardCount());
  }

  @Test
  void allowsMaxReasonableValues() {
    SuitEvaluation eval = new SuitEvaluation(Suit.SPADES, 7, 7, 13);

    assertEquals(7, eval.getUptownStrength());
    assertEquals(7, eval.getDowntownStrength());
    assertEquals(13, eval.getCardCount());
  }

  @Test
  void storesDifferentUptownAndDowntownStrengthsIndependently() {
    SuitEvaluation eval = new SuitEvaluation(Suit.HEARTS, 5, 2, 6);

    assertEquals(5, eval.getUptownStrength());
    assertEquals(2, eval.getDowntownStrength());
  }
}
