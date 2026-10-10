import React, { useState } from 'react';
import { AppBar, Toolbar, Typography, Button, Box, InputBase, alpha } from '@mui/material';
import SearchIcon from '@mui/icons-material/Search';
import { Link as RouterLink, useNavigate } from 'react-router-dom';
import { useAuth } from './context/AuthContext';

function Navbar() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const [searchTerm, setSearchTerm] = useState('');

  const handleLogout = () => { logout(); navigate('/'); };
  const handleSearch = (event) => { event.preventDefault(); if (searchTerm.trim()) { navigate(`/search?query=${searchTerm}`); } };

  return (
    <AppBar position="static" elevation={0} sx={{ borderBottom: (theme) => `1px solid ${theme.palette.divider}` }}>
      <Toolbar>
        <Typography variant="h6" component={RouterLink} to="/" sx={{ flexGrow: 1, color: 'inherit', textDecoration: 'none', fontWeight: 700 }}> CodeVerse </Typography>
        <Box component="form" onSubmit={handleSearch} sx={{ position: 'relative', borderRadius: 20, bgcolor: alpha('#FFF', 0.15), '&:hover': { bgcolor: alpha('#FFF', 0.25) }, mr: 3, width: { xs: 'auto', sm: '300px' } }}>
          <Box sx={{ p: '0 16px', height: '100%', position: 'absolute', display: 'flex', alignItems: 'center', justifyContent: 'center' }}> <SearchIcon /> </Box>
          <InputBase placeholder="Search…" sx={{ color: 'inherit', width: '100%', '& .MuiInputBase-input': { p: '8px 8px 8px 48px' } }} value={searchTerm} onChange={(e) => setSearchTerm(e.target.value)} />
        </Box>
        <Box>
          {user ? (
            <>
              {user.role === 'ADMIN' && (<Button color="inherit" component={RouterLink} to="/admin">Admin</Button>)}
              <Button color="inherit" component={RouterLink} to="/dashboard">Dashboard</Button>
              <Button color="inherit" component={RouterLink} to="/profile" sx={{ mr: 2, ml: 2, textTransform: 'none' }}>Welcome, {user.username}</Button>
              <Button color="inherit" onClick={handleLogout}>Logout</Button>
            </>
          ) : (
            <>
              <Button color="inherit" component={RouterLink} to="/login">Login</Button>
              <Button variant="contained" color="primary" component={RouterLink} to="/register" sx={{ ml: 1 }}>Sign Up</Button>
            </>
          )}
        </Box>
      </Toolbar>
    </AppBar>
  );
}
export default Navbar;