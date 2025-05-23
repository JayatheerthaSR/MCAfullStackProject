import { useState, useContext, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import api from '../../api';
import 'bootstrap/dist/css/bootstrap.min.css';
import { ThemeContext } from '../../contexts/ThemeContext';

const ResetPasswordComponent = () => {
  const [newPassword, setNewPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [message, setMessage] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const [isTokenValid, setIsTokenValid] = useState(false);
  const navigate = useNavigate();
  const { token } = useParams();
  const [passwordValid, setPasswordValid] = useState(false);
  const [passwordError, setPasswordError] = useState('');
  const { theme } = useContext(ThemeContext);
  const isDark = theme === 'dark';

  useEffect(() => {
    const validateToken = async () => {
      if (!token) {
        setError("Reset token is missing from the URL.");
        setIsTokenValid(false);
        setLoading(false);
        return;
      }
      setLoading(true);
      try {
        await api.get(`/auth/reset-password/${token}`);
        setIsTokenValid(true);
        setError('');
      } catch (err) {
        const errorMessage = err.response?.data?.message || 'Invalid or expired reset token. Please request a new one.';
        setError(errorMessage);
        setIsTokenValid(false);
      } finally {
        setLoading(false);
      }
    };

    validateToken();
  }, [token]);

  const validatePassword = (password) => {
    const minLength = 8;
    const hasUpperCase = /[A-Z]/.test(password);
    const hasLowerCase = /[a-z]/.test(password);
    const hasDigit = /[0-9]/.test(password);
    const hasSpecialChar = /[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]/.test(password);

    if (password.length < minLength) {
      setPasswordValid(false);
      setPasswordError(`Password must be at least ${minLength} characters long.`);
    } else if (!hasUpperCase) {
      setPasswordValid(false);
      setPasswordError('Password must contain at least one uppercase letter.');
    } else if (!hasLowerCase) {
      setPasswordValid(false);
      setPasswordError('Password must contain at least one lowercase letter.');
    } else if (!hasDigit) {
      setPasswordValid(false);
      setPasswordError('Password must contain at least one digit.');
    } else if (!hasSpecialChar) {
      setPasswordValid(false);
      setPasswordError('Password must contain at least one special character.');
    } else {
      setPasswordValid(true);
      setPasswordError('');
    }
  };

  const handleNewPasswordChange = (e) => {
    setNewPassword(e.target.value);
    validatePassword(e.target.value);
  };

  const handleConfirmPasswordChange = (e) => {
    setConfirmPassword(e.target.value);
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    setMessage('');
    setError('');

    if (!isTokenValid) {
        setError('Cannot reset password: Token is invalid or expired.');
        return;
    }

    if (newPassword !== confirmPassword) {
      setError('Passwords do not match.');
      return;
    }
    if (!passwordValid) {
      setError('New password does not meet the requirements.');
      return;
    }

    setLoading(true);
    try {
      const response = await api.post(`/auth/reset-password/${token}`, { newPassword });
      setMessage(response.data);
      setTimeout(() => navigate('/login'), 3000);
    } catch (error) {
      setError(error.response?.data?.message || 'Failed to reset password. Invalid or expired token.');
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
              <h2 className={`text-center mb-4 ${isDark ? 'text-primary' : 'text-primary'}`}>Reset Password</h2>
              {loading && <div className="alert alert-info">Loading...</div>}
              {message && <div className="alert alert-success">{message}</div>}
              {error && <div className="alert alert-danger">{error}</div>}

              {!loading && isTokenValid && (
                <form onSubmit={handleSubmit}>
                  <div className="mb-3 form-group">
                    <label htmlFor="newPassword" className="form-label">
                      New Password:
                    </label>
                    <input
                      type="password"
                      className={`form-control ${isDark ? 'bg-dark text-light border-secondary' : ''}`}
                      id="newPassword"
                      value={newPassword}
                      onChange={handleNewPasswordChange}
                      required
                    />
                    {passwordError && <div className="form-text text-danger">{passwordError}</div>}
                  </div>
                  <div className="mb-3 form-group">
                    <label htmlFor="confirmPassword" className="form-label">
                      Confirm New Password:
                    </label>
                    <input
                      type="password"
                      className={`form-control ${isDark ? 'bg-dark text-light border-secondary' : ''}`}
                      id="confirmPassword"
                      value={confirmPassword}
                      onChange={handleConfirmPasswordChange}
                      required
                    />
                    {newPassword !== confirmPassword && confirmPassword !== '' && (
                      <div className="form-text text-danger">Passwords do not match.</div>
                    )}
                  </div>
                  <div className="d-grid">
                    <button
                      type="submit"
                      className="btn btn-primary"
                      disabled={!isTokenValid || loading || !passwordValid || newPassword !== confirmPassword}
                    >
                      {loading ? 'Resetting...' : 'Reset Password'}
                    </button>
                  </div>
                </form>
              )}
              {!loading && !isTokenValid && error && (
                  <div className="text-center mt-3">
                      <p>{error}</p>
                      <button className="btn btn-info" onClick={() => navigate('/forgot-password')}>
                          Request New Password Reset Link
                      </button>
                  </div>
              )}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default ResetPasswordComponent;
