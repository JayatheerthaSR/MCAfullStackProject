import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import LoginPage from './components/auth/LoginPage';
import SignupPage from './components/auth/SignupPage';
import CustomerDashboard from './pages/CustomerDashboard'; // We'll create these later
import AdminDashboard from './pages/AdminDashboard';   // We'll create these later

const App = () => {
    return (
        <Router>
            <Routes>
                <Route path="/login" element={<LoginPage />} />
                <Route path="/signup" element={<SignupPage />} />
                <Route path="/customer/dashboard" element={<CustomerDashboard />} /> {/* Protected route later */}
                <Route path="/admin/dashboard" element={<AdminDashboard />} />     {/* Protected route later */}
                <Route path="/" element={<LoginPage />} /> {/* Default route to login */}
            </Routes>
        </Router>
    );
};

export default App;