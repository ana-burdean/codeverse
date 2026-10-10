import React, { useState } from 'react';
import { Container, Box, Typography, TextField, Button, Snackbar } from '@mui/material'; // Added Snackbar
import Navbar from './Navbar';
import { useNavigate } from 'react-router-dom'; // Added navigate for redirection

function RegisterPage() {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [message, setMessage] = useState('');
  const [openSnackbar, setOpenSnackbar] = useState(false); // Snackbar state
  const navigate = useNavigate(); // Hook for navigation

  const handleCloseSnackbar = () => setOpenSnackbar(false); // Snackbar close handler

  const handleSubmit = (event) => {
    event.preventDefault(); setMessage('');
    fetch('http://localhost:8080/api/auth/register', { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify({ username, password }) })
      .then(async (res) => { if (!res.ok) throw await res.json(); return res.json(); })
      .then((data) => {
        setMessage(`Success! User '${data.username}' created. Redirecting to login...`);
        setOpenSnackbar(true);
        setTimeout(() => navigate('/login'), 2000); // Redirect after 2 seconds
      })
      .catch((err) => { setMessage(`Error: ${err.error || err.message}`); setOpenSnackbar(true); });
  };

  return (
    <>
      <Navbar />
      <Container maxWidth="xs">
        <Box sx={{ marginTop: 8, display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
          <Typography component="h1" variant="h5" sx={{color: 'text.primary'}}> Register </Typography>
          <Box component="form" onSubmit={handleSubmit} noValidate sx={{ mt: 1 }}>
            <TextField margin="normal" required fullWidth id="username" label="Username" name="username" autoFocus value={username} onChange={(e) => setUsername(e.target.value)} />
            <TextField margin="normal" required fullWidth name="password" label="Password" type="password" id="password" value={password} onChange={(e) => setPassword(e.target.value)} />
            <Button type="submit" fullWidth variant="contained" sx={{ mt: 3, mb: 2, bgcolor: 'accent.main' }}> Register </Button>
            {/* Removed inline message */}
          </Box>
        </Box>
      </Container>
      {/* Added Snackbar for feedback */}
      <Snackbar open={openSnackbar} autoHideDuration={6000} onClose={handleCloseSnackbar} message={message} />
    </>
  );
}
export default RegisterPage;