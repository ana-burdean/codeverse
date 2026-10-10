




import React, { useState, useEffect } from 'react';
import {
  Container, Box, Typography, TextField, Button, Paper, Snackbar, Grid, Divider,
  Select, MenuItem, FormControl, InputLabel, List, ListItem, ListItemButton,
  ListItemText, IconButton, Modal, Avatar, CircularProgress,
  Tabs, Tab
} from '@mui/material';
import EditIcon from '@mui/icons-material/Edit';
import DeleteIcon from '@mui/icons-material/Delete';
import Navbar from './Navbar';
import { useAuth } from './context/AuthContext';

// Style for the modals
const modalStyle = {
  position: 'absolute', top: '50%', left: '50%', transform: 'translate(-50%, -50%)',
  width: { xs: '90%', sm: 400 }, bgcolor: 'background.paper',
  border: '1px solid rgba(255, 255, 255, 0.12)', boxShadow: 24, p: 4, color: 'text.primary',
};

// TabPanel component
function TabPanel(props) {
  const { children, value, index, ...other } = props;
  return (
    <div role="tabpanel" hidden={value !== index} id={`admin-tabpanel-${index}`} aria-labelledby={`admin-tab-${index}`} {...other}>
      {value === index && (<Box sx={{ pt: 3 }}>{children}</Box>)}
    </div>
  );
}
function a11yProps(index) { return { id: `admin-tab-${index}`, 'aria-controls': `admin-tabpanel-${index}` }; }

