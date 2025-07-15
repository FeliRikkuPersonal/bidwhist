package com.bidwhist.model;

// Enum to track the status/state of objects like game, kitty, book, etc.
public enum GameObjectStatus {
    PREPARING("Preparing"),
    ACTIVE("Active"),
    CONCLUDED("Concluded");

    private final String label;

    GameObjectStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}

