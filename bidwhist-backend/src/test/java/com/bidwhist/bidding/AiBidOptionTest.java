// src/test/java/com/bidwhist/bidding/AiBidOptionTest.java

package com.bidwhist.bidding;

import com.bidwhist.model.PlayerPos;
import com.bidwhist.model.Suit;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AiBidOptionTest {

    private final PlayerPos player = PlayerPos.P1;

    @Test
    void returnsNullForStrengthBelowFour_Uptown() {
        SuitEvaluation eval = new SuitEvaluation(Suit.CLUBS, 3, 5, 4);
        FinalBid bid = AiBidOption.fromEvaluation(player, eval, BidType.UPTOWN, false);
        assertNull(bid, "Should return null for uptown strength below 4");
    }

    @Test
    void returnsNullForStrengthBelowFour_Downtown() {
        SuitEvaluation eval = new SuitEvaluation(Suit.DIAMONDS, 6, 2, 4);
        FinalBid bid = AiBidOption.fromEvaluation(player, eval, BidType.DOWNTOWN, false);
        assertNull(bid, "Should return null for downtown strength below 4");
    }

    @Test
    void returnsNullForUnsupportedBidType() {
        SuitEvaluation eval = new SuitEvaluation(Suit.HEARTS, 5, 5, 4);
        FinalBid bid = AiBidOption.fromEvaluation(player, eval, BidType.NO_TRUMP, false);
        assertNull(bid, "Should return null for NO_TRUMP bid type");
    }

    @Test
    void capsStrengthToSevenWhenAboveSeven() {
        SuitEvaluation eval = new SuitEvaluation(Suit.SPADES, 10, 3, 4);
        FinalBid bid = AiBidOption.fromEvaluation(player, eval, BidType.UPTOWN, false);
        assertNotNull(bid, "Should not return null when strength is above 7");
        assertEquals(7, bid.getValue(), "Bid value should be capped at 7");
    }

    @Test
    void createsValidFinalBidWithExactStrength() {
        SuitEvaluation eval = new SuitEvaluation(Suit.HEARTS, 5, 4, 4);
        FinalBid bid = AiBidOption.fromEvaluation(player, eval, BidType.UPTOWN, false);
        assertNotNull(bid);
        assertEquals(5, bid.getValue());
        assertEquals(BidType.UPTOWN, bid.getType());
        assertEquals(Suit.HEARTS, bid.getSuit());
        assertFalse(bid.isNo());
        assertEquals(player, bid.getPlayer());
    }

    @Test
    void handlesNoTrumpFlagCorrectly() {
        SuitEvaluation eval = new SuitEvaluation(Suit.SPADES, 6, 6, 4);
        FinalBid bid = AiBidOption.fromEvaluation(player, eval, BidType.DOWNTOWN, true);
        assertNotNull(bid);
        assertTrue(bid.isNo());
        assertEquals(BidType.DOWNTOWN, bid.getType());
        assertEquals(Suit.SPADES, bid.getSuit());
    }
}
