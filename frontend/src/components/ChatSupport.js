// src/components/ChatSupport.js
import React, { useState, useContext } from 'react';
import 'bootstrap/dist/css/bootstrap.min.css';
import { ThemeContext } from '../contexts/ThemeContext';
import './ChatSupport.css';

const ChatSupport = () => {
  const [isOpen, setIsOpen] = useState(false);
  const [messages, setMessages] = useState([]);
  const [newMessage, setNewMessage] = useState('');
  const { theme } = useContext(ThemeContext);
  const isDark = theme === 'dark';

  const toggleChat = () => {
    setIsOpen(!isOpen);
    if (!isOpen && messages.length === 0) {
      // Initial greeting from the AI
      addMessage('AI', 'Hello! How can I help you today?');
    }
  };

  const handleInputChange = (event) => {
    setNewMessage(event.target.value);
  };

  const handleSendMessage = () => {
    if (newMessage.trim()) {
      addMessage('You', newMessage);
      // Simulate AI response (replace with actual API call later)
      setTimeout(() => {
        const aiResponse = getAIResponse(newMessage);
        addMessage('AI', aiResponse);
      }, 500);
      setNewMessage('');
    }
  };

  const addMessage = (sender, text) => {
    setMessages([...messages, { sender, text }]);
  };

  const getAIResponse = (userInput) => {
    const lowerInput = userInput.toLowerCase();
    if (lowerInput.includes('balance')) {
      return 'To check your balance, please go to the dashboard.';
    } else if (lowerInput.includes('transfer')) {
      return 'You can initiate a transfer in the "Transfer" section.';
    } else if (lowerInput.includes('help')) {
      return 'I can help you with checking your balance, making transfers, and finding transaction history.';
    } else if (lowerInput.includes('contact')) {
      return 'Our customer support can be reached at support@mybank.com or 1-800-MYBANK.';
    } else {
      return 'I am still learning. Can you please rephrase your question?';
    }
  };

  return (
    <div className={`chat-support-container ${isDark ? 'dark-theme' : 'light-theme'}`}>
      <button className={`chat-toggle-button ${isOpen ? 'open' : ''} ${isDark ? 'btn-dark' : 'btn-primary'}`} onClick={toggleChat}>
        {isOpen ? 'Close Chat' : 'Chat with AI'}
      </button>

      {isOpen && (
        <div className={`chat-window ${isDark ? 'bg-secondary text-light border-secondary' : 'bg-white border-primary'}`}>
          <div className="chat-header">AI Support</div>
          <div className="chat-messages">
            {messages.map((msg, index) => (
              <div key={index} className={`message ${msg.sender === 'You' ? 'user' : 'ai'}`}>
                <strong>{msg.sender}:</strong> {msg.text}
              </div>
            ))}
          </div>
          <div className="chat-input">
            <input
              type="text"
              className={`form-control ${isDark ? 'bg-dark text-light border-secondary' : ''}`}
              placeholder="Type your message..."
              value={newMessage}
              onChange={handleInputChange}
              onKeyPress={(event) => event.key === 'Enter' && handleSendMessage()}
            />
            <button className={`btn ${isDark ? 'btn-outline-light' : 'btn-outline-primary'} ms-2`} onClick={handleSendMessage}>Send</button>
          </div>
        </div>
      )}
    </div>
  );
};

// Basic CSS for the chat support


export default ChatSupport;