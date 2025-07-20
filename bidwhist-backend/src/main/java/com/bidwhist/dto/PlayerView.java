// src/main/java/com/bidwhist/dto/PlayerView.java

package com.bidwhist.dto;

import com.bidwhist.model.Card;
import com.bidwhist.model.PlayerPos;
import com.bidwhist.model.Team;
import java.util.List;

/**
 * Represents the public-facing view of a player used for UI or client display.
 *
 * This DTO contains key details such as player name, position, team, whether the player is an AI,
 * and the list of cards in their hand. It is intended for sending sanitized game state info
 * to the frontend during gameplay or updates.
 */
public class PlayerView {
  private final String name;
  private final PlayerPos position;
  private final Team team;
  private final boolean isAI;
  private final List<Card> hand;

  public PlayerView(String name, PlayerPos position, Team team, boolean isAI, List<Card> hand) {
    this.name = name;
    this.position = position;
    this.team = team;
    this.isAI = isAI;
    this.hand = hand;
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

  public boolean isAI() {
    return isAI;
  }

  public List<Card> getHand() {
    return hand;
  }
}
