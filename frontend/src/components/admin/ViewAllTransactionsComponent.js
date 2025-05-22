import { useState, useEffect, useContext } from 'react';
import api from '../../api';
import 'bootstrap/dist/css/bootstrap.min.css';
import { ThemeContext } from '../../contexts/ThemeContext';

const ViewAllTransactionsComponent = () => {
  const [transactions, setTransactions] = useState([]);
  const [error, setError] = useState('');
  const [transactionTypeFilter, setTransactionTypeFilter] = useState('');
  const [startDateFilter, setStartDateFilter] = useState('');
  const [endDateFilter, setEndDateFilter] = useState('');
  const [accountNumberFilter, setAccountNumberFilter] = useState('');
  const [page, setPage] = useState(0);
  const [pageSize, setPageSize] = useState(10);
  const [totalElements, setTotalElements] = useState(0);
  const { theme } = useContext(ThemeContext);
  const isDark = theme === 'dark';
  const transactionTypes = ['', 'Debit', 'Credit']; 

  useEffect(() => {
    fetchAllTransactions();
  }, [page, pageSize, transactionTypeFilter, startDateFilter, endDateFilter, accountNumberFilter]);

  const fetchAllTransactions = async () => {
    setError('');
    try {
      const response = await api.get(
        `/admin/transactions?page=${page}&size=${pageSize}&transactionType=${transactionTypeFilter}&startDate=${startDateFilter}&endDate=${endDateFilter}&accountNumber=${accountNumberFilter}`
      );
      setTransactions(response.data.content);
      setTotalElements(response.data.totalElements);
    } catch (error) {
      setError(error.response?.data?.message || 'Failed to fetch transactions.');
    }
  };

  const handleFilterChange = (event) => {
    const { name, value } = event.target;
    switch (name) {
      case 'transactionType':
        setTransactionTypeFilter(value);
        break;
      case 'startDate':
        setStartDateFilter(value);
        break;
      case 'endDate':
        setEndDateFilter(value);
        break;
      case 'accountNumber':
        setAccountNumberFilter(value);
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

  return (
    <div className={`container mt-4 ${isDark ? 'bg-dark text-light' : 'bg-light text-dark'}`}>
      <h2 className="mb-4">All Transactions</h2>
      {error && <div className="alert alert-danger">{error}</div>}

      <div className="mb-3">
        <div className="row g-2 align-items-center">
          <div className="col-md-3">
            <label htmlFor="transactionTypeFilter" className="form-label">Transaction Type:</label>
            <select
              className={`form-select ${isDark ? 'bg-secondary text-light border-secondary' : ''}`}
              id="transactionTypeFilter"
              name="transactionType"
              value={transactionTypeFilter}
              onChange={handleFilterChange}
            >
              {transactionTypes.map((type) => (
                <option key={type} value={type}>{type || 'All Types'}</option>
              ))}
            </select>
          </div>
          <div className="col-md-3">
            <label htmlFor="startDateFilter" className="form-label">Start Date:</label>
            <input type="date" className={`form-control ${isDark ? 'bg-secondary text-light border-secondary' : ''}`} id="startDateFilter" name="startDate" value={startDateFilter} onChange={handleFilterChange} />
          </div>
          <div className="col-md-3">
            <label htmlFor="endDateFilter" className="form-label">End Date:</label>
            <input type="date" className={`form-control ${isDark ? 'bg-secondary text-light border-secondary' : ''}`} id="endDateFilter" name="endDate" value={endDateFilter} onChange={handleFilterChange} />
          </div>
          <div className="col-md-3">
            <label htmlFor="accountNumberFilter" className="form-label">Account Number:</label>
            <input type="text" className={`form-control ${isDark ? 'bg-secondary text-light border-secondary' : ''}`} id="accountNumberFilter" name="accountNumber" value={accountNumberFilter} onChange={handleFilterChange} />
          </div>
        </div>
      </div>

      {transactions.length === 0 ? (
        <div className="alert alert-info">No transactions found.</div>
      ) : (
        <div className="table-responsive">
          <table className={`table table-striped ${isDark ? 'table-dark' : 'table-light'}`}>
            <thead>
              <tr>
                <th>Transaction ID</th>
                <th>User ID</th>
                <th>Type</th>
                <th className="text-end">Amount</th>
                <th>Date</th>
                <th>Description</th>
                <th>Receiver Account</th>
                <th>Sender Account</th>
              </tr>
            </thead>
            <tbody>
              {transactions.map((transaction) => (
                <tr key={transaction.transactionId}>
                  <td>{transaction.transactionId}</td>
                  <td>{transaction.user?.userId}</td>
                  <td>{transaction.amount < 0 ? 'Debit' : 'Credit'}</td>
                  <td className={`text-end ${transaction.amount < 0 ? 'text-danger' : 'text-success'}`}>{Math.abs(transaction.amount)}</td>
                  <td>{new Date(transaction.transactionDate).toLocaleString()}</td>
                  <td>{transaction.description}</td>
                  <td>{transaction.receiverAccountNumber || '-'}</td>
                  <td>{transaction.senderAccountNumber || '-'}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      <div className="d-flex justify-content-between align-items-center mt-3">
        <button onClick={() => window.history.back()} className={`btn btn-outline-secondary ${isDark ? 'btn-outline-light' : ''}`}>Back to Dashboard</button>
        <div className="d-flex align-items-center">
          <div className="me-3">
            <label htmlFor="pageSizeSelect" className="form-label me-2">Items per page:</label>
            <select id="pageSizeSelect" className={`form-select form-select-sm ${isDark ? 'bg-secondary text-light border-secondary' : ''}`} value={pageSize} onChange={handlePageSizeChange}>
              <option value="10">10</option>
              <option value="20">20</option>
              <option value="50">50</option>
            </select>
          </div>
          <nav aria-label="Transaction pagination">
            <ul className="pagination pagination-sm mb-0">
              <li className={`page-item ${page === 0 ? 'disabled' : ''}`}>
                <button className="page-link" onClick={() => handlePageChange(page - 1)}>Previous</button>
              </li>
              <li className="page-item disabled">
                <span className="page-link">Page {page + 1} of {Math.ceil(totalElements / pageSize)}</span>
              </li>
              <li className={`page-item ${page >= Math.ceil(totalElements / pageSize) - 1 ? 'disabled' : ''}`}>
                <button className="page-link" onClick={() => handlePageChange(page + 1)}>Next</button>
              </li>
            </ul>
          </nav>
        </div>
      </div>
    </div>
  );
};

export default ViewAllTransactionsComponent;