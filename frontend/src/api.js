import axios from 'axios';

const api = axios.create({
  baseURL: process.env.REACT_APP_DOMAIN_BACKEND + '/api',
});

api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
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