export const UIDisplayContext = createContext(null);

export function UIDisplayProvider({ children }) {
  const [showAnimatedCards, setShowAnimatedCards] = useState(false);
  const [deckPosition, setDeckPosition] = useState({ x: 0, y: 0 });
  const [animatedCards, setAnimatedCards] = useState([]);
  const [showShuffle, setShowShuffle] = useState(false);
  const [showHands, setShowHands] = useState(false);
  const [showBidding, setShowBidding] = useState(false);

  return (
    <UIDisplayContext.Provider value={{
      showAnimatedCards,
      setShowAnimatedCards,
      deckPosition,
      setDeckPosition,
      animatedCards,
      setAnimatedCards,
      showShuffle,
      setShowShuffle,
      showHands,
      setShowHands,
      showBidding,
      setShowBidding,
    }}>
      {children}
    </UIDisplayContext.Provider>
  );
}

export const useUIDisplay = () => useContext(UIDisplayContext);
