import React from 'react';
import { Routes, Route } from 'react-router-dom';
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
import './App.css';

function App() {
  return (
    <Routes>
      <Route path="/login" element={<LoginComponent />} />
      <Route path="/register" element={<RegistrationComponent />} />

      <Route path="/customer/*" element={<ProtectedRoute allowedRoles={['CUSTOMER']} />}>
        <Route index element={<CustomerDashboard />} />
        <Route path="transactions" element={<ViewTransactionsComponent />} />
        <Route path="beneficiaries/add" element={<AddBeneficiaryComponent />} />
        <Route path="beneficiaries/transfer" element={<TransferMoneyComponent />} />
        <Route path="profile" element={<UserProfileComponent />} />
        <Route path="profile/update" element={<UpdateProfileComponent />} />
      </Route>

      <Route path="/admin/*" element={<ProtectedRoute allowedRoles={['ADMIN']} />}>
        <Route index element={<AdminDashboard />} />
        <Route path="customers" element={<ViewCustomersComponent />} />
        <Route path="transactions" element={<ViewAllTransactionsComponent />} />
        {/* Add more admin routes here */}
      </Route>

      <Route path="/" element={<div>Welcome to the Banking App</div>} />
      <Route path="*" element={<div>Page Not Found</div>} /> {/* Catch-all for 404s */}
    </Routes>
  );
}

export default App;