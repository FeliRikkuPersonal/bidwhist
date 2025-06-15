package com.bidwhist.bidwhist_backend.model;

import java.util.ArrayList;
import java.util.List;

public class Hand {
    public Deck deck;
    public List<Card> cards = new ArrayList<>();

    public Hand(Deck deck) {
        this.deck = deck;
    }
    
    // Used for adding from Kitty
    public Card addCard(Card card) {
        cards.add(card);
        return card;
    }

    // Used for initial population of hand
    public List<Card> addCards(List<Card> newCards) {
        cards.addAll(newCards);
        return newCards;
    }

    public Card transferToKKitty(Card card) {
        deck.getKitty().addCard(card);
        cards.remove(card);
        return card;
    }

    public int size() {
        return cards.size();
    }
}
