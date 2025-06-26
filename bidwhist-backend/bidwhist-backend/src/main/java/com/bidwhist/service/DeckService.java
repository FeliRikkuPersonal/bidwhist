package com.bidwhist.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.bidwhist.model.Card;
import com.bidwhist.model.Rank;
import com.bidwhist.model.Suit;

public class DeckService {

    public static List<Card> createShuffledDeck() {
        List<Card> deck = new ArrayList<>();

        // Add standard 52 cards
        for (Suit suit : Suit.values()) {
            if (suit.isStandardSuit()) {
                for (Rank rank : Rank.values()) {
                    if (rank != Rank.JOKER) {
                        deck.add(new Card(suit, rank));
                    }
                }
            }
        }

        // Add 2 jokers
        deck.add(new Card(Suit.RED_JOKER, Rank.JOKER));
        deck.add(new Card(Suit.BLACK_JOKER, Rank.JOKER));

        Collections.shuffle(deck);
        return deck;
    }
}
