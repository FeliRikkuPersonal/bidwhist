// src/components/HamburgerMenu.jsx

import React, { useState } from 'react';
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
          <a onClick={toggleMenu}>Home</a>
          <a onClick={toggleMenu}>New Game</a>
          <a onClick={toggleMenu}>About</a>
        </div>
      )}
    </div>
  );
};

export default HamburgerMenu;
