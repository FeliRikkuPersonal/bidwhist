// src/animations/DealAnimation.jsx
import { useZoneRefs } from '../context/RefContext';
import { getCardImage } from '../utils/CardUtils';

/**
 * Animate dealing cards clockwise to each player.
 *
 * @param {Array<string>} playerPositions - e.g., ['P1', 'P2', 'P3', 'P4']
 * @param {Array<Object>} cards - flat list of cards with .owner and .cardImage
 * @param {{x: number, y: number}} deckPosition - center of the table
 * @param {Object} positionMap - maps backend positions (P1, P2) → view positions (south, west)
 * @param {Function} setAnimatedCards - updater to push card animation state
 * @param {Function} [onComplete] - optional callback when done
 */
export function dealCardsClockwise(
  playerPositions,
  cards,
  deckPosition,
  positionMap,
  setAnimatedCards,
  showAnimatedCards,
  setShowAnimatedCards,
  onComplete,
  get
) {
  const totalCards = cards.length;
  const delayPerCard = 120; // ms
  console.log("Cards being dealt:", cards.length, cards);

  const grouped = {};
  playerPositions.forEach(pos => {
    grouped[pos] = cards.filter(c => c.owner === pos);
  });

  // Interleave by round
  for (let round = 0; round < grouped[playerPositions[0]].length; round++) {
    for (let p = 0; p < playerPositions.length; p++) {
      const player = playerPositions[p];
      const card = grouped[player][round];
      if (!card) continue;

      const visualPosition = positionMap[player];
      const targetRef = get(visualPosition)?.current;
      const toRect = targetRef?.getBoundingClientRect();

      if (!toRect) {
        console.warn(`Missing ref for player ${player} → ${visualPosition}`);
        continue;
      }

      const parentBounds = document.querySelector('.floating-card-layer')?.getBoundingClientRect();
      const toX = toRect.left + toRect.width / 2 - parentBounds.left;
      const toY = toRect.top + toRect.height / 2 - parentBounds.top;

      const index = round * playerPositions.length + p;

      setTimeout(() => {
        setAnimatedCards(prev => [
          ...prev,
          {
            id: `${card.cardImage}-${index}`,
            ...card,
            from: deckPosition,
            to: { x: toX, y: toY }
          }
        ]);
      }, index * delayPerCard);
    }
  }


  if (onComplete) {
    setTimeout(() => {
      onComplete();
      setShowAnimatedCards(false);
    }, totalCards * delayPerCard + 500);
  }
}
