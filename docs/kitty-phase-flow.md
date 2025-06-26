flowchart TD
    A[Start Kitty Phase] --> B[Winning player receives 6 kitty cards]
    B --> C[Player adds kitty cards to hand]

    C --> D[Player selects 6 cards to discard]
    D --> E{Are 6 cards discarded?}
    E -- No --> F[Show error: Must discard exactly 6 cards]
    F --> D
    E -- Yes --> G[Place discarded cards back into Kitty]

    G --> H[Seal Kitty - mark as DISCARDED]
    H --> I[Assign trump suit from FinalBid]
    I --> J[Assign trump suit to Jokers]
    J --> K[Advance to Play Phase]
