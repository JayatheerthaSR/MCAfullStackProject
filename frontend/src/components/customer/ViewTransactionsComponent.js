import React, { useState, useEffect } from 'react';
import api from '../../api';
import 'bootstrap/dist/css/bootstrap.min.css';
import { useNavigate } from 'react-router-dom';
import { jsPDF } from 'jspdf';
import autoTable from 'jspdf-autotable';
import * as XLSX from 'xlsx';
import { saveAs } from 'file-saver';

const ViewTransactionsComponent = () => {
  const [transactions, setTransactions] = useState([]);
  const [error, setError] = useState('');
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

        const processedTransactions = response.data.transactions.map(transaction => ({
          ...transaction,
          creditDebit: parseFloat(transaction.amount) > 0 ? 'Credit' : 'Debit', // Convert to number for comparison
        }));
        setTransactions(processedTransactions);

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


  const downloadPdf = () => {
    const doc = new jsPDF();
    doc.text('Your Transactions', 10, 10);
    autoTable(doc, {
      head: [['Date', 'Description', 'Amount', 'Credit/Debit', 'Transaction ID']],
      body: transactions.map(transaction => [
        transaction.date,
        transaction.description,
        transaction.amount,
        transaction.creditDebit,
        transaction.transactionId,
      ]),
    });
    doc.save('transactions.pdf');
  };

  const downloadExcel = () => {
    const worksheet = XLSX.utils.json_to_sheet(transactions.map(transaction => ({
      Date: transaction.date,
      Description: transaction.description,
      Amount: transaction.amount,
      'Credit/Debit': transaction.creditDebit,
      'Transaction ID': transaction.transactionId,
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
              <th>Description</th>
              <th className="text-end">Amount</th>
              <th>Credit/Debit</th>
              <th>Transaction ID</th>
            </tr>
          </thead>
          <tbody>
            {transactions.map((transaction) => (
              <tr key={transaction.transactionId}>
                <td>{transaction.date}</td>
                <td>{transaction.description}</td>
                <td className={`text-end ${parseFloat(transaction.amount) < 0 ? 'text-danger' : 'text-success'}`}>{transaction.amount}</td> {/* parseFloat here */}
                <td>{transaction.creditDebit}</td>
                <td>{transaction.transactionId}</td>
              </tr>
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