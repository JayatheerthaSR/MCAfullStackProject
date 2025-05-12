import React, { useState, useEffect } from 'react';

function Greeting() {
  const [greeting, setGreeting] = useState('');
  const [error, setError] = useState(null);

  useEffect(() => {
    fetch('http://localhost:8080/api/greeting')
      .then(response => {
        if (!response.ok) {
          throw new Error(`HTTP error! status: ${response.status}`);
        }
        return response.text(); // Expecting plain text response
      })
      .then(data => {
        setGreeting(data);
      })
      .catch(error => {
        setError(error.message);
        console.error('Error fetching greeting:', error);
      });
  }, []); // Empty dependency array means this runs once after the initial render

  if (error) {
    return <div>Error: {error}</div>;
  }

  return (
    <div>
      <h1>Greeting from Backend:</h1>
      <p>{greeting}</p>
    </div>
  );
}

export default Greeting;