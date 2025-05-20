import React, { useState, useContext } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import api from '../../api'; // Ensure this path is correct based on your project structure
import 'bootstrap/dist/css/bootstrap.min.css';
import { ThemeContext } from '../../contexts/ThemeContext';
// import { AuthContext } from '../../contexts/AuthContext'; // Uncomment and use if you have an AuthContext for global state management

const LoginComponent = () => {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState(''); // State to hold specific error messages for display
  const [loading, setLoading] = useState(false); // State to manage loading indicator for the button

  const navigate = useNavigate();
  // If you have an AuthContext to update global login state (e.g., isLoggedIn)
  // const { login } = useContext(AuthContext);
  const { theme } = useContext(ThemeContext);
  const isDark = theme === 'dark';

  const handleSubmit = async (event) => {
    event.preventDefault(); // Prevent default form submission behavior (page reload)

    setError(''); // Clear any previous error messages
    setLoading(true); // Set loading state to true for UI feedback

    try {
      // Make the API call to your backend login endpoint.
      // Ensure the endpoint matches your Spring Boot AuthController's @RequestMapping and @PostMapping.
      // Based on your logs, '/api/auth/login' is the correct relative path for 'api' instance.
      const response = await api.post('/auth/login', { username, password });

      // Store authentication data in local storage
      localStorage.setItem('token', response.data.token);
      localStorage.setItem('role', response.data.role);
      localStorage.setItem('userId', response.data.userId);

      // Ensure customerId is stored only if available (e.g., for CUSTOMER roles)
      // Your backend should send 'customerId' or null/empty string for ADMINs
      if (response.data.customerId) {
        localStorage.setItem('customerId', response.data.customerId);
      } else {
        localStorage.removeItem('customerId'); // Clear it if not returned
      }

      // If you're using an AuthContext, call its login method to update global state
      // login(response.data.token, response.data.role, response.data.userId, response.data.customerId);

      // Navigate based on the user's role
      if (response.data.role === 'CUSTOMER') {
        navigate('/customer/dashboard');
      } else if (response.data.role === 'ADMIN') {
        navigate('/admin/dashboard');
      } else {
        // Fallback for any other roles or a general dashboard path
        navigate('/dashboard');
      }

    } catch (err) {
      console.error("Login error:", err); // Log the full error for debugging

      // Access the response data, which might be a string or an object
      const backendResponseData = err.response?.data;

      // Conditional error message display based on the type and content of backend response
      if (typeof backendResponseData === 'string') {
        // If the backend directly returns a plain string message (e.g., "Your account is currently inactive...")
        if (backendResponseData.includes("inactive") || backendResponseData.includes("disabled")) {
          setError("Your account is currently inactive. Please contact support.");
        } else if (backendResponseData.includes("Invalid credentials") || backendResponseData.includes("Bad credentials")) {
          setError("Invalid username or password. Please try again.");
        } else {
          // Fallback for any other plain string error messages
          setError(backendResponseData);
        }
      } else if (backendResponseData && typeof backendResponseData === 'object' && backendResponseData.message) {
        // If the backend returns a JSON object with a 'message' field (e.g., from Spring Security's default errors)
        if (backendResponseData.message.includes("inactive") || backendResponseData.message.includes("disabled")) {
          setError("Your account is currently inactive. Please contact support.");
        } else if (backendResponseData.message.includes("Invalid credentials") || backendResponseData.message.includes("Bad credentials")) {
          setError("Invalid username or password. Please try again.");
        } else {
          // Fallback for any other JSON object errors with a 'message' field
          setError(backendResponseData.message);
        }
      } else {
        // Generic fallback for network errors, server unreachable, or unexpected response formats
        setError('Login failed. An unexpected error occurred. Please check your internet connection or try again later.');
      }
    } finally {
      setLoading(false); // Always reset loading state when the request finishes
    }
  };

  return (
    <div className={`container mt-5 ${isDark ? 'bg-dark text-light' : 'bg-light text-dark'}`}>
      <div className="row justify-content-center">
        <div className="col-md-6">
          <div className={`card shadow ${isDark ? 'bg-secondary border-secondary text-light' : 'bg-white'}`}>
            <div className="card-body p-4">
              <h2 className={`text-center mb-4 ${isDark ? 'text-primary' : 'text-primary'}`}>Login</h2>

              {/* Conditional rendering for error messages */}
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