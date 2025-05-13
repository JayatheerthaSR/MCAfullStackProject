import React from 'react';
import { Link } from 'react-router-dom';

const AdminDashboard = () => {
  return (
    <div>
      <h2>Admin Dashboard</h2>
      <p>Welcome, Administrator!</p>
      <ul>
        <li>
          <Link to="/admin/customers">View Customer Details</Link>
        </li>
        <li>
          <Link to="/admin/transactions">View All Transactions</Link>
        </li>
        {/* Add links to other admin functionalities here */}
      </ul>
      <button onClick={() => {
        localStorage.removeItem('token');
        localStorage.removeItem('role');
        window.location.href = '/login'; // Simple logout
      }}>Logout</button>
    </div>
  );
};

export default AdminDashboard;