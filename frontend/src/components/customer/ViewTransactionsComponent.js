import React, { useState, useEffect } from 'react';
import api from '../../api'; // Adjust the import path if needed

const ViewTransactionsComponent = () => {
  const [transactions, setTransactions] = useState([]);
  const [error, setError] = useState('');
  const userId = localStorage.getItem('userId');

  useEffect(() => {
    const fetchTransactions = async () => {
      setError('');
      try {
        const response = await api.get(`/customers/transactions?userId=${userId}`);
        setTransactions(response.data);
      } catch (error) {
        setError(error.response?.data || 'Failed to fetch transactions.');
      }
    };

    if (userId && localStorage.getItem('token')) { // Ensure token is also present
      fetchTransactions();
    } else if (!userId) {
      setError('User ID not found. Please log in again.');
    } else {
      setError('Authentication token not found. Please log in again.');
    }
  }, [userId]);

  return (
    <div>
      <h2>Your Transactions</h2>
      {error && <p style={{ color: 'red' }}>{error}</p>}
      {transactions.length === 0 ? (
        <p>No transactions found.</p>
      ) : (
        <table className="transactions-table">
          <thead>
            <tr>
              <th>Transaction ID</th>
              <th>Type</th>
              <th>Amount</th>
              <th>Date</th>
              <th>Description</th>
              <th>Beneficiary Account</th>
            </tr>
          </thead>
          <tbody>
            {transactions.map((transaction) => (
              <tr key={transaction.transactionId}>
                <td>{transaction.transactionId}</td>
                <td>{transaction.transactionType}</td>
                <td>{transaction.amount}</td>
                <td>{new Date(transaction.transactionDate).toLocaleString()}</td>
                <td>{transaction.description}</td>
                <td>{transaction.beneficiaryAccountNumber || '-'}</td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
      <button onClick={() => window.history.back()}>Back to Dashboard</button>
    </div>
  );
};

export default ViewTransactionsComponent;