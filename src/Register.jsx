import React, { useState } from 'react';
import './Register.css';

const Register = () => {
  const [formData, setFormData] = useState({
    username: '',
    email: '',
    password: '',
    role: '',
  });

  const [message, setMessage] = useState('');

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
      const response = await fetch('/register', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(formData),
      });

      if (response.ok) {
        setMessage('Registration successful! You can now log in.');
        setFormData({ username: '', email: '', password: '', role: '' });
      } else {
        const errorData = await response.json();
        setMessage(
          errorData.message || 'Registration failed. Please try again.'
        );
      }
    } catch (error) {
      setMessage('An error occurred. Please try again later.');
    }
  };

  return (
    <div className='register-container'>
      <h2>Register</h2>
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
          type='email'
          name='email'
          value={formData.email}
          onChange={handleChange}
          placeholder='Email'
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

        <select
          name='role'
          value={formData.role}
          onChange={handleChange}
          required
        >
          <option value='' disabled>
            Select your role
          </option>
          <option value='ROLE_USER'>User</option>
          <option value='ROLE_SELLER'>Seller</option>
        </select>

        <button type='submit'>Register</button>
      </form>
      {message && <p>{message}</p>}
      <p>
        <a href='/login'>Already have an account? Log in</a>
      </p>
    </div>
  );
};

export default Register;
