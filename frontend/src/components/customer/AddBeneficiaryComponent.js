import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../../api';

const AddBeneficiaryComponent = () => {
  const [beneficiaryName, setBeneficiaryName] = useState('');
  const [bankName, setBankName] = useState('');
  const [accountNumber, setAccountNumber] = useState('');
  const [maxTransferLimit, setMaxTransferLimit] = useState('');
  const [error, setError] = useState('');
  const navigate = useNavigate();
  const customerId = localStorage.getItem('userId'); // Assuming userId corresponds to customerId

  const handleSubmit = async (event) => {
    event.preventDefault();
    setError('');
    try {
      const response = await api.post(`/customers/beneficiaries?customerId=${customerId}`, {
        beneficiaryName,
        bankName,
        accountNumber,
        maxTransferLimit: parseFloat(maxTransferLimit) || null,
      });
      navigate('/customer'); // Redirect to customer dashboard or beneficiaries list
    } catch (error) {
      setError(error.response?.data?.message || 'Failed to add beneficiary.');
    }
  };

  return (
    <div>
      <h2>Add New Beneficiary</h2>
      {error && <p style={{ color: 'red' }}>{error}</p>}
      <form onSubmit={handleSubmit}>
        <div>
          <label htmlFor="beneficiaryName">Beneficiary Name:</label>
          <input type="text" id="beneficiaryName" value={beneficiaryName} onChange={(e) => setBeneficiaryName(e.target.value)} required />
        </div>
        <div>
          <label htmlFor="bankName">Bank Name:</label>
          <input type="text" id="bankName" value={bankName} onChange={(e) => setBankName(e.target.value)} required />
        </div>
        <div>
          <label htmlFor="accountNumber">Account Number:</label>
          <input type="text" id="accountNumber" value={accountNumber} onChange={(e) => setAccountNumber(e.target.value)} required />
        </div>
        <div>
          <label htmlFor="maxTransferLimit">Max Transfer Limit:</label>
          <input type="number" id="maxTransferLimit" value={maxTransferLimit} onChange={(e) => setMaxTransferLimit(e.target.value)} />
        </div>
        <button type="submit">Add Beneficiary</button>
        <button onClick={() => navigate('/customer')}>Cancel</button>
      </form>
    </div>
  );
};

export default AddBeneficiaryComponent;