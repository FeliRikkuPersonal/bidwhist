package com.bidwhist.service;


import org.springframework.stereotype.Service;

import com.bidwhist.model.Deck;

@Service
public class DeckService {
    public static Deck createNewDeck() {
        Deck deck = new Deck();
        deck.shuffle();
        return deck;
    }
}