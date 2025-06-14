package com.bidwhist.bidwhist_backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/game")
public class GameController {

    @GetMapping("/ping")
    public String ping() {
        return "Bid Whist backend is live!";
    }
    
}
