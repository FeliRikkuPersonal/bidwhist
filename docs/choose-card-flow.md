flowchart TD
    A[Evaluate Book] 
    A --> B[Identify Lead Suit - card 1]
    B --> C{Has eligible cards?}
    C -->|Yes| D[Filter eligible cards: Lead Suit, Trump Suit, Jokers]
    C -->|No| E[Select losing card based on decision logic]

    D --> F[Choose play based on decision logic]
    E --> F

    F --> G{Is last player in book?}
    G -->|Yes| H[Evaluate Book Winner]
    G -->|No| I[Next player plays]
