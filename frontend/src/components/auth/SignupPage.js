import React, { useState } from 'react';
import axios from 'axios';
import { useNavigate, Link } from 'react-router-dom';
import { TextField, Button, Container, Typography, Box, FormControl, InputLabel, Select, MenuItem } from '@mui/material';

const SignupPage = () => {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [role, setRole] = useState('CUSTOMER');
    const [firstName, setFirstName] = useState('');
    const [lastName, setLastName] = useState('');
    const [emailAddress, setEmailAddress] = useState('');
    const [phoneNumber, setPhoneNumber] = useState('');
    const [address, setAddress] = useState('');
    const [error, setError] = useState('');
    const navigate = useNavigate();

    const handleSignup = async () => {
        setError('');
        try {
            const registrationData = {
                username,
                password,
                role,
                firstName,
                lastName,
                emailAddress,
                phoneNumber,
                address,
            };
            const response = await axios.post('/api/auth/signup', registrationData, {
                headers: {
                    'Content-Type': 'application/json',
                },
            });
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
                    Sign Up
                </Typography>
                {error && (
                    <Typography color="error" sx={{ mt: 1 }}>
                        {error}
                    </Typography>
                )}
                <Box component="form" noValidate sx={{ mt: 1 }}>
                    <TextField required fullWidth id="username" label="Username" name="username" autoComplete="username" autoFocus margin="normal" value={username} onChange={(e) => setUsername(e.target.value)} />
                    <TextField required fullWidth name="password" label="Password" type="password" id="password" autoComplete="new-password" margin="normal" value={password} onChange={(e) => setPassword(e.target.value)} />
                    <FormControl fullWidth margin="normal">
                        <InputLabel id="role-label">Role</InputLabel>
                        <Select labelId="role-label" id="role" value={role} onChange={(e) => setRole(e.target.value)}>
                            <MenuItem value="CUSTOMER">Customer</MenuItem>
                            <MenuItem value="ADMIN">Admin</MenuItem>
                        </Select>
                    </FormControl>
                    {role === 'CUSTOMER' && (
                        <>
                            <TextField required fullWidth label="First Name" margin="normal" value={firstName} onChange={(e) => setFirstName(e.target.value)} />
                            <TextField required fullWidth label="Last Name" margin="normal" value={lastName} onChange={(e) => setLastName(e.target.value)} />
                            <TextField required fullWidth label="Email Address" type="email" margin="normal" value={emailAddress} onChange={(e) => setEmailAddress(e.target.value)} />
                            <TextField fullWidth label="Phone Number" margin="normal" value={phoneNumber} onChange={(e) => setPhoneNumber(e.target.value)} />
                            <TextField fullWidth label="Address" margin="normal" multiline rows={2} value={address} onChange={(e) => setAddress(e.target.value)} />
                        </>
                    )}
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