import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../../api'; // Ensure this import is present

const UpdateProfileComponent = () => {
  const [firstName, setFirstName] = useState('');
  const [lastName, setLastName] = useState('');
  const [email, setEmail] = useState('');
  const [phoneNumber, setPhoneNumber] = useState('');
  const [address, setAddress] = useState('');
  const [error, setError] = useState('');
  const navigate = useNavigate();
  const userId = localStorage.getItem('userId');

  useEffect(() => {
    const fetchProfile = async () => {
      try {
        const response = await api.get(`/customers/profile?userId=${userId}`);
        if (response.data) {
          setFirstName(response.data.user.firstName);
          setLastName(response.data.user.lastName);
          setEmail(response.data.user.email);
          setPhoneNumber(response.data.user.phoneNumber);
          setAddress(response.data.user.address);
        }
      } catch (error) {
        setError('Failed to load profile information.');
      }
    };

    if (userId && localStorage.getItem('token')) {
      fetchProfile();
    }
  }, [userId]);

  const handleSubmit = async (event) => {
    event.preventDefault();
    setError('');
    try {
      await api.put(`/customers/profile?userId=${userId}`, {
        firstName,
        lastName,
        email,
        phoneNumber,
        address,
      });
      navigate('/customer/profile'); // Or wherever you want to redirect after update
    } catch (error) {
      setError(error.response?.data?.message || 'Failed to update profile.');
    }
  };

  return (
    <div>
      <h2>Update Profile</h2>
      {error && <p style={{ color: 'red' }}>{error}</p>}
      <form onSubmit={handleSubmit}>
        <div>
          <label htmlFor="firstName">First Name:</label>
          <input type="text" id="firstName" value={firstName} onChange={(e) => setFirstName(e.target.value)} required />
        </div>
        <div>
          <label htmlFor="lastName">Last Name:</label>
          <input type="text" id="lastName" value={lastName} onChange={(e) => setLastName(e.target.value)} required />
        </div>
        <div>
          <label htmlFor="email">Email:</label>
          <input type="email" id="email" value={email} onChange={(e) => setEmail(e.target.value)} required />
        </div>
        <div>
          <label htmlFor="phoneNumber">Phone Number:</label>
          <input type="tel" id="phoneNumber" value={phoneNumber} onChange={(e) => setPhoneNumber(e.target.value)} />
        </div>
        <div>
          <label htmlFor="address">Address:</label>
          <textarea id="address" value={address} onChange={(e) => setAddress(e.target.value)} />
        </div>
        <button type="submit">Update Profile</button>
        <button onClick={() => navigate('/customer/profile')}>Cancel</button>
      </form>
    </div>
  );
};

export default UpdateProfileComponent;