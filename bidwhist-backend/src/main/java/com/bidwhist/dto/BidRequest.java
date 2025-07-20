// src/main/java/com/bidwhist/dto/BidRequest.java

package com.bidwhist.dto;

import com.bidwhist.bidding.BidType;
import com.bidwhist.bidding.InitialBid;
import com.bidwhist.model.Player;
import com.bidwhist.model.PlayerPos;
import com.bidwhist.model.Suit;

/*
 * DTO representing a player's bid submission from client to server.
 * Includes bid value, bid type (Uptown, Downtown, No), optional suit, and source player.
 */
public class BidRequest {
  private String gameId;
  private PlayerPos player;
  private int value;
  private BidType type;
  private boolean isNo;
  private Suit suit;

  public BidRequest() {}

  public BidRequest(String gameId, PlayerPos player, int value, boolean isNo) {
    this.gameId = gameId;
    this.player = player;
    this.value = value;
    this.isNo = isNo;
  }

  /*
   * Converts a BidRequest into an InitialBid object.
   * If bid value is 0, this is treated as a pass.
   */
  public static InitialBid fromRequest(BidRequest req, Player bidder) {
    if (req.getValue() != 0 && (req.getValue() < 4 || req.getValue() > 7)) {
      throw new IllegalArgumentException("Bid value must be between 4 and 7.");
    }
    if (req.getValue() == 0) {
      return InitialBid.pass(bidder.getPosition());
    }

    return new InitialBid(req.getPlayer(), req.getValue(), req.isNo());
  }

  public String getGameId() {
    return gameId;
  }

  public PlayerPos getPlayer() {
    return player;
  }

  public void setPlayer(PlayerPos player) {
    this.player = player;
  }

  public int getValue() {
    return value;
  }

  public void setValue(int value) {
    this.value = value;
  }

  public BidType getType() {
    return type;
  }

  public void setType(BidType type) {
    this.type = type;
  }

  public boolean isNo() {
    return isNo;
  }

  public void setNo(boolean isNo) {
    this.isNo = isNo;
  }

  public Suit getSuit() {
    return suit;
  }

  public void setSuit(Suit suit) {
    this.suit = suit;
  }

  public boolean isPass() {
    return value == 0;
  }
}
