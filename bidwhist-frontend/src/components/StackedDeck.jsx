import { getCardImage } from "../utils/CardUtils";
import '../css/Card.css';

export const StackedDeck = ({ cards, viewerName, deckPosition }) => (
  <>
    {cards.map((card, i) => (
      <img
        key={i}
        src={getCardImage(card, viewerName, 'SHUFFLING')}
        alt="Card back"
        className="card-img"
        style={{
          position: 'absolute',
          top: deckPosition.y,
          left: deckPosition.x,
          zIndex: i,
        }}
      />
    ))}
  </>
);
