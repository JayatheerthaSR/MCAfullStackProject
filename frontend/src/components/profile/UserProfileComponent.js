import React, { useState, useEffect, useContext } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import api from '../../api';
import 'bootstrap/dist/css/bootstrap.min.css';
import { ThemeContext } from '../../contexts/ThemeContext';

const UserProfileComponent = () => {
  console.log("UserProfileComponent rendered!");
  const userId = localStorage.getItem('userId');
  const role = localStorage.getItem('role');
  console.log("userId before useEffect:", userId);
  console.log("role before useEffect:", role);

  const [profile, setProfile] = useState(null);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();
  const { theme } = useContext(ThemeContext);
  const isDark = theme === 'dark';

  useEffect(() => {
    const fetchProfile = async () => {
      setLoading(true);
      setError('');
      try {
        console.log("userId inside useEffect:", userId);
        console.log("role inside useEffect:", role);
        if (!userId || !role) {
          throw new Error('Missing userId or role in localStorage.');
        }

        let profileEndpoint = '';
        if (role.toLowerCase() === 'customer') {
          profileEndpoint = `/customers/${userId}/profile`;
        } else if (role.toLowerCase() === 'admin') {
          profileEndpoint = `/admins/${userId}/profile`;
        } else {
          setError('Unsupported user role.');
          setLoading(false);
          return;
        }

        const response = await api.get(profileEndpoint);
        setProfile(response.data);

      } catch (error) {
        setError(error.message || 'Failed to load profile information.');
        console.error("Error fetching profile:", error);
      } finally {
        setLoading(false);
      }
    };

    if (userId && role) {
      fetchProfile();
    }
  }, [userId, role]);

  if (error) {
    return <div className={`alert alert-danger mt-4 container ${isDark ? 'bg-dark text-light border-secondary' : ''}`} role="alert">Error: {error}</div>;
  }

  if (loading) {
    return <div className="d-flex justify-content-center mt-5">
      <div className={`spinner-border text-primary ${isDark ? 'border-light' : ''}`} role="status">
        <span className="visually-hidden">Loading profile...</span>
      </div>
    </div>;
  }

  if (!profile) {
    return <div className={`alert alert-warning mt-4 container ${isDark ? 'bg-dark text-light border-secondary' : ''}`} role="alert">No profile data available.</div>;
  }

  const getProfileTitle = () => {
    return role ? `${role.charAt(0).toUpperCase() + role.slice(1)} Profile` : 'User Profile';
  };

  const getEditProfileRoute = () => {
    return role ? `/${role.toLowerCase()}/profile/update` : '/profile/update';
  };

  const getDashboardRoute = () => {
    return role ? `/${role.toLowerCase()}/dashboard` : '/dashboard';
  };

  const getChangePasswordRoute = () => {
    return role ? `/${role.toLowerCase()}/profile/change-password` : '/profile/change-password';
  };

  return (
    <div className={`container mt-5 ${isDark ? 'bg-dark text-light' : 'bg-light text-dark'}`}>
      <h2 className={`mb-4 ${isDark ? 'text-primary' : 'text-primary'}`}>{getProfileTitle()}</h2>
      <div className={`card shadow-lg mb-4 ${isDark ? 'bg-secondary border-secondary' : ''}`}>
        <div className="card-body">
          <h5 className={`card-title text-muted mb-3 ${isDark ? 'text-light' : ''}`}>Personal Information</h5>
          <div className="row mb-3">
            <div className="col-md-6">
              <strong>First Name:</strong> <span className={`ms-2 ${isDark ? 'text-light' : 'text-dark'}`}>{profile.firstName || profile.user?.firstName || 'N/A'}</span>
            </div>
            <div className="col-md-6">
              <strong>Last Name:</strong> <span className={`ms-2 ${isDark ? 'text-light' : 'text-dark'}`}>{profile.lastName || profile.user?.lastName || 'N/A'}</span>
            </div>
          </div>
          <div className="row mb-3">
            <div className="col-md-6">
              <strong>Email:</strong> <span className={`ms-2 ${isDark ? 'text-light' : 'text-dark'}`}>{profile.email || profile.user?.email || 'N/A'}</span>
            </div>
            <div className="col-md-6">
              <strong>Phone Number:</strong> <span className={`ms-2 ${isDark ? 'text-light' : 'text-dark'}`}>{profile.phoneNumber || profile.user?.phoneNumber || profile.phone_number || 'N/A'}</span>
            </div>
          </div>
          <div className="mb-3">
            <strong>Address:</strong> <span className={`ms-2 ${isDark ? 'text-light' : 'text-dark'}`}>{profile.address || profile.user?.address || 'N/A'}</span>
          </div>
        </div>
      </div>

      {profile?.accounts?.length > 0 && (
        <>
          <h3 className={`mb-3 ${isDark ? 'text-info' : 'text-info'}`}>Accounts</h3>
          <div className="list-group mb-4">
            {profile.accounts.map((account) => (
              <div key={account.accountNumber} className={`list-group-item list-group-item-action d-flex justify-content-between align-items-center ${isDark ? 'bg-dark border-secondary text-light' : ''}`}>
                <div>
                  <h6 className="mb-0"><strong>Account Number:</strong> {account.accountNumber}</h6>
                  <small className={`text-muted ${isDark ? 'text-light' : ''}`}>Type: <span className="badge bg-secondary">{account.accountType}</span></small>
                </div>
                <div>
                  <strong>Balance:</strong> <span className="badge bg-success">{account.balance}</span>
                </div>
              </div>
            ))}
          </div>
        </>
      )}

      <div className="d-flex gap-2">
        <Link to={getEditProfileRoute()} className="btn btn-primary me-2">Edit Profile</Link>
        <Link to={getChangePasswordRoute()} className="btn btn-warning me-2">Change Password</Link>
        <button onClick={() => navigate(getDashboardRoute())} className={`btn btn-outline-secondary ${isDark ? 'btn-outline-light' : ''}`}>Back to Dashboard</button>
      </div>
    </div>
  );
};

export default UserProfileComponent;