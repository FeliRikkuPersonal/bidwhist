package com.bidwhist.bidwhist_backend.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Deck {
    public enum PlayerPos {
        P1, P2, P3, P4;
    }

    private final List<Card> cards;
    private Kitty kitty;
    private Map<PlayerPos, Hand> hands;

    public Deck() {
        this.cards = new ArrayList<>();
        buildStandardDeck();
        this.kitty = new Kitty();       // create empty kitty
        createHands();
    }

    private void buildStandardDeck() {
        // Add normal cards (non-jokers)
        for (Card.Suit suit : Card.Suit.values()) {
            for (Card.Rank rank : Card.Rank.values()) {
                if (!isJokerRank(rank)) {
                    cards.add(new Card(suit, rank));
                }
            }
        }

        // Add Jokers
        cards.add(new Card(null, Card.Rank.JOKER_S));
        cards.add(new Card(null, Card.Rank.JOKER_L));
    }

    public void shuffle() {
        Collections.shuffle(cards);
    }

    // Create empty hands
    private void createHands() {
        for (PlayerPos pos : PlayerPos.values()) {
            hands.put(pos, new Hand(this));
        }
    }

    public void deal() {
        for (int i = 0; i < 48; i++) {
            if (i % 4 == 0) {
                hands.get(PlayerPos.P1).addCard(cards.get(i));
            }
            else if (i % 4 == 1) {
                hands.get(PlayerPos.P2).addCard(cards.get(i));
            }
            else if (i % 4 == 2) {
                hands.get(PlayerPos.P3).addCard(cards.get(i));
            }
            else {
                hands.get(PlayerPos.P4).addCard(cards.get(i));
            }
        }

        for (int i = 48; i < 54; i++) {
            kitty.addCard(cards.get(i));
        }
    }

    /* place holder for method to assigning hand to player.
    *  private Hand assignHand(Hand hand) {
    *       -some text here-
    *       return hand;
    *  }
    */ 

    private boolean isJokerRank(Card.Rank rank) {
        return rank == Card.Rank.JOKER_S || rank == Card.Rank.JOKER_L;
    }

    public void resetJokerSuits() {
        for (Card card : cards) {
            card.clearSuit(); // only affects jokers
        }
    }

    // Call to Card.assignSuit() for jokers only
    public void assignTrumpSuitToJokers(Card.Suit trump) {
        for (Card card : cards) {
            card.assignSuit(trump); // only affects jokers
        }
    }

    public List<Card> getCards() {
        return cards;
    }

    public Kitty getKitty() {
        return kitty;
    }
}

