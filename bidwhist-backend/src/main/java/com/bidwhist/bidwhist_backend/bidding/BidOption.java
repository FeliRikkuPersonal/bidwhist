package com.bidwhist.bidwhist_backend.bidding;

/* Takes suit evaluation and provides ability to bid High, Low, or No 
*  and 4, 5, 6, or 7 based on strength. 
*/

public class BidOption {
    public int strength;
    public String type;
    public String mode;
    public boolean isNo;

    public BidOption(int strength, String type, String mode, boolean isNo) {
        this.strength = strength;
        this.type = type;
        this.mode = mode;
        this.isNo = isNo;
    }

    public String toLabel() {
        return switch (strength) {
            case 9  -> "4" + (isNo ? "No" : "");
            case 10 -> "5" + (isNo ? "No" : "");
            case 11 -> "6" + (isNo ? "No" : "");
            case 12 -> "7" + (isNo ? "No" : "");
            default -> null;
        };
    }
}