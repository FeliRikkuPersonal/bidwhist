package com.bidwhist.utils;

import java.util.List;

import com.bidwhist.model.PlayerPos;
import com.bidwhist.model.Player;

public class PlayerUtils {

    public static String getNameByPosition(PlayerPos pos, List<Player> players) {
        return players.stream()
            .filter(p -> p.getPosition() == pos)
            .map(Player::getName)
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("No player found at position: " + pos));
    }
}

