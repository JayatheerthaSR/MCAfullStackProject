import React, { useState, useEffect, useContext } from 'react';
import { NavLink, useNavigate, useLocation } from 'react-router-dom';
import ThemeToggle from './ThemeToggle';
import { ThemeContext } from '../contexts/ThemeContext';
import GlobalSearchComponent from './GlobalSearchComponent';

const Navbar = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const { theme } = useContext(ThemeContext);
  const isDark = theme === 'dark';

  // State to track login status and role, derived from localStorage
  const [currentAuth, setCurrentAuth] = useState(() => ({
    isLoggedIn: !!localStorage.getItem('token'),
    role: localStorage.getItem('role'),
    token: localStorage.getItem('token'),
  }));

  // Update auth state when localStorage changes or location changes
  useEffect(() => {
    // Determine if the current route is a public route
    const publicRoutes = ['/', '/login', '/register', '/forgot-password', '/reset-password'];
    const tokenFromStorage = localStorage.getItem('token');
    const roleFromStorage = localStorage.getItem('role');

    // A user is considered logged in if a token exists AND the route is not a public one
    // This helps manage `isLoggedIn` state more accurately for UI components
    const newIsLoggedIn = !!tokenFromStorage && !publicRoutes.includes(location.pathname);

    setCurrentAuth({
      isLoggedIn: newIsLoggedIn,
      role: roleFromStorage,
      token: tokenFromStorage,
    });

    // Listen for storage events (e.g., logout from another tab/window)
    const handleStorageChange = (event) => {
      if (event.key === 'token' || event.key === 'role') {
        const updatedToken = localStorage.getItem('token');
        const updatedRole = localStorage.getItem('role');
        const updatedIsLoggedIn = !!updatedToken && !publicRoutes.includes(location.pathname);
        setCurrentAuth({
          isLoggedIn: updatedIsLoggedIn,
          role: updatedRole,
          token: updatedToken,
        });
      }
    };

    window.addEventListener('storage', handleStorageChange);

    return () => {
      window.removeEventListener('storage', handleStorageChange);
    };

  }, [location]); // Re-run effect if location changes

  const handleLogout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('role');
    localStorage.removeItem('userId'); // Ensure userId is also cleared on logout
    localStorage.removeItem('customerId'); // Clear customerId if present
    setCurrentAuth({ isLoggedIn: false, role: null, token: null }); // Update state immediately
    navigate('/login');
  };

  return (
    <nav className={`navbar navbar-expand-lg shadow-sm mb-4 rounded ${isDark ? 'navbar-dark bg-dark' : 'navbar-light bg-light'}`}>
      <div className="container-fluid">
        <NavLink className="navbar-brand fw-bold" to="/">
          <i className="bi bi-bank me-2"></i>Banking App
        </NavLink>
        <button
          className="navbar-toggler"
          type="button"
          data-bs-toggle="collapse"
          data-bs-target="#navbarNav"
          aria-controls="navbarNav"
          aria-expanded="false"
          aria-label="Toggle navigation"
        >
          <span className="navbar-toggler-icon"></span>
        </button>
        <div className="collapse navbar-collapse justify-content-end" id="navbarNav">
          <ul className="navbar-nav ms-auto align-items-center">
            {currentAuth.isLoggedIn && (
              <li className="nav-item">
                <NavLink
                  className={`nav-link ${
                    // Highlight Dashboard link if currently on dashboard path for current role
                    (currentAuth.role === 'CUSTOMER' && location.pathname.startsWith('/customer/dashboard')) ||
                    (currentAuth.role === 'ADMIN' && location.pathname.startsWith('/admin/dashboard'))
                      ? 'active'
                      : ''
                  }`}
                  to={currentAuth.role === 'CUSTOMER' ? '/customer/dashboard' : currentAuth.role === 'ADMIN' ? '/admin/dashboard' : '/dashboard'}
                >
                  <i className="bi bi-house-fill me-2"></i>Dashboard
                </NavLink>
              </li>
            )}

            {currentAuth.isLoggedIn && currentAuth.role === 'CUSTOMER' && (
              <>
                <li className="nav-item">
                  <NavLink
                    className={`nav-link ${location.pathname.startsWith('/customer/transactions') ? 'active' : ''}`}
                    to="/customer/transactions"
                  >
                    <i className="bi bi-list-ul me-2"></i>Transactions
                  </NavLink>
                </li>
                <li className="nav-item">
                  <NavLink
                    className={`nav-link ${location.pathname.startsWith('/customer/beneficiaries') ? 'active' : ''}`}
                    to="/customer/beneficiaries"
                  >
                    <i className="bi bi-person-plus-fill me-2"></i>Beneficiaries
                  </NavLink>
                </li>
                <li className="nav-item">
                  <NavLink
                    className={`nav-link ${location.pathname.startsWith('/customer/transfer') ? 'active' : ''}`}
                    to="/customer/transfer"
                  >
                    <i className="bi bi-arrow-left-right me-2"></i>Transfer
                  </NavLink>
                </li>
                <li className="nav-item">
                  <NavLink
                    className={`nav-link ${location.pathname.startsWith('/customer/profile') ? 'active' : ''}`}
                    to="/customer/profile"
                  >
                    <i className="bi bi-person-circle me-2"></i>Profile
                  </NavLink>
                </li>
              </>
            )}

            {currentAuth.isLoggedIn && currentAuth.role === 'ADMIN' && (
              <>
                <li className="nav-item">
                  <NavLink
                    className={`nav-link ${location.pathname.startsWith('/admin/transactions') ? 'active' : ''}`}
                    to="/admin/transactions"
                  >
                    <i className="bi bi-list-ul me-2"></i>All Transactions
                  </NavLink>
                </li>
                <li className="nav-item">
                  <NavLink
                    className={`nav-link ${location.pathname.startsWith('/admin/users') ? 'active' : ''}`}
                    to="/admin/users"
                  >
                    <i className="bi bi-people-fill me-2"></i>Manage Users
                  </NavLink>
                </li>
                 <li className="nav-item">
                  <NavLink
                    className={`nav-link ${location.pathname.startsWith('/admin/profile') ? 'active' : ''}`}
                    to="/admin/profile"
                  >
                    <i className="bi bi-person-circle me-2"></i>Profile
                  </NavLink>
                </li>
              </>
            )}

            {currentAuth.isLoggedIn && (
              <li className="nav-item ms-lg-2">
                <GlobalSearchComponent />
              </li>
            )}

            {currentAuth.isLoggedIn ? (
              <li className="nav-item ms-lg-3">
                <button onClick={handleLogout} className={`nav-link btn btn-link ${isDark ? 'text-light' : 'text-danger'}`}>
                  <i className="bi bi-box-arrow-right me-2"></i>Logout
                </button>
              </li>
            ) : (
              <>
                <li className="nav-item">
                  <NavLink className="nav-link" to="/login" >
                    Login
                  </NavLink>
                </li>
                <li className="nav-item">
                  <NavLink className="nav-link" to="/register" >
                    Register
                  </NavLink>
                </li>
              </>
            )}
            <li className="nav-item ms-lg-3">
              <ThemeToggle />
            </li>
          </ul>
        </div>
      </div>
    </nav>
  );
};

export default Navbar;