import 'bootstrap/dist/css/bootstrap.min.css';
import React from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import LoginComponent from './components/auth/LoginComponent';
import RegistrationComponent from './components/auth/RegistrationComponent';
import CustomerDashboard from './components/customer/CustomerDashboard';
import AdminDashboard from './components/admin/AdminDashboard';
import UpdateProfileComponent from './components/profile/UpdateProfileComponent';
import ViewTransactionsComponent from './components/customer/ViewTransactionsComponent';
import AddBeneficiaryComponent from './components/customer/AddBeneficiaryComponent';
import TransferMoneyComponent from './components/customer/TransferMoneyComponent';
import UserProfileComponent from './components/customer/UserProfileComponent';
import ViewCustomersComponent from './components/admin/ViewCustomersComponent';
import ViewAllTransactionsComponent from './components/admin/ViewAllTransactionsComponent';
import ProtectedRoute from './components/ProtectedRoute';
import ForgotPasswordComponent from './components/auth/ForgotPasswordComponent';
import ResetPasswordComponent from './components/auth/ResetPasswordComponent';
import HomePage from './pages/HomePage';
import Navbar from './components/Navbar';
import Footer from './components/Footer';
import './App.css';
import BeneficiaryListComponent from './components/customer/BeneficiaryListComponent';
import SessionManager from './components/auth/SessionManager'; // Import SessionManager
import ChangePasswordComponent from './components/profile/ChangePasswordComponent'; // Import the new component

function App() {
  return (
    <>
      <Navbar />
      <div className="container mt-4">
        <SessionManager> {/* Wrap with SessionManager instead of IdleTimer */}
          <Routes>
            <Route path="/login" element={<LoginComponent />} />
            <Route path="/register" element={<RegistrationComponent />} />
            <Route path="/forgot-password" element={<ForgotPasswordComponent />} />
            <Route path="/reset-password/:token" element={<ResetPasswordComponent />} />

            <Route path="/customer/*" element={<ProtectedRoute allowedRoles={['CUSTOMER']} />}>
              <Route index element={<Navigate to="dashboard" replace />} />
              <Route path="dashboard" element={<CustomerDashboard />} />
              <Route path="transactions" element={<ViewTransactionsComponent />} />
              <Route path="beneficiaries" element={<BeneficiaryListComponent />} />
              <Route path="beneficiaries/add" element={<AddBeneficiaryComponent />} />
              <Route path="transfer" element={<TransferMoneyComponent />} />
              <Route path="profile" element={<UserProfileComponent />} />
              <Route path="profile/update" element={<UpdateProfileComponent />} />
              {/* Add the Change Password route for customers */}
              <Route path="profile/change-password" element={<ChangePasswordComponent redirectPath="/customer/profile" />} />
            </Route>

            <Route path="/admin/*" element={<ProtectedRoute allowedRoles={['ADMIN']} />}>
              <Route index element={<Navigate to="dashboard" replace />} />
              <Route path="dashboard" element={<AdminDashboard />} />
              <Route path="customers" element={<ViewCustomersComponent />} />
              <Route path="transactions" element={<ViewAllTransactionsComponent />} />
              {/* Add the Change Password route for admins */}
              <Route path="profile/change-password" element={<ChangePasswordComponent redirectPath="/admin/dashboard" />} />
            </Route>

            <Route path="/" element={<HomePage />} />
            <Route path="*" element={<div>Page Not Found</div>} />
          </Routes>
        </SessionManager>
      </div>
      <Footer />
    </>
  );
}

export default App;