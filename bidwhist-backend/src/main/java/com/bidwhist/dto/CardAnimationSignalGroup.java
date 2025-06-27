package com.bidwhist.dto;

import java.util.List;

import com.bidwhist.model.*;

public class CardAnimationSignalGroup {
    private final PlayerPos targetPlayer;
    private final List<Card> cards;
    private final AnimationType animationType;
    private final boolean visible;

    public CardAnimationSignalGroup(PlayerPos targetPlayer, List<Card> cards,
            AnimationType animationType, boolean visible) {
        this.targetPlayer = targetPlayer;
        this.cards = cards;
        this.animationType = animationType;
        this.visible = visible;
    }

    public PlayerPos getTargetPlayer() {
        return targetPlayer;
    }

    public List<Card> getCards() {
        return cards;
    }

    public AnimationType getAnimationType() {
        return animationType;
    }

    public boolean isVisible() {
        return visible;
    }
}
