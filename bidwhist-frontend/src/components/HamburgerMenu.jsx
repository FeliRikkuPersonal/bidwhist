// src/components/HamburgerMenu.jsx

import React, { useState } from 'react';
import handleQuit from '../utils/handleQuit';
import '../css/HamburgerMenu.css'; // Optional styling

const HamburgerMenu = () => {
  const [open, setOpen] = useState(false);

  const toggleMenu = () => setOpen(!open);

  return (
    <div className="hamburger-container">
      <button className="hamburger-icon" onClick={toggleMenu}>
        {/* Hamburger lines */}
        <div className="bar"></div>
        <div className="bar"></div>
        <div className="bar"></div>
      </button>

      {open && (
        <div className="dropdown-menu">
          <a onClick={handleQuit}>Home</a>
          <a onClick={toggleMenu}>New Game</a>
          <a
            href="/site/index.html"
            target="_blank"
            rel="noopener noreferrer"
            onClick={toggleMenu}>About</a>
        </div>
      )}
    </div>
  );
};

export default HamburgerMenu;
