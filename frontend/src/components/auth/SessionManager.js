import { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { jwtDecode } from 'jwt-decode';

const SessionManager = ({ children }) => {
  const navigate = useNavigate();
  const tokenKey = 'token';
  const userIdKey = 'userId';
  const logoutRoute = '/login';

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
          logout();
        } else {
          const timeoutId = setTimeout(logout, timeLeftInMilliseconds);
          return () => clearTimeout(timeoutId);
        }
      } catch (error) {
        console.error("Error decoding JWT:", error);
        localStorage.removeItem(tokenKey);
        localStorage.removeItem(userIdKey);
        navigate(logoutRoute);
      }
    }
    
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