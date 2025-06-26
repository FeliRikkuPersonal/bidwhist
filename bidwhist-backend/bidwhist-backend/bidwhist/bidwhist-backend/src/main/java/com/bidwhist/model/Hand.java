package com.bidwhist.model;

import java.util.ArrayList;
import java.util.List;

public class Hand {
    public List<Card> cards = new ArrayList<>();

    // Not currently used
    public Card addCard(Card card) {
        cards.add(card);

        //replace with animation signal
        return card;
    }

    // Used for initial population of hand and adding kitty
    public List<Card> addCards(List<Card> newCards) {
        cards.addAll(newCards);

        //replace with animation signal
        return newCards;
    }

    public Card removeCard(Card card) {
        cards.remove(card);

        //replace with animation signal
        return card;
    }

    public List<Card> transferToKitty(Kitty kitty, List<Card> discardedCards) {
        if (discardedCards.size() != 6) {
            throw new IllegalStateException("Must discard 6 cards.");
        }
        kitty.receiveCardsFromHand(discardedCards);
        cards.removeAll(discardedCards);

        //replace with animation signal
        return discardedCards;
    }

    public int size() {
        return cards.size();
    }

    public List<Card> getCards() {
        return cards;
    }
}
