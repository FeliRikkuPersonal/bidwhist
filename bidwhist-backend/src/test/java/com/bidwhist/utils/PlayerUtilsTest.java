// src/test/java/com/bidwhist/utils/PlayerUtilsTest.java

package com.bidwhist.utils;

import static org.junit.jupiter.api.Assertions.*;

import com.bidwhist.model.Player;
import com.bidwhist.model.PlayerPos;
import com.bidwhist.model.Team;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PlayerUtilsTest {

  private List<Player> players;

  @BeforeEach
  void setup() {
    players =
        List.of(
            new Player("Alice", false, PlayerPos.P1, Team.A),
            new Player("Bob", true, PlayerPos.P2, Team.B),
            new Player("Carol", true, PlayerPos.P3, Team.A),
            new Player("Dave", false, PlayerPos.P4, Team.B));
  }

  /* ---------------- getNameByPosition ---------------- */

  @Test
  void testGetNameByPosition_valid() {
    String name = PlayerUtils.getNameByPosition(PlayerPos.P2, players);
    assertEquals("Bob", name);
  }

  @Test
  void testGetNameByPosition_notFound_throwsException() {
    List<Player> fewerPlayers = players.subList(0, 2); // only P1 and P2

    Exception exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> PlayerUtils.getNameByPosition(PlayerPos.P4, fewerPlayers));

    assertTrue(exception.getMessage().contains("No player found at position"));
  }

  /* ---------------- getPositionByName ---------------- */

  @Test
  void testGetPositionByName_valid() {
    PlayerPos pos = PlayerUtils.getPositionByName("Carol", players);
    assertEquals(PlayerPos.P3, pos);
  }

  @Test
  void testGetPositionByName_notFound_throwsException() {
    Exception exception =
        assertThrows(
            IllegalArgumentException.class, () -> PlayerUtils.getPositionByName("Eve", players));

    assertTrue(exception.getMessage().contains("No player found named"));
  }

  /* ---------------- getPlayerByPosition ---------------- */

  @Test
  void testGetPlayerByPosition_valid() {
    Player player = PlayerUtils.getPlayerByPosition(PlayerPos.P4, players);
    assertEquals("Dave", player.getName());
    assertEquals(Team.B, player.getTeam());
  }

  @Test
  void testGetPlayerByPosition_notFound_throwsException() {
    List<Player> fewerPlayers = players.subList(0, 3); // only P1–P3

    Exception exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> PlayerUtils.getPlayerByPosition(PlayerPos.P4, fewerPlayers));

    assertTrue(exception.getMessage().contains("No player found at position"));
  }
}
