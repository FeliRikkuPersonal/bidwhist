// src/main/java/com/bidwhist/dto/FinalBidRequest.java

package com.bidwhist.dto;

import com.bidwhist.bidding.BidType;
import com.bidwhist.model.PlayerPos;
import com.bidwhist.model.Suit;

/*
 * DTO used to submit the final bid decision from a player after the bidding phase.
 * Includes the player's position, chosen bid type (e.g., Uptown, Downtown, No Trump),
 * and optionally the suit if applicable.
 */
public class FinalBidRequest {
  private String gameId;
  private PlayerPos player;
  private BidType type;
  private Suit suit;

  public FinalBidRequest() {}

  public FinalBidRequest(String gameId, PlayerPos player, BidType bidtype, Suit suit) {
    this.gameId = gameId;
    this.player = player;
    this.type = bidtype;
    this.suit = suit;
  }

  public PlayerPos getPlayer() {
    return player;
  }

  public void setPlayer(PlayerPos player) {
    this.player = player;
  }

  public BidType getType() {
    return type;
  }

  public void setType(BidType type) {
    this.type = type;
  }

  public Suit getSuit() {
    return suit;
  }

  public void setSuit(Suit suit) {
    this.suit = suit;
  }

  public String getGameId() {
    return gameId;
  }
}
