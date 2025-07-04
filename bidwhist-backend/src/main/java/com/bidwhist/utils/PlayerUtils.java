package com.bidwhist.utils;

import java.util.List;

import com.bidwhist.model.PlayerPos;
import com.bidwhist.model.Player;

public class PlayerUtils {

    public static String getNameByPosition(PlayerPos playerPosition, List<Player> players) {
        return players.stream()
                .filter(p -> p.getPosition() == playerPosition)
                .map(Player::getName)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No player found at position: " + playerPosition));
    }

    public static PlayerPos getPositionByName(String playerName, List<Player> players) {
        return players.stream()
                .filter(p -> p.getName().equals(playerName))
                .map(Player::getPosition)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No player found named " + playerName));
    }

}
