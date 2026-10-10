import React, { useState } from 'react';
import {
  Grid, Card, CardContent, CardActions, Button, Typography,
  CircularProgress, Box, Snackbar, Rating,
} from '@mui/material';
import { useAuth } from './context/AuthContext';
import { Link as RouterLink } from 'react-router-dom';

function CourseList({ courses, loading }) {
  const { user } = useAuth();
  const [snackbar, setSnackbar] = useState({ open: false, message: '' });

  const handleEnroll = (courseId) => {
    if (!user) { setSnackbar({ open: true, message: 'Must log in.' }); return; }
    fetch('http://localhost:8080/api/enrollments', { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify({ userId: user.id, courseId: courseId }) })
      .then(async (res) => { if (!res.ok) throw await res.json(); setSnackbar({ open: true, message: 'Enrolled!' }); })
      .catch((err) => { setSnackbar({ open: true, message: `Error: ${err.error || err.message}` }); });
  };
  const handleCloseSnackbar = () => { setSnackbar({ ...snackbar, open: false }); };

  if (loading) {
    return ( <Box sx={{ display: 'flex', justifyContent: 'center', mt: 4 }}><CircularProgress /></Box> );
  }

  return (
    <>
      <Grid container spacing={4} justifyContent="center">
        {courses.length === 0 ? (
          <Typography sx={{ mt: 4, color: 'text.secondary' }}> No courses found. </Typography>
        ) : (
          courses.map((course) => (
            <Grid key={course.id} xs={12} sm={6} md={4}>
              <RouterLink to={`/courses/${course.id}`} style={{ textDecoration: 'none' }}>
                <Card sx={{ height: '100%', display: 'flex', flexDirection: 'column', transition: '0.3s', '&:hover': { transform: 'scale(1.03)', boxShadow: 6 } }}>
                  <CardContent sx={{ flexGrow: 1 }}>
                    <Typography gutterBottom variant="h5" component="h2" sx={{color: 'text.primary'}}> {course.title} </Typography>
                    <Typography variant="body2" color="text.secondary"> {course.description} </Typography>
                    <Box sx={{ display: 'flex', alignItems: 'center', mt: 2 }}>
                      <Rating value={course.averageRating || 0} readOnly precision={0.5} size="small" />
                      <Typography variant="body2" color="text.secondary" sx={{ ml: 1 }}> ({course.studentCount} students) </Typography>
                    </Box>
                  </CardContent>
                  {user && (
                    <CardActions sx={{ justifyContent: 'flex-start' }}>
                      <Button size="small" variant="contained" onClick={(e) => { e.preventDefault(); handleEnroll(course.id); }} sx={{ bgcolor: 'accent.main', '&:hover': { bgcolor: 'primary.dark' } }}> Enroll </Button>
                    </CardActions>
                  )}
                </Card>
              </RouterLink>
            </Grid>
          ))
        )}
      </Grid>
      <Snackbar open={snackbar.open} autoHideDuration={6000} onClose={handleCloseSnackbar} message={snackbar.message} />
    </>
  );
}

export default CourseList;