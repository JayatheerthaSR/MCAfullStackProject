import { useContext } from 'react';
import { ThemeContext } from '../contexts/ThemeContext';
import './ThemeToggle.css';

const ThemeToggle = () => {
    const { theme, toggleTheme } = useContext(ThemeContext);

    return (
        <button
            onClick={toggleTheme}
            className={`theme-toggle ${theme}`}
            aria-label={theme === 'light' ? 'Switch to dark mode' : 'Switch to light mode'}
        >
            {theme === 'light' ? (
                <i className="bi bi-moon-fill"></i>
            ) : (
                <i className="bi bi-sun-fill"></i>
            )}
        </button>
    );
};

export default ThemeToggle;