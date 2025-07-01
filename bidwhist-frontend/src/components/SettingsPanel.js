export default function SettingsPanel({ applySettings, closeSettings }) {
    return (
        <div className="settings-content">
            <h2>Game Settings</h2>
            <hr style={{ height: '2px', backgroundColor: '#8ca62d', border: 'none' }} />
            <h3>AI Difficulty:</h3>
            <div className="radio-group">
                <label>
                    <input type="radio" name="difficulty" value="easy" />
                    Easy
                </label>
                <label>
                    <input type="radio" name="difficulty" value="normal" />
                    Normal
                </label>
                <label>
                    <input type="radio" name="difficulty" value="hard" />
                    Hard
                </label>
            </div>
            <hr style={{ height: '2px', backgroundColor: '#8ca62d', border: 'none' }} />
            <h3>Card Ranking:</h3>
            <div className="radio-group">
                <label>
                    <input type="radio" name="ranking" value="uptown" />
                    Uptown
                </label>
                <label>
                    <input type="radio" name="ranking" value="downtown" />
                    Downtown
                </label>
            </div>
            <hr style={{ height: '2px', backgroundColor: '#8ca62d', border: 'none' }} />
            <h3>Kitty Cards:</h3>
            <div className="radio-group">
                <label>  {/* No Jokers used */}
                    <input type="radio" name="kitty" value="4" />
                    4
                </label>
                <label>   {/* One Joker used */}
                    <input type="radio" name="kitty" value="5" />
                    5
                </label>
                <label>   {/* Two Jokers used */}
                    <input type="radio" name="kitty" value="6" />
                    6
                </label>
            </div>
            <hr style={{ height: '2px', backgroundColor: 'none', border: 'none' }} />
            <div className="settings-actions">
                <button className="index-button settings-button" onClick={applySettings}>Apply</button>
                <button className="index-button settings-button" onClick={closeSettings}>Close</button>
            </div>
            <button
                className="index-button rules-button"
                onClick={() => window.open('/rules.html', '_blank','noopener,noreferrer')}>Game Rules</button>
        </div>
    );
}