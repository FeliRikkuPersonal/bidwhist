// src/main/java/com/bidwhist/model/RoomStatus.java

package com.bidwhist.model;

public enum RoomStatus {
    WAITING_FOR_PLAYERS,   // Room created, not enough players yet
    READY,                 // Required number of players joined, ready to start
    IN_PROGRESS,           // Game has started
    COMPLETED,             // Game ended successfully
    CANCELLED              // Room manually closed or abandoned before start
}
