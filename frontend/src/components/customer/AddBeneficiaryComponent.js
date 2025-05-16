import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../../api';
import './AddBeneficiaryComponent.css'; // Keep your custom styles if any

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
    const response = await api.post(`/customers/${customerId}/beneficiaries`, {
      beneficiaryName,
      bankName,
      accountNumber,
      maxTransferLimit: parseFloat(maxTransferLimit) || null,
    });

    if (response.status === 201) {
      navigate('/customer/beneficiaries'); // Redirect to beneficiaries list on success
    } else {
      setError('Failed to add beneficiary.'); // Generic error for unexpected success responses
    }
  } catch (error) {
    if (error.response && error.response.status === 409) {
      setError(error.response.data); // Display the error message from the backend
    } else if (error.response && error.response.data && error.response.data.message) {
      setError(error.response.data.message); // Display specific error message from backend
    } else {
      setError('Failed to add beneficiary. Please check your connection or try again.');
    }
  }
};

  return (
    <div className="container mt-5">
      <div className="row justify-content-center">
        <div className="col-md-6">
          <div className="card p-4">
            <h2 className="mb-4">Add New Beneficiary</h2>
            {error && <div className="alert alert-danger" role="alert">{error}</div>}
            <form onSubmit={handleSubmit}>
              <div className="mb-3">
                <label htmlFor="beneficiaryName" className="form-label">Beneficiary Name:</label>
                <input
                  type="text"
                  className="form-control"
                  id="beneficiaryName"
                  value={beneficiaryName}
                  onChange={(e) => setBeneficiaryName(e.target.value)}
                  required
                />
              </div>
              <div className="mb-3">
                <label htmlFor="bankName" className="form-label">Bank Name:</label>
                <input
                  type="text"
                  className="form-control"
                  id="bankName"
                  value={bankName}
                  onChange={(e) => setBankName(e.target.value)}
                  required
                />
              </div>
              <div className="mb-3">
                <label htmlFor="accountNumber" className="form-label">Account Number:</label>
                <input
                  type="text"
                  className="form-control"
                  id="accountNumber"
                  value={accountNumber}
                  onChange={(e) => setAccountNumber(e.target.value)}
                  required
                />
              </div>
              <div className="mb-3">
                <label htmlFor="maxTransferLimit" className="form-label">Max Transfer Limit:</label>
                <input
                  type="number"
                  className="form-control"
                  id="maxTransferLimit"
                  value={maxTransferLimit}
                  onChange={(e) => setMaxTransferLimit(e.target.value)}
                />
              </div>
              <div className="d-grid gap-2">
                <button type="submit" className="btn btn-primary me-2">
                  Add Beneficiary
                </button>
                <button
                  type="button"
                  onClick={() => navigate('/customer/beneficiaries')}
                  className="btn btn-secondary"
                >
                  Cancel
                </button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>
  );
};

export default AddBeneficiaryComponent;