export default function SettingsPanel({ applySettings, closeSettings }) {
    return (
        <div className="settings-content">
            <h2>Game Settings</h2>

            <button className="index-button settings-button" onClick={applySettings}>Apply</button>
            &nbsp;&nbsp;&nbsp;&nbsp;
            <button className="index-button settings-button" onClick={closeSettings}>Close</button>
        </div>
    );
}