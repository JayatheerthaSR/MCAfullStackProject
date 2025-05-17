import React, { useState, useEffect, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../../api';
import 'bootstrap/dist/css/bootstrap.min.css';

const UpdateProfileComponent = () => {
  const [firstName, setFirstName] = useState('');
  const [lastName, setLastName] = useState('');
  const [email, setEmail] = useState('');
  const [newEmail, setNewEmail] = useState('');
  const [phoneNumber, setPhoneNumber] = useState('');
  const [address, setAddress] = useState('');
  const [error, setError] = useState('');
  const [successMessage, setSuccessMessage] = useState('');
  const [loading, setLoading] = useState(false);
  const [profile, setProfile] = useState(null);
  const navigate = useNavigate();
  const userId = localStorage.getItem('userId');
  const role = localStorage.getItem('role');

  // OTP Verification States
  const [isEmailChanging, setIsEmailChanging] = useState(false);
  const [otp, setOtp] = useState('');
  const [isOtpSent, setIsOtpSent] = useState(false);
  const [isOtpVerifying, setIsOtpVerifying] = useState(false);
  const [otpVerificationError, setOtpVerificationError] = useState('');
  const [isNewEmailVerified, setIsNewEmailVerified] = useState(false);

  const fetchProfile = useCallback(async () => {
    setLoading(true);
    setError('');
    if (!userId || !role) {
      setError('Missing user information. Please log in again.');
      setLoading(false);
      return;
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

    try {
      const response = await api.get(profileEndpoint);
      setProfile(response.data);
      setFirstName(response.data.firstName || response.data.user?.firstName || '');
      setLastName(response.data.lastName || response.data.user?.lastName || '');
      setEmail(response.data.email || response.data.user?.email || '');
      setNewEmail(response.data.email || response.data.user?.email || ''); // Initialize newEmail
      setPhoneNumber(response.data.phone_number || response.data.user?.phoneNumber || response.data.phoneNumber || '');
      setAddress(response.data.address || response.data.user?.address || '');
    } catch (error) {
      setError('Failed to load profile information.');
      console.error("Error fetching profile:", error);
    } finally {
      setLoading(false);
    }
  }, [userId, role]);

  useEffect(() => {
    if (userId && role && localStorage.getItem('token')) {
      fetchProfile();
    }
  }, [userId, role, fetchProfile]);

  const handleEmailChange = (e) => {
    const newEmailValue = e.target.value;
    setNewEmail(newEmailValue);
    if (newEmailValue !== email) {
      setIsEmailChanging(true);
      setIsOtpSent(false);
      setIsNewEmailVerified(false);
    } else {
      setIsEmailChanging(false);
      setIsOtpSent(false);
      setIsNewEmailVerified(true); // If email is the same, consider it verified
    }
  };

  const handleSendOtp = async () => {
    setError('');
    setOtpVerificationError('');
    setIsOtpSent(true); // Disable the button temporarily
    try {
      await api.post('/auth/initiate-update-email', { userId, newEmail });
      // Optionally show a success message that OTP has been sent
    } catch (error) {
      setError(error.response?.data?.message || 'Failed to send OTP.');
      setIsOtpSent(false);
    }
  };

  const handleVerifyOtp = async () => {
    setOtpVerificationError('');
    setIsOtpVerifying(true);
    try {
      const response = await api.post('/auth/verify-update-email-otp', { userId, email: newEmail, otp });
      setIsNewEmailVerified(true);
      // Optionally show a success message for OTP verification
    } catch (error) {
      setOtpVerificationError(error.response?.data?.message || 'Invalid or expired OTP.');
      setIsNewEmailVerified(false);
    } finally {
      setIsOtpVerifying(false);
    }
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    setError('');
    setSuccessMessage('');
    if (!userId || !role) {
      setError('Missing user information. Please log in again.');
      return;
    }

    if (isEmailChanging && !isNewEmailVerified) {
      setError('Please verify the new email address.');
      return;
    }

    let updateEndpoint = '/profile/update'; // Using the common endpoint

    try {
      await api.put(updateEndpoint, {
        firstName,
        lastName,
        email: newEmail, // Use the potentially new email
        phone: phoneNumber,
        address,
      });
      setSuccessMessage('Profile updated successfully!');
      setTimeout(() => {
        navigate(`/${role.toLowerCase()}/profile`);
      }, 1500);
    } catch (error) {
      setError(error.response?.data?.message || 'Failed to update profile.');
    }
  };

  const getProfileTitle = () => {
    return role ? `Update ${role.charAt(0).toUpperCase() + role.slice(1)} Profile` : 'Update Profile';
  };

  const getProfileHomeRoute = () => {
    return role ? `/${role.toLowerCase()}/profile` : '/dashboard';
  };

  return (
    <div className="container mt-5">
      <h2 className="mb-4 text-primary">{getProfileTitle()}</h2>
      {error && <div className="alert alert-danger mb-3" role="alert">{error}</div>}
      {successMessage && <div className="alert alert-success mb-3" role="alert">{successMessage}</div>}
      {loading ? (
        <p>Loading profile information...</p>
      ) : (
        <form onSubmit={handleSubmit} className="shadow-lg p-4 rounded">
          <div className="mb-3">
            <label htmlFor="firstName" className="form-label">First Name:</label>
            <input type="text" className="form-control" id="firstName" value={firstName} onChange={(e) => setFirstName(e.target.value)} required />
          </div>
          <div className="mb-3">
            <label htmlFor="lastName" className="form-label">Last Name:</label>
            <input type="text" className="form-control" id="lastName" value={lastName} onChange={(e) => setLastName(e.target.value)} required />
          </div>
          <div className="mb-3">
            <label htmlFor="email" className="form-label">Email:</label>
            <input type="email" className="form-control" id="email" value={newEmail} onChange={handleEmailChange} required />
            {isEmailChanging && !isOtpSent && !isNewEmailVerified && (
              <div className="mt-2">
                <button type="button" className="btn btn-sm btn-outline-info" onClick={handleSendOtp} disabled={isOtpSent}>
                  {isOtpSent ? 'Sending OTP...' : 'Send OTP'}
                </button>
                <small className="form-text text-muted">Click to send OTP to your new email address.</small>
              </div>
            )}
            {isEmailChanging && isOtpSent && !isNewEmailVerified && (
              <div className="mt-2">
                <label htmlFor="otp" className="form-label">OTP:</label>
                <input type="text" className="form-control form-control-sm" id="otp" value={otp} onChange={(e) => setOtp(e.target.value)} required />
                {otpVerificationError && <div className="text-danger mt-1">{otpVerificationError}</div>}
                <button type="button" className="btn btn-sm btn-primary mt-2" onClick={handleVerifyOtp} disabled={isOtpVerifying}>
                  {isOtpVerifying ? 'Verifying...' : 'Verify OTP'}
                </button>
                {isNewEmailVerified && <div className="text-success mt-1">Email verified!</div>}
              </div>
            )}
          </div>
          <div className="mb-3">
            <label htmlFor="phoneNumber" className="form-label">Phone Number (Optional):</label>
            <input type="tel" className="form-control" id="phoneNumber" value={phoneNumber} onChange={(e) => setPhoneNumber(e.target.value)} />
          </div>
          <div className="mb-3">
            <label htmlFor="address" className="form-label">Address:</label>
            <textarea className="form-control" id="address" value={address} onChange={(e) => setAddress(e.target.value)} rows="3" />
            <small className="form-text text-muted">Must be between 5 and 200 characters.</small>
          </div>
          <div className="d-flex gap-2">
            <button type="submit" className="btn btn-primary" disabled={isEmailChanging && !isNewEmailVerified}>
              Update Profile
            </button>
            <button onClick={() => navigate(getProfileHomeRoute())} className="btn btn-outline-secondary">
              Cancel
            </button>
          </div>
        </form>
      )}
    </div>
  );
};

export default UpdateProfileComponent;