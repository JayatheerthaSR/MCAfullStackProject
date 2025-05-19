import React, { useState, useEffect, useContext } from 'react';
import { NavLink, useNavigate, useLocation } from 'react-router-dom';
import ThemeToggle from '../ThemeToggle';
import { ThemeContext } from '../../contexts/ThemeContext'; // Corrected import path
import GlobalSearchComponent from './GlobalSearchComponent'; // Import the search component
import ChatSupport from '../ChatSupport'; // Import the ChatSupport component

const Navbar = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const [isLoggedIn, setIsLoggedIn] = useState(!!localStorage.getItem('token'));
  const role = localStorage.getItem('role');
  const token = localStorage.getItem('token');
  const { theme } = useContext(ThemeContext);
  const isDark = theme === 'dark';

  useEffect(() => {
    const publicRoutes = ['/', '/login', '/register', '/forgot-password', '/reset-password'];
    setIsLoggedIn(!publicRoutes.includes(location.pathname) && !!localStorage.getItem('token'));
  }, [location]);

  const handleLogout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('role');
    setIsLoggedIn(false);
    navigate('/login');
  };

  return (
    <nav className={`navbar navbar-expand-lg shadow-sm mb-4 rounded ${isDark ? 'bg-dark' : 'bg-light'} text-${isDark ? 'light' : 'dark'}`}>
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
          <ul className="navbar-nav ms-auto align-items-center"> {/* Added align-items-center */}
            {isLoggedIn && (
              <li className="nav-item">
                <NavLink
                  className={`nav-link ${location.pathname.startsWith('/dashboard') ? 'active' : ''}`}
                  to={role === 'CUSTOMER' ? '/customer/dashboard' : role === 'ADMIN' ? '/admin/dashboard' : '/dashboard'}
                >
                  <i className="bi bi-house-fill me-2"></i>Dashboard
                </NavLink>
              </li>
            )}

            {isLoggedIn && role === 'CUSTOMER' && (
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

            {isLoggedIn && role === 'ADMIN' && (
              <>
                <li className="nav-item">
                  <NavLink
                    className={`nav-link ${location.pathname.startsWith('/admin/customers') ? 'active' : ''}`}
                    to="/admin/customers"
                  >
                    <i className="bi bi-people-fill me-2"></i>Customers
                  </NavLink>
                </li>
                <li className="nav-item">
                  <NavLink
                    className={`nav-link ${location.pathname.startsWith('/admin/transactions') ? 'active' : ''}`}
                    to="/admin/transactions"
                  >
                    <i className="bi bi-receipt-fill me-2"></i>All Transactions
                  </NavLink>
                </li>
              </>
            )}

            {isLoggedIn && (
              <li className="nav-item ms-lg-2">
                <GlobalSearchComponent />
              </li>
            )}

            {isLoggedIn ? (
              <li className="nav-item ms-lg-3">
                <button onClick={handleLogout} className={`nav-link btn btn-link text-danger ${isDark ? 'text-light' : ''}`}>
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
      <ChatSupport /> {/* Added the ChatSupport component here */}
    </nav>
  );
};

export default Navbar;