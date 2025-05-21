// src/pages/HomePage.js
import React from 'react';
import { Link } from 'react-router-dom';
import { useContext } from 'react';
import { ThemeContext } from '../contexts/ThemeContext';

const HomePage = () => {
  const { theme } = useContext(ThemeContext);
  const isDark = theme === 'dark';

  return (
    <div className={`homepage-container ${isDark ? 'bg-dark text-light' : 'bg-light text-dark'}`}>
      <header className={`py-5 rounded-3 mb-4 ${isDark ? 'bg-secondary text-light' : 'bg-primary text-light'}`}>
        <div className="container">
          <div className="row align-items-center">
            <div className="col-lg-6 text-center text-lg-start">
              <h1 className="display-4 fw-bold">Secure and Modern Banking at Your Fingertips</h1>
              <p className="lead mt-3 mb-4">Experience the future of banking with our intuitive and feature-rich platform.</p>
              <div className="d-grid gap-3 d-md-flex justify-content-md-start">
                <Link to="/login" className={`btn btn-lg ${isDark ? 'btn-outline-light' : 'btn-light'} me-md-3`}>
                  Login
                </Link>
                <Link to="/register" className={`btn btn-lg ${isDark ? 'btn-outline-light' : 'btn-primary'}`}>
                  Register
                </Link>
              </div>
            </div>
            <div className="col-lg-6 d-none d-lg-block">
              {/* Placeholder for an image or illustration */}
              <img
                src="https://img.favpng.com/10/3/3/financial-accounting-finance-bank-financial-services-png-favpng-QGRGaUcYwgkZ5mAUPHd1vU0wc.jpg" // Replace with your actual image
                alt="Modern Banking Illustration"
                className="img-fluid rounded shadow-lg"
              />
            </div>
          </div>
        </div>
      </header>

      <section className={`py-5 ${isDark ? 'bg-dark text-light' : 'bg-white text-dark'}`}>
        <div className="container text-center">
          <h2 className="h3 fw-normal mb-4">Why Choose Our Banking App?</h2>
          <div className="row row-cols-1 row-cols-md-3 g-4">
            <div className="col">
              <div className={`card border-0 shadow-sm h-100 ${isDark ? 'bg-secondary text-light' : 'bg-light text-dark'}`}>
                <div className="card-body">
                  <h5 className="card-title text-primary">Secure Transactions</h5>
                  <p className="card-text">Benefit from advanced security measures to protect your financial data.</p>
                </div>
              </div>
            </div>
            <div className="col">
              <div className={`card border-0 shadow-sm h-100 ${isDark ? 'bg-secondary text-light' : 'bg-light text-dark'}`}>
                <div className="card-body">
                  <h5 className="card-title text-success">Easy Transfers</h5>
                  <p className="card-text">Transfer money to beneficiaries quickly and conveniently.</p>
                </div>
              </div>
            </div>
            <div className="col">
              <div className={`card border-0 shadow-sm h-100 ${isDark ? 'bg-secondary text-light' : 'bg-light text-dark'}`}>
                <div className="card-body">
                  <h5 className="card-title text-info">Manage Your Profile</h5>
                  <p className="card-text">Easily view and update your personal and account details.</p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>
    </div>
  );
};

export default HomePage;