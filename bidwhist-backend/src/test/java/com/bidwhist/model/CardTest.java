// src/main/java/com/bidwhist/model/CardTest.java

package com.bidwhist.model;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"rank", "suit"})
public class CardTest {

  @Test
  void testAssigningSuitToJokers() {
    Card jokerSmall = new Card(null, Rank.JOKER_S);
    Card jokerBig = new Card(null, Rank.JOKER_B);
    jokerSmall.assignSuit(Suit.HEARTS);
    jokerBig.assignSuit(Suit.CLUBS);
    assertTrue(jokerSmall.getSuit() == Suit.HEARTS);
    assertTrue(jokerBig.getSuit() == Suit.CLUBS);
  }

  @Test
  void testClearingJokerSuit() {
    Card jokerSmall = new Card(Suit.SPADES, Rank.JOKER_S);
    Card jokerBig = new Card(Suit.DIAMONDS, Rank.JOKER_B);
    Card twoOfClubs = new Card(Suit.CLUBS, Rank.TWO);
    jokerSmall.clearSuit();
    jokerBig.clearSuit();
    twoOfClubs.clearSuit();
    assertNull(jokerSmall.getSuit());
    assertNull(jokerBig.getSuit());
    assertNotNull(twoOfClubs.getSuit());
  }

  @Test
  void testCompareToMethod() {
    Card jokerSmall = new Card(null, Rank.JOKER_S);
    Card jokerBig = new Card(null, Rank.JOKER_B);
    Card twoOfClubs = new Card(Suit.CLUBS, Rank.TWO);
    Card tenOfClubs = new Card(Suit.CLUBS, Rank.TEN);
    Card twoOfHearts = new Card(Suit.HEARTS, Rank.TWO);

System.out.println("twoOfClubs suit: " + twoOfClubs.getSuit());
System.out.println("tenOfClubs suit: " + tenOfClubs.getSuit());
System.out.println("suit compare: " + twoOfClubs.getSuit().compareTo(tenOfClubs.getSuit()));
System.out.println("rank compare: " + twoOfClubs.getRank().compareTo(tenOfClubs.getRank()));
System.out.println("final result: " + twoOfClubs.compareTo(tenOfClubs));

    assertTrue(jokerSmall.compareTo(jokerBig) < 0);
    assertTrue(jokerSmall.compareTo(twoOfHearts) > 0);
    assertTrue(twoOfClubs.compareTo(tenOfClubs) < 0 );
    assertTrue(twoOfHearts.compareTo(tenOfClubs) < 0);
  }

}