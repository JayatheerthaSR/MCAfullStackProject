import React, { useState, useEffect } from 'react';
import api from '../../api'; // Ensure this import is present
import { Link } from 'react-router-dom';

const UserProfileComponent = () => {
  const [profile, setProfile] = useState(null);
  const [error, setError] = useState('');
  const userId = localStorage.getItem('userId');

  useEffect(() => {
    const fetchProfile = async () => {
      try {
        const response = await api.get(`/customers/profile?userId=${userId}`);
        setProfile(response.data);
      } catch (error) {
        setError('Failed to load profile information.');
      }
    };

    if (userId && localStorage.getItem('token')) {
      fetchProfile();
    }
  }, [userId]);

  if (error) {
    return <div>Error: {error}</div>;
  }

  if (!profile) {
    return <div>Loading profile...</div>;
  }

  return (
    <div>
      <h2>Your Profile</h2>
      <p><strong>First Name:</strong> {profile.user.firstName}</p>
      <p><strong>Last Name:</strong> {profile.user.lastName}</p>
      <p><strong>Email:</strong> {profile.user.email}</p>
      <p><strong>Phone Number:</strong> {profile.user.phoneNumber || 'N/A'}</p>
      <p><strong>Address:</strong> {profile.user.address || 'N/A'}</p>
      <p><strong>Account Number:</strong> {profile.accountNumber}</p>
      <p><strong>Available Balance:</strong> {profile.availableBalance}</p>
      <Link to="/customer/profile/update">Edit Profile</Link>
      <button onClick={() => window.history.back()}>Back to Dashboard</button>
    </div>
  );
};

export default UserProfileComponent;