package com.bidwhist.bidwhist_backend.model;

import java.util.ArrayList;
import java.util.List;

public class Kitty {
    public List<Card> cards;

    public Kitty() {
        this.cards = new ArrayList<>();
    }

    public void addCard(Card card) {
        if (cards.size() >= 6) {
            throw new IllegalStateException("Kitty already has 6 cards.");
        }
        cards.add(card);
    }

    public List<Card> getCards() {
        return new ArrayList<>(cards);
    }

    public void clear() {
        cards.clear();
    }

    /* Assuming we transfer all cards in kitty to player who won bid.
     * Player will then transfer cards back to the kitty until kitty
     * is back at 6 cards.
     */
    public List<Card> transferToHand(Hand hand) {
        hand.addCards(cards);
        List<Card> transferred = new ArrayList<>(cards);
        clear();
        return transferred;
    }

    public int size() {
        return cards.size();
    }

    public boolean isFull() {
        return cards.size() == 6;
    }
}
