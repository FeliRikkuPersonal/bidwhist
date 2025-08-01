// src/main/java/com/bidwhist/model/GameRoomTest.java

package com.bidwhist.model;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class GameRoomTest {

  @Test
  void testAddingPlayersToGameRoom() {
    GameRoom room = new GameRoom("gameId");
    room.addPlayer(new Player("player1", false, PlayerPos.P1, Team.A));
    room.addPlayer("player2");
    room.addPlayer("player3");
    assertTrue(room.getStatus() == RoomStatus.WAITING_FOR_PLAYERS);
    room.addPlayer("player4");
    assertTrue(room.isFull());
    assertTrue(room.getStatus() == RoomStatus.READY);
    assertTrue(room.getPlayerPositionByName("player1") == PlayerPos.P1);
    assertTrue(room.getPlayerPositionByName("player2") == PlayerPos.P2);
    assertTrue(room.getPlayerPositionByName("player3") == PlayerPos.P3);
    assertTrue(room.getPlayerPositionByName("player4") == PlayerPos.P4);
  }
}
