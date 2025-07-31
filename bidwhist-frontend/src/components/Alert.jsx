// src/components/Alert.jsx

import React, { useEffect } from 'react';
import '../css/Alert.css';

/*
 * Alert displays a temporary message with a style based on type ("success", "error", etc).
 * It auto-dismisses after 5 seconds and includes a manual close button if type is 'persist'.
 */
const Alert = ({ message, type, onClose }) => {
  const needsButton = type === 'persist';
  const shortNotice = type === 'yourturn';

  useEffect(() => {
    if (needsButton) return;

    const timeout = shortNotice ? 3000 : 5000;
    const timer = setTimeout(() => {
      onClose();
    }, timeout);

    return () => clearTimeout(timer);
  }, [message, onClose, needsButton, shortNotice]);

  return (
    <div className={`alert ${type}`}>
      <span>{message}</span>
      {needsButton && (
        <button onClick={onClose} className="close-btn">
          x
        </button>
      )}
    </div>
  );
};

export default Alert;
