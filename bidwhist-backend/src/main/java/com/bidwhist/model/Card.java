// src/main/java/com/bidwhist/model/Card.java

package com.bidwhist.model;

import com.bidwhist.dto.CardVisibility;
import com.bidwhist.utils.JokerUtils;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"rank", "suit"})
public class Card implements Comparable<Card> {
  private Rank rank;
  private Suit suit;
  private String cardImage;
  private CardVisibility visibility;

  /*
   * Constructs a card with the specified suit and rank.
   * Jokers may have null suits; image and visibility are initialized.
   */
  public Card(Suit suit, Rank rank) {
    this.rank = rank;
    this.suit = suit;
    this.cardImage = this.getCardImage();
    this.visibility = CardVisibility.HIDDEN;
  }

  /*
   * Assigns a suit to this card if it's a Joker.
   * Used to align Jokers with the winning bid's trump suit.
   */
  public void assignSuit(Suit suit) {
    if (JokerUtils.isJokerRank(rank)) {
      this.suit = suit;
    }
  }

  /*
   * Clears the assigned suit of this card if it's a Joker.
   * Called after a game ends to reset Jokers.
   */
  public void clearSuit() {
    if (JokerUtils.isJokerRank(rank)) {
      this.suit = null;
    }
  }

  /*
   * Checks if card is a Joker.
   */
  public boolean isJoker() {
    return JokerUtils.isJokerRank(this.rank);
  }

  public void setSuit(Suit suit) {
    this.suit = suit;
  }

  /*
   * Compares cards by suit and rank for sorting.
   * Jokers are always sorted after non-jokers.
   */
  @Override
  public int compareTo(Card other) {
    if (this.suit == null && other.suit != null) return 1;
    if (this.suit != null && other.suit == null) return -1;
    if (this.suit == null && other.suit == null) {
      return this.rank.compareTo(other.rank);
    }

    int suitCompare = this.suit.compareTo(other.suit);
    return (suitCompare != 0) ? suitCompare : this.rank.compareTo(other.rank);
  }

  /*
   * Compares two cards for equality based on rank and suit.
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Card card = (Card) o;
    return rank == card.rank && suit == card.suit;
  }

  /*
   * Returns the suit of this card.
   */
  public Suit getSuit() {
    return suit;
  }

  /*
   * Returns the rank of this card.
   */
  public Rank getRank() {
    return rank;
  }

  /*
   * Returns the visibility state of this card.
   */
  public CardVisibility getVisibility() {
    return visibility;
  }

  /*
   * Updates the card's visibility.
   */
  public void setVisibility(CardVisibility newVisibility) {
    this.visibility = newVisibility;
  }

  /*
   * Generates the appropriate image filename based on card attributes.
   */
  public String getCardImage() {
    if (this.rank == null) {
      return "Deck_Back.png";
    } else if (this.rank == Rank.JOKER_B || this.rank == Rank.JOKER_S) {
      return rank.toString().toLowerCase() + ".png";
    } else if (this.rank.getValue() == 11) {
      return suit.toString().toLowerCase() + "_jack.png";
    } else if (this.rank.getValue() == 12) {
      return suit.toString().toLowerCase() + "_queen.png";
    } else if (this.rank.getValue() == 13) {
      return suit.toString().toLowerCase() + "_king.png";
    } else if (this.rank.getValue() == 14) {
      return suit.toString().toLowerCase() + "_ace.png";
    } else {
      return suit.toString().toLowerCase() + "_" + rank.getValue() + ".png";
    }
  }

  /*
   * Returns a string representation of this card (e.g., "Ace of Spades").
   */
  @Override
  public String toString() {
    return rank + "of" + suit;
  }
}
