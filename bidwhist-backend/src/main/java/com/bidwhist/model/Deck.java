package com.bidwhist.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.bidwhist.utils.JokerUtils;

public class Deck {

    private final List<Card> cards;
    private Kitty kitty;

    public Deck() {
        this.cards = new ArrayList<>();
        buildStandardDeck();
        this.kitty = new Kitty();       // create empty kitty
    }

    private void buildStandardDeck() {
        // Add 52 normal cards (non-jokers)
        for (Suit suit : Suit.values()) {
            for (Rank rank : Rank.values()) {
                if (!JokerUtils.isJokerRank(rank)) {
                    cards.add(new Card(suit, rank));
                }
            }
        }

        // Add Jokers
        cards.add(new Card(null, Rank.JOKER_S));
        cards.add(new Card(null, Rank.JOKER_B));

        for (Card card : cards) {
            System.out.println(card.getCardImage());
        }
    }

    public void shuffle() {
        Collections.shuffle(cards);
    }

    public void deal(List<Player> players) {
        for (int i = 0; i < 48; i++) {
            if (i % 4 == 0) {
                players.get(0).addCard(cards.get(i));
            }
            else if (i % 4 == 1) {
                players.get(1).addCard(cards.get(i));
            }
            else if (i % 4 == 2) {
                players.get(2).addCard(cards.get(i));
            }
            else {
                players.get(3).addCard(cards.get(i));
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

    public void resetJokerSuits() {
        for (Card card : cards) {
            card.clearSuit(); // only affects jokers
        }
    }

    // Call to Card.assignSuit() for jokers only
    public void assignTrumpSuitToJokers(Suit trump) {
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

