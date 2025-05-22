import { useState, useContext } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../../api';
import './AddBeneficiaryComponent.css';
import 'bootstrap/dist/css/bootstrap.min.css';
import { ThemeContext } from '../../contexts/ThemeContext';

const AddBeneficiaryComponent = () => {
  const [beneficiaryName, setBeneficiaryName] = useState('');
  const [bankName, setBankName] = useState('');
  const [accountNumber, setAccountNumber] = useState('');
  const [maxTransferLimit, setMaxTransferLimit] = useState('');
  const [error, setError] = useState('');
  const navigate = useNavigate();
  const customerId = localStorage.getItem('userId');
  const { theme } = useContext(ThemeContext);
  const isDark = theme === 'dark';

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
        navigate('/customer/beneficiaries');
      } else {
        setError('Failed to add beneficiary.');
      }
    } catch (error) {
      if (error.response && error.response.status === 409) {
        setError(error.response.data);
      } else if (error.response && error.response.data && error.response.data.message) {
        setError(error.response.data.message);
      } else {
        setError('Failed to add beneficiary. Please check your connection or try again.');
      }
    }
  };

  return (
    <div className={`container mt-5 ${isDark ? 'bg-dark text-light' : 'bg-light text-dark'}`}>
      <div className="row justify-content-center">
        <div className="col-md-6">
          <div className={`card p-4 ${isDark ? 'bg-secondary border-secondary text-light' : 'bg-white'}`}>
            <h2 className="mb-4">Add New Beneficiary</h2>
            {error && <div className="alert alert-danger" role="alert">{error}</div>}
            <form onSubmit={handleSubmit}>
              <div className="mb-3">
                <label htmlFor="beneficiaryName" className="form-label">Beneficiary Name:</label>
                <input
                  type="text"
                  className={`form-control ${isDark ? 'bg-dark text-light border-secondary' : ''}`}
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
                  className={`form-control ${isDark ? 'bg-dark text-light border-secondary' : ''}`}
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
                  className={`form-control ${isDark ? 'bg-dark text-light border-secondary' : ''}`}
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
                  className={`form-control ${isDark ? 'bg-dark text-light border-secondary' : ''}`}
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
                  className={`btn btn-secondary ${isDark ? 'btn-outline-light' : ''}`}
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