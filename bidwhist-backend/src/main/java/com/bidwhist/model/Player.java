// src/main/java/com/bidwhist/model/Player.java

package com.bidwhist.model;

public class Player {
  private String name;
  private Hand hand;
  private final boolean isAI;
  private boolean isDealer;
  private final PlayerPos position;
  private final Team team;

  public Player(String name, boolean isAI, PlayerPos position, Team team) {
    this.name = name;
    this.isAI = isAI;
    this.position = position;
    this.team = team;
    this.hand = new Hand();
  }

  /* Add a card to the player's hand */
  public void addCard(Card card) {
    this.hand.addCard(card);
  }

  /* Remove a card from the player's hand */
  public void removeCard(Card card) {
    this.hand.removeCard(card);
  }

  public boolean isAI() {
    return isAI;
  }

  public boolean isDealer() {
    return isDealer;
  }

  public String getName() {
    return name;
  }

  public PlayerPos getPosition() {
    return position;
  }

  public Team getTeam() {
    return team;
  }

  public Hand getHand() {
    return hand;
  }

  public void setHand(Hand hand) {
    this.hand = hand;
  }

  public void setName(String name) {
    this.name = name;
  }
}
