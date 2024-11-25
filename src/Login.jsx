import React, { useState } from 'react';
import './Login.css'; 
import axios from 'axios';

const Login = () => {
  const [formData, setFormData] = useState({
    username: '',
    password: '',
  });

  const [message, setMessage] = useState('');
  const [error, setError] = useState('');
  const [logoutMessage, setLogoutMessage] = useState('');

  
  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData({
      ...formData,
      [name]: value,
    });
  };

 
  const handleSubmit = async (e) => {
    e.preventDefault();

    try {
      
      const response = await fetch('/login', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(formData),
      });

      if (response.ok) {
        setMessage('Login successful!');
        setError('');
        setLogoutMessage('');
        
      } else {
        const errorData = await response.json();
        setError(errorData.message || 'Login failed. Please try again.');
        setMessage('');
      }
    } catch (error) {
      setError('An error occurred. Please try again later.');
      setMessage('');
    }
  };

  return (
    <div className='container'>
      <h2>Content Management System</h2>
      <h1>Login</h1>
      <div>
        {message && <p className='success-message'>{message}</p>}
        {error && <p className='error-message'>{error}</p>}
        {logoutMessage && <p className='success-message'>{logoutMessage}</p>}
      </div>
      <form onSubmit={handleSubmit}>
        <input
          type='text'
          name='username'
          value={formData.username}
          onChange={handleChange}
          placeholder='Username'
          required
        />
        <input
          type='password'
          name='password'
          value={formData.password}
          onChange={handleChange}
          placeholder='Password'
          required
        />
        <button type='submit'>Login</button>
      </form>
      <p>
        <a href='/register'>Don't have an account? Register here</a>
      </p>
      <p>
        <a href='/forgot-password'>Forgot Password?</a>
      </p>

      
      <div className='social-login'>
        <button type='button'>Login with Facebook</button>
        <button type='button'>Login with Google</button>
      </div>

     
      <footer>
        <p>&copy; 2024 YourWebsite. All rights reserved.</p>
        <div className='footer-links'>
          <a href='#'>Privacy Policy</a> | <a href='#'>Terms of Service</a> |{' '}
          <a href='#'>Contact Us</a>
        </div>
      </footer>
    </div>
  );
};

export default Login;
