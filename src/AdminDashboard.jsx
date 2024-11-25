import React, { useState, useEffect } from 'react';
import axios from 'axios';
import {
  Chart as ChartJS,
  BarElement,
  CategoryScale,
  LinearScale,
} from 'chart.js';
import { Bar } from 'react-chartjs-2';
import UserTable from './UserTable';
import Pagination from './Pagination';
import './AdminDashboard.css';

ChartJS.register(BarElement, CategoryScale, LinearScale);

const AdminDashboard = () => {
  const [users, setUsers] = useState([]);
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);
  const [roleStats, setRoleStats] = useState({});

  useEffect(() => {
    
    axios
      .get(`/api/admin/users?page=${currentPage}`)
      .then((response) => {
        setUsers(response.data.users);
        setTotalPages(response.data.totalPages);
      })
      .catch((error) => console.error('Error fetching users:', error));

    
    axios
      .get('/api/admin/user-role-stats')
      .then((response) => setRoleStats(response.data))
      .catch((error) =>
        console.error('Error fetching role statistics:', error)
      );
  }, [currentPage]);

  const handleEdit = (id) => {
    console.log(`Edit user with id: ${id}`);
  };

  const handleDelete = (id) => {
    console.log(`Delete user with id: ${id}`);
  };

  const handlePageChange = (page) => {
    setCurrentPage(page);
  };

  const chartData = {
    labels: Object.keys(roleStats),
    datasets: [
      {
        label: 'Number of Users per Role',
        data: Object.values(roleStats),
        backgroundColor: 'rgba(75, 192, 192, 0.2)',
        borderColor: 'rgba(75, 192, 192, 1)',
        borderWidth: 1,
      },
    ],
  };

  return (
    <div className='admin-dashboard'>
      <h3 className='dashboard-title'>Admin Page</h3>
      <hr />
      <div className='dashboard-actions'>
        <button className='btn btn-primary'>Add New User</button>
        <button className='btn btn-primary'>Search User</button>
        <button className='btn btn-success'>Download Data</button>
        <button className='btn btn-success'>Upload Data</button>
      </div>
      <UserTable users={users} onEdit={handleEdit} onDelete={handleDelete} />
      <Pagination
        currentPage={currentPage}
        totalPages={totalPages}
        onPageChange={handlePageChange}
      />
      <div className='chart-container'>
        <Bar data={chartData} options={{ responsive: true }} />
      </div>
    </div>
  );
};

export default AdminDashboard;
