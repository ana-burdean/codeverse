import React, { useState, useEffect } from 'react';
import { useParams, useNavigate, Link as RouterLink } from 'react-router-dom';
import {
  Container, Typography, Box, CircularProgress, List, ListItem, ListItemIcon,
  ListItemText, Paper, Divider, Button, TextField, Rating, Snackbar, Grid,
  Accordion, AccordionSummary, AccordionDetails, Modal, IconButton
} from '@mui/material';
import ExpandMoreIcon from '@mui/icons-material/ExpandMore';
import CloseIcon from '@mui/icons-material/Close';
import PlayCircleOutlineIcon from '@mui/icons-material/PlayCircleOutline';
import Navbar from './Navbar';
import { useAuth } from './context/AuthContext';

// ReviewForm component (defined outside)
const ReviewForm = ({ rating, setRating, comment, setComment, handleSubmit, existingReview, onDelete }) => (
    <Box component="form" onSubmit={handleSubmit} sx={{ mt: 2 }}>
        <Typography variant="h5" gutterBottom sx={{ color: 'text.primary' }}>
            {existingReview ? 'Update Your Review' : 'Write a Review'}
        </Typography>
        <Typography variant="body1" sx={{ color: 'text.primary', mb: 1 }}>Your Rating:</Typography>
        <Rating name="review-rating" value={rating} onChange={(event, newValue) => { setRating(newValue || 0); }} sx={{ mb: 2 }}/>
        <TextField label="Your Review" multiline rows={4} fullWidth variant="outlined" value={comment} onChange={(e) => setComment(e.target.value)} sx={{ mb: 2 }}/>
        <Box sx={{ display: 'flex', justifyContent: 'space-between', mt: 2 }}>
            <Button type="submit" variant="contained" color="primary">
                {existingReview ? 'Update Review' : 'Submit Review'}
            </Button>
            {existingReview && ( <Button variant="outlined" color="error" onClick={onDelete}> Delete Review </Button> )}
        </Box>
    </Box>
);

// Lesson Modal Component
const LessonModal = ({ open, onClose, lesson, isEnrolled }) => {
  const modalStyle = {
    position: 'absolute',
    top: '50%',
    left: '50%',
    transform: 'translate(-50%, -50%)',
    width: '80%',
    maxWidth: 800,
    maxHeight: '90vh',
    bgcolor: 'background.paper',
    border: '2px solid #000',
    boxShadow: 24,
    p: 4,
    overflow: 'auto',
  };

  return (
    <Modal open={open} onClose={onClose}>
      <Box sx={modalStyle}>
        <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
          <Typography variant="h4" component="h2">
            {lesson?.title}
          </Typography>
          <IconButton onClick={onClose}>
            <CloseIcon />
          </IconButton>
        </Box>
        
        {isEnrolled ? (
          <Box>
            {/* Lesson Content */}
            <Typography variant="h6" gutterBottom>Lesson Content:</Typography>
            <Paper sx={{ p: 3, bgcolor: 'grey.900' }}>
              <Typography sx={{ whiteSpace: 'pre-wrap' }}>
                {lesson?.content || 'No content available for this lesson.'}
              </Typography>
            </Paper>
          </Box>
        ) : (
          <Box textAlign="center" sx={{ py: 4 }}>
            <Typography variant="h6" color="error" gutterBottom>
              Enrollment Required
            </Typography>
            <Typography>
              Please enroll in this course to access the lesson content.
            </Typography>
          </Box>
        )}
      </Box>
    </Modal>
  );
};

