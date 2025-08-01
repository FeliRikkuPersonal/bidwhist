// src/test/java/com/bidwhist/testCases/TcF01.java

package com.bidwhist.testCases;

import static org.junit.jupiter.api.Assertions.*;

import com.bidwhist.bidding.BidType;
import com.bidwhist.bidding.FinalBid;
import com.bidwhist.bidding.HandEvaluator;
import com.bidwhist.model.GameState;
import com.bidwhist.model.Player;
import com.bidwhist.model.PlayerPos;
import com.bidwhist.model.Team;
import com.bidwhist.testUtils.TestHands;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TcF01 {

  private GameState game;
  private Player aiPlayer;
  private static TestHands testHands;

  @BeforeEach
  public void setUp() {
    testHands = new TestHands();
    game = new GameState("TcF01");
    aiPlayer = new Player("ai", true, PlayerPos.P2, Team.B);
  }

  @Test
  public void noTrumpHigh_isValidEvaluation() {
    aiPlayer.setHand(testHands.getNoTrumpHighHand());
    HandEvaluator evaluator = new HandEvaluator(aiPlayer);
    evaluator.evaluateHand();

    List<FinalBid> bidOptions = evaluator.evaluateAll(aiPlayer.getPosition());

    assertNotNull(bidOptions, "Bid options should not be null");
    assertFalse(bidOptions.isEmpty(), "Expected at least one valid bid option");

    // Optionally, check if a specific expected bid is present
    boolean containsExpected =
        bidOptions.stream()
            .anyMatch(bid -> bid.isNo() && bid.getType() == BidType.UPTOWN && bid.getValue() >= 4);
    assertTrue(containsExpected, "Expected a NO TRUMP bid with value 6 or more");
  }

  @Test
  public void noTrumpLow_isValidEvaluation() {
    aiPlayer.setHand(testHands.getNoTrumpLowHand());
    HandEvaluator evaluator = new HandEvaluator(aiPlayer);
    evaluator.evaluateHand();

    List<FinalBid> bidOptions = evaluator.evaluateAll(aiPlayer.getPosition());

    assertNotNull(bidOptions, "Bid options should not be null");
    assertFalse(bidOptions.isEmpty(), "Expected at least one valid bid option");

    // Optionally, check if a specific expected bid is present
    boolean containsExpected =
        bidOptions.stream()
            .anyMatch(
                bid -> bid.isNo() && bid.getType() == BidType.DOWNTOWN && bid.getValue() >= 4);
    assertTrue(containsExpected, "Expected a NO TRUMP bid with value 6 or more");
  }

  @Test
  public void strongUptownHand_isValidEvaluation() {
    aiPlayer.setHand(testHands.getStrongUptownHand());
    HandEvaluator evaluator = new HandEvaluator(aiPlayer);
    evaluator.evaluateHand();

    List<FinalBid> bidOptions = evaluator.evaluateAll(aiPlayer.getPosition());

    assertNotNull(bidOptions, "Bid options should not be null");
    assertFalse(bidOptions.isEmpty(), "Expected at least one valid bid option");

    // Optionally, check if a specific expected bid is present
    boolean containsExpected =
        bidOptions.stream()
            .anyMatch(bid -> !bid.isNo() && bid.getType() == BidType.UPTOWN && bid.getValue() >= 4);
    assertTrue(containsExpected, "Expected a NO TRUMP bid with value 6 or more");
  }

  @Test
  public void strongDowntownHand_isValidEvaluation() {
    aiPlayer.setHand(testHands.getStrongDowntownHand());
    HandEvaluator evaluator = new HandEvaluator(aiPlayer);
    evaluator.evaluateHand();

    List<FinalBid> bidOptions = evaluator.evaluateAll(aiPlayer.getPosition());

    assertNotNull(bidOptions, "Bid options should not be null");
    assertFalse(bidOptions.isEmpty(), "Expected at least one valid bid option");

    // Optionally, check if a specific expected bid is present
    boolean containsExpected =
        bidOptions.stream()
            .anyMatch(
                bid -> !bid.isNo() && bid.getType() == BidType.DOWNTOWN && bid.getValue() >= 4);
    assertTrue(containsExpected, "Expected a NO TRUMP bid with value 6 or more");
  }

  @Test
  public void strongDowntownWithJokerGapHand_isValidEvaluation() {
    aiPlayer.setHand(testHands.getStrongDowntownWithJokerGapHand());
    HandEvaluator evaluator = new HandEvaluator(aiPlayer);
    evaluator.evaluateHand();

    List<FinalBid> bidOptions = evaluator.evaluateAll(aiPlayer.getPosition());

    assertNotNull(bidOptions, "Bid options should not be null");
    assertFalse(bidOptions.isEmpty(), "Expected at least one valid bid option");

    // Optionally, check if a specific expected bid is present
    boolean containsExpected =
        bidOptions.stream()
            .anyMatch(
                bid -> !bid.isNo() && bid.getType() == BidType.DOWNTOWN && bid.getValue() >= 4);
    assertTrue(containsExpected, "Expected a NO TRUMP bid with value 6 or more");
  }

  @Test
  public void maxUptownStrengthHand_isValidEvaluation() {
    aiPlayer.setHand(testHands.getMaxUptownStrengthHand());
    HandEvaluator evaluator = new HandEvaluator(aiPlayer);
    evaluator.evaluateHand();

    List<FinalBid> bidOptions = evaluator.evaluateAll(aiPlayer.getPosition());

    assertNotNull(bidOptions, "Bid options should not be null");
    assertFalse(bidOptions.isEmpty(), "Expected at least one valid bid option");

    // Optionally, check if a specific expected bid is present
    boolean containsExpected =
        bidOptions.stream()
            .anyMatch(bid -> !bid.isNo() && bid.getType() == BidType.UPTOWN && bid.getValue() == 7);
    assertTrue(containsExpected, "Expected a NO TRUMP bid with value 6 or more");
  }

  @Test
  public void unbridgeableGapsHand_isValidEvaluation() {
    aiPlayer.setHand(testHands.getUnbridgeableGapsHand());
    HandEvaluator evaluator = new HandEvaluator(aiPlayer);
    evaluator.evaluateHand();

    List<FinalBid> bidOptions = evaluator.evaluateAll(aiPlayer.getPosition());

    assertNotNull(bidOptions, "Bid options should not be null");
    assertTrue(bidOptions.isEmpty(), "Expected at least one valid bid option");
  }
}
