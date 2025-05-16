import React, { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { jwtDecode } from 'jwt-decode';

const SessionManager = ({ children }) => {
  const navigate = useNavigate();
  const tokenKey = 'token'; // Replace with your actual token key
  const userIdKey = 'userId'; // Replace with your actual user ID key
  const logoutRoute = '/login'; // Replace with your login route

  const logout = () => {
    localStorage.removeItem(tokenKey);
    localStorage.removeItem(userIdKey);
    navigate(logoutRoute);
  };

  useEffect(() => {
    const token = localStorage.getItem(tokenKey);

    if (token) {
      try {
        const decodedToken = jwtDecode(token);
        const expirationTimeInSeconds = decodedToken.exp;
        const currentTimeInSeconds = Math.floor(Date.now() / 1000);
        const timeLeftInMilliseconds = (expirationTimeInSeconds - currentTimeInSeconds) * 1000;

        if (timeLeftInMilliseconds <= 0) {
          // Token is already expired
          logout();
        } else {
          // Set a timeout to logout when the token expires (or slightly before)
          const timeoutId = setTimeout(logout, timeLeftInMilliseconds);

          // Clear the timeout if the component unmounts
          return () => clearTimeout(timeoutId);
        }
      } catch (error) {
        console.error("Error decoding JWT:", error);
        // Handle invalid token (maybe logout as well)
        localStorage.removeItem(tokenKey);
        localStorage.removeItem(userIdKey);
        navigate(logoutRoute);
      }
    }

    // Optionally, you can also add a listener for storage events
    // in case the token is removed from another tab/window.
    const handleStorageChange = (event) => {
      if (event.key === tokenKey && !localStorage.getItem(tokenKey)) {
        logout();
      }
    };

    window.addEventListener('storage', handleStorageChange);

    return () => {
      window.removeEventListener('storage', handleStorageChange);
    };
  }, [navigate, tokenKey, userIdKey, logoutRoute]);

  return <>{children}</>;
};

export default SessionManager;