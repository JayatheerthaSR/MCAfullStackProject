import React, { useState, useContext } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../api';
import 'bootstrap/dist/css/bootstrap.min.css';
import { ThemeContext } from '../contexts/ThemeContext';

const GlobalSearchComponent = () => {
  const [searchTerm, setSearchTerm] = useState('');
  const [searchResults, setSearchResults] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const navigate = useNavigate();
  const { theme } = useContext(ThemeContext);
  const isDark = theme === 'dark';

  const handleInputChange = (event) => {
    setSearchTerm(event.target.value);
  };

  const handleSearchSubmit = async (event) => {
    event.preventDefault();
    setLoading(true);
    setError('');
    setSearchResults([]);

    if (!searchTerm.trim()) {
      setLoading(false);
      return;
    }

    try {
      // Spring Boot API endpoint for search
      const response = await api.get(`/search?query=${searchTerm}`);
      if (response.status === 200) {
        setSearchResults(response.data);
      } else {
        setError('Search failed.');
      }
    } catch (error) {
      setError(error.response?.data?.message || 'Error during search.');
    } finally {
      setLoading(false);
    }
  };

  const handleResultClick = (result) => {
    // Define navigation based on the 'entityType' returned by Spring Boot
    if (result.entityType === 'account') {
      navigate(`/customer/account/${result.accountNumber}`);
    } else if (result.entityType === 'beneficiary') {
      navigate(`/customer/beneficiary/${result.beneficiaryId}`);
    } else if (result.entityType === 'transaction') {
      navigate(`/customer/transaction/${result.transactionId}`);
    } else {
      console.log('Clicked on:', result);
    }
  };

  return (
    <div className={`global-search-container ${isDark ? 'bg-dark text-light' : 'bg-light text-dark'} p-3`} style={{ width: '300px' }}> {/* Added inline style for width */}
      <form onSubmit={handleSearchSubmit} className="mb-2"> {/* Reduced margin-bottom */}
        <div className="input-group">
          <input
            type="text"
            className={`form-control form-control-sm ${isDark ? 'bg-dark text-light border-secondary' : ''}`}
            placeholder="Search..." 
            value={searchTerm}
            onChange={handleInputChange}
            aria-label="Global Search"
          />
          <button className="btn btn-primary btn-sm" type="submit" disabled={loading}> {/* Made button smaller */}
            {loading ? <span className="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span> : <i className="bi bi-search"></i>} {/* Smaller spinner and search icon */}
          </button>        </div>
      </form>

      {error && <div className="alert alert-danger small">{error}</div>} {/* Made error message smaller */}

      {searchResults.length > 0 && (
        <div className={`search-results-container ${isDark ? 'bg-secondary text-light border-secondary' : 'bg-white'} rounded shadow p-2`} style={{ position: 'absolute', zIndex: 1000, width: '300px', maxHeight: '300px', overflowY: 'auto' }}> {/* Added absolute positioning, z-index, width, max-height, and overflow */}
          <h6 className="mb-2">Search Results</h6> {/* Smaller heading */}
          <ul className="list-group list-group-flush"> {/* Removed default list styling */}
            {searchResults.map((result) => (
              <li
                key={result.id}
                className={`list-group-item list-group-item-action ${isDark ? 'bg-dark text-light border-secondary' : ''} cursor-pointer small p-2`} 
                onClick={() => handleResultClick(result)}
              >
                <strong className="small">{result.displayText}</strong>
                <br />
                <small className="text-muted">{result.entityType}</small>
              </li>
            ))}
            {searchResults.length > 5 && (
              <li className={`list-group-item text-center ${isDark ? 'bg-dark text-light border-secondary' : 'bg-white'} small p-2 text-muted`}>
                Scroll for more results
              </li>
            )}
          </ul>
        </div>
      )}
    </div>
  );
};

export default GlobalSearchComponent;