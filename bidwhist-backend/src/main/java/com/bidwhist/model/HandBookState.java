package com.bidwhist.model;

public enum HandBookState {
    LEAD_HAND,        // Book is empty → player is leading the round
    ELIGIBLE_HAND,    // Player has cards matching lead suit or trump
    INELIGIBLE_HAND   // Player has no matching cards → can play anything
}
