import React, { useState, useContext, useRef, useEffect, useCallback } from 'react'; // Added useCallback
import { useNavigate } from 'react-router-dom';
import api from '../api';
import { ThemeContext } from '../contexts/ThemeContext';
import './GlobalSearchComponent.css';

const GlobalSearchComponent = () => {
  const [searchTerm, setSearchTerm] = useState('');
  const [searchResults, setSearchResults] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [isSearchActive, setIsSearchActive] = useState(false);
  const navigate = useNavigate();
  const { theme } = useContext(ThemeContext);
  const isDark = theme === 'dark';

  const searchContainerRef = useRef(null);
  const searchInputRef = useRef(null); // Ref specifically for the input field

  // Memoized callback for setting active state on focus
  const activateSearch = useCallback(() => {
    if (!isSearchActive) {
      setIsSearchActive(true);
      console.log('Search Activated via useCallback!');
    }
  }, [isSearchActive]);

  // Memoized callback for deactivating search on blur
  const deactivateSearch = useCallback(() => {
    // Only deactivate if the search term is empty AND focus has left the search container
    // Use a small timeout to allow click events on search results to register
    setTimeout(() => {
      if (!searchTerm.trim() && searchContainerRef.current && !searchContainerRef.current.contains(document.activeElement)) {
        setIsSearchActive(false);
        console.log('Search Deactivated via useCallback!');
      }
    }, 100);
  }, [searchTerm]);

  // Effect to handle clicks outside the search component
  useEffect(() => {
    const handleClickOutside = (event) => {
      if (searchContainerRef.current && !searchContainerRef.current.contains(event.target)) {
        setSearchResults([]);
        setError('');
        deactivateSearch(); // Use the memoized callback
      }
    };

    document.addEventListener('mousedown', handleClickOutside);
    return () => {
      document.removeEventListener('mousedown', handleClickOutside);
    };
  }, [deactivateSearch]); // Dependency on deactivateSearch

  // Effect to manage focus/blur on the actual input element
  useEffect(() => {
    const currentInput = searchInputRef.current;
    if (currentInput) {
      // Add event listeners directly to the DOM element
      currentInput.addEventListener('focus', activateSearch);
      currentInput.addEventListener('blur', deactivateSearch);

      // Cleanup function to remove event listeners
      return () => {
        currentInput.removeEventListener('focus', activateSearch);
        currentInput.removeEventListener('blur', deactivateSearch);
      };
    }
  }, [activateSearch, deactivateSearch]); // Re-run if callbacks change

  // Handle input value changes
  const handleInputChange = (event) => {
    setSearchTerm(event.target.value);
    // Ensure search is active if content is typed
    if (event.target.value.trim() && !isSearchActive) {
      setIsSearchActive(true);
    } else if (!event.target.value.trim() && isSearchActive) {
      // If content is cleared, and search is active, attempt to deactivate
      // This is a subtle improvement to handle clearing the input while focused
      deactivateSearch();
    }
  };

  // Function to explicitly focus the input when the wrapper is clicked
  const handleWrapperClick = () => {
    if (searchInputRef.current) {
      searchInputRef.current.focus();
    }
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
      const response = await api.get(`/search?query=${encodeURIComponent(searchTerm)}`);
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
    setSearchTerm('');
    setSearchResults([]);
    setError('');
    setIsSearchActive(false); // Deactivate after selection
    // Also, try to blur the input to collapse the search bar
    if (searchInputRef.current) {
      searchInputRef.current.blur();
    }

    if (result.entityType === 'ACCOUNT') {
      navigate(`/customer/account/${result.id}`);
    } else if (result.entityType === 'BENEFICIARY') {
      navigate(`/customer/beneficiary/${result.id}`);
    } else if (result.entityType === 'TRANSACTION') {
      navigate(`/customer/transaction/${result.id}`);
    } else if (result.entityType === 'USER') {
        navigate(`/admin/users?highlight=${result.id}`);
    }
    else {
      console.warn('Clicked on unknown entity type:', result.entityType, 'Result:', result);
    }
  };

  return (
    // Add onClick to the wrapper to force focus if needed
    <div
      className={`global-search-wrapper ${isSearchActive ? 'is-active' : ''}`}
      ref={searchContainerRef}
      onClick={handleWrapperClick} // This will try to focus the input when wrapper is clicked
    >
      {console.log('Render Cycle: isSearchActive =', isSearchActive, 'Applied class =', `global-search-wrapper ${isSearchActive ? 'is-active' : ''}`)}
      <form onSubmit={handleSearchSubmit} className="d-flex mb-0">
        <div className="input-group input-group-sm">
          <input
            type="text"
            className={`form-control ${isDark ? 'bg-secondary text-light border-secondary' : 'bg-light text-dark'}`}
            placeholder="Search..."
            value={searchTerm}
            onChange={handleInputChange}
            ref={searchInputRef} // Assign ref to the input
            // Removed onFocus and onBlur props as they are handled by useEffect
            aria-label="Global Search input field"
          />
          <button
            className={`btn btn-primary btn-sm ${isDark ? 'btn-outline-light' : ''}`}
            type="submit"
            disabled={loading}
            aria-label="Perform search"
          >
            {loading ? <span className="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span> : <i className="bi bi-search"></i>}
          </button>
        </div>
      </form>

      {isSearchActive && (error || searchResults.length > 0) && (
        <>
          {error && (
            <div className={`alert alert-danger small search-error-dropdown ${isDark ? 'bg-danger text-light' : ''}`}>
              {error}
            </div>
          )}

          {searchResults.length > 0 && (
            <div className={`search-results-dropdown ${isDark ? 'bg-dark border-secondary' : 'bg-white border'} rounded shadow`}>
              <h6 className={`dropdown-header ${isDark ? 'text-light' : 'text-muted'}`}>Search Results</h6>
              <ul className="list-group list-group-flush">
                {searchResults.map((result) => (
                  <li
                    key={result.id || result.displayText}
                    className={`list-group-item list-group-item-action ${isDark ? 'bg-dark text-light border-secondary' : 'bg-white text-dark'}`}
                    onClick={() => handleResultClick(result)}
                  >
                    <div className="search-result-item">
                        <strong className="text-truncate d-block">{result.displayText}</strong>
                        <small className="text-muted">{result.entityType}</small>
                    </div>
                  </li>
                ))}
                {searchResults.length >= 5 && (
                  <li className={`list-group-item text-center ${isDark ? 'bg-dark text-light border-secondary' : 'bg-white text-muted'} small`}>
                    Scroll for more results...
                  </li>
                )}
              </ul>
            </div>
          )}
        </>
      )}
    </div>
  );
};

export default GlobalSearchComponent;