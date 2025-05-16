import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../../api';
import 'bootstrap/dist/css/bootstrap.min.css';

const ChangePasswordComponent = ({ redirectPath }) => {
  const [currentPassword, setCurrentPassword] = useState('');
  const [newPassword, setNewPassword] = useState('');
  const [confirmNewPassword, setConfirmNewPassword] = useState('');
  const [error, setError] = useState('');
  const [successMessage, setSuccessMessage] = useState('');
  const navigate = useNavigate();

  const handleChangePassword = async (e) => {
    e.preventDefault();
    setError('');
    setSuccessMessage('');

    if (newPassword !== confirmNewPassword) {
      setError('New passwords do not match.');
      return;
    }

    try {
      const response = await api.post('auth/change-password', { // Assuming the backend endpoint is the same
        currentPassword,
        newPassword,
      });

      if (response && response.status === 200) {
        setSuccessMessage('Password changed successfully!');
        setTimeout(() => {
          navigate(redirectPath || '/customer/profile'); // Default redirect to customer profile
        }, 1500);
      } else {
        setError(response?.data?.message || 'Failed to change password.');
      }
    } catch (error) {
      setError(error.response?.data?.message || 'Failed to change password.');
      console.error('Error changing password:', error);
    }
  };

  return (
    <div className="container mt-5">
      <h2 className="mb-4 text-primary">Change Password</h2>
      {error && <div className="alert alert-danger mb-3" role="alert">{error}</div>}
      {successMessage && <div className="alert alert-success mb-3" role="alert">{successMessage}</div>}
      <form onSubmit={handleChangePassword} className="shadow-lg p-4 rounded">
        <div className="mb-3">
          <label htmlFor="currentPassword" className="form-label">Current Password</label>
          <input
            type="password"
            className="form-control"
            id="currentPassword"
            value={currentPassword}
            onChange={(e) => setCurrentPassword(e.target.value)}
            required
          />
        </div>
        <div className="mb-3">
          <label htmlFor="newPassword" className="form-label">New Password</label>
          <input
            type="password"
            className="form-control"
            id="newPassword"
            value={newPassword}
            onChange={(e) => setNewPassword(e.target.value)}
            required
          />
        </div>
        <div className="mb-3">
          <label htmlFor="confirmNewPassword" className="form-label">Confirm New Password</label>
          <input
            type="password"
            className="form-control"
            id="confirmNewPassword"
            value={confirmNewPassword}
            onChange={(e) => setConfirmNewPassword(e.target.value)}
            required
          />
        </div>
        <button type="submit" className="btn btn-primary">Change Password</button>
        <button onClick={() => navigate(-1)} className="btn btn-outline-secondary ms-2">Cancel</button>
      </form>
    </div>
  );
};

export default ChangePasswordComponent;