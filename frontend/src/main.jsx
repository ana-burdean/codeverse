import React from 'react';
import ReactDOM from 'react-dom/client';
import { createBrowserRouter, RouterProvider } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import App from './App.jsx';
import RegisterPage from './RegisterPage.jsx';
import LoginPage from './LoginPage.jsx';
import DashboardPage from './DashboardPage.jsx';
import ProtectedRoute from './ProtectedRoute.jsx';
import SearchPage from './SearchPage.jsx';
import CourseDetailPage from './CourseDetailPage.jsx';
import AdminRoute from './AdminRoute.jsx';
import AdminDashboard from './AdminDashboard.jsx'; // Keep import for routing
import ProfilePage from './ProfilePage.jsx';

import './index.css'; // Import reset CSS

// Import MUI Theme components
import { ThemeProvider, CssBaseline } from '@mui/material';
import customTheme from './theme/customTheme.js';

const router = createBrowserRouter([
  { path: '/', element: <App /> },
  { path: '/login', element: <LoginPage /> },
  { path: '/register', element: <RegisterPage /> },
  {
    path: '/dashboard',
    element: ( <ProtectedRoute> <DashboardPage /> </ProtectedRoute> ),
  },
  { path: '/search', element: <SearchPage /> },
  { path: '/courses/:id', element: <CourseDetailPage /> },
  {
    path: '/admin',
    element: ( <AdminRoute> <AdminDashboard /> </AdminRoute> ),
  },
  {
    path: '/profile',
    element: ( <ProtectedRoute> <ProfilePage /> </ProtectedRoute> ),
  },
]);

ReactDOM.createRoot(document.getElementById('root')).render(
  <React.StrictMode>
    <ThemeProvider theme={customTheme}>
      <CssBaseline />
      <AuthProvider>
        <RouterProvider router={router} />
      </AuthProvider>
    </ThemeProvider>
  </React.StrictMode>
);