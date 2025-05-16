import React, { useState, useEffect } from 'react';
import api from '../../api';
import { useNavigate } from 'react-router-dom';

const BeneficiaryListComponent = () => {
  const [beneficiaries, setBeneficiaries] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const navigate = useNavigate();
  const customerId = localStorage.getItem('userId'); // Assuming userId corresponds to customerId

  useEffect(() => {
    const fetchBeneficiaries = async () => {
      setLoading(true);
      setError('');
      try {
        const response = await api.get(`/customers/${customerId}/beneficiaries`);
        setBeneficiaries(response.data);
        setLoading(false);
      } catch (error) {
        setError(error.response?.data?.message || 'Failed to fetch beneficiaries.');
        setLoading(false);
      }
    };

    fetchBeneficiaries();
  }, [customerId]);

  if (loading) {
    return <div className="spinner-border text-primary" role="status">
      <span className="visually-hidden">Loading beneficiaries...</span>
    </div>;
  }

  if (error) {
    return <div className="alert alert-danger" role="alert">{error}</div>;
  }

  return (
    <div>
      <h2>My Beneficiaries</h2>
      {beneficiaries.length === 0 ? (
        <div className="alert alert-info" role="alert">No beneficiaries added yet.</div>
      ) : (
        <table className="table table-striped">
          <thead>
            <tr>
              <th>Beneficiary Name</th>
              <th>Bank Name</th>
              <th>Account Number</th>
              <th>Max Transfer Limit</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {beneficiaries.map((beneficiary) => (
              <tr key={beneficiary.id}>
                <td>{beneficiary.beneficiaryName}</td>
                <td>{beneficiary.bankName}</td>
                <td>{beneficiary.accountNumber}</td>
                <td>{beneficiary.maxTransferLimit !== null ? beneficiary.maxTransferLimit : 'No Limit'}</td>
                <td>
                  {/* Add Bootstrap buttons for actions */}
                  {/* Example: <button className="btn btn-sm btn-outline-danger me-2">Delete</button> */}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
      <div className="d-flex gap-2">
        <button className="btn btn-primary" onClick={() => navigate('/customer/beneficiaries/add')}>Add New Beneficiary</button>
        <button onClick={() => navigate('../dashboard')} className="btn btn-outline-secondary">Back to Dashboard</button>
      </div>
    </div>
  );
};

export default BeneficiaryListComponent;