import React, { useState, useEffect } from 'react';
import api from '../../api';
import 'bootstrap/dist/css/bootstrap.min.css';
import { useNavigate } from 'react-router-dom';
import jsPDF from 'jspdf';
import 'jspdf-autotable';
import * as XLSX from 'xlsx';
import { saveAs } from 'file-saver';

const ViewTransactionsComponent = () => {
  const [transactions, setTransactions] = useState([]);
  const [error, setError] = useState('');
  const [expandedTransactionId, setExpandedTransactionId] = useState(null);
  const userId = localStorage.getItem('userId');
  const token = localStorage.getItem('token');
  const navigate = useNavigate();

  useEffect(() => {
    const fetchTransactions = async () => {
      setError('');
      if (!userId) {
        setError('User ID not found. Please log in again.');
        return;
      }
      if (!token) {
        setError('Authentication token not found. Please log in again.');
        return;
      }

      try {
        const response = await api.get(`/customers/${userId}/transactions`, {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });
        setTransactions(response.data);
      } catch (err) {
        console.error('Failed to fetch transactions:', err);
        if (err.response) {
          setError(`Failed to fetch transactions: ${err.response.data?.message || err.response.statusText}`);
        } else if (err.request) {
          setError('Network error. Please check your connection.');
        } else {
          setError('An unexpected error occurred.');
        }
      }
    };

    fetchTransactions();
  }, [userId, token]);

  const toggleExpand = (transactionId) => {
    setExpandedTransactionId(expandedTransactionId === transactionId ? null : transactionId);
  };

  const downloadPdf = () => {
    const doc = new jsPDF();
    doc.text('Your Transactions', 10, 10);
    doc.autoTable({
      head: [['Date', 'Type', 'Description', 'Amount', 'From Account', 'Beneficiary Account', 'Beneficiary Name']],
      body: transactions.map(transaction => [
        transaction.date,
        transaction.type,
        transaction.description,
        transaction.amount,
        transaction.fromAccount || '-',
        transaction.beneficiaryAccountNumber || '-',
        transaction.beneficiaryName || '-',
      ]),
    });
    doc.save('transactions.pdf');
  };

  const downloadExcel = () => {
    const worksheet = XLSX.utils.json_to_sheet(transactions.map(transaction => ({
      Date: transaction.date,
      Type: transaction.type,
      Description: transaction.description,
      Amount: transaction.amount,
      'From Account': transaction.fromAccount || '-',
      'Beneficiary Account': transaction.beneficiaryAccountNumber || '-',
      'Beneficiary Name': transaction.beneficiaryName || '-',
    })));
    const workbook = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(workbook, worksheet, 'Transactions');
    const excelBuffer = XLSX.write(workbook, { bookType: 'xlsx', type: 'array' });
    const data = new Blob([excelBuffer], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=UTF-8' });
    saveAs(data, 'transactions.xlsx');
  };

  return (
    <div className="container mt-4">
      <h2 className="mb-3">Your Transactions</h2>
      {error && (
        <div className="alert alert-danger" role="alert">
          {error}
        </div>
      )}
      {transactions.length === 0 && !error ? (
        <div className="alert alert-info" role="alert">
          No transactions found.
        </div>
      ) : (
        <table className="table table-striped">
          <thead>
            <tr>
              <th>Date</th>
              <th>Type</th>
              <th>Description</th>
              <th className="text-end">Amount</th>
              <th>Details</th>
            </tr>
          </thead>
          <tbody>
            {transactions.map((transaction) => (
              <React.Fragment key={transaction.transactionId}>
                <tr onClick={() => toggleExpand(transaction.transactionId)} style={{ cursor: 'pointer' }}>
                  <td>{transaction.date}</td>
                  <td>{transaction.type}</td>
                  <td>{transaction.description}</td>
                  <td className={`text-end ${transaction.amount < 0 ? 'text-danger' : 'text-success'}`}>{transaction.amount}</td>
                  <td>
                    {expandedTransactionId === transaction.transactionId ? '▲' : '▼'}
                  </td>
                </tr>
                {expandedTransactionId === transaction.transactionId && (
                  <tr>
                    <td colSpan="5">
                      <div className="p-2">
                        <p><strong>Transaction ID:</strong> {transaction.transactionId}</p>
                        <p><strong>From Account:</strong> {transaction.fromAccount || '-'}</p>
                        <p><strong>Beneficiary Account:</strong> {transaction.beneficiaryAccountNumber || '-'}</p>
                        <p><strong>Beneficiary Name:</strong> {transaction.beneficiaryName || '-'}</p>
                      </div>
                    </td>
                  </tr>
                )}
              </React.Fragment>
            ))}
          </tbody>
        </table>
      )}
      <button onClick={() => navigate('../dashboard')} className="btn btn-outline-secondary me-2">Back to Dashboard</button>
      {transactions.length > 0 && (
        <>
          <button onClick={downloadPdf} className="btn btn-outline-primary me-2">Download as PDF</button>
          <button onClick={downloadExcel} className="btn btn-outline-success">Download as Excel</button>
        </>
      )}
    </div>
  );
};

export default ViewTransactionsComponent;