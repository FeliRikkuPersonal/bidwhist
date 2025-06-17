flowchart TD
    A[Start Evaluation] --> B[Identify Lead Suit (card 1)]
    B --> C[Check each card: Lead Suit? Trump Suit? Assigned Joker?]
    C --> D{Are there any Trump cards played?}
    
    D -- Yes --> E[Winner is highest-ranked Trump]
    D -- No --> F{Are there any Lead Suit cards played?}
    F -- Yes --> G[Winner is highest-ranked Lead Suit]
    F -- No --> H[Winner is lowest-ranked card (no suit match)]
    
    E --> I[Declare Book Winner]
    G --> I
    H --> I