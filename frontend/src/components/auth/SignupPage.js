import React, { useState } from 'react';
import axios from 'axios'; // Import axios directly
import { useNavigate, Link } from 'react-router-dom';
import { TextField, Button, Container, Typography, Box } from '@mui/material';

const SignupPage = () => {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [firstName, setFirstName] = useState('');
    const [lastName, setLastName] = useState('');
    const [emailAddress, setEmailAddress] = useState('');
    const [address, setAddress] = useState('');
    const [phoneNumber, setPhoneNumber] = useState('');
    const [role, setRole] = useState('CUSTOMER'); // Default role
    const [error, setError] = useState('');
    const navigate = useNavigate();

    const handleSignup = async () => {
        setError('');
        try {
            const response = await axios.post( // Use axios directly
                '/api/auth/signup',
                {
                    username: username,
                    password: password,
                    firstName: firstName,
                    lastName: lastName,
                    emailAddress: emailAddress,
                    address: address,
                    phoneNumber: phoneNumber,
                    role: role,
                },
                {
                    headers: {
                        'Content-Type': 'application/json',
                    },
                }
            );

            console.log('Signup successful:', response.data);
            navigate('/login'); // Redirect to login page after successful signup

        } catch (error) {
            console.error('Signup failed:', error.response ? error.response.data : error.message);
            setError(error.response?.data || 'Signup failed. Please try again.');
        }
    };

    return (
        <Container maxWidth="sm">
            <Box sx={{ marginTop: 8, display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
                <Typography component="h1" variant="h5">
                    Sign up
                </Typography>
                {error && (
                    <Typography color="error" sx={{ mt: 1 }}>
                        {error}
                    </Typography>
                )}
                <Box component="form" noValidate sx={{ mt: 1 }}>
                    {/* ... rest of your form fields */}
                    <Button fullWidth variant="contained" color="primary" sx={{ mt: 3, mb: 2 }} onClick={handleSignup}>
                        Sign Up
                    </Button>
                    <Box sx={{ display: 'flex', justifyContent: 'flex-end', mt: 1 }}>
                        <Link to="/login" variant="body2">
                            {"Already have an account? Sign In"}
                        </Link>
                    </Box>
                </Box>
            </Box>
        </Container>
    );
};

export default SignupPage;