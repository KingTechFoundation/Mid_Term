import React from 'react';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import Header from './Header';
import AdminDashboard from './AdminDashboard';

const App = () => {
  return (
    <Router>
      <Header />
      <Routes>
        <Route path='/admin' element={<AdminDashboard />} />
       
      </Routes>
    </Router>
  );
};

export default App;
