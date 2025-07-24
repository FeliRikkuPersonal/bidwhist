// src/components/SettingsPanel.jsx

import React from 'react';

/**
 * A simple UI panel for applying or closing game settings.
 *
 * @param {Function} applySettings - Callback to apply the current settings
 * @param {Function} closeSettings - Callback to close the settings panel
 * @returns {JSX.Element} The settings panel UI
 */
export default function SettingsPanel({ applySettings, closeSettings }) {
  return (
    <div className="settings-content">
      <h2>Game Settings</h2>
      <button className="index-button settings-button" onClick={applySettings}>
        Apply
      </button>
      &nbsp;&nbsp;&nbsp;&nbsp;
      <button className="index-button settings-button" onClick={closeSettings}>
        Close
      </button>
    </div>
  );
}
