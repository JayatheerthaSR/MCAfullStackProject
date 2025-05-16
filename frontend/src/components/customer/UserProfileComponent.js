import React from 'react';
import { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import api from '../../api';
import 'bootstrap/dist/css/bootstrap.min.css';

const UserProfileComponent = () => {
  console.log("UserProfileComponent rendered!");
  const userId = localStorage.getItem('userId');
  const customerId = localStorage.getItem('customerId');
  console.log("userId before useEffect:", userId);
  console.log("customerId before useEffect:", customerId);

  const [profile, setProfile] = useState(null);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    const fetchProfile = async () => {
      setLoading(true);
      try {
        console.log("userId inside useEffect:", userId);
        console.log("customerId inside useEffect:", customerId);
        if (!customerId || !userId) {
          throw new Error('Missing customerId or userId in localStorage.');
        }

        const response = await api.get(`/customers/${customerId}/profile?userId=${userId}`); // Relative to baseURL
        setProfile(response.data); // Assuming the profile data is directly in response.data

      } catch (error) {
        setError(error.message || 'Failed to load profile information.');
        console.error("Error fetching profile:", error);
      } finally {
        setLoading(false);
      }
    };

    if (userId && customerId) {
      fetchProfile();
    }
  }, [userId, customerId]);

  if (error) {
    return <div className="alert alert-danger mt-4 container" role="alert">Error: {error}</div>;
  }

  if (loading) {
    return <div className="d-flex justify-content-center mt-5">
      <div className="spinner-border text-primary" role="status">
        <span className="visually-hidden">Loading profile...</span>
      </div>
    </div>;
  }

  if (!profile) {
    return <div className="alert alert-warning mt-4 container" role="alert">No profile data available.</div>;
  }

  return (
    <div className="container mt-5">
      <h2 className="mb-4 text-primary">Your Profile</h2>
      <div className="card shadow-lg mb-4">
        <div className="card-body">
          <h5 className="card-title text-muted mb-3">Personal Information</h5>
          <div className="row mb-3">
            <div className="col-md-6">
              <strong>First Name:</strong> <span className="ms-2 text-dark">{profile.firstName || 'N/A'}</span>
            </div>
            <div className="col-md-6">
              <strong>Last Name:</strong> <span className="ms-2 text-dark">{profile.lastName || 'N/A'}</span>
            </div>
          </div>
          <div className="row mb-3">
            <div className="col-md-6">
              <strong>Email:</strong> <span className="ms-2 text-dark">{profile.email || 'N/A'}</span>
            </div>
            <div className="col-md-6">
              <strong>Phone Number:</strong> <span className="ms-2 text-dark">{profile.phoneNumber || 'N/A'}</span>
            </div>
          </div>
          <div className="mb-3">
            <strong>Address:</strong> <span className="ms-2 text-dark">{profile.address || 'N/A'}</span>
          </div>
        </div>
      </div>

      <h3 className="mb-3 text-info">Accounts</h3>
      {profile.accounts && profile.accounts.length > 0 ? (
        <div className="list-group mb-4">
          {profile.accounts.map((account) => (
            <div key={account.accountNumber} className="list-group-item list-group-item-action d-flex justify-content-between align-items-center">
              <div>
                <h6 className="mb-0"><strong>Account Number:</strong> {account.accountNumber}</h6>
                <small className="text-muted">Type: <span className="badge bg-secondary">{account.accountType}</span></small>
              </div>
              <div>
                <strong>Balance:</strong> <span className="badge bg-success">{account.balance}</span>
              </div>
            </div>
          ))}
        </div>
      ) : (
        <div className="alert alert-info mt-3" role="alert">No accounts available.</div>
      )}

      <div className="d-flex gap-2">
        <Link to="/customer/profile/update" className="btn btn-primary me-2">Edit Profile</Link>
        {/* Add the Change Password link here */}
        <Link to="/customer/profile/change-password" className="btn btn-warning me-2">Change Password</Link>
        <button onClick={() => navigate('../dashboard')} className="btn btn-outline-secondary">Back to Dashboard</button>
      </div>
    </div>
  );
};

export default UserProfileComponent;