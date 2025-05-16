import React, { useState, useEffect } from 'react';
import { NavLink, useNavigate, useLocation } from 'react-router-dom';

const Navbar = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const [isLoggedIn, setIsLoggedIn] = useState(!!localStorage.getItem('token'));
  const role = localStorage.getItem('role');
  const token = localStorage.getItem('token');

  useEffect(() => {
    const publicRoutes = ['/', '/login', '/register']; // Add all your public routes
    if (publicRoutes.includes(location.pathname)) {
      setIsLoggedIn(false);
    } else {
      setIsLoggedIn(!!localStorage.getItem('token'));
    }
  }, [location]);

  const handleLogout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('role');
    setIsLoggedIn(false);
    navigate('/login');
  };

  return (
    <nav className="navbar navbar-expand-lg navbar-light bg-white shadow-sm mb-4 rounded">
      <div className="container-fluid">
        <NavLink className="navbar-brand" to="/">
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
          <ul className="navbar-nav">
            {isLoggedIn && (
              <li className="nav-item">
                <NavLink
                  className="nav-link"
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
                    className="nav-link"
                    to="/customer/transactions"
                    
                    
                  >
                    <i className="bi bi-list-ul me-2"></i>Transactions
                  </NavLink>
                </li>
                <li className="nav-item">
                  <NavLink
                    className="nav-link"
                    to="/customer/beneficiaries"
                    
                    
                  >
                    <i className="bi bi-person-plus-fill me-2"></i>Beneficiaries
                  </NavLink>
                </li>
                <li className="nav-item">
                  <NavLink
                    className="nav-link"
                    to="/customer/transfer"
                    
                    
                  >
                    <i className="bi bi-arrow-left-right me-2"></i>Transfer
                  </NavLink>
                </li>
                <li className="nav-item">
                  <NavLink
                    className="nav-link"
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
                    className="nav-link"
                    to="/admin/customers"
                    
                    
                  >
                    <i className="bi bi-people-fill me-2"></i>Customers
                  </NavLink>
                </li>
                <li className="nav-item">
                  <NavLink
                    className="nav-link"
                    to="/admin/transactions"
                    
                    
                  >
                    <i className="bi bi-receipt-fill me-2"></i>All Transactions
                  </NavLink>
                </li>
              </>
            )}

            {isLoggedIn ? (
              <li className="nav-item">
                <button onClick={handleLogout} className="nav-link btn btn-link text-danger">
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
          </ul>
        </div>
      </div>
    </nav>
  );
};

export default Navbar;