import React, { useState } from 'react';
import axios from 'axios';
import { useNavigate, Link } from 'react-router-dom';
import { TextField, Button, Container, Typography, Box } from '@mui/material';

const LoginPage = () => {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const navigate = useNavigate();

    const handleLogin = async () => {
        setError(''); // Clear any previous errors
        try {
            const response = await axios.post(
                '/api/auth/login',
                {}, // Empty request body for Basic Auth
                {
                    headers: {
                        'Content-Type': 'application/json',
                        Authorization: `Basic ${btoa(`${username}:${password}`)}`,
                    },
                }
            );

            console.log('Login successful:', response.data);

            // For now, let's just log the success and navigate based on a hardcoded role
            // In a real application, you'd get user role from the backend and store it.
            const userRole = 'CUSTOMER'; // Replace with actual role from backend if available
            localStorage.setItem('isLoggedIn', 'true');
            localStorage.setItem('userRole', userRole);

            if (userRole === 'CUSTOMER') {
                navigate('/customer/dashboard');
            } else if (userRole === 'ADMIN') {
                navigate('/admin/dashboard');
            } else {
                navigate('/unauthorized'); // Or some default page
            }

        } catch (error) {
            console.error('Login failed:', error.response ? error.response.data : error.message);
            setError(error.response?.data || 'Login failed. Please check your credentials.');
        }
    };

    return (
        <Container maxWidth="sm">
            <Box sx={{ marginTop: 8, display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
                <Typography component="h1" variant="h5">
                    Sign in
                </Typography>
                {error && (
                    <Typography color="error" sx={{ mt: 1 }}>
                        {error}
                    </Typography>
                )}
                <Box component="form" noValidate sx={{ mt: 1 }}>
                    <TextField
                        margin="normal"
                        required
                        fullWidth
                        id="username"
                        label="Username"
                        name="username"
                        autoComplete="username"
                        autoFocus
                        value={username}
                        onChange={(e) => setUsername(e.target.value)}
                    />
                    <TextField
                        margin="normal"
                        required
                        fullWidth
                        name="password"
                        label="Password"
                        type="password"
                        id="password"
                        autoComplete="current-password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                    />
                    <Button fullWidth variant="contained" color="primary" sx={{ mt: 3, mb: 2 }} onClick={handleLogin}>
                        Sign In
                    </Button>
                    <Box sx={{ display: 'flex', justifyContent: 'flex-end', mt: 1 }}>
                        <Link to="/signup" variant="body2">
                            {"Don't have an account? Sign Up"}
                        </Link>
                    </Box>
                </Box>
            </Box>
        </Container>
    );
};

export default LoginPage;