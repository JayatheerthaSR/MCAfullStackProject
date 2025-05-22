import { useState, useEffect, useContext } from 'react';
import { Link } from 'react-router-dom';
import api from '../../api';
import 'bootstrap-icons/font/bootstrap-icons.css';
import './CustomerDashboard.css';

import { ThemeContext } from '../../contexts/ThemeContext';

const CustomerDashboard = () => {
  const customerId = localStorage.getItem('userId');
  const [accounts, setAccounts] = useState([]);
  const [accountError, setAccountError] = useState('');
  const [loadingAccounts, setLoadingAccounts] = useState(true);
  const { theme } = useContext(ThemeContext);
  const isDark = theme === 'dark';

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

  const totalBalance = accounts.reduce((sum, account) => sum + account.balance, 0);

  return (
    <div className={`customer-dashboard-container py-5 ${isDark ? 'bg-dark text-light' : 'bg-light text-dark'}`}>
      <div className="container">
        <h2 className="mb-4">Customer Dashboard</h2>
        <p className="lead">Welcome to your dashboard! Here you can manage your accounts, view transactions, and more.</p>

        <div className="mb-4">
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
              <ul className="list-group">
                {accounts.map((account) => (
                  <li key={account.accountNumber} className={`list-group-item d-flex justify-content-between align-items-center ${isDark ? 'bg-secondary text-light' : ''}`}>
                    <div>
                      <strong>Account Number:</strong> {account.accountNumber}
                      <br />
                      <small className="text-muted">Type: <span className="bg-info badge">{account.accountType}</span></small>
                    </div>
                    <span className="badge bg-success rounded-pill">Balance: {account.balance}</span>
                  </li>
                ))}
              </ul>
              <div className="mt-3">
                <strong>Total Balance: <span className="text-success">{totalBalance.toFixed(2)}</span></strong>
              </div>
            </>
          ) : (
            <div className="alert alert-info" role="alert">No accounts available.</div>
          )}
        </div>

        <div className="row row-cols-1 row-cols-md-2 row-cols-lg-3 g-4 mb-4">
          <div className="col">
            <div className={`card shadow h-100 ${isDark ? 'bg-dark text-light' : 'bg-white text-dark'}`}>
              <div className="card-body">
                <h5 className="card-title"><i className="bi bi-list-ul me-2 text-primary"></i>View Transactions</h5>
                <p className="card-text">Check your recent account activity and transaction history.</p>
                <Link to="/customer/transactions" className="btn btn-primary">View Details</Link>
              </div>
            </div>
          </div>
          <div className="col">
            <div className={`card shadow h-100 ${isDark ? 'bg-dark text-light' : 'bg-white text-dark'}`}>
              <div className="card-body">
                <h5 className="card-title"><i className="bi bi-person-lines-fill me-2 text-info"></i>Beneficiaries</h5>
                <p className="card-text">Manage and view your list of added beneficiaries.</p>
                <Link to="/customer/beneficiaries" className="btn btn-info">View List</Link>
              </div>
            </div>
          </div>
          <div className="col">
            <div className={`card shadow h-100 ${isDark ? 'bg-dark text-light' : 'bg-white text-dark'}`}>
              <div className="card-body">
                <h5 className="card-title"><i className="bi bi-arrow-left-right me-2 text-success"></i>Transfer Money</h5>
                <p className="card-text">Make secure transfers to your added beneficiaries.</p>
                <Link to="/customer/transfer" className="btn btn-success">Initiate Transfer</Link>
              </div>
            </div>
          </div>
          <div className="col">
            <div className={`card shadow h-100 ${isDark ? 'bg-dark text-light' : 'bg-white text-dark'}`}>
              <div className="card-body">
                <h5 className="card-title"><i className="bi bi-person-vcard me-2 text-secondary"></i>Profile</h5>
                <p className="card-text">Manage your personal information and account details.</p>
                <Link to="/customer/profile" className="btn btn-secondary">View Profile</Link>
              </div>
            </div>
          </div>
          <div className="col">
            <div className={`card shadow h-100 ${isDark ? 'bg-dark text-light' : 'bg-white text-dark'}`}>
              <div className="card-body">
                <h5 className="card-title"><i className="bi bi-key-fill me-2 text-warning"></i>Change Password</h5>
                <p className="card-text">Update your account password for enhanced security.</p>
                <Link to="/customer/profile/change-password" className="btn btn-warning">Change Password</Link>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default CustomerDashboard;