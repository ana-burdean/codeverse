import React from 'react';
import { Box, Container, Typography, Button } from '@mui/material';
import { Link as RouterLink } from 'react-router-dom';
import { useAuth } from './context/AuthContext';

function HeroSection() {
  const { user } = useAuth();

  return (
    <Box sx={{ bgcolor: 'background.paper', pt: 12, pb: 12, color: 'text.primary' }}>
      <Container maxWidth="lg">
        <Typography
          component="h1" variant="h2" align="center" gutterBottom
          sx={{
            fontWeight: 700,
            background: 'linear-gradient(45deg, #7C4DFF 30%, #5E35B1 90%)',
            WebkitBackgroundClip: 'text', WebkitTextFillColor: 'transparent',
          }}
        >
          Start Your Coding Journey
        </Typography>
        <Typography variant="h5" align="center" color="text.secondary" paragraph>
          Master new skills with expert-led courses. From Java to React, find your path to success.
        </Typography>
        <Box sx={{ display: 'flex', justifyContent: 'center', mt: 4 }}>
          <Button
            component={RouterLink}
            to={user ? "/dashboard" : "/register"}
            variant="contained" color="primary" size="large"
          >
            {user ? "Go To Dashboard" : "Get Started"}
          </Button>
        </Box>
      </Container>
    </Box>
  );
}

export default HeroSection;