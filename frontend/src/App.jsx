import React, { useState, useEffect } from 'react';
import CourseList from './CourseList';
import Navbar from './Navbar';
import HeroSection from './HeroSection';
import { CssBaseline, Box, Typography, Container } from '@mui/material';

function App() {
  const [courses, setCourses] = useState([]);
  const [loading, setLoading] = useState(true);

  // Fetch featured courses (assuming /api/courses returns all for now)
  useEffect(() => {
    setLoading(true);
    fetch('http://localhost:8080/api/courses')
      .then((response) => response.json())
      .then((data) => {
        setCourses(data); // Display all courses on homepage for now
        setLoading(false);
      })
      .catch((error) => {
        console.error('Error fetching featured courses:', error);
        setLoading(false);
      });
  }, []);

  return (
    <>
      <CssBaseline />
      <Navbar />
      <main>
        <HeroSection />
        <Container maxWidth="lg" sx={{ py: 4 }}>
          <Typography
            variant="h4" component="h2" gutterBottom align="center"
            sx={{ color: 'text.primary', mb: 4 }}
          >
            Featured Courses
          </Typography>
          <CourseList courses={courses} loading={loading} />
        </Container>
      </main>
      <Box
        component="footer"
        sx={{ bgcolor: 'background.paper', p: 6, borderTop: (theme) => `1px solid ${theme.palette.divider}` }}
      >
        <Typography variant="subtitle1" align="center" color="text.secondary">
          CodeVerse © 2025
        </Typography>
      </Box>
    </>
  );
}

export default App;