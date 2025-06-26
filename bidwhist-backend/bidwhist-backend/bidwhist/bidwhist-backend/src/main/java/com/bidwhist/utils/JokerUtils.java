package com.bidwhist.utils;

import com.bidwhist.model.Rank;
import com.bidwhist.model.Hand;
import com.bidwhist.model.Card;

public class JokerUtils {
    public static boolean isJokerRank(Rank rank) {
        return rank == Rank.SMALL_JOKER || rank == Rank.BIG_JOKER;
    }

    public static int countJokers(Hand hand) {
        return (int) hand.getCards().stream()
                .map(Card::getRank)
                .filter(JokerUtils::isJokerRank)
                .count();
    }
}
