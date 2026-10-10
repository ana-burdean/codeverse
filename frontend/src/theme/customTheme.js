import { createTheme } from '@mui/material/styles';

const customTheme = createTheme({
  palette: {
    primary: {
      main: '#5E35B1', // Deep purple
      light: '#9575CD',
      dark: '#311B92',
      contrastText: '#FFFFFF',
    },
    secondary: {
      main: '#673AB7',
      light: '#9575CD',
      dark: '#311B92',
      contrastText: '#FFFFFF',
    },
    background: {
      default: '#1E1E1E', // Dark Gray global background
      paper: '#000000',   // Black for paper/cards/containers
    },
    text: {
      primary: '#FFFFFF', // White primary text
      secondary: '#B0B0B0', // Light gray secondary text
    },
    accent: {
      main: '#7C4DFF', // Vibrant purple/blue accent
      contrastText: '#FFFFFF',
    },
    divider: 'rgba(255, 255, 255, 0.12)', // Make divider visible
  },
  typography: {
    fontFamily: [
      'Roboto',
      '"Helvetica Neue"',
      'Arial',
      'sans-serif',
    ].join(','),
    h1: { fontSize: '3rem', fontWeight: 700 },
    h2: { fontSize: '2.5rem', fontWeight: 700 },
    h3: { fontSize: '2rem', fontWeight: 700 },
    h4: { fontSize: '1.5rem', fontWeight: 700 },
  },
  components: {
    MuiAppBar: {
      styleOverrides: {
        root: {
          backgroundColor: '#000000', // Match paper background (black)
          boxShadow: 'none',
        },
      },
    },
    MuiCard: {
      styleOverrides: {
        root: {
          backgroundColor: '#000000', // Match paper background (black)
          color: '#E0E0E0',
          borderRadius: 8,
          border: '1px solid rgba(255, 255, 255, 0.12)', // Subtle border
        },
      },
    },
    MuiPaper: {
      styleOverrides: {
          root: {
              backgroundColor: '#000000', // Match paper background (black)
          }
      }
    },
    MuiButton: {
      styleOverrides: {
        root: {
          borderRadius: 20,
          textTransform: 'none',
        },
        containedPrimary: {
          backgroundColor: '#7C4DFF', // Use accent for primary contained buttons
          '&:hover': {
            backgroundColor: '#651FFF',
          },
        },
      },
    },
    MuiTextField: {
      styleOverrides: {
        root: {
          '& .MuiInputBase-input': {
            color: '#E0E0E0',
          },
          '& .MuiInputLabel-root': {
            color: '#A0A0A0',
          },
          '& .MuiOutlinedInput-root': {
            '& fieldset': {
              borderColor: '#5E35B1',
            },
            '&:hover fieldset': {
              borderColor: '#7C4DFF',
            },
            '&.Mui-focused fieldset': {
              borderColor: '#7C4DFF',
            },
          },
        },
      },
    },
    MuiRating: { // Fix for star visibility
        styleOverrides: {
          iconEmpty: {
            color: 'rgba(255, 255, 255, 0.5)' // Make empty stars semi-transparent white
          }
        }
    },
     MuiSelect: {
        styleOverrides: {
            icon: {
                color: '#A0A0A0',
            },
        }
     },
     MuiMenuItem: {
        styleOverrides: {
            root: {
                '&.Mui-selected': {
                    backgroundColor: 'rgba(124, 77, 255, 0.2)',
                },
                 '&:hover': {
                    backgroundColor: 'rgba(124, 77, 255, 0.1)',
                }
            }
        }
     },
     MuiList: {
        styleOverrides: {
            root: {
                backgroundColor: '#000000', // Match paper background (black)
                color: '#FFFFFF'
            }
        }
     }
  },
});

export default customTheme;