import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../../api';

const TransferMoneyComponent = () => {
  const [beneficiaryAccountNumber, setBeneficiaryAccountNumber] = useState('');
  const [amount, setAmount] = useState('');
  const [description, setDescription] = useState('');
  const [error, setError] = useState('');
  const navigate = useNavigate();
  const customerId = localStorage.getItem('userId'); // Assuming userId corresponds to customerId

  const handleSubmit = async (event) => {
    event.preventDefault();
    setError('');
    try {
      await api.post(`/customers/transfer?customerId=${customerId}`, {
        beneficiaryAccountNumber,
        amount: parseFloat(amount),
        description,
      });
      navigate('/customer'); // Redirect to customer dashboard or transfer success page
    } catch (error) {
      setError(error.response?.data?.message || 'Failed to transfer money.');
    }
  };

  return (
    <div>
      <h2>Transfer Money</h2>
      {error && <p style={{ color: 'red' }}>{error}</p>}
      <form onSubmit={handleSubmit}>
        <div>
          <label htmlFor="beneficiaryAccountNumber">Beneficiary Account Number:</label>
          <input type="text" id="beneficiaryAccountNumber" value={beneficiaryAccountNumber} onChange={(e) => setBeneficiaryAccountNumber(e.target.value)} required />
        </div>
        <div>
          <label htmlFor="amount">Amount:</label>
          <input type="number" id="amount" value={amount} onChange={(e) => setAmount(e.target.value)} required />
        </div>
        <div>
          <label htmlFor="description">Description (Optional):</label>
          <textarea id="description" value={description} onChange={(e) => setDescription(e.target.value)} />
        </div>
        <button type="submit">Transfer</button>
        <button onClick={() => navigate('/customer')}>Cancel</button>
      </form>
    </div>
  );
};

export default TransferMoneyComponent;