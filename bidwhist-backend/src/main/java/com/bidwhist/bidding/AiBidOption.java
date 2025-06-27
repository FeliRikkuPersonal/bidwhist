package com.bidwhist.bidding;

import com.bidwhist.model.PlayerPos;

/**
 * AiBidOption is a utility class that helps convert evaluated suit data into Bid objects,
 * based on the given direction (Uptown/Down) and "No" status.
 */
public class AiBidOption {

    /**
     * Converts a SuitEvaluation + type + isNo flag into a Bid object, or null if strength is too low.
     *
     * @param player The player making the bid (typically an AI).
     * @param eval   The SuitEvaluation result.
     * @param type   The direction of the bid: UPTOWN or DOWNTOWN.
     * @param isNo   Whether this is a "No" bid.
     * @return A valid Bid or null if the strength is below the legal bidding threshold.
     */
    public static FinalBid fromEvaluation(
            PlayerPos player,
            SuitEvaluation eval,
            BidType type,
            boolean isNo
    ) {
        int strength;

        if (type == BidType.UPTOWN) {
            strength = eval.getUptownStrength();
        } else if (type == BidType.DOWNTOWN) {
            strength = eval.getDowntownStrength();
        } else {
            return null; // Not valid for suit-based evaluation
        }

        if (strength < 9 || strength > 12) {
            return null; // Too weak — interpreted as a pass
        }

        int value = strength - 5;
        return new FinalBid(player, value, isNo, false, type, eval.getSuit());
    }
}
