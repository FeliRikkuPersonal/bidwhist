// src/utils/CardUtils.js

/**
 * Returns the image path for a card based on its visibility.
 *
 * @param {Object} card - The card object containing visibility and image info
 * @returns {string} Path to the card image to display
 */
export function getCardImage(card) {
  if (!card) {
    console.warn('getCardImage called with null/undefined card');
    return '/static/img/deck/Deck_Back.png';
  }

  return card.visibility === 'VISIBLE_TO_ALL'
    ? `/static/img/deck/${card.cardImage}`
    : '/static/img/deck/Deck_Back.png';
}
