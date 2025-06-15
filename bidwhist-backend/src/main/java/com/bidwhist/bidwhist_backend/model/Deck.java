package com.bidwhist.bidwhist_backend.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Deck {
    private final List<Card> cards;
    private final List<Card> kitty = new ArrayList<>();
    private final List<Card> player1Hand = new ArrayList<>();
    private final List<Card> player2Hand = new ArrayList<>();
    private final List<Card> player3Hand = new ArrayList<>();
    private final List<Card> player4Hand = new ArrayList<>();

    private List<Card> player1Winnings = new ArrayList<>();
    private List<Card> player2Winnings = new ArrayList<>();
    private List<Card> player3Winnings = new ArrayList<>();
    private List<Card> player4Winnings = new ArrayList<>();

    public Deck() {
        this.cards = new ArrayList<>();
        buildStandardDeck();
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

    public void deal() {
        for (int i = 0; i < 48; i++) {
            if (i % 4 == 0) player1Hand.add(cards.get(i));
            else if (i % 4 == 1) player2Hand.add(cards.get(i));
            else if (i % 4 == 2) player3Hand.add(cards.get(i));
            else player4Hand.add(cards.get(i));
        }

        for (int i = 48; i < 54; i++) {
            kitty.add(cards.get(i));
        }
    }

    private boolean isJokerRank(Card.Rank rank) {
        return rank == Card.Rank.JOKER_S || rank == Card.Rank.JOKER_L;
    }

    public void resetJokerSuits() {
        for (Card card : cards) {
            card.clearSuit(); // only affects jokers
        }
    }

    public void assignTrumpSuitToJokers(Card.Suit trump) {
        for (Card card : cards) {
            card.assignSuit(trump); // only affects jokers
        }
    }

    public List<Card> getCards() {
        return cards;
    }
}

