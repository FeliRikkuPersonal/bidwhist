// src/test/java/com/bidwhist/util/TestCardUtil.java

package com.bidwhist.testUtils;

import com.bidwhist.model.Card;
import com.bidwhist.model.Rank;
import com.bidwhist.model.Suit;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility for testing: generates one instance of every card (including jokers).
 * Allows lookup using human-readable keys like "Ace_of_Hearts", "JOKER_B", etc.
 */
public class TestCardUtil {

    private static final Map<String, Card> allCards = new HashMap<>();

    static {
        // Create standard 52 cards
        for (Suit suit : Suit.values()) {
            for (Rank rank : Rank.values()) {
                if (!rank.name().startsWith("JOKER")) {
                    String key = rank.name() + "_of_" + suit.name();
                    allCards.put(key, new Card(suit, rank));
                }
            }
        }

        // Add Jokers separately (suit is null initially)
        allCards.put("JOKER_B", new Card(null, Rank.JOKER_B));
        allCards.put("JOKER_S", new Card(null, Rank.JOKER_S));
    }

    /**
     * Returns the requested Card object.
     * Example: getCard("Ace_of_Hearts") or getCard("JOKER_B")
     *
     * @param name human-readable key (e.g. "ACE_of_HEARTS", "JOKER_S")
     * @return Card instance or null if not found
     */
    public static Card getCard(String name) {
        return allCards.get(name);
    }

    /**
     * Returns the entire deck map (useful for iteration).
     */
    public static Map<String, Card> getAllCards() {
        return allCards;
    }
}
