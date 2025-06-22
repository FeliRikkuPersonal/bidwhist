// src/components/ModeSelector.js
import React, { useState } from 'react';
import '../ModeSelector.css';

function ModeSelector() {
    const [mode, setMode] = useState('single');
    const [name, setName] = useState('');
    const [lobbyCode, setLobbyCode] = useState('');

    const handleStart = () => {
        if (name.trim()) {
            window.location.href = '/game.html';
        }
    };

    const handleJoin = () => {
        if (name.trim() && lobbyCode.trim()) {
            alert(`Joining lobby ${lobbyCode} as ${name}`);
        }
    };

    const handleCreate = () => {
        if (name.trim()) {
            const code = Math.random().toString(36).substring(2, 8).toUpperCase();
            setLobbyCode(code);
            alert(`Created lobby ${code} as ${name}`);
        }
    };

    return (
        <>
            <div className="mode-slider-toggle">
                <label className="switch">
                    <input
                        type="checkbox"
                        onChange={(e) => setMode(e.target.checked ? 'multi' : 'single')}
                    />
                    <span className="slider"></span>
                    <div className="labels">
                        <span>Single</span>
                        <span>Multi</span>
                    </div>
                </label>
            </div>

            <div className={"mode-form-frame"}>
                <div className={`mode-form-container ${mode}`}>
                    {/* SINGLE PLAYER */}
                    <div className="mode-form single">
                        <input
                            className="index-input-box"
                            type="text"
                            placeholder="Enter your name"
                            value={name}
                            onChange={(e) => setName(e.target.value)}
                        />
                        <button className="index-button" onClick={handleStart}>Start Game</button>
                    </div>

                    {/* MULTIPLAYER */}
                    <div className="mode-form multi">
                        <input
                            className="index-input-box"
                            type="text"
                            placeholder="Enter your name"
                            value={name}
                            onChange={(e) => setName(e.target.value)}
                        />
                        <input
                            className="index-input-box"
                            type="text"
                            placeholder="Lobby Code (to join)"
                            value={lobbyCode}
                            onChange={(e) => setLobbyCode(e.target.value)}
                        />
                        <div className="multiplayer-buttons">
                            <button className="index-button" onClick={handleJoin}>Join Game</button>
                            <button className="index-button" onClick={handleCreate}>Create Lobby</button>
                        </div>
                    </div>
                </div>
            </div>
        </>
    );
}

/*
<p className="index-text">
                Please enter your name to play:<br />
                <input type={"text"} className={"index-input-box"} placeholder={"Enter your name"} />
                <button className={"index-button"}>Start Game</button>
            </p>
 */

export default ModeSelector;
