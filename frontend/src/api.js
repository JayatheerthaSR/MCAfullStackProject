import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8080/api', // Set your Spring Boot API base URL
});

api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
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