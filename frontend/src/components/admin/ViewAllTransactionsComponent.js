import React, { useState, useEffect } from 'react';
import api from '../../api'; // Adjust the import path as needed

const ViewAllTransactionsComponent = () => {
  const [transactions, setTransactions] = useState([]);
  const [error, setError] = useState('');

  useEffect(() => {
    const fetchAllTransactions = async () => {
      setError('');
      try {
        const response = await api.get('/admin/transactions');
        setTransactions(response.data);
      } catch (error) {
        setError(error.response?.data?.message || 'Failed to fetch transactions.');
      }
    };

    if (localStorage.getItem('token')) {
      fetchAllTransactions();
    } else {
      setError('Authentication token not found. Please log in as an admin.');
    }
  }, []);

  return (
    <div>
      <h2>All Transactions</h2>
      {error && <p style={{ color: 'red' }}>{error}</p>}
      {transactions.length === 0 ? (
        <p>No transactions found.</p>
      ) : (
        <table>
          <thead>
            <tr>
              <th>Transaction ID</th>
              <th>User ID</th>
              <th>Type</th>
              <th>Amount</th>
              <th>Date</th>
              <th>Description</th>
              <th>Beneficiary Account</th>
              {/* Add more columns as needed */}
            </tr>
          </thead>
          <tbody>
            {transactions.map((transaction) => (
              <tr key={transaction.transactionId}>
                <td>{transaction.transactionId}</td>
                <td>{transaction.userId}</td>
                <td>{transaction.transactionType}</td>
                <td>{transaction.amount}</td>
                <td>{new Date(transaction.transactionDate).toLocaleString()}</td>
                <td>{transaction.description}</td>
                <td>{transaction.beneficiaryAccountNumber || '-'}</td>
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

export default ViewAllTransactionsComponent;