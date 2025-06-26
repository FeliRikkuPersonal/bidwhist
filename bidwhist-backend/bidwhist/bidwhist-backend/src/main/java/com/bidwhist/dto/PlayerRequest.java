package com.bidwhist.dto;

public class PlayerRequest {
    private String name;

    public PlayerRequest() {
        // Default constructor for JSON
    }

    public PlayerRequest(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
