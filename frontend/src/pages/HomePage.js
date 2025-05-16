// src/pages/HomePage.js
import React from 'react';
import { Link } from 'react-router-dom';
import './HomePage.css';

const HomePage = () => {
  return (
    <div className="homepage-container">
      <header className="homepage-hero bg-primary text-white py-5">
        <div className="container">
          <div className="row align-items-center">
            <div className="col-lg-6 text-center text-lg-start">
              <h1 className="display-4 fw-bold">Secure and Modern Banking at Your Fingertips</h1>
              <p className="lead mt-3 mb-4">Experience the future of banking with our intuitive and feature-rich platform.</p>
              <div className="d-grid gap-3 d-md-flex justify-content-md-start">
                <Link to="/login" className="btn btn-lg btn-light me-md-3">
                  Login
                </Link>
                <Link to="/register" className="btn btn-lg btn-outline-light">
                  Register
                </Link>
              </div>
            </div>
            <div className="col-lg-6 d-none d-lg-block">
              {/* Placeholder for an image or illustration */}
              <img
                src="https://via.placeholder.com/500" // Replace with your actual image
                alt="Modern Banking Illustration"
                className="img-fluid rounded shadow-lg"
              />
            </div>
          </div>
        </div>
      </header>

      <section className="py-5 bg-light">
        <div className="container text-center">
          <h2 className="h3 fw-normal mb-4">Why Choose Our Banking App?</h2>
          <div className="row row-cols-1 row-cols-md-3 g-4">
            <div className="col">
              <div className="card border-0 shadow-sm h-100">
                <div className="card-body">
                  <h5 className="card-title">Secure Transactions</h5>
                  <p className="card-text">Benefit from advanced security measures to protect your financial data.</p>
                </div>
              </div>
            </div>
            <div className="col">
              <div className="card border-0 shadow-sm h-100">
                <div className="card-body">
                  <h5 className="card-title">Easy Transfers</h5>
                  <p className="card-text">Transfer money to beneficiaries quickly and conveniently.</p>
                </div>
              </div>
            </div>
            <div className="col">
              <div className="card border-0 shadow-sm h-100">
                <div className="card-body">
                  <h5 className="card-title">Manage Your Profile</h5>
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