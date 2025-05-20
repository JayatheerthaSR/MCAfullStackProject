// src/components/ThemeToggle.js
import React, { useContext } from 'react';
import { ThemeContext } from '../contexts/ThemeContext';
import './ThemeToggle.css'; // Make sure this path is correct

const ThemeToggle = () => {
    const { theme, toggleTheme } = useContext(ThemeContext);

    return (
        <button
            onClick={toggleTheme}
            className={`theme-toggle ${theme}`}
            aria-label={theme === 'light' ? 'Switch to dark mode' : 'Switch to light mode'} // Added for accessibility
        >
            {theme === 'light' ? (
                <i className="bi bi-moon-fill"></i> // Moon icon for switching TO dark mode
            ) : (
                <i className="bi bi-sun-fill"></i> // Sun icon for switching TO light mode
            )}
        </button>
    );
};

export default ThemeToggle;