import React, { useState, useEffect } from 'react';
import '../css/Alert.css';

const Alert = ({ message, type, onClose }) => {
  // Automatically hide alert after 5 seconds
  useEffect(() => {
    const timer = setTimeout(() => {
      onClose();
    }, 5000); // 5 seconds
    return () => clearTimeout(timer);
  }, [message, onClose]);

  return (
    <div className={`alert ${type}`}>
      <span>{message}</span>
      <button onClick={onClose} className="close-btn">X</button>
    </div>
  );
};

export default Alert;
