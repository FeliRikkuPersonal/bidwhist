flowchart TD
    A[Start Game] --> B[startNewGame]
    B --> C[Play Matches]

    subgraph Game Loop
        C --> D[Deal Phase]
        D --> E[Bid Phase]
        E --> F[Kitty Phase]
        F --> G[Play Phase]
        G --> H[Score Phase]
        H --> I{Score >= Target?}
        I -- No --> D
        I -- Yes --> J[End Game]
    end
