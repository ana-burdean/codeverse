import React, { useState, useEffect } from 'react';
import { useSearchParams } from 'react-router-dom';
import { Container, Typography, Box } from '@mui/material';
import Navbar from './Navbar';
import CourseList from './CourseList'; // Import the reusable CourseList

function SearchPage() {
  const [searchParams] = useSearchParams();
  const query = searchParams.get('query'); // Get the query from the URL

  // Add state for search results and loading
  const [results, setResults] = useState([]);
  const [loading, setLoading] = useState(true);

  // Add fetch logic for the search endpoint
  useEffect(() => {
    // Only search if there is a query
    if (query) {
      setLoading(true);
      fetch(`http://localhost:8080/api/courses/search?query=${query}`)
        .then((response) => response.json())
        .then((data) => {
          setResults(data);
          setLoading(false);
        })
        .catch((error) => {
          console.error('Error fetching search results:', error);
          setLoading(false);
        });
    }
  }, [query]); // Re-run the search every time the query in the URL changes

  return (
    <>
      <Navbar />
      <Container maxWidth="lg" sx={{ py: 4 }}>
        <Box>
          <Typography 
            variant="h4" 
            component="h1" 
            sx={{ color: 'text.primary' }}
          >
            Search Results for:
          </Typography>
          <Typography 
            variant="h5" 
            component="p" 
            sx={{ color: 'text.secondary', mt: 1, mb: 4, fontStyle: 'italic' }}
          >
            "{query}"
          </Typography>

          {/* Pass the results and loading state to CourseList */}
          <CourseList courses={results} loading={loading} />
        </Box>
      </Container>
    </>
  );
}

export default SearchPage;