// src/main/java/com/bidwhist/utils/PlayerUtils.java

package com.bidwhist.utils;

import com.bidwhist.model.Player;
import com.bidwhist.model.PlayerPos;
import java.util.List;

public class PlayerUtils {

  /*
   *
   * Returns the name of the player at the given position.
   * Throws an exception if no player exists at that position.
   *
   */
  public static String getNameByPosition(PlayerPos playerPosition, List<Player> players) {
    return players.stream()
        .filter(p -> p.getPosition() == playerPosition)
        .map(Player::getName)
        .findFirst()
        .orElseThrow(
            () -> new IllegalArgumentException("No player found at position: " + playerPosition));
  }

  /*
   *
   * Returns the position of the player with the given name.
   * Throws an exception if no player matches that name.
   *
   */
  public static PlayerPos getPositionByName(String playerName, List<Player> players) {
    return players.stream()
        .filter(p -> p.getName().equals(playerName))
        .map(Player::getPosition)
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("No player found named " + playerName));
  }

  /*
   *
   * Returns the full Player object for the given position.
   * Throws an exception if the player cannot be found.
   *
   */
  public static Player getPlayerByPosition(PlayerPos playerPosition, List<Player> players) {
    return players.stream()
        .filter(p -> p.getPosition() == playerPosition)
        .findFirst()
        .orElseThrow(
            () -> new IllegalArgumentException("No player found at position: " + playerPosition));
  }
}
