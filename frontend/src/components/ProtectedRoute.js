import React from 'react';
import { Navigate, Outlet } from 'react-router-dom';

const ProtectedRoute = ({ allowedRoles }) => {
  const isAuthenticated = localStorage.getItem('token'); // Or however you store your auth token
  const userRole = localStorage.getItem('role'); // Or however you store user role

  if (!isAuthenticated) {
    return <Navigate to="/login" />;
  }

  if (allowedRoles && !allowedRoles.includes(userRole)) {
    return <Navigate to="/unauthorized" />; // You'll need to create an Unauthorized component
  }

  return <Outlet />;
};

export default ProtectedRoute;