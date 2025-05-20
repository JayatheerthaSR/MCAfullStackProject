import React from 'react';
import ReactDOM from 'react-dom/client';
import App from './App';
import { BrowserRouter } from 'react-router-dom';
import { ThemeProvider } from './contexts/ThemeContext'; // Import ThemeProvider
import './index.css'; // Your global styles

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(
    <React.StrictMode>
        
            <ThemeProvider> {/* Wrap App with ThemeProvider */}
                <App />
            </ThemeProvider>
        
    </React.StrictMode>
);