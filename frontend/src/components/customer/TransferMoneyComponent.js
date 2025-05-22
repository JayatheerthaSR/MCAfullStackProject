import { useState, useEffect, useContext } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../../api';
import 'bootstrap/dist/css/bootstrap.min.css';
import { ThemeContext } from '../../contexts/ThemeContext';

const TransferMoneyComponent = () => {
  const [accounts, setAccounts] = useState([]);
  const [selectedAccount, setSelectedAccount] = useState('');
  const [beneficiaries, setBeneficiaries] = useState([]);
  const [selectedBeneficiary, setSelectedBeneficiary] = useState(null);
  const [amount, setAmount] = useState('');
  const [description, setDescription] = useState('');
  const [error, setError] = useState('');
  const [successMessage, setSuccessMessage] = useState('');
  const [infoMessage, setInfoMessage] = useState('');
  const [maxTransferLimit, setMaxTransferLimit] = useState(null);
  const [availableBalance, setAvailableBalance] = useState(null);
  const [isInternalTransfer, setIsInternalTransfer] = useState(false);
  const [internalRecipientAccountNumber, setInternalRecipientAccountNumber] = useState('');
  const navigate = useNavigate();
  const customerId = localStorage.getItem('userId');
  const { theme } = useContext(ThemeContext);
  const isDark = theme === 'dark';

  const fetchBalance = async () => {
    if (selectedAccount) {
      try {
        const response = await api.get(`/accounts/${selectedAccount}/balance`);
        if (response.status === 200) {
          setAvailableBalance(response.data);
        } else {
          console.error('Failed to fetch balance for account:', selectedAccount);
          setAvailableBalance(null);
        }
      } catch (error) {
        console.error('Failed to fetch balance for account:', selectedAccount, error);
        setAvailableBalance(null);
      }
    } else {
      setAvailableBalance(null);
    }
  };

  useEffect(() => {
    const fetchAccounts = async () => {
      try {
        const response = await api.get(`/customers/${customerId}/accounts`);
        if (response.status === 200) {
          setAccounts(response.data);
          if (response.data.length === 0) {
            setError('No accounts found for this customer.');
          } else {
            setError('');
          }
        } else if (response.status === 404) {
          setError('No accounts found for this customer.');
          setAccounts([]);
        } else if (response.status === 500) {
          setError('Failed to fetch accounts due to a server error. Please try again later.');
          setAccounts([]);
        } else {
          setError(`Failed to fetch accounts with status: ${response.status}`);
          setAccounts([]);
        }
      } catch (error) {
        console.error('Error fetching accounts:', error);
        setError('Network error or an unexpected issue occurred while fetching accounts.');
        setAccounts([]);
      }
    };

    const fetchBeneficiaries = async () => {
      try {
        const response = await api.get(`/customers/${customerId}/beneficiaries`);
        if (response.status === 200) {
          if (response.data && response.data.length > 0) {
            setBeneficiaries(response.data);
            setInfoMessage('');
          } else {
            setBeneficiaries([]);
            setInfoMessage('No beneficiaries have been added yet. Please add a beneficiary first.');
          }
        } else if (response.status === 404) {
          setBeneficiaries([]);
          setInfoMessage('No beneficiaries found for this customer.');
        } else if (response.status === 500) {
          setError('Failed to fetch beneficiaries due to a server error. Please try again later.');
        } else {
          setError(`Failed to fetch beneficiaries with status: ${response.status}`);
        }
      } catch (error) {
        console.error('Error fetching beneficiaries:', error);
        if (error.response) {
          if (error.response.status === 500) {
            setError('Failed to fetch beneficiaries due to a server error. Please try again later.');
          } else {
            setError(`Failed to fetch beneficiaries with status: ${error.response.status}`);
          }
        } else if (error.request) {
          setError('Network error. Please check your internet connection.');
        } else {
          setError('An unexpected error occurred while fetching beneficiaries.');
        }
      }
    };

    fetchAccounts();
    fetchBeneficiaries();
    if (selectedAccount) {
      fetchBalance();
    }
  }, [customerId, selectedAccount]);

  const handleAccountChange = (event) => {
    setSelectedAccount(event.target.value);
    setAmount('');
    setError('');
    setSuccessMessage('');
    fetchBalance();
  };

  const handleTransferTypeChange = (event) => {
    setIsInternalTransfer(event.target.value === 'internal');
    setSelectedBeneficiary(null);
    setInternalRecipientAccountNumber('');
    setAmount('');
    setDescription('');
    setError('');
  };

  const handleBeneficiaryChange = (event) => {
    const beneficiaryId = event.target.value;
    const selected = beneficiaries.find((b) => b.beneficiaryId === parseInt(beneficiaryId));
    setSelectedBeneficiary(selected);
    setMaxTransferLimit(selected ? parseFloat(selected.maxTransferLimit) : null);
    setAmount('');
    setError('');
  };

  const handleInternalRecipientChange = (event) => {
    setInternalRecipientAccountNumber(event.target.value);
    setError('');
  };

  const handleAmountChange = (event) => {
    const newAmount = event.target.value;
    setAmount(newAmount);
    if (!isInternalTransfer && selectedBeneficiary && parseFloat(newAmount) > parseFloat(selectedBeneficiary.maxTransferLimit)) {
      setError(`Amount exceeds the maximum transfer limit of ${selectedBeneficiary.maxTransferLimit}`);
    } else {
      setError('');
    }
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    setError('');
    setSuccessMessage('');

    if (!selectedAccount) {
      setError('Please select an account to transfer from.');
      return;
    }

    const transferData = {
      sourceAccountNumber: selectedAccount,
      amount: parseFloat(amount),
      description: description,
    };

    try {
      let response;
      if (isInternalTransfer) {
        if (!internalRecipientAccountNumber) {
          setError('Please enter the recipient account number for internal transfer.');
          return;
        }
        transferData.recipientAccountNumber = internalRecipientAccountNumber;
        transferData.transferType = 'INTERNAL';
        response = await api.post(`/customers/${customerId}/transfer`, transferData);
      } else {
        if (!selectedBeneficiary) {
          setError('Please select a beneficiary.');
          return;
        }
        if (parseFloat(amount) > parseFloat(selectedBeneficiary.maxTransferLimit)) {
          setError(`Amount exceeds the maximum transfer limit of ${selectedBeneficiary.maxTransferLimit}`);
          return;
        }
        transferData.beneficiaryAccountNumber = selectedBeneficiary.accountNumber;
        transferData.transferType = 'EXTERNAL';
        response = await api.post(`/customers/${customerId}/transfer`, transferData);
      }

      if (response.status === 200) {
        setSuccessMessage(`${isInternalTransfer ? 'Internal ' : ''}Transfer successful!`);
        if (isInternalTransfer) {
          setInternalRecipientAccountNumber('');
        } else {
          setSelectedBeneficiary(null);
          setMaxTransferLimit(null);
        }
        setAmount('');
        setDescription('');
        setTimeout(() => navigate('/customer/dashboard'), 1500);
      } else {
        const errorData = await response.data;
        setError(errorData?.message || `Transfer failed.`);
      }
    } catch (error) {
      console.error("Transfer failed:", error);
      if (error.response) {
        console.log('Error data:', error.response.data);
        console.log('Error status:', error.response.status);

        if (error.response.status === 400) {
          if (error.response.data === 'Insufficient Balance' || error.response.data?.includes('Insufficient balance')) {
            setError('Insufficient balance in the source account.');
          } else if (error.response.data === 'Recipient account not found') {
            setError('The recipient account number was not found.');
          } else if (error.response.data === 'Cannot transfer to the same account') {
            setError('You cannot transfer money to the same account.');
          } else if (error.response.data?.includes('Transfer amount exceeds the maximum transfer limit')) {
            setError(error.response.data);
          } else if (error.response.data === 'Invalid transfer type.') {
            setError('Invalid transfer type.');
          } else {
            setError(error.response.data?.message || 'Transfer failed due to a bad request.');
          }
        } else if (error.response.status === 404) {
          setError(error.response.data?.message || 'Recipient or source account not found.');
        } else if (error.response.status === 500) {
          setError('An internal server error occurred. Please try again later.');
        } else {
          setError(`Transfer failed with status: ${error.response.status}`);
        }
      } else if (error.request) {
        setError('Network error. Please check your internet connection.');
      } else {
        setError('An unexpected error occurred.');
      }
    }
  };

  const handleCancel = () => {
    navigate('/customer/dashboard');
  };

  return (
    <div className={`container mt-5 ${isDark ? 'bg-dark text-light' : 'bg-light text-dark'}`}>
      <div className="row justify-content-center">
        <div className="col-md-6">
          <div className={`card p-4 shadow ${isDark ? 'bg-secondary border-secondary text-light' : 'bg-white'}`}>
            <h2 className={`mb-4 text-center ${isDark ? 'text-primary' : 'text-primary'}`}>Transfer Money</h2>

            <div className="mb-3">
              <label className="form-label">Select Account to Transfer From</label>
              <select
                className={`form-control ${isDark ? 'bg-dark text-light border-secondary' : ''}`}
                value={selectedAccount}
                onChange={handleAccountChange}
                required
                disabled={accounts.length === 0}
              >
                <option value="">{accounts.length === 0 ? 'No Accounts Available' : '-- Select an Account --'}</option>
                {accounts.map((account) => (
                  <option key={account.accountNumber} value={account.accountNumber}>
                    Account Number: {account.accountNumber} (Type: {account.accountType})
                  </option>
                ))}
              </select>
            </div>

            {availableBalance !== null && (
              <p className={`pl-2 text-info ${isDark ? 'text-light' : ''}`}>
                Available Balance: <strong>{availableBalance}</strong>
              </p>
            )}
            {availableBalance === null && selectedAccount && (
              <p className={`mb-3 text-muted text-center ${isDark ? 'text-light' : ''}`}>
                Failed to fetch balance for selected account.
              </p>
            )}

            {error && <div className="alert alert-danger">{error}</div>}
            {successMessage && <div className="alert alert-success">{successMessage}</div>}
            {infoMessage && <div className="alert alert-info">{infoMessage}</div>}

            <form onSubmit={handleSubmit}>
              <div className="mb-3">
                <label className="form-label">Transfer Type</label>
                <select className={`form-control ${isDark ? 'bg-dark text-light border-secondary' : ''}`} onChange={handleTransferTypeChange} value={isInternalTransfer ? 'internal' : 'external'}>
                  <option value="external">External Transfer</option>
                  <option value="internal">Internal Transfer</option>
                </select>
              </div>

              {isInternalTransfer ? (
                <div className="mb-3">
                  <label htmlFor="internalRecipientAccountNumber" className="form-label">Recipient Account Number</label>
                  <input
                    type="text"
                    className={`form-control ${isDark ? 'bg-dark text-light border-secondary' : ''}`}
                    id="internalRecipientAccountNumber"
                    value={internalRecipientAccountNumber}
                    onChange={handleInternalRecipientChange}
                    required
                  />
                </div>
              ) : (
                <div className="mb-3">
                  <label htmlFor="beneficiary" className="form-label">Select Beneficiary</label>
                  <select
                    className={`form-control ${isDark ? 'bg-dark text-light border-secondary' : ''}`}
                    id="beneficiary"
                    onChange={handleBeneficiaryChange}
                    value={selectedBeneficiary ? selectedBeneficiary.beneficiaryId : ''}
                    required
                    disabled={beneficiaries.length === 0}
                  >
                    <option value="">{beneficiaries.length === 0 ? 'No Beneficiaries Added' : '-- Select a Beneficiary --'}</option>
                    {beneficiaries.map((beneficiary) => (
                      <option key={beneficiary.beneficiaryId} value={beneficiary.beneficiaryId}>
                        {beneficiary.beneficiaryName} ({beneficiary.accountNumber})
                      </option>
                    ))}
                  </select>
                </div>
              )}

              {!isInternalTransfer && selectedBeneficiary && (
                <div className="mb-3">
                  <p className={`text-info ${isDark ? 'text-light' : ''}`}>
                    Max Transfer Limit for {selectedBeneficiary.beneficiaryName}: {selectedBeneficiary.maxTransferLimit}
                  </p>
                </div>
              )}

              <div className="mb-3">
                <label htmlFor="amount" className="form-label">Amount</label>
                <input
                  type="number"
                  className={`form-control ${isDark ? 'bg-dark text-light border-secondary' : ''}`}
                  id="amount"
                  value={amount}
                  onChange={handleAmountChange}
                  required
                  min="0.01"
                  step="0.01"
                  max={!isInternalTransfer ? (maxTransferLimit || '') : ''}
                  disabled={accounts.length === 0}
                />
              </div>

              <div className="mb-3">
                <label htmlFor="description" className="form-label">Description (Optional)</label>
                <textarea
                  className={`form-control ${isDark ? 'bg-dark text-light border-secondary' : ''}`}
                  id="description"
                  value={description}
                  onChange={(e) => setDescription(e.target.value)}
                  rows="3"
                  disabled={accounts.length === 0}
                ></textarea>
              </div>

              <div className="d-grid gap-2">
                <button
                  type="submit"
                  className="btn btn-primary btn-lg"
                  disabled={
                    error ||
                    accounts.length === 0 ||
                    !selectedAccount ||
                    (isInternalTransfer && !internalRecipientAccountNumber) ||
                    (!isInternalTransfer && (!selectedBeneficiary || beneficiaries.length === 0))
                  }
                >
                  Transfer
                </button>
                <button type="button" onClick={handleCancel} className={`btn btn-secondary ${isDark ? 'btn-outline-light' : ''}`}>
                  Cancel
                </button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>
  );
};

export default TransferMoneyComponent;