import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../../api';
import 'bootstrap/dist/css/bootstrap.min.css';

const UpdateProfileComponent = () => {
  const [firstName, setFirstName] = useState('');
  const [lastName, setLastName] = useState('');
  const [email, setEmail] = useState('');
  const [phoneNumber, setPhoneNumber] = useState('');
  const [address, setAddress] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false); // Declare loading state
  const [profile, setProfile] = useState(null); // Declare profile state
  const navigate = useNavigate();
  const userId = localStorage.getItem('userId');
  const customerId = localStorage.getItem('customerId'); // Get customerId

  useEffect(() => {
    const fetchProfile = async () => {
      setLoading(true);
      try {
        console.log("userId inside useEffect:", userId);
        console.log("customerId inside useEffect:", customerId);
        if (!customerId || !userId) {
          throw new Error('Missing customerId or userId in localStorage.');
        }

        const response = await api.get(`/customers/${customerId}/profile?userId=${userId}`);
        setProfile(response.data);
        setFirstName(response.data.firstName || ''); // Try accessing directly first
        setLastName(response.data.lastName || '');
        setEmail(response.data.email || '');
        setPhoneNumber(response.data.phoneNumber || '');
        setAddress(response.data.address || '');

        // Check if 'user' property exists and access nested properties if it does
        if (response.data.user) {
          setFirstName(response.data.user.firstName || '');
          setLastName(response.data.user.lastName || '');
          setEmail(response.data.user.email || '');
          setPhoneNumber(response.data.user.phoneNumber || '');
          setAddress(response.data.user.address || '');
        }

      } catch (error) {
        setError('Failed to load profile information.');
        console.error("Error fetching profile:", error);
      } finally {
        setLoading(false);
      }
    };

    if (userId && customerId && localStorage.getItem('token')) {
      fetchProfile();
    }
  }, [userId, customerId]);

  const handleSubmit = async (event) => {
    event.preventDefault();
    setError('');
    try {
      await api.put(`/customers/profile?userId=${userId}`, { // Keep using userId for update as per your backend
        firstName,
        lastName,
        email,
        phoneNumber,
        address,
      });
      navigate('/customer/profile');
    } catch (error) {
      setError(error.response?.data?.message || 'Failed to update profile.');
    }
  };

  return (
    <div className="container mt-5">
      <h2 className="mb-4 text-primary">Update Profile</h2>
      {error && <div className="alert alert-danger mb-3" role="alert">{error}</div>}
      <form onSubmit={handleSubmit} className="shadow-lg p-4 rounded">
        <div className="mb-3">
          <label htmlFor="firstName" className="form-label">First Name:</label>
          <input
            type="text"
            className="form-control"
            id="firstName"
            value={firstName}
            onChange={(e) => setFirstName(e.target.value)}
            required
          />
        </div>
        <div className="mb-3">
          <label htmlFor="lastName" className="form-label">Last Name:</label>
          <input
            type="text"
            className="form-control"
            id="lastName"
            value={lastName}
            onChange={(e) => setLastName(e.target.value)}
            required
          />
        </div>
        <div className="mb-3">
          <label htmlFor="email" className="form-label">Email:</label>
          <input
            type="email"
            className="form-control"
            id="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            required
          />
        </div>
        <div className="mb-3">
          <label htmlFor="phoneNumber" className="form-label">Phone Number:</label>
          <input
            type="tel"
            className="form-control"
            id="phoneNumber"
            value={phoneNumber}
            onChange={(e) => setPhoneNumber(e.target.value)}
          />
        </div>
        <div className="mb-3">
          <label htmlFor="address" className="form-label">Address:</label>
          <textarea
            className="form-control"
            id="address"
            value={address}
            onChange={(e) => setAddress(e.target.value)}
            rows="3"
          ></textarea>
        </div>
        <div className="d-flex gap-2">
          <button type="submit" className="btn btn-primary">
            Update Profile
          </button>
          <button onClick={() => navigate('/customer/profile')} className="btn btn-outline-secondary">
            Cancel
          </button>
        </div>
      </form>
    </div>
  );
};

export default UpdateProfileComponent;