package com.bidwhist.dto;

import com.bidwhist.model.*;

public class CardAnimationSignal {
    private final PlayerPos targetPlayer;
    private final Card card;
    private final AnimationType animationType;
    private final boolean visible;

    public CardAnimationSignal(PlayerPos targetPlayer, Card card, AnimationType animationType, boolean visible) {
        this.targetPlayer = targetPlayer;
        this.card = card;
        this.animationType = animationType;
        this.visible = visible;
    }

    public PlayerPos getTargetPlayer() {
        return targetPlayer;
    }

    public Card getCard() {
        return card;
    }

    public AnimationType getAnimationType() {
        return animationType;
    }

    public boolean isVisible() {
        return visible;
    }
}

