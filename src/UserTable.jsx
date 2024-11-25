import React from 'react';

const UserTable = ({ users, onEdit, onDelete }) => {
  return (
    <table className='table table-bordered table-striped'>
      <thead className='table-dark'>
        <tr>
          <th>Username</th>
          <th>First Name</th>
          <th>Last Name</th>
          <th>Email</th>
          <th>Phone Number</th>
          <th>Profile Picture</th>
          <th>Role</th>
          <th>Actions</th>
        </tr>
      </thead>
      <tbody>
        {users.map((user) => (
          <tr key={user.id}>
            <td>{user.username}</td>
            <td>{user.firstName}</td>
            <td>{user.lastName}</td>
            <td>{user.email}</td>
            <td>{user.phoneNumber}</td>
            <td>
              <img
                src={user.profilePicture}
                alt='Profile'
                width='40'
                height='40'
              />
            </td>
            <td>{user.role}</td>
            <td>
              <button
                onClick={() => onEdit(user.id)}
                className='btn btn-warning btn-sm'
              >
                Edit
              </button>
              <button
                onClick={() => onDelete(user.id)}
                className='btn btn-danger btn-sm'
              >
                Delete
              </button>
            </td>
          </tr>
        ))}
      </tbody>
    </table>
  );
};

export default UserTable;
