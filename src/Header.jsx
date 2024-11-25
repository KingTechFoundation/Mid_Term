import React from 'react';
import { Link } from 'react-router-dom';

const Header = () => {
  return (
    <header className='header-container'>
      <div id='logo'>Content Management System | Admin Dashboard</div>
      <ul className='navigation-menu'>
        <li>
          <Link to='/admin'>Admin Page</Link>
        </li>
      </ul>
      <div className='ms-auto d-flex align-items-center'>
        <select
          id='language-select'
          className='form-select me-3'
          aria-label='Language select'
        >
          <option value='en'>English</option>
          <option value='fr'>Français</option>
          <option value='es'>Español</option>
        </select>
        <Link to='/user/profile' className='d-flex align-items-center me-3'>
          <img
            src='path/to/profile-picture.jpg'
            alt='Profile'
            className='profile-icon'
          />
        </Link>
        <Link to='/logout' className='btn btn-danger'>
          Logout
        </Link>
      </div>
    </header>
  );
};

export default Header;
