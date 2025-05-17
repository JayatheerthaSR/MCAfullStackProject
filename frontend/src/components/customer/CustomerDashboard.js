import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import api from '../../api';
import 'bootstrap-icons/font/bootstrap-icons.css';
import './CustomerDashboard.css'; // Optional: Create a CSS file for styling

const CustomerDashboard = () => {
  const customerId = localStorage.getItem('userId');
  const [accounts, setAccounts] = useState([]);
  const [accountError, setAccountError] = useState(''); // Separate error for accounts
  const [loadingAccounts, setLoadingAccounts] = useState(true);

  useEffect(() => {
    const fetchAccounts = async () => {
      setLoadingAccounts(true);
      setAccountError('');
      try {
        const response = await api.get(`/customers/${customerId}/accounts`);
        if (response.status === 200) {
          setAccounts(response.data);
        } else if (response.status === 404) {
          setAccountError('No accounts found for this customer.');
          setAccounts([]);
        } else {
          setAccountError('Failed to fetch accounts.');
          setAccounts([]);
        }
      } catch (error) {
        console.error('Error fetching accounts:', error);
        setAccountError('Failed to fetch accounts. Please check your connection.');
        setAccounts([]);
      } finally {
        setLoadingAccounts(false);
      }
    };

    if (customerId) {
      fetchAccounts();
    }
  }, [customerId]);

  // Calculate total balance
  const totalBalance = accounts.reduce((sum, account) => sum + account.balance, 0);

  return (
    <div className="customer-dashboard-container">
      <h2>Customer Dashboard</h2>
      <p>Welcome to your dashboard! Here you can manage your accounts, view transactions, and more.</p>

      {/* Display Accounts and Balances */}
      <div className="mb-4 accounts-section">
        <h3>Your Accounts</h3>
        {loadingAccounts ? (
          <div className="d-flex justify-content-center">
            <div className="spinner-border text-primary" role="status">
              <span className="visually-hidden">Loading accounts...</span>
            </div>
          </div>
        ) : accountError ? (
          <div className="alert alert-danger" role="alert">{accountError}</div>
        ) : accounts.length > 0 ? (
          <>
            <div className="list-group">
              {accounts.map((account) => (
                <div key={account.accountNumber} className="list-group-item d-flex justify-content-between align-items-center account-item">
                  <div>
                    <strong>Account Number:</strong> {account.accountNumber}
                    <br />
                    <small className="text-muted">Type: {account.accountType}</small>
                  </div>
                  <span className="badge bg-success rounded-pill balance-badge">Balance: ${account.balance}</span>
                </div>
              ))}
            </div>
            <div className="mt-3 total-balance">
              <strong>Total Balance: ${totalBalance.toFixed(2)}</strong>
            </div>
          </>
        ) : (
          <div className="alert alert-info" role="alert">No accounts available.</div>
        )}
      </div>

      <div className="row row-cols-1 row-cols-md-2 row-cols-lg-3 g-4 mb-4">
        <div className="col">
          <div className="card shadow h-100 dashboard-card">
            <div className="card-body">
              <h5 className="card-title"><i className="bi bi-list-ul me-2"></i>View Transactions</h5>
              <p className="card-text">Check your recent account activity and transaction history.</p>
              <Link to="/customer/transactions" className="btn btn-primary dashboard-button">View Details</Link>
            </div>
          </div>
        </div>
        <div className="col">
          <div className="card shadow h-100 dashboard-card">
            <div className="card-body">
              <h5 className="card-title"><i className="bi bi-person-lines-fill me-2"></i>Beneficiaries</h5>
              <p className="card-text">Manage and view your list of added beneficiaries.</p>
              <Link to="/customer/beneficiaries" className="btn btn-info text-white dashboard-button">View List</Link>
            </div>
          </div>
        </div>
        <div className="col">
          <div className="card shadow h-100 dashboard-card">
            <div className="card-body">
              <h5 className="card-title"><i className="bi bi-arrow-left-right me-2"></i>Transfer Money</h5>
              <p className="card-text">Make secure transfers to your added beneficiaries.</p>
              <Link to="/customer/transfer" className="btn btn-primary dashboard-button">Initiate Transfer</Link>
            </div>
          </div>
        </div>
        <div className="col">
          <div className="card shadow h-100 dashboard-card">
            <div className="card-body">
              <h5 className="card-title"><i className="bi bi-person-vcard me-2"></i>Profile</h5>
              <p className="card-text">Manage your personal information and account details.</p>
              <Link to="/customer/profile" className="btn btn-secondary dashboard-button">View Profile</Link>
            </div>
          </div>
        </div>
        <div className="col">
          <div className="card shadow h-100 dashboard-card">
            <div className="card-body">
              <h5 className="card-title"><i className="bi bi-key-fill me-2"></i>Change Password</h5>
              <p className="card-text">Update your account password for enhanced security.</p>
              <Link to="/customer/profile/change-password" className="btn btn-warning dashboard-button">Change Password</Link>
            </div>
          </div>
        </div>
        {/* More cards can be added here */}
      </div>
    </div>
  );
};

export default CustomerDashboard;