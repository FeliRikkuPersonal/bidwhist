// src/utils/CardUtils.js
export function getCardImage(card, viewerName, contextPhase) {
  if (!card) {
    console.warn("getCardImage called with null/undefined card");
    return "/static/img/deck/Deck_Back.png";
  }

  switch (card.visibility) {
    case 'VISIBLE_TO_ALL':
      return `/static/img/deck/${card.cardImage}`;

    case 'VISIBLE_TO_SELF':
      return card.owner === viewerName
        ? `/static/img/deck/${card.cardImage}`
        : `/static/img/deck/Deck_Back.png`;

    case 'HIDDEN':
    default:
      return '/static/img/deck/Deck_Back.png';
  }
}
