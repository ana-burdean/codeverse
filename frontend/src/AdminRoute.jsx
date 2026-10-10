import React from 'react';
import { useAuth } from './context/AuthContext';
import { Navigate } from 'react-router-dom';
import { Box, Typography } from '@mui/material'; // Import Box/Typography for error message

function AdminRoute({ children }) {
  const { user } = useAuth();
  if (!user) { return <Navigate to="/login" replace />; }
  if (user.role !== 'ADMIN') {
    return ( <Box sx={{ p: 4, textAlign: 'center' }}> <Typography variant="h4" color="error">Access Denied</Typography> <Typography sx={{ color: 'text.secondary' }}>No permission.</Typography> </Box> );
  }
  return children;
}
export default AdminRoute;