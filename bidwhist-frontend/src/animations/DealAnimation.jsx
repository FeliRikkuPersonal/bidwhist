// src/animations/DealAnimation.jsx

/**
*
* Animate dealing cards clockwise to each player.
* Each card is animated from the deck to the player's hand based on their position.
* The animation is timed to mimic a real-world dealing sequence, interleaved by round.
*
* @param {Array<string>} playerPositions - e.g., ['P1', 'P2', 'P3', 'P4']
* @param {Array<Object>} cards - flat list of cards with .owner and .cardImage
* @param {Object} positionMap - maps backend positions (P1, P2) → view positions (south, west, etc.)
* @param {Function} [onComplete] - optional callback when all animations finish
* @param {Function} get - retrieves registered DOM refs for players
* @param {Object} deckPosition - x/y coordinates of the deck
* @param {Function} setAnimatedCards - state setter to add animated cards
* @param {Function} setShowAnimatedCards - toggles visibility of animated cards
* @param {Function} setBidPhase - triggers transition to bidding phase
*
*/
export function dealCardsClockwise(
  playerPositions,
  cards,
  positionMap,
  onComplete,
  get,
  deckPosition,
  setAnimatedCards,
  setShowAnimatedCards,
  setBidPhase,
) {
  const totalCards = cards.length;
  const delayPerCard = 120; /* milliseconds between each card */

  /* Group cards by player */
  const grouped = {};
  playerPositions.forEach((pos) => {
    grouped[pos] = cards.filter((c) => c.owner === pos);
  });

  /*
  * Interleave cards by round, so each player receives one card per cycle
  * This simulates realistic clockwise dealing
  */
  for (let round = 0; round < grouped[playerPositions[0]].length; round++) {
    for (let p = 0; p < playerPositions.length; p++) {
      const player = playerPositions[p];
      const card = grouped[player][round];
      if (!card) continue;

      const visualPosition = positionMap[player];
      const targetRef = get(`hand-${visualPosition}`)?.current;
      const toRect = targetRef?.getBoundingClientRect();

      if (!toRect) {
        console.warn(`Missing ref for player ${player} → ${visualPosition}`);
        continue;
      }

      const parentBounds = document.querySelector('.floating-card-layer')?.getBoundingClientRect();
      const toX = toRect.left + toRect.width / 2 - parentBounds.left;
      const toY = toRect.top + toRect.height / 2 - parentBounds.top;

      const index = round * playerPositions.length + p;

      /* Schedule animation for this card */
      setTimeout(() => {
        setAnimatedCards((prev) => [
          ...prev,
          {
            id: `${card.cardImage}-${index}`,
            ...card,
            from: deckPosition,
            to: { x: toX, y: toY },
          },
        ]);
      }, index * delayPerCard);
    }
  }

  /*
  * Final callback to end animation sequence and trigger next phase
  */
  if (onComplete) {
    setTimeout(
      () => {
        onComplete();
        setShowAnimatedCards(false);
        setBidPhase(true);
      },
      totalCards * delayPerCard + 500,
    );
  }
}
