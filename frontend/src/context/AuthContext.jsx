import React, { createContext, useState, useContext, useEffect } from 'react';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);

  useEffect(() => {
    const storedUser = localStorage.getItem('user');
    if (storedUser) {
      try { // Add try-catch for invalid JSON
          setUser(JSON.parse(storedUser));
      } catch (error) {
          console.error("Failed to parse stored user:", error);
          localStorage.removeItem('user'); // Clear invalid data
      }
    }
  }, []);

  const login = (userData) => {
    localStorage.setItem('user', JSON.stringify(userData));
    setUser(userData);
  };

  const logout = () => {
    localStorage.removeItem('user');
    setUser(null);
  };

  return ( <AuthContext.Provider value={{ user, login, logout }}> {children} </AuthContext.Provider> );
}
export const useAuth = () => { return useContext(AuthContext); };