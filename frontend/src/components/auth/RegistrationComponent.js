import { useState, useContext } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import api from '../../api';
import 'bootstrap/dist/css/bootstrap.min.css';
import { ThemeContext } from '../../contexts/ThemeContext';

const RegistrationComponent = () => {
  const [registrationData, setRegistrationData] = useState({
    username: '',
    password: '',
    confirmPassword: '',
    firstName: '',
    lastName: '',
    email: '',
    address: '',
    role: 'CUSTOMER',
  });

  const [phone, setPhone] = useState('');
  const [passwordValid, setPasswordValid] = useState(false);
  const [passwordError, setPasswordError] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState('');
  const [showOtpVerification, setShowOtpVerification] = useState(false);
  const [otp, setOtp] = useState('');
  const [verificationError, setVerificationError] = useState('');
  const [verificationLoading, setVerificationLoading] = useState(false);
  const navigate = useNavigate();
  const { theme } = useContext(ThemeContext);
  const isDark = theme === 'dark';

  const handleChange = (e) => {
    const { name, value } = e.target;
    setRegistrationData(prevState => ({
      ...prevState,
      [name]: value
    }));

    if (name === 'password') {
      validatePassword(value);
    }
  };

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

  const handleInitialRegistration = async (event) => {
    event.preventDefault();
    setError('');
    if (registrationData.password !== registrationData.confirmPassword) {
      setError('Passwords do not match.');
      return;
    }
    setLoading(true);
    try {
      const response = await api.post('/auth/register', {
        username: registrationData.username,
        password: registrationData.password,
        firstName: registrationData.firstName,
        lastName: registrationData.lastName,
        email: registrationData.email,
        address: registrationData.address,
        role: registrationData.role,
        phone: phone,
      });

      setMessage(response.data);
      setShowOtpVerification(true);
    } catch (err) {
      setError(err.response?.data || 'Registration failed. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  const handleVerifyOTP = async (event) => {
    event.preventDefault();
    setVerificationError('');
    setVerificationLoading(true);
    try {
      const response = await api.post('/auth/verify-otp', { email: registrationData.email, otp });
      setMessage(response.data);
      setTimeout(() => navigate('/login'), 3000);
    } catch (err) {
      setVerificationError(err.response?.data || 'OTP verification failed. Please try again.');
    } finally {
      setVerificationLoading(false);
    }
  };

  return (
    <div className={`container mt-5 ${isDark ? 'bg-dark text-light' : 'bg-light text-dark'}`}>
      <div className="row justify-content-center">
        <div className="col-md-6">
          <div className={`card shadow ${isDark ? 'bg-secondary border-secondary text-light' : 'bg-white'}`}>
            <div className="card-body p-4">
              <h2 className={`text-center mb-4 ${isDark ? 'text-primary' : 'text-primary'}`}>Register</h2>
              {error && <div className="alert alert-danger">{error}</div>}
              {message && <div className="alert alert-info">{message}</div>}

              {!showOtpVerification ? (
                <form onSubmit={handleInitialRegistration}>
                  <div className="mb-3">
                    <label htmlFor="username" className="form-label">Username:</label>
                    <input type="text" className={`form-control ${isDark ? 'bg-dark text-light border-secondary' : ''}`} id="username" name="username" value={registrationData.username} onChange={handleChange} required />
                  </div>
                  <div className="mb-3">
                    <label htmlFor="password" className="form-label">Password:</label>
                    <input
                      type="password"
                      className={`form-control ${isDark ? 'bg-dark text-light border-secondary' : ''}`}
                      id="password"
                      name="password"
                      value={registrationData.password}
                      onChange={handleChange}
                      required
                    />
                    {passwordError && <div className="form-text text-danger">{passwordError}</div>}
                  </div>
                  <div className="mb-3">
                    <label htmlFor="confirmPassword" className="form-label">Confirm Password:</label>
                    <input
                      type="password"
                      className={`form-control ${isDark ? 'bg-dark text-light border-secondary' : ''}`}
                      id="confirmPassword"
                      name="confirmPassword"
                      value={registrationData.confirmPassword}
                      onChange={handleChange}
                      required
                    />
                    {registrationData.password !== registrationData.confirmPassword && registrationData.confirmPassword !== '' && (
                      <div className="form-text text-danger">Passwords do not match.</div>
                    )}
                  </div>
                  <div className="mb-3">
                    <label htmlFor="firstName" className="form-label">First Name:</label>
                    <input type="text" className={`form-control ${isDark ? 'bg-dark text-light border-secondary' : ''}`} id="firstName" name="firstName" value={registrationData.firstName} onChange={handleChange} required />
                  </div>
                  <div className="mb-3">
                    <label htmlFor="lastName" className="form-label">Last Name:</label>
                    <input type="text" className={`form-control ${isDark ? 'bg-dark text-light border-secondary' : ''}`} id="lastName" name="lastName" value={registrationData.lastName} onChange={handleChange} required />
                  </div>
                  <div className="mb-3">
                    <label htmlFor="email" className="form-label">Email:</label>
                    <input type="email" className={`form-control ${isDark ? 'bg-dark text-light border-secondary' : ''}`} id="email" name="email" value={registrationData.email} onChange={handleChange} required />
                  </div>
                  <div className="mb-3">
                    <label htmlFor="address" className="form-label">Address:</label>
                    <input type="text" className={`form-control ${isDark ? 'bg-dark text-light border-secondary' : ''}`} id="address" name="address" value={registrationData.address} onChange={handleChange} required />
                  </div>
                  <div className="mb-3">
                    <label htmlFor="phone" className="form-label">Phone Number (Optional):</label>
                    <input
                      type="tel"
                      className={`form-control ${isDark ? 'bg-dark text-light border-secondary' : ''}`}
                      id="phone"
                      name="phone"
                      value={phone}
                      onChange={(e) => setPhone(e.target.value)}
                    />
                  </div>
                  <div className="mb-3">
                    <label htmlFor="role" className="form-label">Role:</label>
                    <select className={`form-select ${isDark ? 'bg-dark text-light border-secondary' : ''}`} id="role" name="role" value={registrationData.role} onChange={handleChange}>
                      <option value="CUSTOMER">Customer</option>
                      <option value="ADMIN">Admin</option>
                    </select>
                  </div>
                  <div className="d-grid">
                    <button
                      type="submit"
                      className="btn btn-primary"
                      disabled={loading || !passwordValid || registrationData.password !== registrationData.confirmPassword}
                    >
                      {loading ? 'Registering...' : 'Register'}
                    </button>
                  </div>
                  <p className={`mt-3 text-center ${isDark ? 'text-light' : 'text-muted'}`}>
                    Already have an account? <Link to="/login" className={`${isDark ? 'text-info' : 'text-primary'}`}>Login</Link>
                  </p>
                </form>
              ) : (
                <div>
                  <h3 className={`text-center mb-4 ${isDark ? 'text-primary' : 'text-primary'}`}>Verify OTP</h3>
                  {verificationError && <div className="alert alert-danger">{verificationError}</div>}
                  <form onSubmit={handleVerifyOTP}>
                    <div className="mb-3">
                      <label htmlFor="otp" className="form-label">Enter OTP:</label>
                      <input
                        type="text"
                        className={`form-control ${isDark ? 'bg-dark text-light border-secondary' : ''}`}
                        id="otp"
                        value={otp}
                        onChange={(e) => setOtp(e.target.value)}
                        required
                      />
                    </div>
                    <div className="d-grid">
                      <button type="submit" className="btn btn-primary" disabled={verificationLoading}>
                        {verificationLoading ? 'Verifying...' : 'Verify OTP'}
                      </button>
                    </div>
                  </form>
                </div>
              )}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default RegistrationComponent;