import { useState, useEffect, useContext } from 'react';
import api from '../../api';
import 'bootstrap/dist/css/bootstrap.min.css';
import { ThemeContext } from '../../contexts/ThemeContext';

const UserManagementComponent = () => {
  const [users, setUsers] = useState([]);
  const [error, setError] = useState('');
  const [page, setPage] = useState(0);
  const [pageSize, setPageSize] = useState(10);
  const [totalElements, setTotalElements] = useState(0);
  const [usernameFilter, setUsernameFilter] = useState('');
  const [emailFilter, setEmailFilter] = useState('');
  const [roleFilter, setRoleFilter] = useState('');
  const [isActiveFilter, setIsActiveFilter] = useState('');
  const [editingRoleUserId, setEditingRoleUserId] = useState(null);
  const [newRole, setNewRole] = useState('');
  const { theme } = useContext(ThemeContext);
  const isDark = theme === 'dark';

  const loggedInUserId = localStorage.getItem('userId');

  useEffect(() => {
    fetchAllUsers();
  }, [page, pageSize, usernameFilter, emailFilter, roleFilter, isActiveFilter]);

  const fetchAllUsers = async () => {
    setError('');
    try {
      const params = new URLSearchParams();
      params.append('page', page);
      params.append('size', pageSize);

      if (usernameFilter) params.append('username', usernameFilter);
      if (emailFilter) params.append('email', emailFilter);
      if (roleFilter) params.append('role', roleFilter);
      if (isActiveFilter !== '') params.append('isActive', isActiveFilter);

      const response = await api.get(`/admin/users?${params.toString()}`);
      setUsers(response.data.content);
      setTotalElements(response.data.totalElements || 0);
    } catch (error) {
      setError(error.response?.data?.message || 'Failed to fetch users.');
    }
  };

  const handleFilterChange = (event) => {
    const { name, value } = event.target;
    switch (name) {
      case 'username':
        setUsernameFilter(value);
        break;
      case 'email':
        setEmailFilter(value);
        break;
      case 'role':
        setRoleFilter(value);
        break;
      case 'isActive':
        setIsActiveFilter(value);
        break;
      default:
        break;
    }
    setPage(0);
  };

  const handlePageChange = (newPage) => {
    if (newPage >= 0 && newPage < Math.ceil(totalElements / pageSize)) {
      setPage(newPage);
    }
  };

  const handlePageSizeChange = (event) => {
    const newSize = parseInt(event.target.value, 10);
    setPageSize(newSize);
    setPage(0);
  };

  const handleToggleActive = async (userId, currentActive) => {
    if (userId.toString() === loggedInUserId) {
      alert("You cannot deactivate/activate your own account!");
      return;
    }
    try {
      const response = await api.put(`/admin/users/${userId}/status?isActive=${!currentActive}`);
      if (response.status === 200) {
        setUsers(users.map(user =>
          user.userId === userId ? { ...user, active: !currentActive } : user
        ));
      } else {
        setError('Failed to update user status.');
      }
    } catch (error) {
      setError(error.response?.data?.message || 'Failed to update user status.');
    }
  };

  const handleEditRole = (userId, currentRole) => {
    if (userId.toString() === loggedInUserId) {
      alert("You cannot change the role of your own account!");
      return;
    }
    setEditingRoleUserId(userId);
    setNewRole(currentRole);
  };

  const handleNewRoleChange = (event) => {
    setNewRole(event.target.value);
  };

  const handleSaveRole = async (userId) => {
    if (userId.toString() === loggedInUserId) {
      alert("You cannot change the role of your own account!");
      return;
    }
    try {
      const response = await api.put(`/admin/users/${userId}/role?role=${newRole}`);
      if (response.status === 200) {
        setUsers(users.map(user =>
          user.userId === userId ? { ...user, role: newRole } : user
        ));
        setEditingRoleUserId(null);
      } else {
        setError('Failed to update user role.');
      }
    } catch (error) {
      setError(error.response?.data?.message || 'Failed to update user role.');
    }
  };

  const handleCancelEditRole = () => {
    setEditingRoleUserId(null);
  };

  const handleDeleteUser = async (userId) => {
    if (userId.toString() === loggedInUserId) {
      alert("You cannot delete your own account!");
      return;
    }
    if (window.confirm(`Are you sure you want to delete user with ID: ${userId}? This action cannot be undone.`)) {
      try {
        const response = await api.delete(`/admin/users/${userId}`);
        if (response.status === 204) {
          fetchAllUsers();
        } else {
          setError('Failed to delete user.');
        }
      } catch (error) {
        setError(error.response?.data?.message || 'Failed to delete user.');
      }
    }
  };

  const totalPages = Math.ceil(totalElements / pageSize);

  return (
    <div className={`container mt-4 ${isDark ? 'bg-dark text-light' : 'bg-light text-dark'}`}>
      <h2 className="mb-4">User Management</h2>
      {error && <div className="alert alert-danger">{error}</div>}

      <div className="mb-3">
        <div className="row g-2 align-items-center">
          <div className="col-md-3">
            <label htmlFor="usernameFilter" className="form-label">Username:</label>
            <input type="text" className={`form-control ${isDark ? 'bg-secondary text-light border-secondary' : ''}`} id="usernameFilter" name="username" value={usernameFilter} onChange={handleFilterChange} />
          </div>
          <div className="col-md-3">
            <label htmlFor="emailFilter" className="form-label">Email:</label>
            <input type="text" className={`form-control ${isDark ? 'bg-secondary text-light border-secondary' : ''}`} id="emailFilter" name="email" value={emailFilter} onChange={handleFilterChange} />
          </div>
          <div className="col-md-3">
            <label htmlFor="roleFilter" className="form-label">Role:</label>
            <select className={`form-select ${isDark ? 'bg-secondary text-light border-secondary' : ''}`} id="roleFilter" name="role" value={roleFilter} onChange={handleFilterChange}>
              <option value="">All Roles</option>
              <option value="CUSTOMER">Customer</option>
              <option value="ADMIN">Admin</option>
            </select>
          </div>
          <div className="col-md-3">
            <label htmlFor="isActiveFilter" className="form-label">Is Active:</label>
            <select className={`form-select ${isDark ? 'bg-secondary text-light border-secondary' : ''}`} id="isActiveFilter" name="isActive" value={isActiveFilter} onChange={handleFilterChange}>
              <option value="">All</option>
              <option value="true">Active</option>
              <option value="false">Inactive</option>
            </select>
          </div>
        </div>
      </div>

      {users.length === 0 ? (
        <div className="alert alert-info">No users found matching current filters.</div>
      ) : (
        <div className="table-responsive">
          <table className={`table table-striped ${isDark ? 'table-dark' : 'table-light'}`}>
            <thead>
              <tr>
                <th>User ID</th>
                <th>Username</th>
                <th>Email</th>
                <th>Role</th>
                <th>Is Active</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {users.map((user) => (
                <tr key={user.userId}>
                  <td>{user.userId}</td>
                  <td>{user.username}</td>
                  <td>{user.email}</td>
                  <td>
                    {editingRoleUserId === user.userId ? (
                      <div className="d-flex align-items-center">
                        <select
                          className={`form-select form-select-sm ${isDark ? 'bg-secondary text-light border-secondary' : ''} me-2`}
                          value={newRole}
                          onChange={handleNewRoleChange}
                          disabled={user.userId.toString() === loggedInUserId}
                        >
                          <option value="CUSTOMER">Customer</option>
                          <option value="ADMIN">Admin</option>
                        </select>
                        <button className="btn btn-sm btn-success me-1" onClick={() => handleSaveRole(user.userId)} disabled={user.userId.toString() === loggedInUserId}>Save</button>
                        <button className="btn btn-sm btn-secondary" onClick={handleCancelEditRole} disabled={user.userId.toString() === loggedInUserId}>Cancel</button>
                      </div>
                    ) : (
                      <span>{user.role}</span>
                    )}
                  </td>
                  <td>{user.active ? <span className="text-success">Yes</span> : <span className="text-danger">No</span>}</td>
                  <td>
                    <button
                      className={`btn btn-sm ${user.active ? 'btn-danger' : 'btn-success'} me-2`}
                      onClick={() => handleToggleActive(user.userId, user.active)}
                      disabled={user.userId.toString() === loggedInUserId}
                    >
                      {user.active ? 'Deactivate' : 'Activate'}
                    </button>
                    {editingRoleUserId !== user.userId && (
                      <button
                        className="btn btn-sm btn-info me-2"
                        onClick={() => handleEditRole(user.userId, user.role)}
                        disabled={user.userId.toString() === loggedInUserId}
                      >
                        Edit Role
                      </button>
                    )}
                    <button
                      className="btn btn-sm btn-danger"
                      onClick={() => handleDeleteUser(user.userId)}
                      disabled={user.userId.toString() === loggedInUserId}
                    >
                      Delete
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      <div className="d-flex justify-content-between align-items-center mt-3">
        <button onClick={() => window.history.back()} className={`btn btn-outline-secondary ${isDark ? 'btn-outline-light' : ''}`}>Back to Dashboard</button>
        <div className="d-flex align-items-center">
          <div className="me-3 d-flex align-items-center">
            <label htmlFor="pageSizeSelect" className="form-label me-2 mb-0">Items per page:</label>
            <select id="pageSizeSelect" className={`form-select form-select-sm ${isDark ? 'bg-secondary text-light border-secondary' : ''}`} value={pageSize} onChange={handlePageSizeChange}>
              <option value="10">10</option>
              <option value="20">20</option>
              <option value="50">50</option>
            </select>
          </div>
          <nav aria-label="User pagination">
            <ul className="pagination pagination-sm mb-0">
              <li className={`page-item ${page === 0 ? 'disabled' : ''}`}>
                <button className="page-link" onClick={() => handlePageChange(page - 1)} disabled={page === 0}>Previous</button>
              </li>
              <li className="page-item disabled">
                <span className="page-link">Page {page + 1} of {totalPages}</span>
              </li>
              <li className={`page-item ${page >= totalPages - 1 ? 'disabled' : ''}`}>
                <button className="page-link" onClick={() => handlePageChange(page + 1)} disabled={page >= totalPages - 1}>Next</button>
              </li>
            </ul>
          </nav>
        </div>
      </div>
    </div>
  );
};

export default UserManagementComponent;