import React, { useState } from 'react';
import './App.css';
import ModeSelector from './components/ModeSelector';
import PlayerZone from './components/PlayerZone';
import CardPlayZone from './components/CardPlayZone';
import SettingsPanel from './components/SettingsPanel';
import Scoreboard from './components/Scoreboard';

function App() {
    const [playerName, setPlayerName] = useState('');
    const [isMultiplayer, setIsMultiplayer] = useState(false);
    const [showWelcome, setShowWelcome] = useState(true);
    const [showSettings, setShowSettings] = useState(false);
    const [hands, setHands] = useState([[], [], [], []]);
    const [playCard, setPlayCard] = useState(false);

    const handleStartGame = () => {
        if (playerName.trim() !== "") {
            const fullDeck = shuffleDeck(buildDeck());
            const dealtHands = dealHands(fullDeck);
            setHands(dealtHands);
            setShowWelcome(false);
        }
    };

    const toggleSettings = () => {
        setShowSettings(prev => !prev);
    }

    // Card randomization framework
    const suits = ['Heart', 'Diamond', 'Spade', 'Club'];
    const ranks = ['2','3','4','5','6','7','8','9','10','J','Q','K','A'];
    const jokers = ['Joker_B', 'Joker_L'];

    function buildDeck() {
        const deck = [];
        suits.forEach(suit => {
            ranks.forEach(rank => deck.push(`${suit}_${rank}.png`));
        });
        jokers.forEach(j => deck.push(`${j}.png`));
        return deck;
    }

    function shuffleDeck(deck) {
        for (let i = deck.length - 1; i > 0; i--) {
            const j = Math.floor(Math.random() * (i + 1));
            [deck[i], deck[j]] = [deck[j], deck[i]];
        }
        return deck;
    }

    function dealHands(deck, numPlayers = 4) {
        const hands = Array.from({ length: numPlayers }, () => []);
        for (let i = 0; i < 13 * numPlayers; i++) {
            hands[i % numPlayers].push(deck[i]);
        }
        return hands;
    }

    return (
        <div className="index-wrapper">
            {showWelcome ? (
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
                    <div className="settings-icon" onClick={toggleSettings}>⚙</div>
                    {showSettings && (
                        <div className="index-container settings-panel">
                            <SettingsPanel closeSettings={toggleSettings} />
                        </div>
                    )}
                </>
            )}
        </div>
    );
}

export default App;
