import React from 'react';
import { Container, Typography, Box, Paper } from '@mui/material';
import Navbar from './Navbar';
import { useAuth } from './context/AuthContext';

function ProfilePage() {
  const { user } = useAuth();

  if (!user) { return ( <> <Navbar /> <Container><Typography>Please log in.</Typography></Container> </> ); }

  return (
    <>
      <Navbar />
      <Container maxWidth="sm" sx={{ py: 4 }}>
        <Typography variant="h4" component="h1" gutterBottom sx={{ color: 'primary.main' }}> My Profile </Typography>
        <Paper elevation={3} sx={{ p: 3 }}>
          <Box sx={{ mb: 2 }}>
            <Typography variant="h6" sx={{ color: 'text.primary' }}>Username:</Typography>
            <Typography sx={{ color: 'text.secondary' }}>{user.username}</Typography>
          </Box>
          <Box>
            <Typography variant="h6" sx={{ color: 'text.primary' }}>Role:</Typography>
            <Typography sx={{ color: 'text.secondary' }}>{user.role}</Typography>
          </Box>
        </Paper>
      </Container>
    </>
  );
}
export default ProfilePage;