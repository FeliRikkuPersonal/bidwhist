import { getRef } from '../utils/RefRegistry';
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
export function dealCardsClockwise(playerPositions, cards, deckPosition, positionMap, setAnimatedCards, onComplete) {
  const totalCards = cards.length;
  const delayPerCard = 120; // ms

  cards.forEach((card, i) => {
    const owner = card.owner; // e.g., "P2"
    const visualPosition = positionMap[owner]; // e.g., "west"
    const targetRef = getRef(`PlayerZone-${visualPosition}`)?.current;
    const toRect = targetRef?.getBoundingClientRect();

    if (!toRect) {
      console.warn(`Missing ref for player ${owner} → ${visualPosition}`);
      return;
    }

    const toX = toRect.left + toRect.width / 2;
    const toY = toRect.top + toRect.height / 2;

    setTimeout(() => {
      setAnimatedCards(prev => [
        ...prev,
        {
          id: `${card.cardImage}-${i}`,
          card,
          from: deckPosition,
          to: { x: toX, y: toY }
        }
      ]);
    }, i * delayPerCard);
  });

  if (onComplete) {
    setTimeout(() => {
      onComplete();
    }, totalCards * delayPerCard + 500);
  }
}
