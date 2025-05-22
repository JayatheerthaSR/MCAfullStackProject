import { useState, useContext } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import api from '../../api';
import 'bootstrap/dist/css/bootstrap.min.css';
import { ThemeContext } from '../../contexts/ThemeContext';

const LoginComponent = () => {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const navigate = useNavigate();
  const { theme } = useContext(ThemeContext);
  const isDark = theme === 'dark';

  const handleSubmit = async (event) => {
    event.preventDefault();

    setError('');
    setLoading(true);

    try {
      const response = await api.post('/auth/login', { username, password });

      localStorage.setItem('token', response.data.token);
      localStorage.setItem('role', response.data.role);
      localStorage.setItem('userId', response.data.userId);

      if (response.data.customerId) {
        localStorage.setItem('customerId', response.data.customerId);
      } else {
        localStorage.removeItem('customerId');
      }

      if (response.data.role === 'CUSTOMER') {
        navigate('/customer/dashboard');
      } else if (response.data.role === 'ADMIN') {
        navigate('/admin/dashboard');
      } else {
        navigate('/dashboard');
      }

    } catch (err) {
      console.error("Login error:", err);
      const backendResponseData = err.response?.data;
      if (typeof backendResponseData === 'string') {
        if (backendResponseData.includes("inactive") || backendResponseData.includes("disabled")) {
          setError("Your account is currently inactive. Please contact support.");
        } else if (backendResponseData.includes("Invalid credentials") || backendResponseData.includes("Bad credentials")) {
          setError("Invalid username or password. Please try again.");
        } else {
          setError(backendResponseData);
        }
      } else if (backendResponseData && typeof backendResponseData === 'object' && backendResponseData.message) {
        if (backendResponseData.message.includes("inactive") || backendResponseData.message.includes("disabled")) {
          setError("Your account is currently inactive. Please contact support.");
        } else if (backendResponseData.message.includes("Invalid credentials") || backendResponseData.message.includes("Bad credentials")) {
          setError("Invalid username or password. Please try again.");
        } else {
          setError(backendResponseData.message);
        }
      } else {
        setError('Login failed. An unexpected error occurred. Please check your internet connection or try again later.');
      }
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
              <h2 className={`text-center mb-4 ${isDark ? 'text-primary' : 'text-primary'}`}>Login</h2>

              {error && (
                <div className="alert alert-danger text-center" role="alert">
                  {error}
                </div>
              )}

              <form onSubmit={handleSubmit}>
                <div className="mb-3">
                  <label htmlFor="username" className="form-label">
                    Username:
                  </label>
                  <input
                    type="text"
                    className={`form-control ${isDark ? 'bg-dark text-light border-secondary' : ''}`}
                    id="username"
                    value={username}
                    onChange={(e) => setUsername(e.target.value)}
                    required
                  />
                </div>
                <div className="mb-3">
                  <label htmlFor="password" className="form-label">
                    Password:
                  </label>
                  <input
                    type="password"
                    className={`form-control ${isDark ? 'bg-dark text-light border-secondary' : ''}`}
                    id="password"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    required
                  />
                </div>
                <div className="d-grid">
                  <button type="submit" className="btn btn-primary" disabled={loading}>
                    {loading ? 'Logging in...' : 'Login'}
                  </button>
                </div>
                <p className={`mt-3 text-center ${isDark ? 'text-light' : 'text-muted'}`}>
                  Don't have an account? <Link to="/register" className={`${isDark ? 'text-info' : 'text-primary'}`}>Register</Link> | <Link to="/forgot-password" className={`${isDark ? 'text-info' : 'text-primary'}`}>Forgot Password?</Link>
                </p>
              </form>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default LoginComponent;