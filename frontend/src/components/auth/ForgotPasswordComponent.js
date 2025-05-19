import React, { useState, useContext } from 'react';
import { useNavigate, Link } from 'react-router-dom'; // Import Link here!
import api from '../../api';
import 'bootstrap/dist/css/bootstrap.min.css';
import { ThemeContext } from '../../contexts/ThemeContext';

const ForgotPasswordComponent = () => {
  const [usernameOrEmail, setUsernameOrEmail] = useState('');
  const [message, setMessage] = useState('');
  const [error, setError] = useState('');
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const { theme } = useContext(ThemeContext);
  const isDark = theme === 'dark';

  const handleSubmit = async (event) => {
    event.preventDefault();
    setMessage('');
    setError('');
    setLoading(true);
    try {
      const response = await api.post('/auth/forgot-password', { usernameOrEmail });
      setMessage(response.data);
    } catch (error) {
      setError(error.response?.data || 'Failed to send reset link. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className={`container mt-5 ${isDark ? 'bg-dark text-light' : 'bg-light text-dark'}`}>
      <div className="row justify-content-center">
        <div className="col-md-6">
          <div className={`card shadow ${isDark ? 'bg-secondary border-secondary text-light' : 'bg-white'}`}>
            <div className="card-body p-4">
              <h2 className={`text-center mb-4 ${isDark ? 'text-primary' : 'text-primary'}`}>Forgot Password</h2>
              {message && <div className="alert alert-success">{message}</div>}
              {error && <div className="alert alert-danger">{error}</div>}
              <form onSubmit={handleSubmit}>
                <div className="mb-3 form-group">
                  <label htmlFor="usernameOrEmail" className="form-label">
                    Username or Email:
                  </label>
                  <input
                    type="text"
                    className={`form-control ${isDark ? 'bg-dark text-light border-secondary' : ''}`}
                    id="usernameOrEmail"
                    value={usernameOrEmail}
                    onChange={(e) => setUsernameOrEmail(e.target.value)}
                    required
                  />
                </div>
                <div className="d-grid">
                  <button type="submit" className="btn btn-primary" disabled={loading}>
                    {loading ? 'Sending...' : 'Send Reset Link'}
                  </button>
                </div>
                <p className={`mt-3 text-center ${isDark ? 'text-light' : 'text-muted'}`}>
                  Remember your password? <Link to="/login" className={`${isDark ? 'text-info' : 'text-primary'}`}>Login</Link>
                </p>
              </form>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default ForgotPasswordComponent;