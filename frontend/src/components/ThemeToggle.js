import React, { useContext } from 'react';
import { ThemeContext } from '../contexts/ThemeContext';
import './ThemeToggle.css'; // Optional: for toggle-specific styles

const ThemeToggle = () => {
    const { theme, toggleTheme } = useContext(ThemeContext);

    return (
        <button onClick={toggleTheme} className={`theme-toggle ${theme}`}>
            {theme === 'light' ? 'Switch to Dark' : 'Switch to Light'}
        </button>
    );
};

export default ThemeToggle;