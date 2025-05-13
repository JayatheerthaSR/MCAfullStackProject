import React from 'react';
import { Link } from 'react-router-dom';

const CustomerDashboard = () => {
  return (
    <div>
      <h2>Customer Dashboard</h2>
      <p>Welcome, Customer!</p>
      <ul>
        <li>
          <Link to="/customer/transactions">View Transactions</Link>
        </li>
        <li>
          <Link to="/customer/beneficiaries/add">Add Beneficiary</Link>
        </li>
        <li>
          <Link to="/customer/beneficiaries/transfer">Transfer Money</Link>
        </li>
        <li>
          <Link to="/customer/profile">View/Edit Profile</Link>
        </li>
      </ul>
      <button onClick={() => {
        localStorage.removeItem('token');
        localStorage.removeItem('role');
        window.location.href = '/login'; // Simple logout
      }}>Logout</button>
    </div>
  );
};

export default CustomerDashboard;