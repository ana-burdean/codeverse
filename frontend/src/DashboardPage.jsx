import React, { useState, useEffect } from 'react';
import {
  Grid,
  Card,
  CardContent,
  Typography,
  Container,
  CircularProgress,
  Box,
  Stack // Using Stack for title section
} from '@mui/material';
import Navbar from './Navbar';
import { useAuth } from './context/AuthContext';
import { Link as RouterLink } from 'react-router-dom'; // Keep link

function DashboardPage() {
  const [courses, setCourses] = useState([]);
  const [loading, setLoading] = useState(true);
  const { user } = useAuth();

  useEffect(() => {
    if (user) {
      setLoading(true);
      // Fetch enrolled courses (backend should return courses with direct lessons list)
      fetch(`http://localhost:8080/api/users/${user.id}/courses`)
        .then(response => {
            if (!response.ok) throw new Error('Failed to fetch enrolled courses');
            return response.json();
        })
        .then(data => {
          setCourses(data);
          setLoading(false);
        })
        .catch(error => {
          console.error('Error fetching enrolled courses:', error);
          setLoading(false);
        });
    } else {
      setLoading(false); // If no user, stop loading
    }
  }, [user]); // Re-fetch if user changes

  if (loading) {
    return (
      <>
        <Navbar />
        <Box sx={{ display: 'flex', justifyContent: 'center', mt: 4 }}><CircularProgress /></Box>
      </>
    );
  }

  return (
    <>
      <Navbar />
      <Container sx={{ py: 5 }}>
        {/* Title Section */}
        <Stack spacing={1} alignItems="center" mb={4}>
          <Typography
            variant="h3" component="h1" gutterBottom align="center"
            sx={{ color: 'primary.main', fontWeight: 700, mt: 2 }}
          >
            My Dashboard
          </Typography>
          <Typography variant="subtitle1" align="center" sx={{ color: 'text.secondary' }}>
            Welcome! Here are your enrolled courses.
          </Typography>
        </Stack>

        {/* Course Grid */}
        {courses.length === 0 ? (
          <Typography align="center" sx={{ color: 'text.secondary', mt: 6 }}>
            You are not enrolled in any courses yet.
          </Typography>
        ) : (
          <Grid container spacing={4} justifyContent="center">
            {courses.map(course => (
              <Grid key={course.id} item xs={12} sm={6} md={4}>
                <RouterLink to={`/courses/${course.id}`} style={{ textDecoration: 'none' }}>
                  {/* Card Styling */}
                  <Card
                    sx={{
                      height: '100%', display: 'flex', flexDirection: 'column',
                      borderRadius: 3, bgcolor: 'background.paper', // Use theme paper color
                      color: 'text.primary', boxShadow: 3,
                      border: '1px solid rgba(255, 255, 255, 0.12)', // Subtle border
                      transition: 'transform 0.3s ease, box-shadow 0.3s ease',
                      '&:hover': {
                        transform: 'scale(1.03)',
                        boxShadow: 6,
                      },
                    }}
                  >
                    <CardContent>
                      <Typography
                        gutterBottom variant="h5" component="h2" // Use h5 for consistency
                        sx={{ color: 'text.primary', fontWeight: 600 }}
                      >
                        {course.title}
                      </Typography>
                      <Typography variant="body2" color="text.secondary">
                        {course.description}
                      </Typography>
                      {/* Removed module/lesson display from dashboard card */}
                    </CardContent>
                  </Card>
                </RouterLink>
              </Grid>
            ))}
          </Grid>
        )}
      </Container>
    </>
  );
}

export default DashboardPage;