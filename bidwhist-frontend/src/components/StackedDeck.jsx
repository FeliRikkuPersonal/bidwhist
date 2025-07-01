import { getCardImage } from "../utils/CardUtils";

export const StackedDeck = ({ cards, deckPosition, viewerName }) => (
  <>
    {cards.map((card, i) => (
      <img
        key={i}
        src={getCardImage(card, viewerName, 'SHUFFLING')}
        alt="Card back"
        className="card-img stacked-card"
        style={{
          position: 'absolute',
          top: deckPosition.y,
          left: deckPosition.x,
          transform: `translate(-50%, -50%)`,
          zIndex: i,
        }}
      />
    ))}
  </>
);
