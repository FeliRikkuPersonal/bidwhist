// src/components/Alert.jsx

import React, { useState, useEffect } from 'react';
import '../css/Alert.css';

/*
 *
 * Alert displays a temporary message with a style based on type ("success", "error", etc).
 * It auto-dismisses after 5 seconds and includes a manual close button.
 *
 */
const Alert = ({ message, type, onClose }) => {
  let needsButton;
  /*
   * Automatically hides the alert after 5 seconds
   */
  useEffect(() => {
    if (type === 'persist') {
      needsButton = true;
      return;
    } else {
      needsButton = false;
    }

    const timer = setTimeout(() => {
      onClose();
    }, 5000);

    return () => clearTimeout(timer);
  }, [message, onClose]);

  /*
   * Render the alert box with message and close button
   */
  return (
    <div className={`alert ${type}`}>
      <span>{message}</span>
      {needsButton &&
        <button onClick={onClose} className="close-btn">
          x
        </button>
      }
    </div>
  );
};

export default Alert;
