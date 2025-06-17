flowchart TD
    A[Evaluate Book] 
    A --> B{Is Book Empty?}

    B -- Yes --> C[Set HandBookState: LeadHand = Full Hand]
    B -- No --> D[Identify Lead Suit - card 1]
    D --> E[Check player's hand against Lead and Trump suits]

    E --> F{Has Lead suit or Trump?}
    F -- Yes --> G[Set HandBookState: EligibleHand]
    F -- No --> H[Set HandBookState: IneligibleHand]

    C --> I[Choose card based on decision logic using HandBookState]
    G --> I
    H --> I

    I --> J[Play card to book]
    J --> K{Is last player in book?}
    K -->|Yes| L[Evaluate Book Winner]
    K -->|No| M[Next player plays]
