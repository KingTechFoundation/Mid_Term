import React from 'react'
import ReactDOM from 'react-dom/client'
import { BrowserRouter } from 'react-router-dom'
import { CssBaseline, ThemeProvider, createTheme } from '@mui/material'
import App from './App.tsx'
import './index.css'

const theme = createTheme({
  palette: {
    mode: 'light',
    primary: { main: '#0057b7' },
    secondary: { main: '#ffd200' },
    background: { default: '#f7f9fc' }
  },
  shape: { borderRadius: 10 }
})

ReactDOM.createRoot(document.getElementById('root')!).render(
  <React.StrictMode>
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <BrowserRouter>
        <App />
      </BrowserRouter>
    </ThemeProvider>
  </React.StrictMode>
)
