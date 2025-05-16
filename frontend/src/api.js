import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8080/api', // Set your Spring Boot API base URL
});

api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    // Only add the Authorization header if a token exists
    // and the request is NOT for the login or register endpoints
    if (token && config.url !== '/auth/login' && config.url !== '/auth/register') {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

export const forgotPassword = (usernameOrEmail) => {
  return api.post('/auth/forgot-password', { usernameOrEmail });
};

export const resetPassword = (token, newPassword) => {
  return api.post('/auth/reset-password', { token, newPassword });
};

export default api;