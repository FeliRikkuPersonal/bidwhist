flowchart TD
    A[Start Bid Phase] --> B[Initialize bid turn index]
    B --> C[Player takes turn to bid]

    C --> D{Is player AI?}
    D -->|Yes| E[Evaluate hand with HandEvaluator]
    E --> F[Store FinalBid in cache]
    F --> G[Submit InitialBid from FinalBid]

    D -->|No| H[Submit InitialBid via API]

    G --> I[Compare with current highest bid]
    H --> I

    I --> J[Update highest bid if needed]
    J --> K{Total bids = 4?}
    K -->|No| C
    K -->|Yes| L{Is winning player AI?}

    L -->|Yes| M[Use cached FinalBid → Set winning stats]
    L -->|No| N[Wait for player to /finalizeBid]

    M --> O[Advance to Kitty Phase]
    N --> O
