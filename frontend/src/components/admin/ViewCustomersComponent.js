import React, { useState, useEffect } from 'react';
import api from '../../api';

const ViewCustomersComponent = () => {
  const [customers, setCustomers] = useState([]);
  const [error, setError] = useState('');

  useEffect(() => {
    const fetchCustomers = async () => {
      setError('');
      try {
        const response = await api.get('/admin/customers');
        setCustomers(response.data);
      } catch (error) {
        setError(error.response?.data?.message || 'Failed to fetch customers.');
      }
    };

    if (localStorage.getItem('token')) {
      fetchCustomers();
    } else {
      setError('Authentication token not found. Please log in as an admin.');
    }
  }, []);

  return (
    <div>
      <h2>View Customers</h2>
      {error && <p style={{ color: 'red' }}>{error}</p>}
      {customers.length === 0 ? (
        <p>No customers found.</p>
      ) : (
        <table>
          <thead>
            <tr>
              <th>User ID</th>
              <th>Username</th>
              <th>First Name</th>
              <th>Last Name</th>
              <th>Email</th>
              <th>Role</th>
              {/* Add more columns as needed */}
            </tr>
          </thead>
          <tbody>
            {customers.map((customer) => (
              <tr key={customer.userId}>
                <td>{customer.userId}</td>
                <td>{customer.username}</td>
                <td>{customer.firstName}</td>
                <td>{customer.lastName}</td>
                <td>{customer.email}</td>
                <td>{customer.role}</td>
                {/* Add more data cells as needed */}
              </tr>
            ))}
          </tbody>
        </table>
      )}
      <button onClick={() => window.history.back()}>Back to Dashboard</button>
    </div>
  );
};

export default ViewCustomersComponent;