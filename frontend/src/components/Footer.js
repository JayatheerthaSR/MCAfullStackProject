import { useContext } from 'react';
import { ThemeContext } from '../contexts/ThemeContext';

const Footer = () => {
  const { theme } = useContext(ThemeContext);
  const isDark = theme === 'dark';

  return (
    <footer className={`py-3 mt-5 text-center ${isDark ? 'bg-dark text-light' : 'bg-light text-muted'}`}>
      <p className="mb-0">
        <i className="bi bi-c-circle me-2"></i> 2025 Banking App. All rights reserved.
      </p>
    </footer>
  );
};

export default Footer;