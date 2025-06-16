package com.bidwhist.model;

import java.util.ArrayList;
import java.util.List;

public class Kitty {
    public enum State {
        UNCLAIMED, CLAIMED, DISCARDED
    }
    public List<Card> cards;
    public State state;

    public Kitty() {
        this.cards = new ArrayList<>();
        this.state = State.UNCLAIMED;
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
        setState(State.CLAIMED);


        // replace with animation signal
        return transferred;
    }

    public List<Card> receiveCardsFromHand(List<Card> newCards) {
        if (newCards.size() != 6) {
            throw new IllegalArgumentException("You must discard exactly 6 cards to the kitty.");
        }
        for (Card card : newCards) {
            cards.add(card);
        }

        setState(State.DISCARDED);

        // replace with animation signal
        return cards;
    }

    public int size() {
        return cards.size();
    }

    public boolean isFull() {
        return cards.size() == 6;
    }

    public void setState(State state) {
        this.state = state;
    }
}
