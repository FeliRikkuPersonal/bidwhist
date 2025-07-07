package com.bidwhist.model;

import java.util.ArrayList;
/* Books will be holders of 4 cards. These are the cards played during
 * a round. Once all cards are played, the book will be evaluated to 
 * decide who won the book.
 */
import java.util.List;

public class Book {
    public List<PlayedCard> cards = new ArrayList<>();

    public Book(List<PlayedCard> cards) {
        this.cards = cards;
    }
    
    public List<PlayedCard> getCards() {
        return cards;
    }
}

