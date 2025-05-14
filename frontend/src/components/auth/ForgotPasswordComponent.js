import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../../api';

const ForgotPasswordComponent = () => {
  const [usernameOrEmail, setUsernameOrEmail] = useState('');
  const [message, setMessage] = useState('');
  const [error, setError] = useState('');
  const navigate = useNavigate();

  const handleSubmit = async (event) => {
    event.preventDefault();
    setMessage('');
    setError('');
    try {
      const response = await api.post('/auth/forgot-password', { usernameOrEmail });
      setMessage(response.data); // Display the message from the backend
    } catch (error) {
      setError(error.response?.data || 'Failed to send reset link. Please try again.');
    }
  };

  return (
    <div>
      <h2>Forgot Password</h2>
      {message && <p style={{ color: 'green' }}>{message}</p>}
      {error && <p style={{ color: 'red' }}>{error}</p>}
      <form onSubmit={handleSubmit}>
        <div>
          <label htmlFor="usernameOrEmail">Username or Email:</label>
          <input
            type="text"
            id="usernameOrEmail"
            value={usernameOrEmail}
            onChange={(e) => setUsernameOrEmail(e.target.value)}
            required
          />
        </div>
        <button type="submit">Send Reset Link</button>
        <p>
          Remember your password? <a href="/login">Login</a>
        </p>
      </form>
    </div>
  );
};

export default ForgotPasswordComponent;