import React, { useState } from 'react';
import './App.css';
import ModeSelector from './components/ModeSelector';
import PlayerZone from './components/PlayerZone';
import CardPlayZone from './components/CardPlayZone';
import SettingsPanel from './components/SettingsPanel';
import Scoreboard from './components/Scoreboard';
import BiddingPanel from './components/BiddingPanel';
import FinalScorePanel from './components/FinalScorePanel';

function App() {
    const [playerName, setPlayerName] = useState('');
    const [isMultiplayer, setIsMultiplayer] = useState(false);
    const [showWelcome, setShowWelcome] = useState(true);
    const [showSettings, setShowSettings] = useState(false);
    const [hands, setHands] = useState([[], [], [], []]);
    const [playCard, setPlayCard] = useState(false);
    const [showBidding, setShowBidding] = useState(false);
    const [finalScores, setFinalScore] = useState(false);

    // Method to handle starting a game.  Used for new games as well.
    const handleStartGame = () => {
        setFinalScore(null);
        if (playerName.trim() !== "") {
            const fullDeck = shuffleDeck(buildDeck());
            const dealtHands = dealHands(fullDeck);
            setHands(dealtHands);
            setShowWelcome(false);
            setShowBidding(true);
        }
    };

    // Allows for the Setting Panel to appear.
    const toggleSettings = () => {
        setShowSettings(prev => !prev);
    }

    // Card randomization framework
    const suits = ['Heart', 'Diamond', 'Spade', 'Club'];
    const ranks = ['2','3','4','5','6','7','8','9','10','J','Q','K','A'];
    const jokers = ['Joker_B', 'Joker_L'];

    // Function that builds the deck.
    function buildDeck() {
        const deck = [];
        suits.forEach(suit => {
            ranks.forEach(rank => deck.push(`${suit}_${rank}.png`));
        });
        jokers.forEach(j => deck.push(`${j}.png`));
        return deck;
    }

    // Shuffle Deck
    function shuffleDeck(deck) {
        for (let i = deck.length - 1; i > 0; i--) {
            const j = Math.floor(Math.random() * (i + 1));
            [deck[i], deck[j]] = [deck[j], deck[i]];
        }
        return deck;
    }

    // Deal Cards
    function dealHands(deck, numPlayers = 4) {
        const hands = Array.from({ length: numPlayers }, () => []);
        for (let i = 0; i < 13 * numPlayers; i++) {
            hands[i % numPlayers].push(deck[i]);
        }
        return hands;
    }

    // Triggers end game state and shows the final scoreboard.
    const endGame = () => {
        {/* Arbitrary numbers to see panel in UI.  Need to connect to backend */}
        const scores = [
            {team: playerName, points: 7},
            {team: "AI Team", points: 0}
        ];
        setFinalScore(scores);
    }

    // Submit Bid
    const handleBidSubmit = (bid) => {
        console.log('Bid submitted:', bid);
        setShowBidding(false); // Hide bidding panel after bid
    };

    // Skip bid
    const handleSkipBid = () => {
        console.log('Bid skipped.');
        setShowBidding(false); // Hide if skipped
    };

    return (
        <div className="index-wrapper">
            {showWelcome ? ( // Allows user to input their name, or select multiplayer.
                <div className="index-container">
                    <h1>Welcome to Bid Whist Online!</h1>
                    <ModeSelector
                        isMultiplayer={isMultiplayer}
                        setIsMultiplayer={setIsMultiplayer}
                        playerName={playerName}
                        setPlayerName={setPlayerName}
                        onStartGame={handleStartGame}
                    />
                </div>
            ) : (
                <>
                    {/* Main game area */}
                    <div className="index-container">
                        <CardPlayZone playerPosition="south" />
                        <div className="game-buttons">
                            <button className="index-button game-button" onClick={playCard}>Play Card</button>
                            <button className="index-button game-button" onClick={playCard}>Reset Hand</button>
                        </div>
                        <Scoreboard />
                    </div>

                    <div className="player-hand-wrapper">
                        <PlayerZone position="south" name="You" showHand={true} cards={hands[0]}/>
                        <PlayerZone position="north" name="AI Player" showHand={false} cards={hands[1]} />
                        <PlayerZone position="east" name="AI Player" showHand={false} cards={hands[2]} />
                        <PlayerZone position="west" name="AI Player" showHand={false} cards={hands[3]} />
                    </div>

                    {showBidding && (
                        <BiddingPanel closeBidding={() => setShowBidding(false)} />
                    )}
                    <div className="settings-icon" onClick={toggleSettings}>⚙</div>
                    {showSettings && (
                        <div className="index-container settings-panel">
                            <SettingsPanel closeSettings={toggleSettings} />
                        </div>
                    )}
                    {finalScores ? (
                        <FinalScorePanel scores={finalScores} onNewGame={handleStartGame} />
                    ) : (
                        <>
                            {/* Existing Game UI */} {/* Temporary Button to force endGame state */}
                            <button onClick={endGame}>End Game</button>
                            {/* Remove with integration */}
                        </>
                    )}
                </>
            )}
        </div>
    );
}

export default App;
