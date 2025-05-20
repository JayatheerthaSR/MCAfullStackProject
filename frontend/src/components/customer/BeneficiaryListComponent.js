import React, { useState, useEffect, useContext } from 'react';
import api from '../../api';
import { useNavigate } from 'react-router-dom';
import 'bootstrap/dist/css/bootstrap.min.css';
import { ThemeContext } from '../../contexts/ThemeContext';

const BeneficiaryListComponent = () => {
  const [beneficiaries, setBeneficiaries] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const navigate = useNavigate();
  const customerId = localStorage.getItem('userId'); // Assuming userId corresponds to customerId
  const { theme } = useContext(ThemeContext);
  const isDark = theme === 'dark';

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

  const handleDeleteBeneficiary = async (beneficiaryId, beneficiaryName) => {
    if (window.confirm(`Are you sure you want to delete beneficiary "${beneficiaryName}"? This action cannot be undone.`)) {
      try {
        // --- IMPORTANT: CORRECTED API CALL PATH ---
        // Now sending to: /api/customers/{customerId}/beneficiaries/{beneficiaryId}
        await api.delete(`/customers/${customerId}/beneficiaries/${beneficiaryId}`);
        // --- END CORRECTED API CALL PATH ---

        // Remove the deleted beneficiary from the state to update the UI
        setBeneficiaries(beneficiaries.filter(b => b.beneficiaryId !== beneficiaryId));
        alert('Beneficiary deleted successfully!');
      } catch (error) {
        setError(error.response?.data?.message || 'Failed to delete beneficiary.');
        alert(`Error deleting beneficiary: ${error.response?.data?.message || 'Unknown error'}`);
      }
    }
  };

  if (loading) {
    return (
      <div className={`d-flex justify-content-center mt-5 ${isDark ? 'text-light' : ''}`}>
        <div className={`spinner-border text-primary ${isDark ? 'border-light' : ''}`} role="status">
          <span className="visually-hidden">Loading beneficiaries...</span>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className={`alert alert-danger mt-3 ${isDark ? 'bg-dark text-light border-secondary' : ''}`} role="alert">
        {error}
      </div>
    );
  }

  return (
    <div className={`container mt-4 ${isDark ? 'bg-dark text-light' : 'bg-light text-dark'}`}>
      <h2 className="mb-4">My Beneficiaries</h2>
      {beneficiaries.length === 0 ? (
        <div className={`alert alert-info ${isDark ? 'bg-secondary text-light border-secondary' : ''}`} role="alert">
          No beneficiaries added yet.
        </div>
      ) : (
        <div className="table-responsive">
          <table className={`table table-striped ${isDark ? 'table-dark' : 'table-light'}`}>
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
                <tr key={beneficiary.beneficiaryId}>
                  <td>{beneficiary.beneficiaryName}</td>
                  <td>{beneficiary.bankName}</td>
                  <td>{beneficiary.accountNumber}</td>
                  <td>{beneficiary.maxTransferLimit !== null ? beneficiary.maxTransferLimit : 'No Limit'}</td>
                  <td>
                    <button
                      className={`btn btn-sm ${isDark ? 'btn-outline-danger' : 'btn-danger'}`}
                      onClick={() => handleDeleteBeneficiary(beneficiary.beneficiaryId, beneficiary.beneficiaryName)}
                    >
                      Delete
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
      <div className="d-flex gap-2 mt-3">
        <button className={`btn ${isDark ? 'btn-light text-dark' : 'btn-primary'}`} onClick={() => navigate('/customer/beneficiaries/add')}>Add New Beneficiary</button>
        <button onClick={() => navigate('../dashboard')} className={`btn btn-outline-secondary ${isDark ? 'btn-outline-light' : ''}`}>Back to Dashboard</button>
      </div>
    </div>
  );
};

export default BeneficiaryListComponent;