// src/main/java/com/bidwhist/utils/CardUtils.java

package com.bidwhist.utils;

import java.util.List;

import com.bidwhist.model.Card;
import com.bidwhist.model.Rank;
import com.bidwhist.model.Suit;

public class CardUtils {

  /*
   *
   * Checks if two cards are equal by comparing rank and suit.
   * Safely handles Jokers and null suits.
   *
   */
  public static boolean cardsMatch(Card c1, Card c2) {
    if (c1 == null || c2 == null)
      return false;

    boolean ranksMatch = c1.getRank() != null && c1.getRank().equals(c2.getRank());

    boolean suitsMatch = (c1.getSuit() == null && c2.getSuit() == null)
        || (c1.getSuit() != null && c1.getSuit().equals(c2.getSuit()));

    return ranksMatch && suitsMatch;
  }

  /**
   * Checks whether all higher-ranked cards in the same suit have already been
   * played. This helps AI determine if a card is now the highest in play.
   *
   * @param candidate   the card being considered
   * @param playedCards list of cards that have already been played
   * @return true if no higher-ranked card in the same suit remains unplayed
   */
  public static boolean allHigherTrumpCardsPlayed(Card card, List<Card> playedCards, Suit trumpSuit) {
    if (card == null || card.getSuit() == null || card.getRank() == null || trumpSuit == null) {
      return false;
    }

    if (!card.getSuit().equals(trumpSuit)) {
      return false;
    }

    int candidateValue = card.getRank().getValue();
    return Rank.getOrderedRanks().stream()
        .map(Rank::getValue)
        .filter(v -> v > candidateValue)
        .allMatch(v -> playedCards.stream().anyMatch(
            c -> trumpSuit.equals(c.getSuit()) && c.getRank().getValue() == v));
  }

}
