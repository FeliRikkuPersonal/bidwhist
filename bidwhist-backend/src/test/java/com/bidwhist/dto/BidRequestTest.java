// src/main/java/com/bidwhist/dto/BidRequestTest.java

package com.bidwhist.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.bidwhist.bidding.InitialBid;
import com.bidwhist.model.Player;
import com.bidwhist.model.PlayerPos;
import com.bidwhist.model.Team;

public class BidRequestTest {
  
    @Test
    void testGetInitialBidFromRequest() {
        Player player = new Player("name", false, PlayerPos.P1, Team.A);
        BidRequest request = new BidRequest("gameId", PlayerPos.P1, 4, false);
        InitialBid initBid = BidRequest.fromRequest(request, player);
        assertEquals(initBid.getClass(), InitialBid.class);
        assertTrue(initBid.getValue() == 4);
        assertFalse(initBid.isNo());
    }

}