function AdminDashboard() {
  const { user } = useAuth();

  // --- States ---
  const [title, setTitle] = useState('');
  const [description, setDescription] = useState('');
  const [courses, setCourses] = useState([]);
  const [loadingCourses, setLoadingCourses] = useState(true);
  const [selectedCourseForLesson, setSelectedCourseForLesson] = useState('');
  const [lessonTitle, setLessonTitle] = useState('');
  const [lessonContent, setLessonContent] = useState('');
  const [message, setMessage] = useState('');
  const [openSnackbar, setOpenSnackbar] = useState(false);
  const [editCourseModalOpen, setEditCourseModalOpen] = useState(false);
  const [currentCourse, setCurrentCourse] = useState(null);
  const [editTitle, setEditTitle] = useState('');
  const [editDescription, setEditDescription] = useState('');
  const [userList, setUserList] = useState([]);
  const [loadingUsers, setLoadingUsers] = useState(true);
  const [selectedCourseLessons, setSelectedCourseLessons] = useState([]);
  const [editLessonModalOpen, setEditLessonModalOpen] = useState(false);
  const [currentLesson, setCurrentLesson] = useState(null);
  const [editLessonTitle, setEditLessonTitle] = useState('');
  const [editLessonContent, setEditLessonContent] = useState('');
  const [tabValue, setTabValue] = useState(0);
  const [newModuleTitle, setNewModuleTitle] = useState('');
  const [selectedCourseForNewModule, setSelectedCourseForNewModule] = useState('');

  // --- Fetch Initial Data ---
  useEffect(() => {
    setLoadingCourses(true);
    fetch('http://localhost:8080/api/courses').then(r=>r.json()).then(setCourses).catch(console.error).finally(()=>setLoadingCourses(false));
  }, []);
  useEffect(() => {
    if (user?.role === 'ADMIN') {
      setLoadingUsers(true);
      fetch(`http://localhost:8080/api/admin/users?adminUserId=${user.id}`)
        .then(r=>{if(!r.ok) throw Error('Failed'); return r.json()})
        .then(setUserList)
        .catch(console.error)
        .finally(()=>setLoadingUsers(false));
    } else {
      setLoadingUsers(false);
    }
  }, [user]);

  // --- Handlers ---
  const handleSnackbar = (msg) => { setMessage(msg); setOpenSnackbar(true); };
  const handleCloseSnackbar = () => { setOpenSnackbar(false); };
  const handleTabChange = (event, newValue) => { setTabValue(newValue); };

  const handleCourseSubmit = (event) => {
    event.preventDefault(); if (!user) return;
    fetch('http://localhost:8080/api/courses', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ adminUserId: user.id, title, description })
    })
    .then(async (res) => { if (!res.ok) {const d=await res.json(); throw new Error(d.error||'Failed');} return res.json(); })
    .then((newCourse) => {
      handleSnackbar(`Course '${newCourse.title}' created.`);
      setCourses([...courses, newCourse]);
      setTitle(''); setDescription('');
    })
    .catch((err) => { handleSnackbar(`Error: ${err.message}`); });
  };
  const handleDeleteCourse = (id) => {
    if (!user || !window.confirm('Delete course?')) return;
    fetch(`http://localhost:8080/api/courses/${id}?adminUserId=${user.id}`, { method: 'DELETE' })
    .then(async (res) => { if (!res.ok && res.status !== 204) {const d=await res.json(); throw new Error(d.error||'Failed');} handleSnackbar('Course deleted.'); setCourses(courses.filter(c => c.id !== id)); if(currentCourse?.id === id) {setCurrentCourse(null); setSelectedCourseLessons([]);} })
    .catch((err) => { handleSnackbar(`Error: ${err.message}`); });
  };
  const handleOpenEditCourseModal = (course) => { setCurrentCourse(course); setEditTitle(course.title); setEditDescription(course.description); setEditCourseModalOpen(true); };
  const handleCloseEditCourseModal = () => { setEditCourseModalOpen(false); };
  const handleUpdateCourse = (event) => {
    event.preventDefault(); if (!user || !currentCourse) return;
    fetch(`http://localhost:8080/api/courses/${currentCourse.id}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ adminUserId: user.id, title: editTitle, description: editDescription })
    })
    .then(async res => { if (!res.ok) {const d=await res.json(); throw new Error(d.error||'Failed');} return res.json(); })
    .then((updatedCourse) => {
      handleSnackbar(`Course updated.`);
      setCourses(courses.map(c => c.id === updatedCourse.id ? updatedCourse : c));
      setCurrentCourse(updatedCourse);
      handleCloseEditCourseModal();
    })
    .catch((err) => { handleSnackbar(`Error: ${err.message}`); });
  };
  const handleLessonSubmit = (event) => {
    event.preventDefault(); if (!user || !selectedCourseForLesson) { handleSnackbar('Select a course first.'); return; }
    fetch('http://localhost:8080/api/lessons', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ adminUserId: user.id, courseId: selectedCourseForLesson, title: lessonTitle, content: lessonContent })
    })
    .then(async res => { if (!res.ok) {const d=await res.json(); throw new Error(d.error||'Failed');} return res.json(); })
    .then((newLesson) => {
      handleSnackbar(`Lesson added.`);
      if (currentCourse && currentCourse.id === selectedCourseForLesson) { setSelectedCourseLessons([...selectedCourseLessons, newLesson]); }
      setSelectedCourseForLesson(''); setLessonTitle(''); setLessonContent('');
    })
    .catch((err) => { handleSnackbar(`Error: ${err.message}`); });
  };
  const handleOpenEditLessonModal = (lesson) => { if (!currentCourse) return; setCurrentLesson(lesson); setEditLessonTitle(lesson.title); setEditLessonContent(lesson.content); setEditLessonModalOpen(true); };
  const handleCloseEditLessonModal = () => { setEditLessonModalOpen(false); setCurrentLesson(null); };
  const handleUpdateLesson = (event) => {
    event.preventDefault(); if (!user || !currentLesson || !currentCourse) return;
    const requestBody = { adminUserId: user.id, courseId: currentCourse.id, title: editLessonTitle, content: editLessonContent };
    fetch(`http://localhost:8080/api/lessons/${currentLesson.id}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(requestBody)
    })
      .then(async res => { if (!res.ok) {const d=await res.json(); throw new Error(d.error||'Failed');} return res.json(); })
      .then(updatedLesson => { handleSnackbar('Lesson updated.'); setSelectedCourseLessons(selectedCourseLessons.map(l => l.id === updatedLesson.id ? updatedLesson : l)); handleCloseEditLessonModal(); })
      .catch(err => { handleSnackbar(`Error: ${err.message}`); });
  };
  const handleDeleteLesson = (lessonId) => {
    if (!user || !currentCourse || !window.confirm('Delete lesson?')) return;
    fetch(`http://localhost:8080/api/lessons/${lessonId}?courseId=${currentCourse.id}&adminUserId=${user.id}`, { method: 'DELETE' })
      .then(async res => { if (!res.ok && res.status !== 204) {const d=await res.json(); throw new Error(d.error||'Failed');} handleSnackbar('Lesson deleted.'); setSelectedCourseLessons(selectedCourseLessons.filter(l => l.id !== lessonId)); })
      .catch(err => { handleSnackbar(`Error: ${err.message}`); });
  };
  const handleDeleteUser = (id) => {
    if (!user || id === user.id || !window.confirm('Delete user?')) return;
    fetch(`http://localhost:8080/api/admin/users/${id}?adminUserId=${user.id}`, { method: 'DELETE' })
    .then(async (res) => { if (!res.ok && res.status !== 204) {const d=await res.json(); throw new Error(d.error||'Failed');} handleSnackbar('User deleted.'); setUserList(userList.filter(u => u.id !== id)); })
    .catch((err) => { handleSnackbar(`Error: ${err.message}`); });
  };

  const selectCourseForLessonManagement = (course) => {
    setCurrentCourse(course);
    fetch(`http://localhost:8080/api/courses/${course.id}`)
      .then(res => res.json())
      .then(detailedCourse => setSelectedCourseLessons(detailedCourse.lessons || []))
      .catch(err => console.error("Error fetching course details for lessons", err));
  };

  // --- JSX Render ---
  return (
    <>
      <Navbar />
      <Container maxWidth="lg" sx={{ py: 4 }}>
        <Typography variant="h4" component="h1" gutterBottom sx={{ color: 'primary.main', mb: 2 }}>Admin Dashboard</Typography>

        {/* Tabs */}
        <Box sx={{ borderBottom: 1, borderColor: 'divider', mb: 2 }}>
          <Tabs value={tabValue} onChange={handleTabChange} aria-label="Admin sections">
            <Tab label="Courses" {...a11yProps(0)} sx={{ color: 'text.secondary' }}/>
            <Tab label="Lessons" {...a11yProps(1)} sx={{ color: 'text.secondary' }}/>
            <Tab label="Users" {...a11yProps(2)} sx={{ color: 'text.secondary' }}/>
          </Tabs>
        </Box>

        {/* Tab Panels */}
        {/* Courses Tab */}
        <TabPanel value={tabValue} index={0}>
          <Grid container spacing={4}>
            <Grid item xs={12} md={5}>
              <Paper elevation={3} sx={{ p: 4, height: '100%' }}>
                <Typography variant="h5">Create Course</Typography>
                <Box component="form" onSubmit={handleCourseSubmit} noValidate sx={{ display: 'flex', flexDirection: 'column', height: '100%' }}>
                  <TextField margin="normal" required fullWidth label="Title" value={title} onChange={(e) => setTitle(e.target.value)} />
                  <TextField margin="normal" required fullWidth label="Description" multiline rows={4} value={description} onChange={(e) => setDescription(e.target.value)} sx={{ flexGrow: 1 }} />
                  <Button type="submit" fullWidth variant="contained" sx={{ mt: 'auto', bgcolor: 'accent.main' }}>Create</Button>
                </Box>
              </Paper>
            </Grid>
            <Grid item xs={12} md={7}>
              <Box>
                <Typography variant="h5">Manage Courses</Typography>
                <Paper elevation={3}>
                  <List sx={{ maxHeight: 400, overflow: 'auto' }}>
                    {loadingCourses ? (
                      <ListItem><CircularProgress size={24}/></ListItem>
                    ) : courses.length === 0 ? (
                      <ListItem><ListItemText primary="No courses."/></ListItem>
                    ) : (
                      courses.map((c) => (
                        <ListItem
                          key={c.id}
                          divider
                          secondaryAction={
                            <Box>
                              <IconButton
                                edge="end"
                                onClick={(e) => { e.stopPropagation(); handleOpenEditCourseModal(c); }}
                                sx={{ color: 'primary.light' }}
                              >
                                <EditIcon />
                              </IconButton>
                              <IconButton
                                edge="end"
                                onClick={(e) => { e.stopPropagation(); handleDeleteCourse(c.id); }}
                                sx={{ ml: 1 }}
                              >
                                <DeleteIcon color="error" />
                              </IconButton>
                            </Box>
                          }
                          sx={{ pr: 9 }}
                        >
                          <ListItemText
                            primary={c.title}
                            secondary={`ID: ${c.id}`}
                            sx={{ wordBreak: 'break-word' }}
                          />
                        </ListItem>
                      ))
                    )}
                  </List>
                </Paper>
              </Box>
            </Grid>
          </Grid>
        </TabPanel>

        {/* Lessons Tab */}
        <TabPanel value={tabValue} index={1}>
          <Grid container spacing={4}>
            <Grid item xs={12} md={5}>
              <Paper elevation={3} sx={{ p: 4, height: '100%' }}>
                <Typography variant="h5">Add Lesson</Typography>
                <Box component="form" onSubmit={handleLessonSubmit} noValidate sx={{ display: 'flex', flexDirection: 'column', height: '100%' }}>
                  <FormControl fullWidth margin="normal">
                    <InputLabel>Course</InputLabel>
                    <Select value={selectedCourseForLesson} label="Course" onChange={(e) => setSelectedCourseForLesson(e.target.value)}>
                      {loadingCourses ? (
                        <MenuItem disabled>...</MenuItem>
                      ) : courses.map((c) => (
                        <MenuItem key={c.id} value={c.id}>{c.title}</MenuItem>
                      ))}
                    </Select>
                  </FormControl>
                  <TextField margin="normal" required fullWidth label="Title" value={lessonTitle} onChange={(e) => setLessonTitle(e.target.value)} />
                  <TextField margin="normal" required fullWidth label="Content" multiline rows={4} value={lessonContent} onChange={(e) => setLessonContent(e.target.value)} sx={{ flexGrow: 1 }}/>
                  <Button type="submit" fullWidth variant="contained" sx={{ mt: 'auto', bgcolor: 'accent.main' }}>Add</Button>
                </Box>
              </Paper>
            </Grid>
            <Grid item xs={12} md={7}>
              <Box>
                <Typography variant="h5">Manage Lessons</Typography>
                <FormControl fullWidth margin="normal">
                  <InputLabel>Select Course to Manage</InputLabel>
                  <Select value={currentCourse?.id || ''} label="Select Course to Manage" onChange={(e) => {
                    const courseId = e.target.value;
                    const selected = courses.find(c => c.id === courseId);
                    selectCourseForLessonManagement(selected);
                  }}>
                    {loadingCourses ? <MenuItem disabled>...</MenuItem> : courses.map((c) => (
                      <MenuItem key={c.id} value={c.id}>{c.title}</MenuItem>
                    ))}
                  </Select>
                </FormControl>
                <Paper elevation={3} sx={{minHeight: 300, mt: 2, width: '100%'}}>
                  {currentCourse ? (
                    <List sx={{ maxHeight: 300, overflow: 'auto' }}>
                      {selectedCourseLessons.length === 0 ? (
                        <ListItem><ListItemText primary="No lessons." /></ListItem>
                      ) : (
                        selectedCourseLessons.map((l) => (
                          <ListItem
                            key={l.id}
                            divider
                            secondaryAction={
                              <Box>
                                <IconButton
                                  edge="end"
                                  onClick={() => handleOpenEditLessonModal(l)}
                                  sx={{ color: 'primary.main' }}
                                >
                                  <EditIcon fontSize="small"/>
                                </IconButton>
                                <IconButton
                                  edge="end"
                                  onClick={() => handleDeleteLesson(l.id)}
                                  sx={{ ml: 1 }}
                                >
                                  <DeleteIcon color="error" fontSize="small"/>
                                </IconButton>
                              </Box>
                            }
                            sx={{ pr: 9 }}
                          >
                            <ListItemText
                              primary={l.title}
                              secondary={`ID: ${l.id}`}
                              sx={{ wordBreak: 'break-word' }}
                            />
                          </ListItem>
                        ))
                      )}
                    </List>
                  ) : (
                    <Typography sx={{ p: 2 }}>Select course.</Typography>
                  )}
                </Paper>
              </Box>
            </Grid>
          </Grid>
        </TabPanel>

        {/* Users Tab */}
        <TabPanel value={tabValue} index={2}>
          <Box>
            <Typography variant="h5">Manage Users</Typography>
            <Paper elevation={3}>
              <List sx={{ maxHeight: 400, overflow: 'auto' }}>
                {loadingUsers ? (
                  <ListItem><CircularProgress size={24}/></ListItem>
                ) : userList.length === 0 ? (
                  <ListItem><ListItemText primary="No users."/></ListItem>
                ) : (
                  userList.map((u) => (
                    <ListItem
                      key={u.id}
                      divider
                      secondaryAction={
                        <Box>
                          <IconButton
                            edge="end"
                            onClick={() => handleDeleteUser(u.id)}
                            disabled={u.id === user?.id}
                          >
                            <DeleteIcon sx={{ color: u.id === user?.id ? 'grey.500' : 'error.main' }}/>
                          </IconButton>
                        </Box>
                      }
                    >
                      <Avatar sx={{ bgcolor: 'primary.main', mr: 2 }}>{u.username.charAt(0).toUpperCase()}</Avatar>
                      <ListItemText primary={u.username} secondary={`ID: ${u.id} | Role: ${u.role}`} />
                    </ListItem>
                  ))
                )}
              </List>
            </Paper>
          </Box>
        </TabPanel>

        {/* Modals */}
        <Modal open={editCourseModalOpen} onClose={handleCloseEditCourseModal}>
          <Box sx={modalStyle} component="form" onSubmit={handleUpdateCourse} noValidate>
            <Typography variant="h6">Edit Course</Typography>
            <TextField margin="normal" required fullWidth label="Title" value={editTitle} onChange={(e) => setEditTitle(e.target.value)} sx={{ mt: 2 }}/>
            <TextField margin="normal" required fullWidth label="Description" multiline rows={4} value={editDescription} onChange={(e) => setEditDescription(e.target.value)} />
            <Box sx={{ mt: 3, display: 'flex', justifyContent: 'flex-end' }}>
              <Button onClick={handleCloseEditCourseModal} sx={{ mr: 1 }}>Cancel</Button>
              <Button type="submit" variant="contained">Save</Button>
            </Box>
          </Box>
        </Modal>
        <Modal open={editLessonModalOpen} onClose={handleCloseEditLessonModal}>
          <Box sx={modalStyle} component="form" onSubmit={handleUpdateLesson} noValidate>
            <Typography variant="h6">Edit Lesson</Typography>
            <TextField margin="normal" required fullWidth label="Title" value={editLessonTitle} onChange={(e) => setEditLessonTitle(e.target.value)} sx={{ mt: 2 }}/>
            <TextField margin="normal" required fullWidth label="Content" multiline rows={4} value={editLessonContent} onChange={(e) => setEditLessonContent(e.target.value)} />
            <Box sx={{ mt: 3, display: 'flex', justifyContent: 'flex-end' }}>
              <Button onClick={handleCloseEditLessonModal} sx={{ mr: 1 }}>Cancel</Button>
              <Button type="submit" variant="contained">Save</Button>
            </Box>
          </Box>
        </Modal>

        {/* Snackbar */}
        <Snackbar open={openSnackbar} autoHideDuration={6000} onClose={handleCloseSnackbar} message={message} />
      </Container>
    </>
  );
}
export default AdminDashboard;
