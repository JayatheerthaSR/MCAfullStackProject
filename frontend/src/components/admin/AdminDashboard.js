import React, { useContext } from 'react';
import { Link } from 'react-router-dom';
import { ThemeContext } from '../../contexts/ThemeContext';

const AdminDashboard = () => {
  const { theme } = useContext(ThemeContext);
  const isDark = theme === 'dark';

  const handleLogout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('role');
    window.location.href = '/login';
  };

  return (
    <div className={`container py-5 ${isDark ? 'bg-dark text-light' : 'bg-light text-dark'}`}>
      <h2 className="mb-4">Admin Dashboard</h2>
      <p className="lead">Welcome, Administrator! Here you can manage various aspects of the application.</p>

      <div className="row row-cols-1 row-cols-md-2 g-4 mb-4">
        <div className="col">
          <div className={`card shadow h-100 ${isDark ? 'bg-secondary text-light' : 'bg-white text-dark'}`}>
            <div className="card-body">
              <h5 className="card-title"><i className="bi bi-list-ul me-2 text-primary"></i>View All Transactions</h5>
              <p className="card-text">See a comprehensive history of all transactions within the system.</p>
              <Link to="/admin/transactions" className="btn btn-primary">View Details</Link>
            </div>
          </div>
        </div>

        <div className="col">
          <div className={`card shadow h-100 ${isDark ? 'bg-secondary text-light' : 'bg-white text-dark'}`}>
            <div className="card-body">
              <h5 className="card-title"><i className="bi bi-people-fill me-2 text-info"></i>Manage Users</h5>
              <p className="card-text">View, add, and manage user accounts within the banking application.</p>
              <Link to="/admin/users" className="btn btn-info">Manage Users</Link>
            </div>
          </div>
        </div>

        <div className="col">
          <div className={`card shadow h-100 ${isDark ? 'bg-secondary text-light' : 'bg-white text-dark'}`}>
            <div className="card-body">
              <h5 className="card-title"><i className="bi bi-person-vcard me-2 text-secondary"></i>View Profile</h5>
              <p className="card-text">Manage your administrator profile information.</p>
              <Link to="/admin/profile" className="btn btn-secondary">View Profile</Link>
            </div>
          </div>
        </div>

        {/* Add more cards for other admin functionalities here */}
      </div>

      
    </div>
  );
};

export default AdminDashboard;