function CourseDetailPage() {
  const { id: courseIdParam } = useParams();
  const { user } = useAuth();
  const navigate = useNavigate();

  // State variables
  const [course, setCourse] = useState(null);
  const [loading, setLoading] = useState(true);
  const [isEnrolled, setIsEnrolled] = useState(false);
  const [checkingEnrollment, setCheckingEnrollment] = useState(true);
  const [reviews, setReviews] = useState([]);
  const [userReview, setUserReview] = useState(null);
  const [rating, setRating] = useState(0);
  const [comment, setComment] = useState('');
  const [snackbar, setSnackbar] = useState({ open: false, message: '' });
  
  // New states for lesson playback
  const [selectedLesson, setSelectedLesson] = useState(null);
  const [lessonModalOpen, setLessonModalOpen] = useState(false);

  // Fetch course details
  useEffect(() => {
    setLoading(true); 
    setCourse(null); 
    setReviews([]); 
    setUserReview(null);
    
    fetch(`http://localhost:8080/api/courses/${courseIdParam}`)
      .then(r => { 
        if (!r.ok) throw Error('Course fetch failed'); 
        return r.json(); 
      })
      .then(data => {
        console.log('Course data:', data);
        console.log('Course lessons:', data.lessons);
        setCourse(data);
      })
      .catch(err => { 
        console.error(err); 
        setCourse(null); 
        setLoading(false); 
      });
  }, [courseIdParam]);

  // Fetch reviews
  useEffect(() => {
    if (course) {
      fetch(`http://localhost:8080/api/reviews?courseId=${course.id}`)
        .then(r => r.json())
        .then(data => {
          setReviews(data);
          const ur = data.find(r => r.userId === user?.id);
          setUserReview(ur || null);
          if (ur) { 
            setRating(ur.rating); 
            setComment(ur.comment || ''); 
          } else { 
            setRating(0); 
            setComment(''); 
          }
        })
        .catch(console.error)
        .finally(() => setLoading(false));
    }
  }, [course, user]);

  // Check enrollment status
  useEffect(() => {
    setIsEnrolled(false); 
    setCheckingEnrollment(true);
    if (user && courseIdParam) {
      fetch(`http://localhost:8080/api/users/${user.id}/courses`)
        .then(response => { 
          if (!response.ok) throw new Error('Enrollment check failed'); 
          return response.json(); 
        })
        .then(enrolledCourses => {
          const currentCourseIdNum = parseInt(courseIdParam);
          const enrolled = enrolledCourses.some(c => c.id === currentCourseIdNum);
          setIsEnrolled(enrolled);
        })
        .catch(error => { 
          console.error('Error checking enrollment:', error); 
          setIsEnrolled(false); 
        })
        .finally(() => { 
          setCheckingEnrollment(false); 
        });
    } else { 
      setIsEnrolled(false); 
      setCheckingEnrollment(false); 
    }
  }, [courseIdParam, user]);

  // --- Handlers ---
  const handleSnackbar = (msg) => { setSnackbar({ open: true, message: msg }); };
  const handleCloseSnackbar = () => { setSnackbar({ ...snackbar, open: false }); };
  
  // Lesson playback handlers
  const handlePlayLesson = (lesson) => {
    setSelectedLesson(lesson);
    setLessonModalOpen(true);
  };

  const handleCloseLessonModal = () => {
    setLessonModalOpen(false);
    setSelectedLesson(null);
  };

  const handleReviewSubmit = (e) => { 
    e.preventDefault(); 
    if(rating===0){handleSnackbar('Select rating.'); return;} 
    fetch('http://localhost:8080/api/reviews', { 
      method: 'POST', 
      headers:{'Content-Type':'application/json'}, 
      body: JSON.stringify({userId:user.id, courseId:parseInt(courseIdParam), rating, comment}) 
    })
    .then(async res => {
      if(!res.ok) throw await res.json(); 
      return res.json();
    })
    .then(newReview => { 
      handleSnackbar('Review submitted!'); 
      setReviews([...reviews, newReview]); 
      setUserReview(newReview); 
    })
    .catch(err => { 
      handleSnackbar(`Error: ${err.error || err.message}`); 
    }); 
  };
  
  const handleUpdateReview = (e) => { 
    e.preventDefault(); 
    if(!userReview) return; 
    fetch(`http://localhost:8080/api/reviews/${userReview.id}`, { 
      method: 'PUT', 
      headers:{'Content-Type':'application/json'}, 
      body: JSON.stringify({userId:user.id, courseId:parseInt(courseIdParam), rating, comment}) 
    })
    .then(async res => {
      if(!res.ok) throw await res.json(); 
      return res.json();
    })
    .then(updatedReview => { 
      handleSnackbar('Review updated!'); 
      setReviews(reviews.map(r => r.id === updatedReview.id ? updatedReview : r)); 
      setUserReview(updatedReview); 
    })
    .catch(err => { 
      handleSnackbar(`Error: ${err.error || err.message}`); 
    }); 
  };
  
  const handleDeleteReview = () => { 
    if(!userReview || !window.confirm('Delete review?')) return; 
    fetch(`http://localhost:8080/api/reviews/${userReview.id}?userId=${user.id}`, { 
      method: 'DELETE' 
    })
    .then(async res => { 
      if (!res.ok && res.status !== 204) throw await res.json(); 
      handleSnackbar('Review deleted.'); 
      setReviews(reviews.filter(r => r.id !== userReview.id)); 
      setUserReview(null); 
      setRating(0); 
      setComment(''); 
    })
    .catch(err => { 
      handleSnackbar(`Error: ${err.error || err.message}`); 
    }); 
  };
  
  const handleEnroll = () => { 
    if(!user) {handleSnackbar('Login required.'); return;} 
    fetch('http://localhost:8080/api/enrollments', { 
      method: 'POST', 
      headers:{'Content-Type':'application/json'}, 
      body: JSON.stringify({userId:user.id, courseId:parseInt(courseIdParam)}) 
    })
    .then(async res => {
      if(!res.ok) throw await res.json(); 
      handleSnackbar('Enrolled!'); 
      setIsEnrolled(true);
    })
    .catch(err => { 
      handleSnackbar(`Error: ${err.error || err.message}`); 
    }); 
  };

  // --- Loading States ---
  if (loading || checkingEnrollment) { 
    return ( 
      <> 
        <Navbar /> 
        <Box sx={{ display: 'flex', justifyContent: 'center', mt: 4 }}>
          <CircularProgress />
        </Box> 
      </> 
    ); 
  }
  
  // --- Not Found State ---
  if (!course) { 
    return ( 
      <> 
        <Navbar /> 
        <Container sx={{ py: 4 }}>
          <Typography variant="h4" color="error" align="center">Course not found.</Typography>
        </Container> 
      </> 
    ); 
  }

  // --- Main Render ---
  return (
    <>
      <Navbar />
      
      {/* Lesson Modal */}
      <LessonModal 
        open={lessonModalOpen}
        onClose={handleCloseLessonModal}
        lesson={selectedLesson}
        isEnrolled={isEnrolled}
      />

      {/* Header */}
      <Box sx={{ bgcolor: 'background.paper', py: 8 }}>
        <Container maxWidth="lg">
          <Typography component="h1" variant="h2" gutterBottom sx={{ fontWeight: 700, color: 'text.primary' }}>
            {course.title}
          </Typography>
          <Typography variant="h5" color="text.secondary" paragraph>
            {course.description}
          </Typography>
          <Box sx={{ display: 'flex', gap: 2, alignItems: 'center' }}>
            {user && !isEnrolled && (
              <Button variant="contained" color="primary" size="large" onClick={handleEnroll}>
                Enroll Now
              </Button>
            )}
            {isEnrolled && (
              <Typography variant="body1" sx={{ color: 'success.main' }}>
                ✓ You are enrolled in this course
              </Typography>
            )}
          </Box>
        </Container>
      </Box>

      {/* Content */}
      <Container maxWidth="lg" sx={{ py: 4 }}>
        <Grid container spacing={4}>
          {/* Left Column - Lessons */}
          <Grid item xs={12} md={8}>
            <Typography variant="h4" gutterBottom sx={{ color: 'text.primary' }}>
              Course Lessons
            </Typography>
            
            {course.lessons && course.lessons.length > 0 ? (
              <Accordion defaultExpanded sx={{ bgcolor: 'background.paper', color: 'primary.main' }}>
                <AccordionSummary expandIcon={<ExpandMoreIcon sx={{ color: 'text.secondary' }} />}>
                  <Typography variant="h6">
                    All Lessons ({course.lessons.length})
                  </Typography>
                </AccordionSummary>
                <AccordionDetails sx={{ bgcolor: '#000' }}>
                  <List dense>
                    {course.lessons.map(lesson => (
                      <ListItem 
                        key={lesson.id}
                        sx={{ 
                          cursor: 'pointer',
                          '&:hover': { bgcolor: 'grey.900' }
                        }}
                      >
                        <ListItemIcon>
                          <IconButton 
                            onClick={() => handlePlayLesson(lesson)}
                            sx={{ color: 'accent.main' }}
                          >
                            <PlayCircleOutlineIcon />
                          </IconButton>
                        </ListItemIcon>
                        <ListItemText 
                          primary={lesson.title} 
                          secondary={lesson.content ? `${lesson.content.substring(0, 100)}${lesson.content.length > 100 ? '...' : ''}` : 'No description'}
                          primaryTypographyProps={{ color: 'text.primary' }} 
                          secondaryTypographyProps={{ color: 'text.secondary' }} 
                        />
                      </ListItem>
                    ))}
                  </List>
                </AccordionDetails>
              </Accordion>
            ) : (
              <Paper elevation={3} sx={{ p: 3 }}>
                <Typography sx={{ color: 'text.secondary' }}>
                  No lessons available for this course yet.
                </Typography>
              </Paper>
            )}
          </Grid>

          {/* Right Column (Reviews) */}
          <Grid item xs={12} md={4}>
            <Typography variant="h4" gutterBottom sx={{ color: 'text.primary' }}>
              Reviews
            </Typography>
            
            {/* Display existing reviews */}
            <Paper elevation={1} sx={{ p: 2, mb: 3 }}>
              <Typography variant="h6" sx={{ color: 'primary.main', mb: 1 }}>
                Student Feedback
              </Typography>
              {reviews.length === 0 ? (
                <Typography sx={{ color: 'text.secondary' }}>No reviews yet.</Typography>
              ) : (
                <List sx={{ maxHeight: 300, overflow: 'auto' }}>
                  {reviews.map(review => (
                    <ListItem key={review.id} divider alignItems="flex-start">
                      <ListItemText
                        primary={<Rating value={review.rating} readOnly size="small" />}
                        secondary={review.comment || 'No comment'}
                        primaryTypographyProps={{ component: 'div', mb: 0.5, color: 'text.primary' }}
                        secondaryTypographyProps={{ color: 'text.secondary' }}
                      />
                    </ListItem>
                  ))}
                </List>
              )}
            </Paper>
            
            {/* Review Form Section */}
            <Paper elevation={3} sx={{ p: 3 }}>
              {user ? (
                isEnrolled ? (
                  <ReviewForm
                    rating={rating}
                    setRating={setRating}
                    comment={comment}
                    setComment={setComment}
                    handleSubmit={userReview ? handleUpdateReview : handleReviewSubmit}
                    existingReview={userReview}
                    onDelete={handleDeleteReview}
                  />
                ) : (
                  <Typography sx={{ color: 'text.secondary' }}>
                    You must be enrolled to leave a review.
                  </Typography>
                )
              ) : (
                <Typography sx={{ color: 'text.secondary' }}>
                  Please <Button component={RouterLink} to="/login">login</Button> to leave a review.
                </Typography>
              )}
            </Paper>
          </Grid>
        </Grid>
      </Container>
      
      <Snackbar 
        open={snackbar.open} 
        autoHideDuration={6000} 
        onClose={handleCloseSnackbar} 
        message={snackbar.message} 
      />
    </>
  );
}

export default CourseDetailPage;